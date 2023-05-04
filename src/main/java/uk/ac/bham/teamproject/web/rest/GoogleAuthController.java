package uk.ac.bham.teamproject.web.rest;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import uk.ac.bham.teamproject.domain.*;
import uk.ac.bham.teamproject.repository.*;
import uk.ac.bham.teamproject.security.SecurityUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@RestController
@RequestMapping("/api")
@Transactional
public class GoogleAuthController {
    private final Logger log = LoggerFactory.getLogger(GoogleAuthController.class);

    @Value("${application.google-client-id}")
    private String CLIENT_ID;

    @Value("${application.google-client-secret}")
    private String CLIENT_SECRET;

    private final Set<String> SCOPES = new HashSet<>(Arrays.asList(
        "https://www.googleapis.com/auth/userinfo.email",
        "https://www.googleapis.com/auth/calendar",
        "https://www.googleapis.com/auth/calendar.events"
    ));

    @Value("${application.google-redirect}")
    private String REDIRECT_URI;

    @Value("${application.google-encryption-key-token}")
    private String ENCRYPTION_KEY_TOKEN;

    @Value("${application.google-encryption-key-state}")
    private String ENCRYPTION_KEY_STATE;

    private static final String TOKEN = "token";

    private static final String STATE = "state";

    private static final String CUSTOM_EVENT = "c";

    private static final String ORDER = "o";

    private static final String ORDER_PREFIX = "Order #";

    private static final String USER_INFO_ENDPOINT = "https://www.googleapis.com/oauth2/v1/userinfo?access_token=";

    private static final String TOKEN_REVOKE_ENDPOINT = "https://oauth2.googleapis.com/revoke?token=";

    private GoogleAuthorizationCodeFlow flow;

    private final GoogleCalendarAccessTokenRepository googleCalendarAccessTokenRepository;

    private final CalendarEventRepository calendarEventRepository;

    private final UserOrdersRepository ordersRepository;

    private final UserRepository userRepository;

    private final GoogleCalendarAsyncController googleCalendarAsyncController;


    public GoogleAuthController(GoogleCalendarAccessTokenRepository googleCalendarAccessTokenRepository, UserRepository userRepository, CalendarEventRepository calendarEventRepository, UserOrdersRepository ordersRepository, GoogleCalendarAsyncController googleCalendarAsyncController) {
        this.googleCalendarAccessTokenRepository = googleCalendarAccessTokenRepository;
        this.calendarEventRepository = calendarEventRepository;
        this.ordersRepository = ordersRepository;
        this.userRepository = userRepository;
        this.googleCalendarAsyncController = googleCalendarAsyncController;
    }

    //    API Endpoint to trigger a Google Authentication flow
    @GetMapping("/gauth")
    public ResponseEntity<String> gAuthorise(HttpServletRequest request, HttpServletResponse response) {

        //Build Google OAuth2 flow object with ClientID, ClientSecret and Scopes set above
        flow = new GoogleAuthorizationCodeFlow.Builder(
            new NetHttpTransport(), new GsonFactory(), CLIENT_ID, CLIENT_SECRET, SCOPES)
            .build();

        // Create a state token to prevent request forgery.

        String securityToken = new BigInteger(130, new SecureRandom()).toString(32);

        //Get the current logged in user to store the token against
        Optional<User> currentUser = getCurrentLoggedInUser();
        String currentUserLogin;
        if (currentUser.isPresent()) {
            currentUserLogin = currentUser.get().getLogin();
        } else {
            log.error("User is not authenticated: exiting");
            return ResponseEntity.status(401).build();
        }

        //Get the address of the page the user was on before they entered the authorisation flow
        //The user will get redirected to this page following authentication
        String referer = request.getHeader("Referer");
        try {
            //Set the state parameter to be an encryption of:
            //- security token (to prevent CSRF)
            //- user login (to know who to store the token against as user is unauthenticated when they return to the callback endpoint
            //- referer (to redirect user to after authentication)
            //security is guaranteed by the random security token encrypted with the other details
            String fullStateEncrypted = encrypt((securityToken + "," + currentUserLogin + "," + referer), STATE);

            // Store the security token in the session for later validation
            request.getSession().setAttribute("state", securityToken);

            // Build the URL for Google Authentication to redirect the user to
            // - state is passed back to the callback endpoint for verification
            // - redirectUri is the URL of the API endpoint Google redirects to for us to handle post authentication
            String authUrl = flow.newAuthorizationUrl()
                .setState(fullStateEncrypted)
                .setRedirectUri(REDIRECT_URI)
                .build();

            //Build a JSON object to respond via REST
            JsonObject jsonResponse = new JsonObject();

            //Pass the authorisation URL back to the user to get them redirected to
            jsonResponse.addProperty("url", authUrl);
            return ResponseEntity.ok().body(jsonResponse.toString());
        } catch (Exception e) {
            log.error("Error encrypting state: " + e);
            return ResponseEntity.internalServerError().build();
        }
    }

