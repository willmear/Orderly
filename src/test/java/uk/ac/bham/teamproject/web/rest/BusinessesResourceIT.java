package uk.ac.bham.teamproject.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import uk.ac.bham.teamproject.IntegrationTest;
import uk.ac.bham.teamproject.domain.Businesses;
import uk.ac.bham.teamproject.repository.BusinessesRepository;

/**
 * Integration tests for the {@link BusinessesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BusinessesResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SUMMARY = "AAAAAAAAAA";
    private static final String UPDATED_SUMMARY = "BBBBBBBBBB";

    private static final Integer DEFAULT_RATING = 1;
    private static final Integer UPDATED_RATING = 2;

    private static final String DEFAULT_LOCATION = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/businesses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BusinessesRepository businessesRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBusinessesMockMvc;

    private Businesses businesses;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Businesses createEntity(EntityManager em) {
        Businesses businesses = new Businesses()
            .name(DEFAULT_NAME)
            .summary(DEFAULT_SUMMARY)
            .rating(DEFAULT_RATING)
            .location(DEFAULT_LOCATION)
            .type(DEFAULT_TYPE)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE);
        return businesses;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Businesses createUpdatedEntity(EntityManager em) {
        Businesses businesses = new Businesses()
            .name(UPDATED_NAME)
            .summary(UPDATED_SUMMARY)
            .rating(UPDATED_RATING)
            .location(UPDATED_LOCATION)
            .type(UPDATED_TYPE)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);
        return businesses;
    }

    @BeforeEach
    public void initTest() {
        businesses = createEntity(em);
    }

    @Test
    @Transactional
    void createBusinesses() throws Exception {
        int databaseSizeBeforeCreate = businessesRepository.findAll().size();
        // Create the Businesses
        restBusinessesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(businesses)))
            .andExpect(status().isCreated());

        // Validate the Businesses in the database
        List<Businesses> businessesList = businessesRepository.findAll();
        assertThat(businessesList).hasSize(databaseSizeBeforeCreate + 1);
        Businesses testBusinesses = businessesList.get(businessesList.size() - 1);
        assertThat(testBusinesses.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBusinesses.getSummary()).isEqualTo(DEFAULT_SUMMARY);
        assertThat(testBusinesses.getRating()).isEqualTo(DEFAULT_RATING);
        assertThat(testBusinesses.getLocation()).isEqualTo(DEFAULT_LOCATION);
        assertThat(testBusinesses.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testBusinesses.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testBusinesses.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void createBusinessesWithExistingId() throws Exception {
        // Create the Businesses with an existing ID
        businesses.setId(1L);

        int databaseSizeBeforeCreate = businessesRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBusinessesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(businesses)))
            .andExpect(status().isBadRequest());

        // Validate the Businesses in the database
        List<Businesses> businessesList = businessesRepository.findAll();
        assertThat(businessesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = businessesRepository.findAll().size();
        // set the field null
        businesses.setName(null);

        // Create the Businesses, which fails.

        restBusinessesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(businesses)))
            .andExpect(status().isBadRequest());

        List<Businesses> businessesList = businessesRepository.findAll();
        assertThat(businessesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = businessesRepository.findAll().size();
        // set the field null
        businesses.setType(null);

        // Create the Businesses, which fails.

        restBusinessesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(businesses)))
            .andExpect(status().isBadRequest());

        List<Businesses> businessesList = businessesRepository.findAll();
        assertThat(businessesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBusinesses() throws Exception {
        // Initialize the database
        businessesRepository.saveAndFlush(businesses);

        // Get all the businessesList
        restBusinessesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(businesses.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].summary").value(hasItem(DEFAULT_SUMMARY.toString())))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING)))
            .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
    }

    @Test
    @Transactional
    void getBusinesses() throws Exception {
        // Initialize the database
        businessesRepository.saveAndFlush(businesses);

        // Get the businesses
        restBusinessesMockMvc
            .perform(get(ENTITY_API_URL_ID, businesses.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(businesses.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.summary").value(DEFAULT_SUMMARY.toString()))
            .andExpect(jsonPath("$.rating").value(DEFAULT_RATING))
            .andExpect(jsonPath("$.location").value(DEFAULT_LOCATION))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)));
    }

    @Test
    @Transactional
    void getNonExistingBusinesses() throws Exception {
        // Get the businesses
        restBusinessesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBusinesses() throws Exception {
        // Initialize the database
        businessesRepository.saveAndFlush(businesses);

        int databaseSizeBeforeUpdate = businessesRepository.findAll().size();

        // Update the businesses
        Businesses updatedBusinesses = businessesRepository.findById(businesses.getId()).get();
        // Disconnect from session so that the updates on updatedBusinesses are not directly saved in db
        em.detach(updatedBusinesses);
        updatedBusinesses
            .name(UPDATED_NAME)
            .summary(UPDATED_SUMMARY)
            .rating(UPDATED_RATING)
            .location(UPDATED_LOCATION)
            .type(UPDATED_TYPE)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        restBusinessesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBusinesses.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBusinesses))
            )
            .andExpect(status().isOk());

        // Validate the Businesses in the database
        List<Businesses> businessesList = businessesRepository.findAll();
        assertThat(businessesList).hasSize(databaseSizeBeforeUpdate);
        Businesses testBusinesses = businessesList.get(businessesList.size() - 1);
        assertThat(testBusinesses.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBusinesses.getSummary()).isEqualTo(UPDATED_SUMMARY);
        assertThat(testBusinesses.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testBusinesses.getLocation()).isEqualTo(UPDATED_LOCATION);
        assertThat(testBusinesses.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testBusinesses.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testBusinesses.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingBusinesses() throws Exception {
        int databaseSizeBeforeUpdate = businessesRepository.findAll().size();
        businesses.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBusinessesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, businesses.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(businesses))
            )
            .andExpect(status().isBadRequest());

        // Validate the Businesses in the database
        List<Businesses> businessesList = businessesRepository.findAll();
        assertThat(businessesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBusinesses() throws Exception {
        int databaseSizeBeforeUpdate = businessesRepository.findAll().size();
        businesses.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBusinessesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(businesses))
            )
            .andExpect(status().isBadRequest());

        // Validate the Businesses in the database
        List<Businesses> businessesList = businessesRepository.findAll();
        assertThat(businessesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBusinesses() throws Exception {
        int databaseSizeBeforeUpdate = businessesRepository.findAll().size();
        businesses.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBusinessesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(businesses)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Businesses in the database
        List<Businesses> businessesList = businessesRepository.findAll();
        assertThat(businessesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBusinessesWithPatch() throws Exception {
        // Initialize the database
        businessesRepository.saveAndFlush(businesses);

        int databaseSizeBeforeUpdate = businessesRepository.findAll().size();

        // Update the businesses using partial update
        Businesses partialUpdatedBusinesses = new Businesses();
        partialUpdatedBusinesses.setId(businesses.getId());

        partialUpdatedBusinesses.name(UPDATED_NAME).summary(UPDATED_SUMMARY).location(UPDATED_LOCATION).type(UPDATED_TYPE);

        restBusinessesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBusinesses.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBusinesses))
            )
            .andExpect(status().isOk());

        // Validate the Businesses in the database
        List<Businesses> businessesList = businessesRepository.findAll();
        assertThat(businessesList).hasSize(databaseSizeBeforeUpdate);
        Businesses testBusinesses = businessesList.get(businessesList.size() - 1);
        assertThat(testBusinesses.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBusinesses.getSummary()).isEqualTo(UPDATED_SUMMARY);
        assertThat(testBusinesses.getRating()).isEqualTo(DEFAULT_RATING);
        assertThat(testBusinesses.getLocation()).isEqualTo(UPDATED_LOCATION);
        assertThat(testBusinesses.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testBusinesses.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testBusinesses.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateBusinessesWithPatch() throws Exception {
        // Initialize the database
        businessesRepository.saveAndFlush(businesses);

        int databaseSizeBeforeUpdate = businessesRepository.findAll().size();

        // Update the businesses using partial update
        Businesses partialUpdatedBusinesses = new Businesses();
        partialUpdatedBusinesses.setId(businesses.getId());

        partialUpdatedBusinesses
            .name(UPDATED_NAME)
            .summary(UPDATED_SUMMARY)
            .rating(UPDATED_RATING)
            .location(UPDATED_LOCATION)
            .type(UPDATED_TYPE)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        restBusinessesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBusinesses.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBusinesses))
            )
            .andExpect(status().isOk());

        // Validate the Businesses in the database
        List<Businesses> businessesList = businessesRepository.findAll();
        assertThat(businessesList).hasSize(databaseSizeBeforeUpdate);
        Businesses testBusinesses = businessesList.get(businessesList.size() - 1);
        assertThat(testBusinesses.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBusinesses.getSummary()).isEqualTo(UPDATED_SUMMARY);
        assertThat(testBusinesses.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testBusinesses.getLocation()).isEqualTo(UPDATED_LOCATION);
        assertThat(testBusinesses.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testBusinesses.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testBusinesses.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingBusinesses() throws Exception {
        int databaseSizeBeforeUpdate = businessesRepository.findAll().size();
        businesses.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBusinessesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, businesses.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(businesses))
            )
            .andExpect(status().isBadRequest());

        // Validate the Businesses in the database
        List<Businesses> businessesList = businessesRepository.findAll();
        assertThat(businessesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBusinesses() throws Exception {
        int databaseSizeBeforeUpdate = businessesRepository.findAll().size();
        businesses.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBusinessesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(businesses))
            )
            .andExpect(status().isBadRequest());

        // Validate the Businesses in the database
        List<Businesses> businessesList = businessesRepository.findAll();
        assertThat(businessesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBusinesses() throws Exception {
        int databaseSizeBeforeUpdate = businessesRepository.findAll().size();
        businesses.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBusinessesMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(businesses))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Businesses in the database
        List<Businesses> businessesList = businessesRepository.findAll();
        assertThat(businessesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBusinesses() throws Exception {
        // Initialize the database
        businessesRepository.saveAndFlush(businesses);

        int databaseSizeBeforeDelete = businessesRepository.findAll().size();

        // Delete the businesses
        restBusinessesMockMvc
            .perform(delete(ENTITY_API_URL_ID, businesses.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Businesses> businessesList = businessesRepository.findAll();
        assertThat(businessesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
