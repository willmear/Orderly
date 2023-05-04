package uk.ac.bham.teamproject.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import uk.ac.bham.teamproject.domain.Targets;
import uk.ac.bham.teamproject.repository.TargetsRepository;

/**
 * Integration tests for the {@link TargetsResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TargetsResourceIT {

    private static final String DEFAULT_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_TEXT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/targets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TargetsRepository targetsRepository;

    @Mock
    private TargetsRepository targetsRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTargetsMockMvc;

    private Targets targets;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Targets createEntity(EntityManager em) {
        Targets targets = new Targets().text(DEFAULT_TEXT);
        return targets;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Targets createUpdatedEntity(EntityManager em) {
        Targets targets = new Targets().text(UPDATED_TEXT);
        return targets;
    }

    @BeforeEach
    public void initTest() {
        targets = createEntity(em);
    }

    @Test
    @Transactional
    void createTargets() throws Exception {
        int databaseSizeBeforeCreate = targetsRepository.findAll().size();
        // Create the Targets
        restTargetsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(targets)))
            .andExpect(status().isCreated());

        // Validate the Targets in the database
        List<Targets> targetsList = targetsRepository.findAll();
        assertThat(targetsList).hasSize(databaseSizeBeforeCreate + 1);
        Targets testTargets = targetsList.get(targetsList.size() - 1);
        assertThat(testTargets.getText()).isEqualTo(DEFAULT_TEXT);
    }

    @Test
    @Transactional
    void createTargetsWithExistingId() throws Exception {
        // Create the Targets with an existing ID
        targets.setId(1L);

        int databaseSizeBeforeCreate = targetsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTargetsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(targets)))
            .andExpect(status().isBadRequest());

        // Validate the Targets in the database
        List<Targets> targetsList = targetsRepository.findAll();
        assertThat(targetsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTargets() throws Exception {
        // Initialize the database
        targetsRepository.saveAndFlush(targets);

        // Get all the targetsList
        restTargetsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(targets.getId().intValue())))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTargetsWithEagerRelationshipsIsEnabled() throws Exception {
        when(targetsRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTargetsMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(targetsRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTargetsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(targetsRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTargetsMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(targetsRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTargets() throws Exception {
        // Initialize the database
        targetsRepository.saveAndFlush(targets);

        // Get the targets
        restTargetsMockMvc
            .perform(get(ENTITY_API_URL_ID, targets.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(targets.getId().intValue()))
            .andExpect(jsonPath("$.text").value(DEFAULT_TEXT));
    }

    @Test
    @Transactional
    void getNonExistingTargets() throws Exception {
        // Get the targets
        restTargetsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTargets() throws Exception {
        // Initialize the database
        targetsRepository.saveAndFlush(targets);

        int databaseSizeBeforeUpdate = targetsRepository.findAll().size();

        // Update the targets
        Targets updatedTargets = targetsRepository.findById(targets.getId()).get();
        // Disconnect from session so that the updates on updatedTargets are not directly saved in db
        em.detach(updatedTargets);
        updatedTargets.text(UPDATED_TEXT);

        restTargetsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTargets.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTargets))
            )
            .andExpect(status().isOk());

        // Validate the Targets in the database
        List<Targets> targetsList = targetsRepository.findAll();
        assertThat(targetsList).hasSize(databaseSizeBeforeUpdate);
        Targets testTargets = targetsList.get(targetsList.size() - 1);
        assertThat(testTargets.getText()).isEqualTo(UPDATED_TEXT);
    }

    @Test
    @Transactional
    void putNonExistingTargets() throws Exception {
        int databaseSizeBeforeUpdate = targetsRepository.findAll().size();
        targets.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTargetsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, targets.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(targets))
            )
            .andExpect(status().isBadRequest());

        // Validate the Targets in the database
        List<Targets> targetsList = targetsRepository.findAll();
        assertThat(targetsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTargets() throws Exception {
        int databaseSizeBeforeUpdate = targetsRepository.findAll().size();
        targets.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTargetsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(targets))
            )
            .andExpect(status().isBadRequest());

        // Validate the Targets in the database
        List<Targets> targetsList = targetsRepository.findAll();
        assertThat(targetsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTargets() throws Exception {
        int databaseSizeBeforeUpdate = targetsRepository.findAll().size();
        targets.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTargetsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(targets)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Targets in the database
        List<Targets> targetsList = targetsRepository.findAll();
        assertThat(targetsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTargetsWithPatch() throws Exception {
        // Initialize the database
        targetsRepository.saveAndFlush(targets);

        int databaseSizeBeforeUpdate = targetsRepository.findAll().size();

        // Update the targets using partial update
        Targets partialUpdatedTargets = new Targets();
        partialUpdatedTargets.setId(targets.getId());

        restTargetsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTargets.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTargets))
            )
            .andExpect(status().isOk());

        // Validate the Targets in the database
        List<Targets> targetsList = targetsRepository.findAll();
        assertThat(targetsList).hasSize(databaseSizeBeforeUpdate);
        Targets testTargets = targetsList.get(targetsList.size() - 1);
        assertThat(testTargets.getText()).isEqualTo(DEFAULT_TEXT);
    }

    @Test
    @Transactional
    void fullUpdateTargetsWithPatch() throws Exception {
        // Initialize the database
        targetsRepository.saveAndFlush(targets);

        int databaseSizeBeforeUpdate = targetsRepository.findAll().size();

        // Update the targets using partial update
        Targets partialUpdatedTargets = new Targets();
        partialUpdatedTargets.setId(targets.getId());

        partialUpdatedTargets.text(UPDATED_TEXT);

        restTargetsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTargets.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTargets))
            )
            .andExpect(status().isOk());

        // Validate the Targets in the database
        List<Targets> targetsList = targetsRepository.findAll();
        assertThat(targetsList).hasSize(databaseSizeBeforeUpdate);
        Targets testTargets = targetsList.get(targetsList.size() - 1);
        assertThat(testTargets.getText()).isEqualTo(UPDATED_TEXT);
    }

    @Test
    @Transactional
    void patchNonExistingTargets() throws Exception {
        int databaseSizeBeforeUpdate = targetsRepository.findAll().size();
        targets.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTargetsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, targets.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(targets))
            )
            .andExpect(status().isBadRequest());

        // Validate the Targets in the database
        List<Targets> targetsList = targetsRepository.findAll();
        assertThat(targetsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTargets() throws Exception {
        int databaseSizeBeforeUpdate = targetsRepository.findAll().size();
        targets.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTargetsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(targets))
            )
            .andExpect(status().isBadRequest());

        // Validate the Targets in the database
        List<Targets> targetsList = targetsRepository.findAll();
        assertThat(targetsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTargets() throws Exception {
        int databaseSizeBeforeUpdate = targetsRepository.findAll().size();
        targets.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTargetsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(targets)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Targets in the database
        List<Targets> targetsList = targetsRepository.findAll();
        assertThat(targetsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTargets() throws Exception {
        // Initialize the database
        targetsRepository.saveAndFlush(targets);

        int databaseSizeBeforeDelete = targetsRepository.findAll().size();

        // Delete the targets
        restTargetsMockMvc
            .perform(delete(ENTITY_API_URL_ID, targets.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Targets> targetsList = targetsRepository.findAll();
        assertThat(targetsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
