package uk.ac.bham.teamproject.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.ZoneId;
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
import uk.ac.bham.teamproject.domain.UserOrders;
import uk.ac.bham.teamproject.repository.UserOrdersRepository;

/**
 * Integration tests for the {@link UserOrdersResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class UserOrdersResourceIT {

    private static final Long DEFAULT_ORDER_NUM = 1L;
    private static final Long UPDATED_ORDER_NUM = 2L;

    private static final String DEFAULT_ORDER_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_ORDER_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_DELIVERY_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_DELIVERY_ADDRESS = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_ORDERED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_ORDERED = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_DUE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DUE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Long DEFAULT_CUSTOMER_ID = 1L;
    private static final Long UPDATED_CUSTOMER_ID = 2L;

    private static final Integer DEFAULT_PRODUCTION_TIME = 1;
    private static final Integer UPDATED_PRODUCTION_TIME = 2;

    private static final Float DEFAULT_PRODUCTION_COST = 1F;
    private static final Float UPDATED_PRODUCTION_COST = 2F;

    private static final Float DEFAULT_PRICE = 1F;
    private static final Float UPDATED_PRICE = 2F;

    private static final String ENTITY_API_URL = "/api/user-orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserOrdersRepository userOrdersRepository;

    @Mock
    private UserOrdersRepository userOrdersRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserOrdersMockMvc;

    private UserOrders userOrders;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserOrders createEntity(EntityManager em) {
        UserOrders userOrders = new UserOrders()
            .orderNum(DEFAULT_ORDER_NUM)
            .orderDescription(DEFAULT_ORDER_DESCRIPTION)
            .deliveryAddress(DEFAULT_DELIVERY_ADDRESS)
            .dateOrdered(DEFAULT_DATE_ORDERED)
            .dueDate(DEFAULT_DUE_DATE)
            .customerID(DEFAULT_CUSTOMER_ID)
            .productionTime(DEFAULT_PRODUCTION_TIME)
            .productionCost(DEFAULT_PRODUCTION_COST)
            .price(DEFAULT_PRICE);
        return userOrders;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserOrders createUpdatedEntity(EntityManager em) {
        UserOrders userOrders = new UserOrders()
            .orderNum(UPDATED_ORDER_NUM)
            .orderDescription(UPDATED_ORDER_DESCRIPTION)
            .deliveryAddress(UPDATED_DELIVERY_ADDRESS)
            .dateOrdered(UPDATED_DATE_ORDERED)
            .dueDate(UPDATED_DUE_DATE)
            .customerID(UPDATED_CUSTOMER_ID)
            .productionTime(UPDATED_PRODUCTION_TIME)
            .productionCost(UPDATED_PRODUCTION_COST)
            .price(UPDATED_PRICE);
        return userOrders;
    }

    @BeforeEach
    public void initTest() {
        userOrders = createEntity(em);
    }

    @Test
    @Transactional
    void createUserOrders() throws Exception {
        int databaseSizeBeforeCreate = userOrdersRepository.findAll().size();
        // Create the UserOrders
        restUserOrdersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userOrders)))
            .andExpect(status().isCreated());

        // Validate the UserOrders in the database
        List<UserOrders> userOrdersList = userOrdersRepository.findAll();
        assertThat(userOrdersList).hasSize(databaseSizeBeforeCreate + 1);
        UserOrders testUserOrders = userOrdersList.get(userOrdersList.size() - 1);
        assertThat(testUserOrders.getOrderNum()).isEqualTo(DEFAULT_ORDER_NUM);
        assertThat(testUserOrders.getOrderDescription()).isEqualTo(DEFAULT_ORDER_DESCRIPTION);
        assertThat(testUserOrders.getDeliveryAddress()).isEqualTo(DEFAULT_DELIVERY_ADDRESS);
        assertThat(testUserOrders.getDateOrdered()).isEqualTo(DEFAULT_DATE_ORDERED);
        assertThat(testUserOrders.getDueDate()).isEqualTo(DEFAULT_DUE_DATE);
        assertThat(testUserOrders.getCustomerID()).isEqualTo(DEFAULT_CUSTOMER_ID);
        assertThat(testUserOrders.getProductionTime()).isEqualTo(DEFAULT_PRODUCTION_TIME);
        assertThat(testUserOrders.getProductionCost()).isEqualTo(DEFAULT_PRODUCTION_COST);
        assertThat(testUserOrders.getPrice()).isEqualTo(DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void createUserOrdersWithExistingId() throws Exception {
        // Create the UserOrders with an existing ID
        userOrders.setId(1L);

        int databaseSizeBeforeCreate = userOrdersRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserOrdersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userOrders)))
            .andExpect(status().isBadRequest());

        // Validate the UserOrders in the database
        List<UserOrders> userOrdersList = userOrdersRepository.findAll();
        assertThat(userOrdersList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkOrderNumIsRequired() throws Exception {
        int databaseSizeBeforeTest = userOrdersRepository.findAll().size();
        // set the field null
        userOrders.setOrderNum(null);

        // Create the UserOrders, which fails.

        restUserOrdersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userOrders)))
            .andExpect(status().isBadRequest());

        List<UserOrders> userOrdersList = userOrdersRepository.findAll();
        assertThat(userOrdersList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOrderDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = userOrdersRepository.findAll().size();
        // set the field null
        userOrders.setOrderDescription(null);

        // Create the UserOrders, which fails.

        restUserOrdersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userOrders)))
            .andExpect(status().isBadRequest());

        List<UserOrders> userOrdersList = userOrdersRepository.findAll();
        assertThat(userOrdersList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDueDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = userOrdersRepository.findAll().size();
        // set the field null
        userOrders.setDueDate(null);

        // Create the UserOrders, which fails.

        restUserOrdersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userOrders)))
            .andExpect(status().isBadRequest());

        List<UserOrders> userOrdersList = userOrdersRepository.findAll();
        assertThat(userOrdersList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCustomerIDIsRequired() throws Exception {
        int databaseSizeBeforeTest = userOrdersRepository.findAll().size();
        // set the field null
        userOrders.setCustomerID(null);

        // Create the UserOrders, which fails.

        restUserOrdersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userOrders)))
            .andExpect(status().isBadRequest());

        List<UserOrders> userOrdersList = userOrdersRepository.findAll();
        assertThat(userOrdersList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = userOrdersRepository.findAll().size();
        // set the field null
        userOrders.setPrice(null);

        // Create the UserOrders, which fails.

        restUserOrdersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userOrders)))
            .andExpect(status().isBadRequest());

        List<UserOrders> userOrdersList = userOrdersRepository.findAll();
        assertThat(userOrdersList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUserOrders() throws Exception {
        // Initialize the database
        userOrdersRepository.saveAndFlush(userOrders);

        // Get all the userOrdersList
        restUserOrdersMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userOrders.getId().intValue())))
            .andExpect(jsonPath("$.[*].orderNum").value(hasItem(DEFAULT_ORDER_NUM.intValue())))
            .andExpect(jsonPath("$.[*].orderDescription").value(hasItem(DEFAULT_ORDER_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].deliveryAddress").value(hasItem(DEFAULT_DELIVERY_ADDRESS)))
            .andExpect(jsonPath("$.[*].dateOrdered").value(hasItem(DEFAULT_DATE_ORDERED.toString())))
            .andExpect(jsonPath("$.[*].dueDate").value(hasItem(DEFAULT_DUE_DATE.toString())))
            .andExpect(jsonPath("$.[*].customerID").value(hasItem(DEFAULT_CUSTOMER_ID.intValue())))
            .andExpect(jsonPath("$.[*].productionTime").value(hasItem(DEFAULT_PRODUCTION_TIME)))
            .andExpect(jsonPath("$.[*].productionCost").value(hasItem(DEFAULT_PRODUCTION_COST.doubleValue())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUserOrdersWithEagerRelationshipsIsEnabled() throws Exception {
        when(userOrdersRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUserOrdersMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(userOrdersRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUserOrdersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(userOrdersRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUserOrdersMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(userOrdersRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getUserOrders() throws Exception {
        // Initialize the database
        userOrdersRepository.saveAndFlush(userOrders);

        // Get the userOrders
        restUserOrdersMockMvc
            .perform(get(ENTITY_API_URL_ID, userOrders.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userOrders.getId().intValue()))
            .andExpect(jsonPath("$.orderNum").value(DEFAULT_ORDER_NUM.intValue()))
            .andExpect(jsonPath("$.orderDescription").value(DEFAULT_ORDER_DESCRIPTION))
            .andExpect(jsonPath("$.deliveryAddress").value(DEFAULT_DELIVERY_ADDRESS))
            .andExpect(jsonPath("$.dateOrdered").value(DEFAULT_DATE_ORDERED.toString()))
            .andExpect(jsonPath("$.dueDate").value(DEFAULT_DUE_DATE.toString()))
            .andExpect(jsonPath("$.customerID").value(DEFAULT_CUSTOMER_ID.intValue()))
            .andExpect(jsonPath("$.productionTime").value(DEFAULT_PRODUCTION_TIME))
            .andExpect(jsonPath("$.productionCost").value(DEFAULT_PRODUCTION_COST.doubleValue()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()));
    }

    @Test
    @Transactional
    void getNonExistingUserOrders() throws Exception {
        // Get the userOrders
        restUserOrdersMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserOrders() throws Exception {
        // Initialize the database
        userOrdersRepository.saveAndFlush(userOrders);

        int databaseSizeBeforeUpdate = userOrdersRepository.findAll().size();

        // Update the userOrders
        UserOrders updatedUserOrders = userOrdersRepository.findById(userOrders.getId()).get();
        // Disconnect from session so that the updates on updatedUserOrders are not directly saved in db
        em.detach(updatedUserOrders);
        updatedUserOrders
            .orderNum(UPDATED_ORDER_NUM)
            .orderDescription(UPDATED_ORDER_DESCRIPTION)
            .deliveryAddress(UPDATED_DELIVERY_ADDRESS)
            .dateOrdered(UPDATED_DATE_ORDERED)
            .dueDate(UPDATED_DUE_DATE)
            .customerID(UPDATED_CUSTOMER_ID)
            .productionTime(UPDATED_PRODUCTION_TIME)
            .productionCost(UPDATED_PRODUCTION_COST)
            .price(UPDATED_PRICE);

        restUserOrdersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUserOrders.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedUserOrders))
            )
            .andExpect(status().isOk());

        // Validate the UserOrders in the database
        List<UserOrders> userOrdersList = userOrdersRepository.findAll();
        assertThat(userOrdersList).hasSize(databaseSizeBeforeUpdate);
        UserOrders testUserOrders = userOrdersList.get(userOrdersList.size() - 1);
        assertThat(testUserOrders.getOrderNum()).isEqualTo(UPDATED_ORDER_NUM);
        assertThat(testUserOrders.getOrderDescription()).isEqualTo(UPDATED_ORDER_DESCRIPTION);
        assertThat(testUserOrders.getDeliveryAddress()).isEqualTo(UPDATED_DELIVERY_ADDRESS);
        assertThat(testUserOrders.getDateOrdered()).isEqualTo(UPDATED_DATE_ORDERED);
        assertThat(testUserOrders.getDueDate()).isEqualTo(UPDATED_DUE_DATE);
        assertThat(testUserOrders.getCustomerID()).isEqualTo(UPDATED_CUSTOMER_ID);
        assertThat(testUserOrders.getProductionTime()).isEqualTo(UPDATED_PRODUCTION_TIME);
        assertThat(testUserOrders.getProductionCost()).isEqualTo(UPDATED_PRODUCTION_COST);
        assertThat(testUserOrders.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    @Transactional
    void putNonExistingUserOrders() throws Exception {
        int databaseSizeBeforeUpdate = userOrdersRepository.findAll().size();
        userOrders.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserOrdersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userOrders.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userOrders))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserOrders in the database
        List<UserOrders> userOrdersList = userOrdersRepository.findAll();
        assertThat(userOrdersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserOrders() throws Exception {
        int databaseSizeBeforeUpdate = userOrdersRepository.findAll().size();
        userOrders.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserOrdersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userOrders))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserOrders in the database
        List<UserOrders> userOrdersList = userOrdersRepository.findAll();
        assertThat(userOrdersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserOrders() throws Exception {
        int databaseSizeBeforeUpdate = userOrdersRepository.findAll().size();
        userOrders.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserOrdersMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userOrders)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserOrders in the database
        List<UserOrders> userOrdersList = userOrdersRepository.findAll();
        assertThat(userOrdersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserOrdersWithPatch() throws Exception {
        // Initialize the database
        userOrdersRepository.saveAndFlush(userOrders);

        int databaseSizeBeforeUpdate = userOrdersRepository.findAll().size();

        // Update the userOrders using partial update
        UserOrders partialUpdatedUserOrders = new UserOrders();
        partialUpdatedUserOrders.setId(userOrders.getId());

        partialUpdatedUserOrders.orderNum(UPDATED_ORDER_NUM).dueDate(UPDATED_DUE_DATE).price(UPDATED_PRICE);

        restUserOrdersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserOrders.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserOrders))
            )
            .andExpect(status().isOk());

        // Validate the UserOrders in the database
        List<UserOrders> userOrdersList = userOrdersRepository.findAll();
        assertThat(userOrdersList).hasSize(databaseSizeBeforeUpdate);
        UserOrders testUserOrders = userOrdersList.get(userOrdersList.size() - 1);
        assertThat(testUserOrders.getOrderNum()).isEqualTo(UPDATED_ORDER_NUM);
        assertThat(testUserOrders.getOrderDescription()).isEqualTo(DEFAULT_ORDER_DESCRIPTION);
        assertThat(testUserOrders.getDeliveryAddress()).isEqualTo(DEFAULT_DELIVERY_ADDRESS);
        assertThat(testUserOrders.getDateOrdered()).isEqualTo(DEFAULT_DATE_ORDERED);
        assertThat(testUserOrders.getDueDate()).isEqualTo(UPDATED_DUE_DATE);
        assertThat(testUserOrders.getCustomerID()).isEqualTo(DEFAULT_CUSTOMER_ID);
        assertThat(testUserOrders.getProductionTime()).isEqualTo(DEFAULT_PRODUCTION_TIME);
        assertThat(testUserOrders.getProductionCost()).isEqualTo(DEFAULT_PRODUCTION_COST);
        assertThat(testUserOrders.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    @Transactional
    void fullUpdateUserOrdersWithPatch() throws Exception {
        // Initialize the database
        userOrdersRepository.saveAndFlush(userOrders);

        int databaseSizeBeforeUpdate = userOrdersRepository.findAll().size();

        // Update the userOrders using partial update
        UserOrders partialUpdatedUserOrders = new UserOrders();
        partialUpdatedUserOrders.setId(userOrders.getId());

        partialUpdatedUserOrders
            .orderNum(UPDATED_ORDER_NUM)
            .orderDescription(UPDATED_ORDER_DESCRIPTION)
            .deliveryAddress(UPDATED_DELIVERY_ADDRESS)
            .dateOrdered(UPDATED_DATE_ORDERED)
            .dueDate(UPDATED_DUE_DATE)
            .customerID(UPDATED_CUSTOMER_ID)
            .productionTime(UPDATED_PRODUCTION_TIME)
            .productionCost(UPDATED_PRODUCTION_COST)
            .price(UPDATED_PRICE);

        restUserOrdersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserOrders.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserOrders))
            )
            .andExpect(status().isOk());

        // Validate the UserOrders in the database
        List<UserOrders> userOrdersList = userOrdersRepository.findAll();
        assertThat(userOrdersList).hasSize(databaseSizeBeforeUpdate);
        UserOrders testUserOrders = userOrdersList.get(userOrdersList.size() - 1);
        assertThat(testUserOrders.getOrderNum()).isEqualTo(UPDATED_ORDER_NUM);
        assertThat(testUserOrders.getOrderDescription()).isEqualTo(UPDATED_ORDER_DESCRIPTION);
        assertThat(testUserOrders.getDeliveryAddress()).isEqualTo(UPDATED_DELIVERY_ADDRESS);
        assertThat(testUserOrders.getDateOrdered()).isEqualTo(UPDATED_DATE_ORDERED);
        assertThat(testUserOrders.getDueDate()).isEqualTo(UPDATED_DUE_DATE);
        assertThat(testUserOrders.getCustomerID()).isEqualTo(UPDATED_CUSTOMER_ID);
        assertThat(testUserOrders.getProductionTime()).isEqualTo(UPDATED_PRODUCTION_TIME);
        assertThat(testUserOrders.getProductionCost()).isEqualTo(UPDATED_PRODUCTION_COST);
        assertThat(testUserOrders.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    @Transactional
    void patchNonExistingUserOrders() throws Exception {
        int databaseSizeBeforeUpdate = userOrdersRepository.findAll().size();
        userOrders.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserOrdersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userOrders.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userOrders))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserOrders in the database
        List<UserOrders> userOrdersList = userOrdersRepository.findAll();
        assertThat(userOrdersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserOrders() throws Exception {
        int databaseSizeBeforeUpdate = userOrdersRepository.findAll().size();
        userOrders.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserOrdersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userOrders))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserOrders in the database
        List<UserOrders> userOrdersList = userOrdersRepository.findAll();
        assertThat(userOrdersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserOrders() throws Exception {
        int databaseSizeBeforeUpdate = userOrdersRepository.findAll().size();
        userOrders.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserOrdersMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(userOrders))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserOrders in the database
        List<UserOrders> userOrdersList = userOrdersRepository.findAll();
        assertThat(userOrdersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserOrders() throws Exception {
        // Initialize the database
        userOrdersRepository.saveAndFlush(userOrders);

        int databaseSizeBeforeDelete = userOrdersRepository.findAll().size();

        // Delete the userOrders
        restUserOrdersMockMvc
            .perform(delete(ENTITY_API_URL_ID, userOrders.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserOrders> userOrdersList = userOrdersRepository.findAll();
        assertThat(userOrdersList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
