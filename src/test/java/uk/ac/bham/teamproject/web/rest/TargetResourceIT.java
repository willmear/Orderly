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
import uk.ac.bham.teamproject.domain.Target;
import uk.ac.bham.teamproject.repository.TargetRepository;

/**
 * Integration tests for the {@link TargetResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TargetResourceIT {

    private static final String DEFAULT_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_TEXT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/targets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TargetRepository targetRepository;

    @Mock
    private TargetRepository targetRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTargetMockMvc;

    private Target target;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Target createEntity(EntityManager em) {
        Target target = new Target().text(DEFAULT_TEXT);
        return target;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Target createUpdatedEntity(EntityManager em) {
        Target target = new Target().text(UPDATED_TEXT);
        return target;
    }

    @BeforeEach
    public void initTest() {
        target = createEntity(em);
    }

    @Test
    @Transactional
    void createTarget() throws Exception {
        int databaseSizeBeforeCreate = targetRepository.findAll().size();
        // Create the Target
        restTargetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(target)))
            .andExpect(status().isCreated());

        // Validate the Target in the database
        List<Target> targetList = targetRepository.findAll();
        assertThat(targetList).hasSize(databaseSizeBeforeCreate + 1);
        Target testTarget = targetList.get(targetList.size() - 1);
        assertThat(testTarget.getText()).isEqualTo(DEFAULT_TEXT);
    }

    @Test
    @Transactional
    void createTargetWithExistingId() throws Exception {
        // Create the Target with an existing ID
        target.setId(1L);

        int databaseSizeBeforeCreate = targetRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTargetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(target)))
            .andExpect(status().isBadRequest());

        // Validate the Target in the database
        List<Target> targetList = targetRepository.findAll();
        assertThat(targetList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTargets() throws Exception {
        // Initialize the database
        targetRepository.saveAndFlush(target);

        // Get all the targetList
        restTargetMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(target.getId().intValue())))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTargetsWithEagerRelationshipsIsEnabled() throws Exception {
        when(targetRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTargetMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(targetRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTargetsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(targetRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTargetMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(targetRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTarget() throws Exception {
        // Initialize the database
        targetRepository.saveAndFlush(target);

        // Get the target
        restTargetMockMvc
            .perform(get(ENTITY_API_URL_ID, target.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(target.getId().intValue()))
            .andExpect(jsonPath("$.text").value(DEFAULT_TEXT));
    }

    @Test
    @Transactional
    void getNonExistingTarget() throws Exception {
        // Get the target
        restTargetMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTarget() throws Exception {
        // Initialize the database
        targetRepository.saveAndFlush(target);

        int databaseSizeBeforeUpdate = targetRepository.findAll().size();

        // Update the target
        Target updatedTarget = targetRepository.findById(target.getId()).get();
        // Disconnect from session so that the updates on updatedTarget are not directly saved in db
        em.detach(updatedTarget);
        updatedTarget.text(UPDATED_TEXT);

        restTargetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTarget.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTarget))
            )
            .andExpect(status().isOk());

        // Validate the Target in the database
        List<Target> targetList = targetRepository.findAll();
        assertThat(targetList).hasSize(databaseSizeBeforeUpdate);
        Target testTarget = targetList.get(targetList.size() - 1);
        assertThat(testTarget.getText()).isEqualTo(UPDATED_TEXT);
    }

    @Test
    @Transactional
    void putNonExistingTarget() throws Exception {
        int databaseSizeBeforeUpdate = targetRepository.findAll().size();
        target.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTargetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, target.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(target))
            )
            .andExpect(status().isBadRequest());

        // Validate the Target in the database
        List<Target> targetList = targetRepository.findAll();
        assertThat(targetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTarget() throws Exception {
        int databaseSizeBeforeUpdate = targetRepository.findAll().size();
        target.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTargetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(target))
            )
            .andExpect(status().isBadRequest());

        // Validate the Target in the database
        List<Target> targetList = targetRepository.findAll();
        assertThat(targetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTarget() throws Exception {
        int databaseSizeBeforeUpdate = targetRepository.findAll().size();
        target.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTargetMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(target)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Target in the database
        List<Target> targetList = targetRepository.findAll();
        assertThat(targetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTargetWithPatch() throws Exception {
        // Initialize the database
        targetRepository.saveAndFlush(target);

        int databaseSizeBeforeUpdate = targetRepository.findAll().size();

        // Update the target using partial update
        Target partialUpdatedTarget = new Target();
        partialUpdatedTarget.setId(target.getId());

        partialUpdatedTarget.text(UPDATED_TEXT);

        restTargetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTarget.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTarget))
            )
            .andExpect(status().isOk());

        // Validate the Target in the database
        List<Target> targetList = targetRepository.findAll();
        assertThat(targetList).hasSize(databaseSizeBeforeUpdate);
        Target testTarget = targetList.get(targetList.size() - 1);
        assertThat(testTarget.getText()).isEqualTo(UPDATED_TEXT);
    }

    @Test
    @Transactional
    void fullUpdateTargetWithPatch() throws Exception {
        // Initialize the database
        targetRepository.saveAndFlush(target);

        int databaseSizeBeforeUpdate = targetRepository.findAll().size();

        // Update the target using partial update
        Target partialUpdatedTarget = new Target();
        partialUpdatedTarget.setId(target.getId());

        partialUpdatedTarget.text(UPDATED_TEXT);

        restTargetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTarget.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTarget))
            )
            .andExpect(status().isOk());

        // Validate the Target in the database
        List<Target> targetList = targetRepository.findAll();
        assertThat(targetList).hasSize(databaseSizeBeforeUpdate);
        Target testTarget = targetList.get(targetList.size() - 1);
        assertThat(testTarget.getText()).isEqualTo(UPDATED_TEXT);
    }

    @Test
    @Transactional
    void patchNonExistingTarget() throws Exception {
        int databaseSizeBeforeUpdate = targetRepository.findAll().size();
        target.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTargetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, target.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(target))
            )
            .andExpect(status().isBadRequest());

        // Validate the Target in the database
        List<Target> targetList = targetRepository.findAll();
        assertThat(targetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTarget() throws Exception {
        int databaseSizeBeforeUpdate = targetRepository.findAll().size();
        target.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTargetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(target))
            )
            .andExpect(status().isBadRequest());

        // Validate the Target in the database
        List<Target> targetList = targetRepository.findAll();
        assertThat(targetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTarget() throws Exception {
        int databaseSizeBeforeUpdate = targetRepository.findAll().size();
        target.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTargetMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(target)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Target in the database
        List<Target> targetList = targetRepository.findAll();
        assertThat(targetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTarget() throws Exception {
        // Initialize the database
        targetRepository.saveAndFlush(target);

        int databaseSizeBeforeDelete = targetRepository.findAll().size();

        // Delete the target
        restTargetMockMvc
            .perform(delete(ENTITY_API_URL_ID, target.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Target> targetList = targetRepository.findAll();
        assertThat(targetList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