    //    API Endpoint that Google redirects the user to after authentication
    @GetMapping("/gauth/callback")
    public void oauthCallback(HttpServletRequest request, HttpServletResponse response) {
        //Get the authorisation code from Google
        String authCode = request.getParameter("code");
        try {
            //Get the state parameter passed back, decrypt it, and split it into individual parts
            String[] state = decrypt(request.getParameter("state"), STATE).split(",");
            String reqSecToken = state[0];
            String userLogin = state[1];
            String originUrl = state[2];

            //Get the security token we stored in the session, and remove it after use
            String sessionSecToken = request.getSession().getAttribute("state").toString();
            request.getSession().removeAttribute("state");

            //If these tokens don't match, return 401 unauthorised
            if (!reqSecToken.equals(sessionSecToken)) {
                log.error("Potential cross site request forgery!");
                response.setStatus(401);
                return;
            }


            if (authCode != null) {
                //Exchange the authorisation code for an access token
                TokenResponse tokenResponse = flow.newTokenRequest(authCode)
                    .setRedirectUri(REDIRECT_URI)
                    .execute();

                //calculate the token's expiry time (usually 3600 seconds, or 1 hour, from now)
                Instant expiryTime = Instant.now().plusSeconds(tokenResponse.getExpiresInSeconds());

                //get the access token, and encrypt it
                String encryptedToken = encrypt(tokenResponse.getAccessToken(), TOKEN);

                //Attempt to get the current logged in user (login from state parameter)
                Optional<User> user = userRepository.findOneByLogin(userLogin);
                if (user.isEmpty()) {
                    //User not found
                    log.error("User {} not found", userLogin);
                } else {
                    //Create a new GoogleCalendarAccessToken object with the required attributes
                    GoogleCalendarAccessToken token = new GoogleCalendarAccessToken().encryptedToken(encryptedToken).expires(expiryTime).user(user.get());
                    //store the token in the database
                    storeToken(token, userLogin);
                }
            }
            // If there is no authorisation code, the user did not grant permission

            if (originUrl != null) {
                //if the origin URL was passed, redirect them to it
                response.sendRedirect(originUrl);
            } else {
                //fallback to calendar URL
                response.sendRedirect("/calendar");
            }
        } catch (Exception e) {
            log.error("Error decrypting state: " + e);
        }
    }

    //    Encrypts a string according to the mode (which determines which key is used)
//    - mode = TOKEN -> string is encrypted as an access token
//    - mode = STATE -> string is encrypted as the state parameter
    private String encrypt(String token, String mode) throws Exception {
        //get a key based on the mode
        SecretKey key = generateKey(mode);

        //use AES encryption
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedString = cipher.doFinal(token.getBytes());

        //Encode as a Base64 string
        return Base64.getEncoder().encodeToString(encryptedString);
    }

    //    Decrypts a string according to the mode (which determines which key is used)
//    - mode = TOKEN -> string is decrypted as an access token
//    - mode = STATE -> string is decrypted as the state parameter
    private String decrypt(String encryptedToken, String mode) throws Exception {
        //get a key based on the mode
        SecretKey key = generateKey(mode);

        //use AES encryption
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);

