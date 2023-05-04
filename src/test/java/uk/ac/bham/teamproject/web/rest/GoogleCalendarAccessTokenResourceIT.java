package uk.ac.bham.teamproject.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import uk.ac.bham.teamproject.domain.GoogleCalendarAccessToken;
import uk.ac.bham.teamproject.repository.GoogleCalendarAccessTokenRepository;

/**
 * Integration tests for the {@link GoogleCalendarAccessTokenResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class GoogleCalendarAccessTokenResourceIT {

    private static final String DEFAULT_ENCRYPTED_TOKEN = "AAAAAAAAAA";
    private static final String UPDATED_ENCRYPTED_TOKEN = "BBBBBBBBBB";

    private static final Instant DEFAULT_EXPIRES = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXPIRES = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/google-calendar-access-tokens";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private GoogleCalendarAccessTokenRepository googleCalendarAccessTokenRepository;

    @Mock
    private GoogleCalendarAccessTokenRepository googleCalendarAccessTokenRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGoogleCalendarAccessTokenMockMvc;

    private GoogleCalendarAccessToken googleCalendarAccessToken;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GoogleCalendarAccessToken createEntity(EntityManager em) {
        GoogleCalendarAccessToken googleCalendarAccessToken = new GoogleCalendarAccessToken()
            .encryptedToken(DEFAULT_ENCRYPTED_TOKEN)
            .expires(DEFAULT_EXPIRES);
        return googleCalendarAccessToken;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GoogleCalendarAccessToken createUpdatedEntity(EntityManager em) {
        GoogleCalendarAccessToken googleCalendarAccessToken = new GoogleCalendarAccessToken()
            .encryptedToken(UPDATED_ENCRYPTED_TOKEN)
            .expires(UPDATED_EXPIRES);
        return googleCalendarAccessToken;
    }

    @BeforeEach
    public void initTest() {
        googleCalendarAccessToken = createEntity(em);
    }

    @Test
    @Transactional
    void createGoogleCalendarAccessToken() throws Exception {
        int databaseSizeBeforeCreate = googleCalendarAccessTokenRepository.findAll().size();
        // Create the GoogleCalendarAccessToken
        restGoogleCalendarAccessTokenMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(googleCalendarAccessToken))
            )
            .andExpect(status().isCreated());

        // Validate the GoogleCalendarAccessToken in the database
        List<GoogleCalendarAccessToken> googleCalendarAccessTokenList = googleCalendarAccessTokenRepository.findAll();
        assertThat(googleCalendarAccessTokenList).hasSize(databaseSizeBeforeCreate + 1);
        GoogleCalendarAccessToken testGoogleCalendarAccessToken = googleCalendarAccessTokenList.get(
            googleCalendarAccessTokenList.size() - 1
        );
        assertThat(testGoogleCalendarAccessToken.getEncryptedToken()).isEqualTo(DEFAULT_ENCRYPTED_TOKEN);
        assertThat(testGoogleCalendarAccessToken.getExpires()).isEqualTo(DEFAULT_EXPIRES);
    }

    @Test
    @Transactional
    void createGoogleCalendarAccessTokenWithExistingId() throws Exception {
        // Create the GoogleCalendarAccessToken with an existing ID
        googleCalendarAccessToken.setId(1L);

        int databaseSizeBeforeCreate = googleCalendarAccessTokenRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGoogleCalendarAccessTokenMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(googleCalendarAccessToken))
            )
            .andExpect(status().isBadRequest());

        // Validate the GoogleCalendarAccessToken in the database
        List<GoogleCalendarAccessToken> googleCalendarAccessTokenList = googleCalendarAccessTokenRepository.findAll();
        assertThat(googleCalendarAccessTokenList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkEncryptedTokenIsRequired() throws Exception {
        int databaseSizeBeforeTest = googleCalendarAccessTokenRepository.findAll().size();
        // set the field null
        googleCalendarAccessToken.setEncryptedToken(null);

        // Create the GoogleCalendarAccessToken, which fails.

        restGoogleCalendarAccessTokenMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(googleCalendarAccessToken))
            )
            .andExpect(status().isBadRequest());

        List<GoogleCalendarAccessToken> googleCalendarAccessTokenList = googleCalendarAccessTokenRepository.findAll();
        assertThat(googleCalendarAccessTokenList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkExpiresIsRequired() throws Exception {
        int databaseSizeBeforeTest = googleCalendarAccessTokenRepository.findAll().size();
        // set the field null
        googleCalendarAccessToken.setExpires(null);

        // Create the GoogleCalendarAccessToken, which fails.

        restGoogleCalendarAccessTokenMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(googleCalendarAccessToken))
            )
            .andExpect(status().isBadRequest());

        List<GoogleCalendarAccessToken> googleCalendarAccessTokenList = googleCalendarAccessTokenRepository.findAll();
        assertThat(googleCalendarAccessTokenList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllGoogleCalendarAccessTokens() throws Exception {
        // Initialize the database
        googleCalendarAccessTokenRepository.saveAndFlush(googleCalendarAccessToken);

        // Get all the googleCalendarAccessTokenList
        restGoogleCalendarAccessTokenMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(googleCalendarAccessToken.getId().intValue())))
            .andExpect(jsonPath("$.[*].encryptedToken").value(hasItem(DEFAULT_ENCRYPTED_TOKEN)))
            .andExpect(jsonPath("$.[*].expires").value(hasItem(DEFAULT_EXPIRES.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllGoogleCalendarAccessTokensWithEagerRelationshipsIsEnabled() throws Exception {
        when(googleCalendarAccessTokenRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restGoogleCalendarAccessTokenMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(googleCalendarAccessTokenRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllGoogleCalendarAccessTokensWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(googleCalendarAccessTokenRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restGoogleCalendarAccessTokenMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(googleCalendarAccessTokenRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getGoogleCalendarAccessToken() throws Exception {
        // Initialize the database
        googleCalendarAccessTokenRepository.saveAndFlush(googleCalendarAccessToken);

        // Get the googleCalendarAccessToken
        restGoogleCalendarAccessTokenMockMvc
            .perform(get(ENTITY_API_URL_ID, googleCalendarAccessToken.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(googleCalendarAccessToken.getId().intValue()))
            .andExpect(jsonPath("$.encryptedToken").value(DEFAULT_ENCRYPTED_TOKEN))
            .andExpect(jsonPath("$.expires").value(DEFAULT_EXPIRES.toString()));
    }

    @Test
    @Transactional
    void getNonExistingGoogleCalendarAccessToken() throws Exception {
        // Get the googleCalendarAccessToken
        restGoogleCalendarAccessTokenMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingGoogleCalendarAccessToken() throws Exception {
        // Initialize the database
        googleCalendarAccessTokenRepository.saveAndFlush(googleCalendarAccessToken);

        int databaseSizeBeforeUpdate = googleCalendarAccessTokenRepository.findAll().size();

        // Update the googleCalendarAccessToken
        GoogleCalendarAccessToken updatedGoogleCalendarAccessToken = googleCalendarAccessTokenRepository
            .findById(googleCalendarAccessToken.getId())
            .get();
        // Disconnect from session so that the updates on updatedGoogleCalendarAccessToken are not directly saved in db
        em.detach(updatedGoogleCalendarAccessToken);
        updatedGoogleCalendarAccessToken.encryptedToken(UPDATED_ENCRYPTED_TOKEN).expires(UPDATED_EXPIRES);

        restGoogleCalendarAccessTokenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedGoogleCalendarAccessToken.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedGoogleCalendarAccessToken))
            )
            .andExpect(status().isOk());

        // Validate the GoogleCalendarAccessToken in the database
        List<GoogleCalendarAccessToken> googleCalendarAccessTokenList = googleCalendarAccessTokenRepository.findAll();
        assertThat(googleCalendarAccessTokenList).hasSize(databaseSizeBeforeUpdate);
        GoogleCalendarAccessToken testGoogleCalendarAccessToken = googleCalendarAccessTokenList.get(
            googleCalendarAccessTokenList.size() - 1
        );
        assertThat(testGoogleCalendarAccessToken.getEncryptedToken()).isEqualTo(UPDATED_ENCRYPTED_TOKEN);
        assertThat(testGoogleCalendarAccessToken.getExpires()).isEqualTo(UPDATED_EXPIRES);
    }

    @Test
    @Transactional
    void putNonExistingGoogleCalendarAccessToken() throws Exception {
        int databaseSizeBeforeUpdate = googleCalendarAccessTokenRepository.findAll().size();
        googleCalendarAccessToken.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGoogleCalendarAccessTokenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, googleCalendarAccessToken.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(googleCalendarAccessToken))
            )
            .andExpect(status().isBadRequest());

        // Validate the GoogleCalendarAccessToken in the database
        List<GoogleCalendarAccessToken> googleCalendarAccessTokenList = googleCalendarAccessTokenRepository.findAll();
        assertThat(googleCalendarAccessTokenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGoogleCalendarAccessToken() throws Exception {
        int databaseSizeBeforeUpdate = googleCalendarAccessTokenRepository.findAll().size();
        googleCalendarAccessToken.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGoogleCalendarAccessTokenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(googleCalendarAccessToken))
            )
            .andExpect(status().isBadRequest());

        // Validate the GoogleCalendarAccessToken in the database
        List<GoogleCalendarAccessToken> googleCalendarAccessTokenList = googleCalendarAccessTokenRepository.findAll();
        assertThat(googleCalendarAccessTokenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGoogleCalendarAccessToken() throws Exception {
        int databaseSizeBeforeUpdate = googleCalendarAccessTokenRepository.findAll().size();
        googleCalendarAccessToken.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGoogleCalendarAccessTokenMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(googleCalendarAccessToken))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the GoogleCalendarAccessToken in the database
        List<GoogleCalendarAccessToken> googleCalendarAccessTokenList = googleCalendarAccessTokenRepository.findAll();
        assertThat(googleCalendarAccessTokenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGoogleCalendarAccessTokenWithPatch() throws Exception {
        // Initialize the database
        googleCalendarAccessTokenRepository.saveAndFlush(googleCalendarAccessToken);

        int databaseSizeBeforeUpdate = googleCalendarAccessTokenRepository.findAll().size();

        // Update the googleCalendarAccessToken using partial update
        GoogleCalendarAccessToken partialUpdatedGoogleCalendarAccessToken = new GoogleCalendarAccessToken();
        partialUpdatedGoogleCalendarAccessToken.setId(googleCalendarAccessToken.getId());

        partialUpdatedGoogleCalendarAccessToken.expires(UPDATED_EXPIRES);

        restGoogleCalendarAccessTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGoogleCalendarAccessToken.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGoogleCalendarAccessToken))
            )
            .andExpect(status().isOk());

        // Validate the GoogleCalendarAccessToken in the database
        List<GoogleCalendarAccessToken> googleCalendarAccessTokenList = googleCalendarAccessTokenRepository.findAll();
        assertThat(googleCalendarAccessTokenList).hasSize(databaseSizeBeforeUpdate);
        GoogleCalendarAccessToken testGoogleCalendarAccessToken = googleCalendarAccessTokenList.get(
            googleCalendarAccessTokenList.size() - 1
        );
        assertThat(testGoogleCalendarAccessToken.getEncryptedToken()).isEqualTo(DEFAULT_ENCRYPTED_TOKEN);
        assertThat(testGoogleCalendarAccessToken.getExpires()).isEqualTo(UPDATED_EXPIRES);
    }

    @Test
    @Transactional
    void fullUpdateGoogleCalendarAccessTokenWithPatch() throws Exception {
        // Initialize the database
        googleCalendarAccessTokenRepository.saveAndFlush(googleCalendarAccessToken);

        int databaseSizeBeforeUpdate = googleCalendarAccessTokenRepository.findAll().size();

        // Update the googleCalendarAccessToken using partial update
        GoogleCalendarAccessToken partialUpdatedGoogleCalendarAccessToken = new GoogleCalendarAccessToken();
        partialUpdatedGoogleCalendarAccessToken.setId(googleCalendarAccessToken.getId());

        partialUpdatedGoogleCalendarAccessToken.encryptedToken(UPDATED_ENCRYPTED_TOKEN).expires(UPDATED_EXPIRES);

        restGoogleCalendarAccessTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGoogleCalendarAccessToken.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGoogleCalendarAccessToken))
            )
            .andExpect(status().isOk());

        // Validate the GoogleCalendarAccessToken in the database
        List<GoogleCalendarAccessToken> googleCalendarAccessTokenList = googleCalendarAccessTokenRepository.findAll();
        assertThat(googleCalendarAccessTokenList).hasSize(databaseSizeBeforeUpdate);
        GoogleCalendarAccessToken testGoogleCalendarAccessToken = googleCalendarAccessTokenList.get(
            googleCalendarAccessTokenList.size() - 1
        );
        assertThat(testGoogleCalendarAccessToken.getEncryptedToken()).isEqualTo(UPDATED_ENCRYPTED_TOKEN);
        assertThat(testGoogleCalendarAccessToken.getExpires()).isEqualTo(UPDATED_EXPIRES);
    }

    @Test
    @Transactional
    void patchNonExistingGoogleCalendarAccessToken() throws Exception {
        int databaseSizeBeforeUpdate = googleCalendarAccessTokenRepository.findAll().size();
        googleCalendarAccessToken.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGoogleCalendarAccessTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, googleCalendarAccessToken.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(googleCalendarAccessToken))
            )
            .andExpect(status().isBadRequest());

        // Validate the GoogleCalendarAccessToken in the database
        List<GoogleCalendarAccessToken> googleCalendarAccessTokenList = googleCalendarAccessTokenRepository.findAll();
        assertThat(googleCalendarAccessTokenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGoogleCalendarAccessToken() throws Exception {
        int databaseSizeBeforeUpdate = googleCalendarAccessTokenRepository.findAll().size();
        googleCalendarAccessToken.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGoogleCalendarAccessTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(googleCalendarAccessToken))
            )
            .andExpect(status().isBadRequest());

        // Validate the GoogleCalendarAccessToken in the database
        List<GoogleCalendarAccessToken> googleCalendarAccessTokenList = googleCalendarAccessTokenRepository.findAll();
        assertThat(googleCalendarAccessTokenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGoogleCalendarAccessToken() throws Exception {
        int databaseSizeBeforeUpdate = googleCalendarAccessTokenRepository.findAll().size();
        googleCalendarAccessToken.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGoogleCalendarAccessTokenMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(googleCalendarAccessToken))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the GoogleCalendarAccessToken in the database
        List<GoogleCalendarAccessToken> googleCalendarAccessTokenList = googleCalendarAccessTokenRepository.findAll();
        assertThat(googleCalendarAccessTokenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGoogleCalendarAccessToken() throws Exception {
        // Initialize the database
        googleCalendarAccessTokenRepository.saveAndFlush(googleCalendarAccessToken);

        int databaseSizeBeforeDelete = googleCalendarAccessTokenRepository.findAll().size();

        // Delete the googleCalendarAccessToken
        restGoogleCalendarAccessTokenMockMvc
            .perform(delete(ENTITY_API_URL_ID, googleCalendarAccessToken.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<GoogleCalendarAccessToken> googleCalendarAccessTokenList = googleCalendarAccessTokenRepository.findAll();
        assertThat(googleCalendarAccessTokenList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
