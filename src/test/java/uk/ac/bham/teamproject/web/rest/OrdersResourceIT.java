package uk.ac.bham.teamproject.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.ZoneId;
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
import uk.ac.bham.teamproject.IntegrationTest;
import uk.ac.bham.teamproject.domain.Orders;
import uk.ac.bham.teamproject.repository.OrdersRepository;

/**
 * Integration tests for the {@link OrdersResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OrdersResourceIT {

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

    private static final String ENTITY_API_URL = "/api/orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrdersMockMvc;

    private Orders orders;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Orders createEntity(EntityManager em) {
        Orders orders = new Orders()
            .orderNum(DEFAULT_ORDER_NUM)
            .orderDescription(DEFAULT_ORDER_DESCRIPTION)
            .deliveryAddress(DEFAULT_DELIVERY_ADDRESS)
            .dateOrdered(DEFAULT_DATE_ORDERED)
            .dueDate(DEFAULT_DUE_DATE)
            .customerID(DEFAULT_CUSTOMER_ID)
            .productionTime(DEFAULT_PRODUCTION_TIME)
            .productionCost(DEFAULT_PRODUCTION_COST)
            .price(DEFAULT_PRICE);
        return orders;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Orders createUpdatedEntity(EntityManager em) {
        Orders orders = new Orders()
            .orderNum(UPDATED_ORDER_NUM)
            .orderDescription(UPDATED_ORDER_DESCRIPTION)
            .deliveryAddress(UPDATED_DELIVERY_ADDRESS)
            .dateOrdered(UPDATED_DATE_ORDERED)
            .dueDate(UPDATED_DUE_DATE)
            .customerID(UPDATED_CUSTOMER_ID)
            .productionTime(UPDATED_PRODUCTION_TIME)
            .productionCost(UPDATED_PRODUCTION_COST)
            .price(UPDATED_PRICE);
        return orders;
    }

    @BeforeEach
    public void initTest() {
        orders = createEntity(em);
    }

    @Test
    @Transactional
    void createOrders() throws Exception {
        int databaseSizeBeforeCreate = ordersRepository.findAll().size();
        // Create the Orders
        restOrdersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orders)))
            .andExpect(status().isCreated());

        // Validate the Orders in the database
        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeCreate + 1);
        Orders testOrders = ordersList.get(ordersList.size() - 1);
        assertThat(testOrders.getOrderNum()).isEqualTo(DEFAULT_ORDER_NUM);
        assertThat(testOrders.getOrderDescription()).isEqualTo(DEFAULT_ORDER_DESCRIPTION);
        assertThat(testOrders.getDeliveryAddress()).isEqualTo(DEFAULT_DELIVERY_ADDRESS);
        assertThat(testOrders.getDateOrdered()).isEqualTo(DEFAULT_DATE_ORDERED);
        assertThat(testOrders.getDueDate()).isEqualTo(DEFAULT_DUE_DATE);
        assertThat(testOrders.getCustomerID()).isEqualTo(DEFAULT_CUSTOMER_ID);
        assertThat(testOrders.getProductionTime()).isEqualTo(DEFAULT_PRODUCTION_TIME);
        assertThat(testOrders.getProductionCost()).isEqualTo(DEFAULT_PRODUCTION_COST);
        assertThat(testOrders.getPrice()).isEqualTo(DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void createOrdersWithExistingId() throws Exception {
        // Create the Orders with an existing ID
        orders.setId(1L);

        int databaseSizeBeforeCreate = ordersRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrdersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orders)))
            .andExpect(status().isBadRequest());

        // Validate the Orders in the database
        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkOrderNumIsRequired() throws Exception {
        int databaseSizeBeforeTest = ordersRepository.findAll().size();
        // set the field null
        orders.setOrderNum(null);

        // Create the Orders, which fails.

        restOrdersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orders)))
            .andExpect(status().isBadRequest());

        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOrderDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = ordersRepository.findAll().size();
        // set the field null
        orders.setOrderDescription(null);

        // Create the Orders, which fails.

        restOrdersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orders)))
            .andExpect(status().isBadRequest());

        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDueDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = ordersRepository.findAll().size();
        // set the field null
        orders.setDueDate(null);

        // Create the Orders, which fails.

        restOrdersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orders)))
            .andExpect(status().isBadRequest());

        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCustomerIDIsRequired() throws Exception {
        int databaseSizeBeforeTest = ordersRepository.findAll().size();
        // set the field null
        orders.setCustomerID(null);

        // Create the Orders, which fails.

        restOrdersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orders)))
            .andExpect(status().isBadRequest());

        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = ordersRepository.findAll().size();
        // set the field null
        orders.setPrice(null);

        // Create the Orders, which fails.

        restOrdersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orders)))
            .andExpect(status().isBadRequest());

        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllOrders() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get all the ordersList
        restOrdersMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orders.getId().intValue())))
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

    @Test
    @Transactional
    void getOrders() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        // Get the orders
        restOrdersMockMvc
            .perform(get(ENTITY_API_URL_ID, orders.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(orders.getId().intValue()))
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
    void getNonExistingOrders() throws Exception {
        // Get the orders
        restOrdersMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOrders() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        int databaseSizeBeforeUpdate = ordersRepository.findAll().size();

        // Update the orders
        Orders updatedOrders = ordersRepository.findById(orders.getId()).get();
        // Disconnect from session so that the updates on updatedOrders are not directly saved in db
        em.detach(updatedOrders);
        updatedOrders
            .orderNum(UPDATED_ORDER_NUM)
            .orderDescription(UPDATED_ORDER_DESCRIPTION)
            .deliveryAddress(UPDATED_DELIVERY_ADDRESS)
            .dateOrdered(UPDATED_DATE_ORDERED)
            .dueDate(UPDATED_DUE_DATE)
            .customerID(UPDATED_CUSTOMER_ID)
            .productionTime(UPDATED_PRODUCTION_TIME)
            .productionCost(UPDATED_PRODUCTION_COST)
            .price(UPDATED_PRICE);

        restOrdersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedOrders.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedOrders))
            )
            .andExpect(status().isOk());

        // Validate the Orders in the database
        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeUpdate);
        Orders testOrders = ordersList.get(ordersList.size() - 1);
        assertThat(testOrders.getOrderNum()).isEqualTo(UPDATED_ORDER_NUM);
        assertThat(testOrders.getOrderDescription()).isEqualTo(UPDATED_ORDER_DESCRIPTION);
        assertThat(testOrders.getDeliveryAddress()).isEqualTo(UPDATED_DELIVERY_ADDRESS);
        assertThat(testOrders.getDateOrdered()).isEqualTo(UPDATED_DATE_ORDERED);
        assertThat(testOrders.getDueDate()).isEqualTo(UPDATED_DUE_DATE);
        assertThat(testOrders.getCustomerID()).isEqualTo(UPDATED_CUSTOMER_ID);
        assertThat(testOrders.getProductionTime()).isEqualTo(UPDATED_PRODUCTION_TIME);
        assertThat(testOrders.getProductionCost()).isEqualTo(UPDATED_PRODUCTION_COST);
        assertThat(testOrders.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    @Transactional
    void putNonExistingOrders() throws Exception {
        int databaseSizeBeforeUpdate = ordersRepository.findAll().size();
        orders.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrdersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orders.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orders))
            )
            .andExpect(status().isBadRequest());

        // Validate the Orders in the database
        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrders() throws Exception {
        int databaseSizeBeforeUpdate = ordersRepository.findAll().size();
        orders.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrdersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orders))
            )
            .andExpect(status().isBadRequest());

        // Validate the Orders in the database
        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrders() throws Exception {
        int databaseSizeBeforeUpdate = ordersRepository.findAll().size();
        orders.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrdersMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orders)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Orders in the database
        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOrdersWithPatch() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        int databaseSizeBeforeUpdate = ordersRepository.findAll().size();

        // Update the orders using partial update
        Orders partialUpdatedOrders = new Orders();
        partialUpdatedOrders.setId(orders.getId());

        partialUpdatedOrders
            .orderNum(UPDATED_ORDER_NUM)
            .deliveryAddress(UPDATED_DELIVERY_ADDRESS)
            .dueDate(UPDATED_DUE_DATE)
            .productionTime(UPDATED_PRODUCTION_TIME);

        restOrdersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrders.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrders))
            )
            .andExpect(status().isOk());

        // Validate the Orders in the database
        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeUpdate);
        Orders testOrders = ordersList.get(ordersList.size() - 1);
        assertThat(testOrders.getOrderNum()).isEqualTo(UPDATED_ORDER_NUM);
        assertThat(testOrders.getOrderDescription()).isEqualTo(DEFAULT_ORDER_DESCRIPTION);
        assertThat(testOrders.getDeliveryAddress()).isEqualTo(UPDATED_DELIVERY_ADDRESS);
        assertThat(testOrders.getDateOrdered()).isEqualTo(DEFAULT_DATE_ORDERED);
        assertThat(testOrders.getDueDate()).isEqualTo(UPDATED_DUE_DATE);
        assertThat(testOrders.getCustomerID()).isEqualTo(DEFAULT_CUSTOMER_ID);
        assertThat(testOrders.getProductionTime()).isEqualTo(UPDATED_PRODUCTION_TIME);
        assertThat(testOrders.getProductionCost()).isEqualTo(DEFAULT_PRODUCTION_COST);
        assertThat(testOrders.getPrice()).isEqualTo(DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void fullUpdateOrdersWithPatch() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        int databaseSizeBeforeUpdate = ordersRepository.findAll().size();

        // Update the orders using partial update
        Orders partialUpdatedOrders = new Orders();
        partialUpdatedOrders.setId(orders.getId());

        partialUpdatedOrders
            .orderNum(UPDATED_ORDER_NUM)
            .orderDescription(UPDATED_ORDER_DESCRIPTION)
            .deliveryAddress(UPDATED_DELIVERY_ADDRESS)
            .dateOrdered(UPDATED_DATE_ORDERED)
            .dueDate(UPDATED_DUE_DATE)
            .customerID(UPDATED_CUSTOMER_ID)
            .productionTime(UPDATED_PRODUCTION_TIME)
            .productionCost(UPDATED_PRODUCTION_COST)
            .price(UPDATED_PRICE);

        restOrdersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrders.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrders))
            )
            .andExpect(status().isOk());

        // Validate the Orders in the database
        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeUpdate);
        Orders testOrders = ordersList.get(ordersList.size() - 1);
        assertThat(testOrders.getOrderNum()).isEqualTo(UPDATED_ORDER_NUM);
        assertThat(testOrders.getOrderDescription()).isEqualTo(UPDATED_ORDER_DESCRIPTION);
        assertThat(testOrders.getDeliveryAddress()).isEqualTo(UPDATED_DELIVERY_ADDRESS);
        assertThat(testOrders.getDateOrdered()).isEqualTo(UPDATED_DATE_ORDERED);
        assertThat(testOrders.getDueDate()).isEqualTo(UPDATED_DUE_DATE);
        assertThat(testOrders.getCustomerID()).isEqualTo(UPDATED_CUSTOMER_ID);
        assertThat(testOrders.getProductionTime()).isEqualTo(UPDATED_PRODUCTION_TIME);
        assertThat(testOrders.getProductionCost()).isEqualTo(UPDATED_PRODUCTION_COST);
        assertThat(testOrders.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    @Transactional
    void patchNonExistingOrders() throws Exception {
        int databaseSizeBeforeUpdate = ordersRepository.findAll().size();
        orders.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrdersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, orders.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orders))
            )
            .andExpect(status().isBadRequest());

        // Validate the Orders in the database
        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrders() throws Exception {
        int databaseSizeBeforeUpdate = ordersRepository.findAll().size();
        orders.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrdersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orders))
            )
            .andExpect(status().isBadRequest());

        // Validate the Orders in the database
        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrders() throws Exception {
        int databaseSizeBeforeUpdate = ordersRepository.findAll().size();
        orders.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrdersMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(orders)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Orders in the database
        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOrders() throws Exception {
        // Initialize the database
        ordersRepository.saveAndFlush(orders);

        int databaseSizeBeforeDelete = ordersRepository.findAll().size();

        // Delete the orders
        restOrdersMockMvc
            .perform(delete(ENTITY_API_URL_ID, orders.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Orders> ordersList = ordersRepository.findAll();
        assertThat(ordersList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
