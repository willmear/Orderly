package uk.ac.bham.teamproject.web.rest;


import uk.ac.bham.teamproject.IntegrationTest;
import uk.ac.bham.teamproject.domain.Inventory;
import uk.ac.bham.teamproject.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * Integration tests for the {@link InventoryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class InventoryResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/inventories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + ( 2 * Integer.MAX_VALUE ));

    @Autowired
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryRepository inventoryRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInventoryMockMvc;

    private Inventory inventory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Inventory createEntity(EntityManager em) {
        Inventory inventory = new Inventory()
            .name(DEFAULT_NAME)
            .quantity(DEFAULT_QUANTITY)
            .status(DEFAULT_STATUS);
        return inventory;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Inventory createUpdatedEntity(EntityManager em) {
        Inventory inventory = new Inventory()
            .name(UPDATED_NAME)
            .quantity(UPDATED_QUANTITY)
            .status(UPDATED_STATUS);
        return inventory;
    }

    @BeforeEach
    public void initTest() {
        inventory = createEntity(em);
    }

    @Test
    @Transactional
    void createInventory() throws Exception {
        int databaseSizeBeforeCreate = inventoryRepository.findAll().size();
        // Create the Inventory
        restInventoryMockMvc.perform(post(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(inventory)))
            .andExpect(status().isCreated());

        // Validate the Inventory in the database
        List<Inventory> inventoryList = inventoryRepository.findAll();
        assertThat(inventoryList).hasSize(databaseSizeBeforeCreate + 1);
        Inventory testInventory = inventoryList.get(inventoryList.size() - 1);
        assertThat(testInventory.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testInventory.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testInventory.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createInventoryWithExistingId() throws Exception {
        // Create the Inventory with an existing ID
        inventory.setId(1L);

        int databaseSizeBeforeCreate = inventoryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInventoryMockMvc.perform(post(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(inventory)))
            .andExpect(status().isBadRequest());

        // Validate the Inventory in the database
        List<Inventory> inventoryList = inventoryRepository.findAll();
        assertThat(inventoryList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = inventoryRepository.findAll().size();
        // set the field null
        inventory.setName(null);

        // Create the Inventory, which fails.


        restInventoryMockMvc.perform(post(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(inventory)))
            .andExpect(status().isBadRequest());

        List<Inventory> inventoryList = inventoryRepository.findAll();
        assertThat(inventoryList).hasSize(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        int databaseSizeBeforeTest = inventoryRepository.findAll().size();
        // set the field null
        inventory.setQuantity(null);

        // Create the Inventory, which fails.


        restInventoryMockMvc.perform(post(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(inventory)))
            .andExpect(status().isBadRequest());

        List<Inventory> inventoryList = inventoryRepository.findAll();
        assertThat(inventoryList).hasSize(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = inventoryRepository.findAll().size();
        // set the field null
        inventory.setStatus(null);

        // Create the Inventory, which fails.


        restInventoryMockMvc.perform(post(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(inventory)))
            .andExpect(status().isBadRequest());

        List<Inventory> inventoryList = inventoryRepository.findAll();
        assertThat(inventoryList).hasSize(databaseSizeBeforeTest);

    }

    @Test
    @Transactional
    void getAllInventories() throws Exception {
        // Initialize the database
        inventoryRepository.saveAndFlush(inventory);

        // Get all the inventoryList
        restInventoryMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(inventory.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    @SuppressWarnings({"unchecked"})
    void getAllInventoriesWithEagerRelationshipsIsEnabled() throws Exception {
        when(inventoryRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInventoryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true"))
            .andExpect(status().isOk());

        verify(inventoryRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    void getAllInventoriesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(inventoryRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInventoryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false"))
            .andExpect(status().isOk());
        verify(inventoryRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getInventory() throws Exception {
        // Initialize the database
        inventoryRepository.saveAndFlush(inventory);

        // Get the inventory
        restInventoryMockMvc.perform(get(ENTITY_API_URL_ID, inventory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(inventory.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS));
    }
    @Test
    @Transactional
    void getNonExistingInventory() throws Exception {
        // Get the inventory
        restInventoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInventory() throws Exception {
        // Initialize the database
        inventoryRepository.saveAndFlush(inventory);

        int databaseSizeBeforeUpdate = inventoryRepository.findAll().size();

        // Update the inventory
        Inventory updatedInventory = inventoryRepository.findById(inventory.getId()).get();
        // Disconnect from session so that the updates on updatedInventory are not directly saved in db
        em.detach(updatedInventory);
        updatedInventory
            .name(UPDATED_NAME)
            .quantity(UPDATED_QUANTITY)
            .status(UPDATED_STATUS);

        restInventoryMockMvc.perform(put(ENTITY_API_URL_ID, updatedInventory.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedInventory)))
            .andExpect(status().isOk());

        // Validate the Inventory in the database
        List<Inventory> inventoryList = inventoryRepository.findAll();
        assertThat(inventoryList).hasSize(databaseSizeBeforeUpdate);
        Inventory testInventory = inventoryList.get(inventoryList.size() - 1);
        assertThat(testInventory.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testInventory.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testInventory.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void putNonExistingInventory() throws Exception {
        int databaseSizeBeforeUpdate = inventoryRepository.findAll().size();
        inventory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInventoryMockMvc.perform(put(ENTITY_API_URL_ID, inventory.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(inventory)))
            .andExpect(status().isBadRequest());

        // Validate the Inventory in the database
        List<Inventory> inventoryList = inventoryRepository.findAll();
        assertThat(inventoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInventory() throws Exception {
        int databaseSizeBeforeUpdate = inventoryRepository.findAll().size();
        inventory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryMockMvc.perform(put(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(inventory)))
            .andExpect(status().isBadRequest());

        // Validate the Inventory in the database
        List<Inventory> inventoryList = inventoryRepository.findAll();
        assertThat(inventoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInventory() throws Exception {
        int databaseSizeBeforeUpdate = inventoryRepository.findAll().size();
        inventory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryMockMvc.perform(put(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(inventory)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Inventory in the database
        List<Inventory> inventoryList = inventoryRepository.findAll();
        assertThat(inventoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInventoryWithPatch() throws Exception {
        // Initialize the database
        inventoryRepository.saveAndFlush(inventory);

int databaseSizeBeforeUpdate = inventoryRepository.findAll().size();

// Update the inventory using partial update
Inventory partialUpdatedInventory = new Inventory();
partialUpdatedInventory.setId(inventory.getId());

    partialUpdatedInventory
        .quantity(UPDATED_QUANTITY)
        .status(UPDATED_STATUS);

restInventoryMockMvc.perform(patch(ENTITY_API_URL_ID, partialUpdatedInventory.getId())
.contentType("application/merge-patch+json")
.content(TestUtil.convertObjectToJsonBytes(partialUpdatedInventory)))
.andExpect(status().isOk());

// Validate the Inventory in the database
List<Inventory> inventoryList = inventoryRepository.findAll();
assertThat(inventoryList).hasSize(databaseSizeBeforeUpdate);
Inventory testInventory = inventoryList.get(inventoryList.size() - 1);
    assertThat(testInventory.getName()).isEqualTo(DEFAULT_NAME);
    assertThat(testInventory.getQuantity()).isEqualTo(UPDATED_QUANTITY);
    assertThat(testInventory.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateInventoryWithPatch() throws Exception {
        // Initialize the database
        inventoryRepository.saveAndFlush(inventory);

  
int databaseSizeBeforeUpdate = inventoryRepository.findAll().size();

// Update the inventory using partial update
Inventory partialUpdatedInventory = new Inventory();
partialUpdatedInventory.setId(inventory.getId());

    partialUpdatedInventory
        .name(UPDATED_NAME)
        .quantity(UPDATED_QUANTITY)
        .status(UPDATED_STATUS);

restInventoryMockMvc.perform(patch(ENTITY_API_URL_ID, partialUpdatedInventory.getId())
.contentType("application/merge-patch+json")
.content(TestUtil.convertObjectToJsonBytes(partialUpdatedInventory)))
.andExpect(status().isOk());

// Validate the Inventory in the database
List<Inventory> inventoryList = inventoryRepository.findAll();
assertThat(inventoryList).hasSize(databaseSizeBeforeUpdate);
Inventory testInventory = inventoryList.get(inventoryList.size() - 1);
    assertThat(testInventory.getName()).isEqualTo(UPDATED_NAME);
    assertThat(testInventory.getQuantity()).isEqualTo(UPDATED_QUANTITY);
    assertThat(testInventory.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingInventory() throws Exception {
        int databaseSizeBeforeUpdate = inventoryRepository.findAll().size();
        inventory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInventoryMockMvc.perform(patch(ENTITY_API_URL_ID, inventory.getId())
            .contentType("application/merge-patch+json")
            .content(TestUtil.convertObjectToJsonBytes(inventory)))
            .andExpect(status().isBadRequest());

        // Validate the Inventory in the database
        List<Inventory> inventoryList = inventoryRepository.findAll();
        assertThat(inventoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInventory() throws Exception {
        int databaseSizeBeforeUpdate = inventoryRepository.findAll().size();
        inventory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryMockMvc.perform(patch(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType("application/merge-patch+json")
            .content(TestUtil.convertObjectToJsonBytes(inventory)))
            .andExpect(status().isBadRequest());

        // Validate the Inventory in the database
        List<Inventory> inventoryList = inventoryRepository.findAll();
        assertThat(inventoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInventory() throws Exception {
        int databaseSizeBeforeUpdate = inventoryRepository.findAll().size();
        inventory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryMockMvc.perform(patch(ENTITY_API_URL)
            .contentType("application/merge-patch+json")
            .content(TestUtil.convertObjectToJsonBytes(inventory)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Inventory in the database
        List<Inventory> inventoryList = inventoryRepository.findAll();
        assertThat(inventoryList).hasSize(databaseSizeBeforeUpdate);

    }

    @Test
    @Transactional
    void deleteInventory() throws Exception {
        // Initialize the database
        inventoryRepository.saveAndFlush(inventory);

        int databaseSizeBeforeDelete = inventoryRepository.findAll().size();

        // Delete the inventory
        restInventoryMockMvc.perform(delete(ENTITY_API_URL_ID, inventory.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Inventory> inventoryList = inventoryRepository.findAll();
        assertThat(inventoryList).hasSize(databaseSizeBeforeDelete - 1);

    }
}
