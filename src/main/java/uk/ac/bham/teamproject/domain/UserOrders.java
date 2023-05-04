package uk.ac.bham.teamproject.domain;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A UserOrders.
 */
@Entity
@Table(name = "user_orders")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserOrders implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "order_num", nullable = false)
    private Long orderNum;

    @NotNull
    @Column(name = "order_description", nullable = false)
    private String orderDescription;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "date_ordered")
    private LocalDate dateOrdered;

    @NotNull
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    private Long customerID;

    @Column(name = "production_time")
    private Integer productionTime;

    @Column(name = "production_cost")
    private Float productionCost;

    @NotNull
    @Column(name = "price", nullable = false)
    private Float price;

    @ManyToOne
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserOrders id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderNum() {
        return this.orderNum;
    }

    public UserOrders orderNum(Long orderNum) {
        this.setOrderNum(orderNum);
        return this;
    }

    public void setOrderNum(Long orderNum) {
        this.orderNum = orderNum;
    }

    public String getOrderDescription() {
        return this.orderDescription;
    }

    public UserOrders orderDescription(String orderDescription) {
        this.setOrderDescription(orderDescription);
        return this;
    }

    public void setOrderDescription(String orderDescription) {
        this.orderDescription = orderDescription;
    }

    public String getDeliveryAddress() {
        return this.deliveryAddress;
    }

    public UserOrders deliveryAddress(String deliveryAddress) {
        this.setDeliveryAddress(deliveryAddress);
        return this;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public LocalDate getDateOrdered() {
        return this.dateOrdered;
    }

    public UserOrders dateOrdered(LocalDate dateOrdered) {
        this.setDateOrdered(dateOrdered);
        return this;
    }

    public void setDateOrdered(LocalDate dateOrdered) {
        this.dateOrdered = dateOrdered;
    }

    public LocalDate getDueDate() {
        return this.dueDate;
    }

    public UserOrders dueDate(LocalDate dueDate) {
        this.setDueDate(dueDate);
        return this;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Long getCustomerID() {
        return this.customerID;
    }

    public UserOrders customerID(Long customerID) {
        this.setCustomerID(customerID);
        return this;
    }

    public void setCustomerID(Long customerID) {
        this.customerID = customerID;
    }

    public Integer getProductionTime() {
        return this.productionTime;
    }

    public UserOrders productionTime(Integer productionTime) {
        this.setProductionTime(productionTime);
        return this;
    }

    public void setProductionTime(Integer productionTime) {
        this.productionTime = productionTime;
    }

    public Float getProductionCost() {
        return this.productionCost;
    }

    public UserOrders productionCost(Float productionCost) {
        this.setProductionCost(productionCost);
        return this;
    }

    public void setProductionCost(Float productionCost) {
        this.productionCost = productionCost;
    }

    public Float getPrice() {
        return this.price;
    }

    public UserOrders price(Float price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserOrders user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserOrders)) {
            return false;
        }
        return id != null && id.equals(((UserOrders) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserOrders{" +
            "id=" + getId() +
            ", orderNum=" + getOrderNum() +
            ", orderDescription='" + getOrderDescription() + "'" +
            ", deliveryAddress='" + getDeliveryAddress() + "'" +
            ", dateOrdered='" + getDateOrdered() + "'" +
            ", dueDate='" + getDueDate() + "'" +
            ", customerID=" + getCustomerID() +
            ", productionTime=" + getProductionTime() +
            ", productionCost=" + getProductionCost() +
            ", price=" + getPrice() +
            "}";
    }
}