        //decode from Base64, and return as a String
        byte[] decryptedString = cipher.doFinal(Base64.getDecoder().decode(encryptedToken));
        return new String(decryptedString);
    }

    //    Generates a key for encryption/decryption based on a mode
    private SecretKey generateKey(String mode) {
        byte[] key;

        switch (mode) {
            case TOKEN:
                key = Base64.getDecoder().decode(ENCRYPTION_KEY_TOKEN);
                break;
            case STATE:
                key = Base64.getDecoder().decode(ENCRYPTION_KEY_STATE);
                break;
            default:
                log.error("Unknown key generation mode. Should be either TOKEN or STATE");
                return null;
        }
        return new SecretKeySpec(key, "AES");
    }

    //    Stores an encrypted token in the database
    private void storeToken(GoogleCalendarAccessToken token, String userLogin) {
        try {
            //Check if the user already has an access token stored (if they authenticated previously)
            Optional<GoogleCalendarAccessToken> maybeStoredToken = getGoogleAuthToken(userLogin);

            //If user hasn't already got a token stored
            if (maybeStoredToken.isEmpty()) {
                //ensure the ID is set to null as new entry in database
                token.setId(null);

                if (token.getUser() == null) {
                    log.error("Token has no associated user, so not storing");
                    return;
                }

                //save in repository
                googleCalendarAccessTokenRepository.save(token);
            } else {
                //User already has token stored
                if (maybeStoredToken.get().getUser() == null) {
                    log.error("Token has no associated user, so not storing");
                    return;
                }

                //cannot overwrite someone else's token
                if (maybeStoredToken.get().getUser() != token.getUser()) {
                    log.error("User mismatch");
                    return;
                }

                //Get the token object already stored
                GoogleCalendarAccessToken storedToken = maybeStoredToken.get();
                //Replace access token with the new one
                storedToken.setEncryptedToken(token.getEncryptedToken());
                //Update the expiry time
                storedToken.setExpires(token.getExpires());

                //save in repository
                googleCalendarAccessTokenRepository.save(storedToken);
            }
        } catch (Exception e) {
            System.out.println("Error trying to store token: " + e);
        }

    }

    //    Gets a googleAuthToken object for a given userLogin
    public Optional<GoogleCalendarAccessToken> getGoogleAuthToken(String userLogin) throws Exception {
        //Attempt to find the user by the given login
        Optional<User> user = userRepository.findOneByLogin(userLogin);
        if (user.isEmpty()) {
            //Cannot find user
            log.error("Unable to find user \"{}\", so cannot return token", userLogin);
            throw new Exception("Unable to verify if user already has token: User not found");
        }

        //Attempt to get an access token for the found user - returns an Optional object so can be empty
        return googleCalendarAccessTokenRepository.findGoogleCalendarAccessTokenByUser(user.get());
    }

    //    Helper method to get the current logged in user
    private Optional<User> getCurrentLoggedInUser() {
        //Attempt to get the current user login
        Optional<String> currentUserLoginOptional = SecurityUtils.getCurrentUserLogin();
        if (currentUserLoginOptional.isEmpty()) {
            log.error("getCurrentLoggedInUser(): current user not found");
            return Optional.empty();
        }

        //find the user object in the user database - returns Optional object so can be empty
        return userRepository.findOneByLogin(currentUserLoginOptional.get());
    }

    //    REST endpoint to check whether a user has a valid GoogleCalendarAccessToken
    @GetMapping("/gauth/authcheck")
    public ResponseEntity<String> isAuthenticated() {
        //Attempt to get the current user
        //requests to this endpoint should therefore contain credentials
        Optional<User> currentUser = getCurrentLoggedInUser();
        if (currentUser.isEmpty()) {
            log.error("User not found");
            return ResponseEntity.badRequest().build();
        }

        //Attempt to get an access token for the current user
        Optional<GoogleCalendarAccessToken> maybeToken = googleCalendarAccessTokenRepository.findGoogleCalendarAccessTokenByUser(currentUser.get());

        boolean authStatus;

        if (maybeToken.isPresent()) {
            //check if token is valid
            authStatus = isTokenValid(maybeToken.get());
        } else {
            //if user has no token
            authStatus = false;
        }
        //Create the JSON to return as response
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("authStatus", authStatus);

        if (authStatus) {
            //if authorised, get the user's email
            String email = getUserEmail(maybeToken.get());
            jsonResponse.addProperty("email", email);
        }
        return ResponseEntity.ok().body(jsonResponse.toString());
    }

    private boolean isTokenValid(GoogleCalendarAccessToken token) {
        //Get the stored expiry time of the token
        Instant tokenExpiry = token.getExpires();
        //5 minute validity threshold (if token expires in 5 minutes time, consider it invalid)
        Instant validityThreshold = Instant.now().plusSeconds(300);
        int timeDifference = tokenExpiry.compareTo(validityThreshold);
        //valid if timeDifference is positive
        return (timeDifference > 0);
    }

    //    Get's a user's email address of their authorised Google Account
    private String getUserEmail(GoogleCalendarAccessToken token) {
        //client closed automatically on exit
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            //Get the API endpoint
            String endpoint = USER_INFO_ENDPOINT + decrypt(token.getEncryptedToken(), TOKEN);
            //Build the GET request object
            HttpGet request = new HttpGet(endpoint);
            //Execute the request and get the response
            CloseableHttpResponse response = client.execute(request);
            //convert the response into a JSON object
            JsonElement jsonResponse = new Gson().fromJson(EntityUtils.toString(response.getEntity()), JsonElement.class);
            //return just the value of the 'email' parameter
            return jsonResponse.getAsJsonObject().get("email").getAsString();
        } catch (Exception e) {
            log.error("Error getting user's email: " + e);
            return null;
        }
    }

    //REST request to sign the current user out of their Google Account
    // - Deletes their access token from the database
    // - Invalidates their last known access token with Google, so cannot be used again
    @PostMapping("/gauth/signout")
    public ResponseEntity<String> signOut() {
        //json object to pass as response with the following properties:
        //- authStatus = is the user still signed in? true = signed in, false = signed out
        //- tokenStatus = has the user's access token been invalidated? true = yes, false = no
        JsonObject jsonResponse = new JsonObject();
        try {
            //get the current logged in user, and check auth status in app
            Optional<User> currentUser = getCurrentLoggedInUser();
            if (currentUser.isEmpty()) {
                throw new Exception("User not authenticated");
            }

            //get their access token, if they have one
            Optional<GoogleCalendarAccessToken> accessToken = getGoogleAuthToken(currentUser.get().getLogin());
            if (accessToken.isEmpty()) {
                throw new Exception("User has no associated access token");
            }

            //delete their token from the database
            googleCalendarAccessTokenRepository.delete(accessToken.get());

            //invalidate the token, and return the response of that POST call
            int status = invalidateToken(accessToken.get()).getStatusCodeValue();
            if (status == 200) {
                jsonResponse.addProperty("authStatus", false);
                jsonResponse.addProperty("tokenStatus", false);
                return ResponseEntity.ok().body(jsonResponse.toString());
            } else {
                jsonResponse.addProperty("authStatus", false);
                jsonResponse.addProperty("tokenStatus", true);
                return ResponseEntity.internalServerError().body(jsonResponse.toString());
            }
        } catch (Exception e) {
            log.error("Error occurred whilst trying to sign out: " + e);
            jsonResponse.addProperty("authStatus", true);
            jsonResponse.addProperty("tokenStatus", true);
            return ResponseEntity.internalServerError().body(jsonResponse.toString());
        }
    }

    //Sends a POST request to a Google API endpoint, with the given access token
    //and invalidates it so it cannot be used again
    private ResponseEntity<Void> invalidateToken(GoogleCalendarAccessToken token) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            //build the URL
            //decrypt and encode the URL so that it can be passed over HTTP
            String endpoint = TOKEN_REVOKE_ENDPOINT + URLEncoder.encode(decrypt(token.getEncryptedToken(), TOKEN), StandardCharsets.UTF_8);
            HttpPost httpPost = new HttpPost(endpoint);

            //check the response
            CloseableHttpResponse response = client.execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                return ResponseEntity.ok().build();
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            log.error("Error occurred whilst trying to invalidate token: " + e);
            return ResponseEntity.internalServerError().build();
        }
    }

