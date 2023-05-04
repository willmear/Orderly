package uk.ac.bham.teamproject.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.ac.bham.teamproject.web.rest.TestUtil.sameInstant;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.bham.teamproject.IntegrationTest;
import uk.ac.bham.teamproject.domain.CalendarEvent;
import uk.ac.bham.teamproject.repository.CalendarEventRepository;

/**
 * Integration tests for the {@link CalendarEventResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CalendarEventResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_START = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_START = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_END = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_END = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_LOCATION = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/calendar-events";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CalendarEventRepository calendarEventRepository;

    @Mock
    private CalendarEventRepository calendarEventRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCalendarEventMockMvc;

    private CalendarEvent calendarEvent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CalendarEvent createEntity(EntityManager em) {
        CalendarEvent calendarEvent = new CalendarEvent()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .start(DEFAULT_START)
            .end(DEFAULT_END)
            .location(DEFAULT_LOCATION);
        return calendarEvent;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CalendarEvent createUpdatedEntity(EntityManager em) {
        CalendarEvent calendarEvent = new CalendarEvent()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .start(UPDATED_START)
            .end(UPDATED_END)
            .location(UPDATED_LOCATION);
        return calendarEvent;
    }

    @BeforeEach
    public void initTest() {
        calendarEvent = createEntity(em);
    }

    @Test
    @Transactional
    void createCalendarEvent() throws Exception {
        int databaseSizeBeforeCreate = calendarEventRepository.findAll().size();
        // Create the CalendarEvent
        restCalendarEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(calendarEvent)))
            .andExpect(status().isCreated());

        // Validate the CalendarEvent in the database
        List<CalendarEvent> calendarEventList = calendarEventRepository.findAll();
        assertThat(calendarEventList).hasSize(databaseSizeBeforeCreate + 1);
        CalendarEvent testCalendarEvent = calendarEventList.get(calendarEventList.size() - 1);
        assertThat(testCalendarEvent.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCalendarEvent.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCalendarEvent.getStart()).isEqualTo(DEFAULT_START);
        assertThat(testCalendarEvent.getEnd()).isEqualTo(DEFAULT_END);
        assertThat(testCalendarEvent.getLocation()).isEqualTo(DEFAULT_LOCATION);
    }

    @Test
    @Transactional
    void createCalendarEventWithExistingId() throws Exception {
        // Create the CalendarEvent with an existing ID
        calendarEvent.setId(1L);

        int databaseSizeBeforeCreate = calendarEventRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCalendarEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(calendarEvent)))
            .andExpect(status().isBadRequest());

        // Validate the CalendarEvent in the database
        List<CalendarEvent> calendarEventList = calendarEventRepository.findAll();
        assertThat(calendarEventList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = calendarEventRepository.findAll().size();
        // set the field null
        calendarEvent.setName(null);

        // Create the CalendarEvent, which fails.

        restCalendarEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(calendarEvent)))
            .andExpect(status().isBadRequest());

        List<CalendarEvent> calendarEventList = calendarEventRepository.findAll();
        assertThat(calendarEventList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartIsRequired() throws Exception {
        int databaseSizeBeforeTest = calendarEventRepository.findAll().size();
        // set the field null
        calendarEvent.setStart(null);

        // Create the CalendarEvent, which fails.

        restCalendarEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(calendarEvent)))
            .andExpect(status().isBadRequest());

        List<CalendarEvent> calendarEventList = calendarEventRepository.findAll();
        assertThat(calendarEventList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndIsRequired() throws Exception {
        int databaseSizeBeforeTest = calendarEventRepository.findAll().size();
        // set the field null
        calendarEvent.setEnd(null);

        // Create the CalendarEvent, which fails.

        restCalendarEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(calendarEvent)))
            .andExpect(status().isBadRequest());

        List<CalendarEvent> calendarEventList = calendarEventRepository.findAll();
        assertThat(calendarEventList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCalendarEvents() throws Exception {
        // Initialize the database
        calendarEventRepository.saveAndFlush(calendarEvent);

        // Get all the calendarEventList
        restCalendarEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(calendarEvent.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].start").value(hasItem(sameInstant(DEFAULT_START))))
            .andExpect(jsonPath("$.[*].end").value(hasItem(sameInstant(DEFAULT_END))))
            .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCalendarEventsWithEagerRelationshipsIsEnabled() throws Exception {
        when(calendarEventRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCalendarEventMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(calendarEventRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCalendarEventsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(calendarEventRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCalendarEventMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(calendarEventRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCalendarEvent() throws Exception {
        // Initialize the database
        calendarEventRepository.saveAndFlush(calendarEvent);

        // Get the calendarEvent
        restCalendarEventMockMvc
            .perform(get(ENTITY_API_URL_ID, calendarEvent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(calendarEvent.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.start").value(sameInstant(DEFAULT_START)))
            .andExpect(jsonPath("$.end").value(sameInstant(DEFAULT_END)))
            .andExpect(jsonPath("$.location").value(DEFAULT_LOCATION));
    }

    @Test
    @Transactional
    void getNonExistingCalendarEvent() throws Exception {
        // Get the calendarEvent
        restCalendarEventMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCalendarEvent() throws Exception {
        // Initialize the database
        calendarEventRepository.saveAndFlush(calendarEvent);

        int databaseSizeBeforeUpdate = calendarEventRepository.findAll().size();

        // Update the calendarEvent
        CalendarEvent updatedCalendarEvent = calendarEventRepository.findById(calendarEvent.getId()).get();
        // Disconnect from session so that the updates on updatedCalendarEvent are not directly saved in db
        em.detach(updatedCalendarEvent);
        updatedCalendarEvent
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .start(UPDATED_START)
            .end(UPDATED_END)
            .location(UPDATED_LOCATION);

        restCalendarEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCalendarEvent.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCalendarEvent))
            )
            .andExpect(status().isOk());

        // Validate the CalendarEvent in the database
        List<CalendarEvent> calendarEventList = calendarEventRepository.findAll();
        assertThat(calendarEventList).hasSize(databaseSizeBeforeUpdate);
        CalendarEvent testCalendarEvent = calendarEventList.get(calendarEventList.size() - 1);
        assertThat(testCalendarEvent.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCalendarEvent.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCalendarEvent.getStart()).isEqualTo(UPDATED_START);
        assertThat(testCalendarEvent.getEnd()).isEqualTo(UPDATED_END);
        assertThat(testCalendarEvent.getLocation()).isEqualTo(UPDATED_LOCATION);
    }

    @Test
    @Transactional
    void putNonExistingCalendarEvent() throws Exception {
        int databaseSizeBeforeUpdate = calendarEventRepository.findAll().size();
        calendarEvent.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCalendarEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, calendarEvent.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(calendarEvent))
            )
            .andExpect(status().isBadRequest());

        // Validate the CalendarEvent in the database
        List<CalendarEvent> calendarEventList = calendarEventRepository.findAll();
        assertThat(calendarEventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCalendarEvent() throws Exception {
        int databaseSizeBeforeUpdate = calendarEventRepository.findAll().size();
        calendarEvent.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCalendarEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(calendarEvent))
            )
            .andExpect(status().isBadRequest());

        // Validate the CalendarEvent in the database
        List<CalendarEvent> calendarEventList = calendarEventRepository.findAll();
        assertThat(calendarEventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCalendarEvent() throws Exception {
        int databaseSizeBeforeUpdate = calendarEventRepository.findAll().size();
        calendarEvent.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCalendarEventMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(calendarEvent)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CalendarEvent in the database
        List<CalendarEvent> calendarEventList = calendarEventRepository.findAll();
        assertThat(calendarEventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCalendarEventWithPatch() throws Exception {
        // Initialize the database
        calendarEventRepository.saveAndFlush(calendarEvent);

        int databaseSizeBeforeUpdate = calendarEventRepository.findAll().size();

        // Update the calendarEvent using partial update
        CalendarEvent partialUpdatedCalendarEvent = new CalendarEvent();
        partialUpdatedCalendarEvent.setId(calendarEvent.getId());

        partialUpdatedCalendarEvent.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restCalendarEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCalendarEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCalendarEvent))
            )
            .andExpect(status().isOk());

        // Validate the CalendarEvent in the database
        List<CalendarEvent> calendarEventList = calendarEventRepository.findAll();
        assertThat(calendarEventList).hasSize(databaseSizeBeforeUpdate);
        CalendarEvent testCalendarEvent = calendarEventList.get(calendarEventList.size() - 1);
        assertThat(testCalendarEvent.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCalendarEvent.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCalendarEvent.getStart()).isEqualTo(DEFAULT_START);
        assertThat(testCalendarEvent.getEnd()).isEqualTo(DEFAULT_END);
        assertThat(testCalendarEvent.getLocation()).isEqualTo(DEFAULT_LOCATION);
    }

    @Test
    @Transactional
    void fullUpdateCalendarEventWithPatch() throws Exception {
        // Initialize the database
        calendarEventRepository.saveAndFlush(calendarEvent);

        int databaseSizeBeforeUpdate = calendarEventRepository.findAll().size();

        // Update the calendarEvent using partial update
        CalendarEvent partialUpdatedCalendarEvent = new CalendarEvent();
        partialUpdatedCalendarEvent.setId(calendarEvent.getId());

        partialUpdatedCalendarEvent
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .start(UPDATED_START)
            .end(UPDATED_END)
            .location(UPDATED_LOCATION);

        restCalendarEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCalendarEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCalendarEvent))
            )
            .andExpect(status().isOk());

        // Validate the CalendarEvent in the database
        List<CalendarEvent> calendarEventList = calendarEventRepository.findAll();
        assertThat(calendarEventList).hasSize(databaseSizeBeforeUpdate);
        CalendarEvent testCalendarEvent = calendarEventList.get(calendarEventList.size() - 1);
        assertThat(testCalendarEvent.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCalendarEvent.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCalendarEvent.getStart()).isEqualTo(UPDATED_START);
        assertThat(testCalendarEvent.getEnd()).isEqualTo(UPDATED_END);
        assertThat(testCalendarEvent.getLocation()).isEqualTo(UPDATED_LOCATION);
    }

    @Test
    @Transactional
    void patchNonExistingCalendarEvent() throws Exception {
        int databaseSizeBeforeUpdate = calendarEventRepository.findAll().size();
        calendarEvent.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCalendarEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, calendarEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(calendarEvent))
            )
            .andExpect(status().isBadRequest());

        // Validate the CalendarEvent in the database
        List<CalendarEvent> calendarEventList = calendarEventRepository.findAll();
        assertThat(calendarEventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCalendarEvent() throws Exception {
        int databaseSizeBeforeUpdate = calendarEventRepository.findAll().size();
        calendarEvent.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCalendarEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(calendarEvent))
            )
            .andExpect(status().isBadRequest());

        // Validate the CalendarEvent in the database
        List<CalendarEvent> calendarEventList = calendarEventRepository.findAll();
        assertThat(calendarEventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCalendarEvent() throws Exception {
        int databaseSizeBeforeUpdate = calendarEventRepository.findAll().size();
        calendarEvent.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCalendarEventMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(calendarEvent))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CalendarEvent in the database
        List<CalendarEvent> calendarEventList = calendarEventRepository.findAll();
        assertThat(calendarEventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCalendarEvent() throws Exception {
        // Initialize the database
        calendarEventRepository.saveAndFlush(calendarEvent);

        int databaseSizeBeforeDelete = calendarEventRepository.findAll().size();

        // Delete the calendarEvent
        restCalendarEventMockMvc
            .perform(delete(ENTITY_API_URL_ID, calendarEvent.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CalendarEvent> calendarEventList = calendarEventRepository.findAll();
        assertThat(calendarEventList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
