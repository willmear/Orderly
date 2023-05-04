package uk.ac.bham.teamproject.web.rest;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GoogleCalendarAsyncController {

    private final Logger log = LoggerFactory.getLogger(GoogleCalendarAsyncController.class);
    @Async
    public void pushEvents(Calendar calendarService, String calendarId, List<Event> customEvents, List<Event> orders, List<Event> toDelete) {
        try {
            //delete events to be deleted first
            for(Event ev : toDelete) {
                calendarService.events().delete(calendarId, ev.getId()).execute();
            }

            //Add custom events from calendarEvent database
            for (Event ev : customEvents) {
                calendarService.events().insert(calendarId, ev).execute();
            }

            //Add orders from the orders database
            for (Event ord : orders) {
                calendarService.events().insert(calendarId, ord).execute();
            }
        } catch(Exception e) {
            log.error("Error occurred pushing events to Google Calendar: " + e);
        }
    }
}