//    API Endpoint to get a user's Google Calendar Events, if they are authenticated
    @GetMapping("/gcal")
    public ResponseEntity<List<Event>> getGoogleCalendarEvents() {
        Optional<User> currentUser = getCurrentLoggedInUser();
        if (currentUser.isEmpty()) {
            log.error("Cannot find current user: user not authenticated");
            return ResponseEntity.noContent().build();
        }

        try {
            Optional<GoogleCalendarAccessToken> accessToken = getGoogleAuthToken(currentUser.get().getLogin());

            if (accessToken.isEmpty()) {
                throw new Exception("User is not signed into a Google Account");
            }

            if (!isTokenValid(accessToken.get())) {
                log.error("Last known access token for user \"" + currentUser.get().getLogin() + "\" is invalid. Require re-authentication");
                return ResponseEntity.status(401).build();
            }

            //Get the access token and decrypt it
            String unencryptedAccessToken = decrypt(accessToken.get().getEncryptedToken(), TOKEN);

            HttpTransport transport = new NetHttpTransport();

            GsonFactory gsonFactory = GsonFactory.getDefaultInstance();

            Credential credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                .setTransport(transport)
                .setJsonFactory(gsonFactory)
                .setTokenServerEncodedUrl("https://oauth2.googleapis.com/token")
                .build();

            credential.setAccessToken(unencryptedAccessToken);

            Calendar calendarService = new Calendar.Builder(transport, gsonFactory, credential)
                .setApplicationName("Orderly")
                .build();

            String calendarId = null;
            //get a list of the calendars that the user has
            List<CalendarListEntry> calendarList = calendarService.calendarList().list().execute().getItems();
            for (CalendarListEntry cal : calendarList) {
                //find the "Orderly" calendar to get its id
                //value is not stored in database to allow user to delete/recreate calendar if they wish
                if (cal.getSummary().equals("Orderly")) {
                    calendarId = cal.getId();
                    break;
                }
            }

            //If the user doesn't have an "Orderly" calendar, create one
            if (calendarId == null) {
                com.google.api.services.calendar.model.Calendar newOrderlyCalendar = new com.google.api.services.calendar.model.Calendar();
                newOrderlyCalendar.setSummary("Orderly");
                //insert the calendar into the user's calendars list
                com.google.api.services.calendar.model.Calendar orderlyCalendar = calendarService.calendars().insert(newOrderlyCalendar).execute();
                //get the new calendar's id for future use
                calendarId = orderlyCalendar.getId();
            }

            //get all of the events from the Orderly Google calendar
            //items may be passed in several "pages", so ensure we get them all
            Events existingGcalEvents = calendarService.events().list(calendarId).execute();
            List<Event> allExistingGoogleEvents = new ArrayList<>();
            //token to get the next page
            String nextPageToken;
            while (true) {
                //add this current page's events to the allExistingGoogleEvents list
                allExistingGoogleEvents.addAll(existingGcalEvents.getItems());

                //get the next page token
                nextPageToken = existingGcalEvents.getNextPageToken();

                //if that was the last page, break out of the loop
                if (nextPageToken == null) {
                    break;
                } else {
                    //if there is another page, get the events from that page
                    existingGcalEvents = calendarService.events().list(calendarId).setPageToken(nextPageToken).execute();
                }
            }
            //convert calendar events and orders into Google Calendar Events
            List<Event> customEventsToInsert = getAllCustomEventsForGoogleCalendar();
            List<Event> ordersToInsert = getOrdersForGoogleCalendar();
            List<Event> eventsToDelete = new ArrayList<>();
            //get uncategorised google calendar events and store them
            List<Event> googleEvents = new ArrayList<>();

            //for each event in the user's Google Calendar, get its extended properties object
            Event.ExtendedProperties extendedProperties;
            for (Event calEv : allExistingGoogleEvents) {
                extendedProperties = calEv.getExtendedProperties();
                if (extendedProperties == null) {
                    //unknown event, so add to google events list
                    googleEvents.add(calEv);
                } else {
                    if (!extendedProperties.getShared().containsKey("orderlyId")) {
                        //if the event doesn't contain an orderlyId, consider it a solo Google event
                        googleEvents.add(calEv);
                    } else {
                        //event is a custom event or order
                        //delete from calendar
                        //if event/order is still in database, it will be readded (and propagate any changes)
                        //if event/order has been deleted on Orderly, it will not be readded
                        eventsToDelete.add(calEv);
                    }
                }
            }

            //asynchronously delete/push new events to Google Calendar
            //improves performance, as Google only events are returned immediately
            this.googleCalendarAsyncController.pushEvents(calendarService, calendarId, customEventsToInsert, ordersToInsert, eventsToDelete);

            //return list of Google only events
            return ResponseEntity.ok().body(googleEvents);
        } catch (Exception e) {
            log.error("Error occurred whilst trying to get Google Calendar Events: " + e);
            return ResponseEntity.internalServerError().build();
        }
    }

    //    Returns a list of calendar events (parsed as a Google Event object) ready to be pushed to Google Calendar
    private List<Event> getAllCustomEventsForGoogleCalendar() {
        List<CalendarEvent> calendarEvents = calendarEventRepository.findByUserIsCurrentUser();
        List<Event> googleCalendarEvents = new ArrayList<>();
        HashMap<String, String> extProp = new HashMap<>();
        for (CalendarEvent event : calendarEvents) {
            extProp.clear();
            extProp.put("orderlyId", CUSTOM_EVENT + event.getId());
            googleCalendarEvents.add(new Event()
                .setSummary(event.getName())
                .setDescription(event.getDescription())
                .setLocation(event.getLocation())
                .setStart(zonedDateTimeToGoogleDateTime(event.getStart()))
                .setEnd(zonedDateTimeToGoogleDateTime(event.getEnd()))
                .setColorId("11")
                .setExtendedProperties(new Event.ExtendedProperties()
                    .set("orderlyId", CUSTOM_EVENT + event.getId().toString())
                    .setShared(extProp)));
        }

        return googleCalendarEvents;
    }

