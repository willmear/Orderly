package uk.ac.bham.teamproject.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;
import uk.ac.bham.teamproject.domain.UserOrders;
import uk.ac.bham.teamproject.repository.UserOrdersRepository;
import uk.ac.bham.teamproject.repository.UserRepository;
import uk.ac.bham.teamproject.security.SecurityUtils;
import uk.ac.bham.teamproject.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link uk.ac.bham.teamproject.domain.UserOrders}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class UserOrdersResource {

    private final Logger log = LoggerFactory.getLogger(UserOrdersResource.class);

    private static final String ENTITY_NAME = "userOrdersUserOrders";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserOrdersRepository userOrdersRepository;

    private final UserRepository userRepository;

    public UserOrdersResource(UserOrdersRepository userOrdersRepository, UserRepository userRepository) {
        this.userOrdersRepository = userOrdersRepository;
        this.userRepository = userRepository;
    }

    /**
     * {@code POST  /user-orders} : Create a new userOrders.
     *
     * @param userOrders the userOrders to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userOrders, or with status {@code 400 (Bad Request)} if the userOrders has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-orders")
    public ResponseEntity<UserOrders> createUserOrders(@Valid @RequestBody UserOrders userOrders) throws URISyntaxException {
        log.debug("REST request to save UserOrders : {}", userOrders);
        if (userOrders.getId() != null) {
            throw new BadRequestAlertException("A new userOrders cannot already have an ID", ENTITY_NAME, "idexists");
        }

        userOrders.setUser(
            userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().map(Object::toString).orElse("")).orElse(null)
        );

        UserOrders result = userOrdersRepository.save(userOrders);
        return ResponseEntity
            .created(new URI("/api/user-orders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-orders/:id} : Updates an existing userOrders.
     *
     * @param id the id of the userOrders to save.
     * @param userOrders the userOrders to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userOrders,
     * or with status {@code 400 (Bad Request)} if the userOrders is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userOrders couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-orders/{id}")
    public ResponseEntity<UserOrders> updateUserOrders(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserOrders userOrders
    ) throws URISyntaxException {
        log.debug("REST request to update UserOrders : {}, {}", id, userOrders);
        if (userOrders.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userOrders.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userOrdersRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UserOrders result = userOrdersRepository.save(userOrders);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userOrders.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /user-orders/:id} : Partial updates given fields of an existing userOrders, field will ignore if it is null
     *
     * @param id the id of the userOrders to save.
     * @param userOrders the userOrders to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userOrders,
     * or with status {@code 400 (Bad Request)} if the userOrders is not valid,
     * or with status {@code 404 (Not Found)} if the userOrders is not found,
     * or with status {@code 500 (Internal Server Error)} if the userOrders couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/user-orders/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserOrders> partialUpdateUserOrders(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserOrders userOrders
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserOrders partially : {}, {}", id, userOrders);
        if (userOrders.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userOrders.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userOrdersRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserOrders> result = userOrdersRepository
            .findById(userOrders.getId())
            .map(existingUserOrders -> {
                if (userOrders.getOrderNum() != null) {
                    existingUserOrders.setOrderNum(userOrders.getOrderNum());
                }
                if (userOrders.getOrderDescription() != null) {
                    existingUserOrders.setOrderDescription(userOrders.getOrderDescription());
                }
                if (userOrders.getDeliveryAddress() != null) {
                    existingUserOrders.setDeliveryAddress(userOrders.getDeliveryAddress());
                }
                if (userOrders.getDateOrdered() != null) {
                    existingUserOrders.setDateOrdered(userOrders.getDateOrdered());
                }
                if (userOrders.getDueDate() != null) {
                    existingUserOrders.setDueDate(userOrders.getDueDate());
                }
                if (userOrders.getCustomerID() != null) {
                    existingUserOrders.setCustomerID(userOrders.getCustomerID());
                }
                if (userOrders.getProductionTime() != null) {
                    existingUserOrders.setProductionTime(userOrders.getProductionTime());
                }
                if (userOrders.getProductionCost() != null) {
                    existingUserOrders.setProductionCost(userOrders.getProductionCost());
                }
                if (userOrders.getPrice() != null) {
                    existingUserOrders.setPrice(userOrders.getPrice());
                }

                return existingUserOrders;
            })
            .map(userOrdersRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userOrders.getId().toString())
        );
    }

    /**
     * {@code GET  /user-orders} : get all the userOrders.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userOrders in body.
     */
    @GetMapping("/user-orders")
    public List<UserOrders> getAllUserOrders(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all UserOrders");
        if (SecurityUtils.hasCurrentUserThisAuthority("ROLE_ADMIN")) {
            if (eagerload) {
                return userOrdersRepository.findAllWithEagerRelationships();
            } else {
                return userOrdersRepository.findAll();
            }
        } else {
            return userOrdersRepository.findByUserIsCurrentUser();
        }
    }

    /**
     * {@code GET  /user-orders/:id} : get the "id" userOrders.
     *
     * @param id the id of the userOrders to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userOrders, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-orders/{id}")
    public ResponseEntity<UserOrders> getUserOrders(@PathVariable Long id) {
        log.debug("REST request to get UserOrders : {}", id);
        Optional<UserOrders> userOrders = userOrdersRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(userOrders);
    }

    /**
     * {@code DELETE  /user-orders/:id} : delete the "id" userOrders.
     *
     * @param id the id of the userOrders to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-orders/{id}")
    public ResponseEntity<Void> deleteUserOrders(@PathVariable Long id) {
        log.debug("REST request to delete UserOrders : {}", id);
        userOrdersRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /*
     * FOR FINANCES PAGE
     */
    /**
     * GET countByProduct
     */
    @GetMapping("/user-orders/count-by-product")
    public Map<String, Long> countByProduct() {
        Map<String, Long> result = new HashMap<>();
        List<Object[]> counts = userOrdersRepository.countByUserDescription();
        for (Object[] row : counts) {
            result.put((String) row[0], (Long) row[1]);
        }
        return result;
    }

    /**
     * GET countByProductOneMonth
     */
    @GetMapping("/user-orders/count-by-product-one-month")
    public Map<String, Long> countByProductOneMonth() {
        Map<String, Long> result = new HashMap<>();
        LocalDate startDate = LocalDate.now().minusMonths(1);
        List<Object[]> counts = userOrdersRepository.countByUserDescriptionOneMonth(startDate);
        for (Object[] row : counts) {
            result.put((String) row[0], (Long) row[1]);
        }
        return result;
    }

    /**
     * GET countByProductSixMonths
     */
    @GetMapping("/user-orders/count-by-product-six-months")
    public Map<String, Long> countByProductSixMonths() {
        Map<String, Long> result = new HashMap<>();
        LocalDate startDate = LocalDate.now().minusMonths(6);
        List<Object[]> counts = userOrdersRepository.countByUserDescriptionSixMonths(startDate);
        for (Object[] row : counts) {
            result.put((String) row[0], (Long) row[1]);
        }
        return result;
    }

    /**
     * GET countByProductOneYear
     */
    @GetMapping("/user-orders/count-by-product-one-year")
    public Map<String, Long> countByProductOneYear() {
        Map<String, Long> result = new HashMap<>();
        LocalDate startDate = LocalDate.now().minusMonths(12);
        List<Object[]> counts = userOrdersRepository.countByUserDescriptionOneYear(startDate);
        for (Object[] row : counts) {
            result.put((String) row[0], (Long) row[1]);
        }
        return result;
    }

    /**
     * GET revenueByProduct
     */
    @GetMapping("/user-orders/revenue-by-product")
    public Map<String, Double> revenueByProduct() {
        Map<String, Double> result = new HashMap<>();
        List<Object[]> revenue = userOrdersRepository.revenueByProduct();
        for (Object[] row : revenue) {
            result.put((String) row[0], (Double) row[1]);
        }
        return result;
    }

    /**
     * GET revenueByProductOneMonth
     */
    @GetMapping("/user-orders/revenue-by-product-one-month")
    public Map<String, Double> revenueByProductOneMonth() {
        Map<String, Double> result = new HashMap<>();
        LocalDate startDate = LocalDate.now().minusMonths(1);
        List<Object[]> revenue = userOrdersRepository.revenueByProductOneMonth(startDate);
        for (Object[] row : revenue) {
            result.put((String) row[0], (Double) row[1]);
        }
        return result;
    }

    /**
     * GET revenueByProductSixMonths
     */
    @GetMapping("/user-orders/revenue-by-product-six-months")
    public Map<String, Double> revenueByProductSixMonths() {
        Map<String, Double> result = new HashMap<>();
        LocalDate startDate = LocalDate.now().minusMonths(6);
        List<Object[]> revenue = userOrdersRepository.revenueByProductSixMonths(startDate);
        for (Object[] row : revenue) {
            result.put((String) row[0], (Double) row[1]);
        }
        return result;
    }

    /**
     * GET revenueByProductOneYear
     */
    @GetMapping("/user-orders/revenue-by-product-one-year")
    public Map<String, Double> revenueByProductOneYear() {
        Map<String, Double> result = new HashMap<>();
        LocalDate startDate = LocalDate.now().minusMonths(12);
        List<Object[]> revenue = userOrdersRepository.revenueByProductOneYear(startDate);
        for (Object[] row : revenue) {
            result.put((String) row[0], (Double) row[1]);
        }
        return result;
    }

    /**
     * GET revenueByMonth
     */
    @GetMapping("/user-orders/revenue-by-month")
    public Map<Month, Double> revenueByMonth() {
        Map<Month, Double> result = new EnumMap<>(Month.class);
        List<Object[]> revenue = userOrdersRepository.revenueByMonth();
        for (Object[] row : revenue) {
            LocalDate fromObj = (LocalDate) row[0];

            result.put((Month) fromObj.getMonth(), (Double) row[1]);
        }
        return result;
    }

    /**
     * GET revenueAndLoss
     */

    @GetMapping("/user-orders/revenue-and-loss")
    public Map<Month, List<Double>> revenueAndLoss() {
        Map<Month, List<Double>> result = new EnumMap<>(Month.class);
        List<Double> values = new ArrayList<>();
        List<Object[]> revenueAndLoss = userOrdersRepository.revenueAndLoss();
        for (Object[] row : revenueAndLoss) {
            LocalDate fromObj = (LocalDate) row[0];
            values.add((Double) row[1]);
            values.add((Double) row[2]);
            result.put((Month) fromObj.getMonth(), values);
        }
        return result;
    }
}
