package uk.ac.bham.teamproject.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;
import uk.ac.bham.teamproject.domain.CalendarEvent;
import uk.ac.bham.teamproject.domain.User;
import uk.ac.bham.teamproject.repository.CalendarEventRepository;
import uk.ac.bham.teamproject.repository.UserRepository;
import uk.ac.bham.teamproject.security.SecurityUtils;
import uk.ac.bham.teamproject.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link uk.ac.bham.teamproject.domain.CalendarEvent}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CalendarEventResource {

    private final Logger log = LoggerFactory.getLogger(CalendarEventResource.class);

    private static final String ENTITY_NAME = "calendarEvent";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CalendarEventRepository calendarEventRepository;

    private final UserRepository userRepository;

    private final GoogleAuthController googleAuthController;

    public CalendarEventResource(CalendarEventRepository calendarEventRepository, UserRepository userRepository, GoogleAuthController googleAuthController) {
        this.calendarEventRepository = calendarEventRepository;
        this.userRepository = userRepository;
        this.googleAuthController = googleAuthController;
    }

    /**
     * {@code POST  /calendar-events} : Create a new calendarEvent.
     *
     * @param calendarEvent the calendarEvent to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new calendarEvent, or with status {@code 400 (Bad Request)} if the calendarEvent has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/calendar-events")
    public ResponseEntity<CalendarEvent> createCalendarEvent(@Valid @RequestBody CalendarEvent calendarEvent) throws URISyntaxException {
        log.debug("REST request to save CalendarEvent : {}", calendarEvent);
        if (calendarEvent.getId() != null) {
            throw new BadRequestAlertException("A new calendarEvent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        //Ensure the user property of the event is set to the currently logged in user only
        Optional<String> maybeUserLogin = SecurityUtils.getCurrentUserLogin();
        if(maybeUserLogin.isEmpty()) {
            //user is not authenticated
            return ResponseEntity.badRequest().build();
        }
        Optional<User> maybeUser = userRepository.findOneByLogin(maybeUserLogin.get());
        if(maybeUser.isEmpty()) {
            //couldn't find user in database
            return ResponseEntity.badRequest().build();
        }
        calendarEvent.setUser(maybeUser.get());

        CalendarEvent result = calendarEventRepository.save(calendarEvent);
        //changed alert to use the event name as the parameter instead of event id for readability
        return ResponseEntity
            .created(new URI("/api/calendar-events/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getName()))
            .body(result);
    }

    /**
     * {@code PUT  /calendar-events/:id} : Updates an existing calendarEvent.
     *
     * @param id the id of the calendarEvent to save.
     * @param calendarEvent the calendarEvent to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated calendarEvent,
     * or with status {@code 400 (Bad Request)} if the calendarEvent is not valid,
     * or with status {@code 500 (Internal Server Error)} if the calendarEvent couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/calendar-events/{id}")
    public ResponseEntity<CalendarEvent> updateCalendarEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CalendarEvent calendarEvent
    ) throws URISyntaxException {
        log.debug("REST request to update CalendarEvent : {}, {}", id, calendarEvent);
        if (calendarEvent.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, calendarEvent.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!calendarEventRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        CalendarEvent result = calendarEventRepository.save(calendarEvent);
        //changed alert to use the event name as the parameter instead of event id for readability
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, calendarEvent.getName()))
            .body(result);
    }

    /**
     * {@code PATCH  /calendar-events/:id} : Partial updates given fields of an existing calendarEvent, field will ignore if it is null
     *
     * @param id the id of the calendarEvent to save.
     * @param calendarEvent the calendarEvent to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated calendarEvent,
     * or with status {@code 400 (Bad Request)} if the calendarEvent is not valid,
     * or with status {@code 404 (Not Found)} if the calendarEvent is not found,
     * or with status {@code 500 (Internal Server Error)} if the calendarEvent couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/calendar-events/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CalendarEvent> partialUpdateCalendarEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CalendarEvent calendarEvent
    ) throws URISyntaxException {
        log.debug("REST request to partial update CalendarEvent partially : {}, {}", id, calendarEvent);
        if (calendarEvent.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, calendarEvent.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!calendarEventRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CalendarEvent> result = calendarEventRepository
            .findById(calendarEvent.getId())
            .map(existingCalendarEvent -> {
                if (calendarEvent.getName() != null) {
                    existingCalendarEvent.setName(calendarEvent.getName());
                }
                if (calendarEvent.getDescription() != null) {
                    existingCalendarEvent.setDescription(calendarEvent.getDescription());
                }
                if (calendarEvent.getStart() != null) {
                    existingCalendarEvent.setStart(calendarEvent.getStart());
                }
                if (calendarEvent.getEnd() != null) {
                    existingCalendarEvent.setEnd(calendarEvent.getEnd());
                }
                if (calendarEvent.getLocation() != null) {
                    existingCalendarEvent.setLocation(calendarEvent.getLocation());
                }

                return existingCalendarEvent;
            })
            .map(calendarEventRepository::save);

        //changed alert to use the event name as the parameter instead of event id for readability
        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, calendarEvent.getName())
        );
    }

    /**
     * {@code GET  /calendar-events} : get all the calendarEvents.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of calendarEvents in body.
     */
    @GetMapping("/calendar-events")
    public List<CalendarEvent> getAllCalendarEvents(@RequestParam(required = false, defaultValue = "false") boolean eagerload, @RequestParam(required = false, defaultValue = "false") boolean allEvents) {
        log.debug("REST request to get all CalendarEvents");
        //If the current user is an admin, and has requested all events (from all users) are returned, do so
        if(allEvents && SecurityUtils.hasCurrentUserThisAuthority("ROLE_ADMIN")) {
            if(eagerload) {
                return calendarEventRepository.findAllWithEagerRelationships();
            } else {
                return calendarEventRepository.findAll();
            }
        } else {
            //If user is not an admin, or admin has not requested everyone's events (e.g. for their own calendar), return only the events assigned to that user
            return calendarEventRepository.findByUserIsCurrentUser();
        }
    }

    /**
     * {@code GET  /calendar-events/:id} : get the "id" calendarEvent.
     *
     * @param id the id of the calendarEvent to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the calendarEvent, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/calendar-events/{id}")
    public ResponseEntity<CalendarEvent> getCalendarEvent(@PathVariable Long id) {
        log.debug("REST request to get CalendarEvent : {}", id);

        Optional<CalendarEvent> calendarEvent = calendarEventRepository.findOneWithEagerRelationships(id);
        //if the requesting user is either an admin or the owner of the event, then provide event
        //else, return that the content doesn't exist
        if(SecurityUtils.hasCurrentUserThisAuthority("ROLE_ADMIN") || isOwner(SecurityUtils.getCurrentUserLogin(), calendarEvent)) {
            if(calendarEvent.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return ResponseUtil.wrapOrNotFound(calendarEvent);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    /**
     * {@code DELETE  /calendar-events/:id} : delete the "id" calendarEvent.
     *
     * @param id the id of the calendarEvent to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/calendar-events/{id}")
    public ResponseEntity<Void> deleteCalendarEvent(@PathVariable Long id) {
        log.debug("REST request to delete CalendarEvent : {}", id);
        String eventName = calendarEventRepository.getReferenceById(id).getName();
        calendarEventRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, eventName))
            .build();
    }

//    Determines whether the calendar event passed in is owned by the user login passed in
    private boolean isOwner(Optional<String> login, Optional<CalendarEvent> calendarEvent) {
        if(login.isEmpty() || userRepository.findOneByLogin(login.get()).isEmpty()) {
            throw new BadRequestAlertException("You need to be signed in to access this resource", ENTITY_NAME, "unauthorised");
        }
        if(calendarEvent.isEmpty()) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        return (userRepository.findOneByLogin(login.get()).get()).equals(calendarEvent.get().getUser());
    }

    @GetMapping("/calendar-events/summary")
    public ResponseEntity<String> getEventsSummary() {
        try {
            Optional<String> currentUserLoginOptional = SecurityUtils.getCurrentUserLogin();
            if(currentUserLoginOptional.isEmpty()) {
                throw new Exception("User is not authenticated");
            }
            Optional<User> currentUserOptional = userRepository.findOneByLogin(currentUserLoginOptional.get());
            if(currentUserOptional.isEmpty()) {
                throw new Exception("User is not authenticated");
            }

            List<CalendarEvent> topCalendarEvents = calendarEventRepository.findTop5ByUserAndStartAfterOrderByStart(currentUserOptional.get(), ZonedDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault()));

            List<Event> topGoogleEvents;

            JsonElement googleAuthResponse = new Gson().fromJson(googleAuthController.isAuthenticated().getBody(), JsonElement.class);

            boolean googleAuthStatus = googleAuthResponse.getAsJsonObject().get("authStatus").getAsBoolean();

            if(googleAuthStatus) {
                topGoogleEvents = googleAuthController.readOnlyEvents(5);
                if(topGoogleEvents.isEmpty()) {
                    //no Google events to display, so just return calendar events as JSON
                    return ResponseEntity.ok(new Gson().toJson(topCalendarEvents));
                } else {
                    return ResponseEntity.ok(sortEvents(topCalendarEvents, topGoogleEvents, 5));
                }
            } else {
                //user is not signed into a Google Account, so has no Google events, just calendar events
                return ResponseEntity.ok(new Gson().toJson(topCalendarEvents));
            }
        } catch(Exception e) {
            log.error("Error obtaining event summary: " + e);
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    private String sortEvents(List<CalendarEvent> calendarEvents, List<Event> googleEvents, int num) {
        List<JsonObject> sortedEvents = new ArrayList<>();
        Event googleEvent;
        DateTime googleDateTime;
        long googleDateTimeVal;
        int remaining = num;

        while(sortedEvents.size() < num) {
            if(calendarEvents.isEmpty()) {
                //no calendar events left so just google events, until remaining=0 or google events is empty
                for(int g = 0; (g < remaining) && !(googleEvents.isEmpty()); g++) {
                    sortedEvents.add(googleEventToJson(googleEvents.get(0)));
                    remaining--;
                    googleEvents.remove(0);
                }
                break;
            }

            if(googleEvents.isEmpty()) {
                //no calendar events left so just google events, until remaining=0 or google events is empty
                for(int c = 0; (c < remaining) && !(calendarEvents.isEmpty()); c++) {
                    sortedEvents.add(calendarEventToJson(calendarEvents.get(0)));
                    remaining--;
                    calendarEvents.remove(0);
                }
                break;
            }

            //compare the 2 sorted lists
            googleEvent = googleEvents.get(0);
            googleDateTime = googleEvent.getStart().getDateTime();
            if(googleDateTime == null) {
                //all day event so uses just getDate
                googleDateTimeVal = Instant.ofEpochMilli(googleEvent.getStart().getDate().getValue()).getEpochSecond();
            } else {
                googleDateTimeVal = Instant.ofEpochMilli(googleDateTime.getValue()).getEpochSecond();
            }

            if(calendarEvents.get(0).getStart().toInstant().getEpochSecond() < googleDateTimeVal) {
                //calendar event comes first
                sortedEvents.add(calendarEventToJson(calendarEvents.get(0)));
                remaining--;
                calendarEvents.remove(0);
            } else {
                //google event comes first
                sortedEvents.add(googleEventToJson(googleEvent));
                remaining--;
                googleEvents.remove(0);
            }
        }
        //done sorting, return JSON String
        return new Gson().toJson(sortedEvents);
    }

    private JsonObject googleEventToJson(Event event) {
        JsonObject json = new JsonObject();
        json.addProperty("eventType", "Google");
        json.addProperty("name", event.getSummary());
        json.addProperty("description", event.getDescription());
        if(event.getStart().getDateTime() == null) {
            //all day event
            json.addProperty("start", event.getStart().getDate().toStringRfc3339());
            json.addProperty("end", event.getEnd().getDate().toStringRfc3339());
        } else {
            json.addProperty("start", event.getStart().getDateTime().toStringRfc3339());
            json.addProperty("end", event.getEnd().getDateTime().toStringRfc3339());
        }
        json.addProperty("location", event.getLocation());

        return json;
    }

    private JsonObject calendarEventToJson(CalendarEvent event) {
        JsonObject json = new JsonObject();
        json.addProperty("eventType", "Calendar");
        json.addProperty("name", event.getName());
        json.addProperty("description", event.getDescription());
        json.addProperty("start", googleAuthController.zonedDateTimeToGoogleDateTime(event.getStart()).getDateTime().toStringRfc3339());
        json.addProperty("end", googleAuthController.zonedDateTimeToGoogleDateTime(event.getEnd()).getDateTime().toStringRfc3339());
        json.addProperty("location", event.getLocation());

        return json;
    }
}