//    Returns a list of orders (parsed as a Google Event object) ready to be pushed to Google Calendar
    private List<Event> getOrdersForGoogleCalendar() {
        List<UserOrders> orders = ordersRepository.findByUserIsCurrentUser();
        List<Event> googleCalendarOrders = new ArrayList<>();
        HashMap<String, String> extProp = new HashMap<>();
        for (UserOrders ord : orders) {
            extProp.clear();
            extProp.put("orderlyId", ORDER + ord.getId());
            googleCalendarOrders.add(new Event()
                .setSummary(ORDER_PREFIX + ord.getOrderNum())
                .setStart(localDateToGoogleDateTime(ord.getDueDate()))
                .setEnd(localDateToGoogleDateTime(ord.getDueDate().plusDays(1)))
                .setColorId("5")
                .setExtendedProperties(new Event.ExtendedProperties()
                    .set("orderlyId", ORDER + ord.getId().toString())
                    .setShared(extProp)));
        }
        return googleCalendarOrders;
    }

    public ZonedDateTime googleDateTimeToZonedDateTime(EventDateTime eventDateTime) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(eventDateTime.getDateTime().getValue()), ZoneId.systemDefault());
    }

//    Converts a zonedDateTime object to DateTime object (from Google library)
    public EventDateTime zonedDateTimeToGoogleDateTime(ZonedDateTime zonedDateTime) {
        return new EventDateTime().setDateTime(new DateTime(zonedDateTime.toInstant().toEpochMilli())).setTimeZone(ZoneId.systemDefault().toString());
    }

//    Converts a localDate object to a DateTime object (from Google library)
    public EventDateTime localDateToGoogleDateTime(LocalDate localDate) {
        return new EventDateTime().setDateTime(new DateTime(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())).setTimeZone(ZoneId.systemDefault().toString());
    }

    private Event calendarEventToGoogleEvent(CalendarEvent event) {
        HashMap<String, String> extProp = new HashMap<>();
        extProp.put("orderlyId", CUSTOM_EVENT + event.getId());
        return new Event()
            .setSummary(event.getName())
            .setDescription(event.getDescription())
            .setLocation(event.getLocation())
            .setStart(zonedDateTimeToGoogleDateTime(event.getStart()))
            .setEnd(zonedDateTimeToGoogleDateTime(event.getEnd()))
            .setExtendedProperties(new Event.ExtendedProperties()
                .set("orderlyId", CUSTOM_EVENT + event.getId().toString())
                .setShared(extProp));
    }

//    returns a list of Google Events from the users calendar, without writing to it
    public List<Event> readOnlyEvents(int num) {
        Optional<User> currentUser = getCurrentLoggedInUser();
        if (currentUser.isEmpty()) {
            log.error("Cannot find current user: user not authenticated");
            //return empty list
            return List.of();
        }

        try {
            Optional<GoogleCalendarAccessToken> accessToken = getGoogleAuthToken(currentUser.get().getLogin());

            if (accessToken.isEmpty()) {
                throw new Exception("User is not signed into a Google Account");
            }

            if (!isTokenValid(accessToken.get())) {
                log.error("Last known access token for user \"" + currentUser.get().getLogin() + "\" is invalid. Require re-authentication");
                //return empty list
                return List.of();
            }

            //Get the access token and decrypt it
            String unencryptedAccessToken = decrypt(accessToken.get().getEncryptedToken(), TOKEN);

            HttpTransport transport = new NetHttpTransport();

            GsonFactory gsonFactory = GsonFactory.getDefaultInstance();

            Credential credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                .setTransport(transport)
                .setJsonFactory(gsonFactory)
                .setTokenServerEncodedUrl("https://oauth2.googleapis.com/token")
                .build();

            credential.setAccessToken(unencryptedAccessToken);

            Calendar calendarService = new Calendar.Builder(transport, gsonFactory, credential)
                .setApplicationName("Orderly")
                .build();

            String calendarId = null;
            //get a list of the calendars that the user has
            List<CalendarListEntry> calendarList = calendarService.calendarList().list().execute().getItems();
            for (CalendarListEntry cal : calendarList) {
                //find the "Orderly" calendar to get its id
                //value is not stored in database to allow user to delete/recreate calendar if they wish
                if (cal.getSummary().equals("Orderly")) {
                    calendarId = cal.getId();
                    break;
                }
            }

            if(calendarId == null) {
                //user doesn't have an Orderly calendar, so has no events to return
                return List.of();
            } else {
                //get all of the events from the Orderly Google calendar
                //items may be passed in several "pages", so ensure we get them all
                Events existingGcalEvents = calendarService.events().list(calendarId).setOrderBy("startTime").setSingleEvents(true).setTimeMin(new DateTime(new Date().toInstant().toEpochMilli())).execute();
                List<Event> allExistingGoogleEvents = new ArrayList<>();
                //token to get the next page
                String nextPageToken;
                while (true) {
                    //add this current page's events to the allExistingGoogleEvents list
                    allExistingGoogleEvents.addAll(existingGcalEvents.getItems());

                    //get the next page token
                    nextPageToken = existingGcalEvents.getNextPageToken();

                    //if that was the last page, break out of the loop
                    if (nextPageToken == null) {
                        break;
                    } else {
                        //if there is another page, get the events from that page
                        existingGcalEvents = calendarService.events().list(calendarId).setPageToken(nextPageToken).execute();
                    }
                }

                List<Event> topNGoogleOnlyEvents = new ArrayList<>();
                Event.ExtendedProperties extProp;
                Event curEvnt;
                for(int i = 0; (i < allExistingGoogleEvents.size()) && (topNGoogleOnlyEvents.size() < (num + 1)); i++) {
                    curEvnt = allExistingGoogleEvents.get(i);
                    extProp = curEvnt.getExtendedProperties();
                    if(extProp == null) {
                        //if no extended property set, it's a plain Google Event (not created in Orderly)
                        topNGoogleOnlyEvents.add(curEvnt);
                    } else {
                        if(!extProp.getShared().containsKey("orderlyId")) {
                            //extended property may have been set by another application that uses Google Calendar API
                            //either way, not an Orderly event, so add it
                            topNGoogleOnlyEvents.add(curEvnt);
                        }
                    }
                }

                return topNGoogleOnlyEvents;
            }
        } catch (Exception e) {
            log.error("Error occurred trying to get " + num + " events from Google Calendar: " + e);
            //return empty list
            e.printStackTrace();
            return List.of();
        }
    }
}
