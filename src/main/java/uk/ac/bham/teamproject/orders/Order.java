package uk.ac.bham.teamproject.orders;

import java.time.LocalDate;
import javax.persistence.*;

@Entity
@Table
public class Order {

    @Id
    @SequenceGenerator(name = "order_sequence", sequenceName = "order_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_sequence")
    private Long orderId;

    private String orderDescription;
    private String deliveryAddress;
    private LocalDate dateOrdered;
    private LocalDate dueDate;
    private Long customerId;
    private Integer productionTime;
    private Float productionCost;
    private Float price;
    private String product;

    //private List<Product> products;

    public Order() {}

    public Order(
        Long orderId,
        String orderDescription,
        String deliveryAddress,
        LocalDate dateOrdered,
        LocalDate dueDate,
        Long customerId,
        Integer productionTime,
        Float productionCost,
        Float price,
        String product
        //List<Product> products
    ) {
        this.orderId = orderId;
        this.orderDescription = orderDescription;
        this.deliveryAddress = deliveryAddress;
        this.dateOrdered = dateOrdered;
        this.dueDate = dueDate;
        this.customerId = customerId;
        this.productionTime = productionTime;
        this.productionCost = productionCost;
        this.price = price;
        this.product = product;
        //this.products = products;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderDescription() {
        return orderDescription;
    }

    public void setOrderDescription(String orderDescription) {
        this.orderDescription = orderDescription;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public LocalDate getDateOrdered() {
        return dateOrdered;
    }

    public void setDateOrdered(LocalDate dateOrdered) {
        this.dateOrdered = dateOrdered;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Integer getProductionTime() {
        return productionTime;
    }

    public void setProductionTime(Integer productionTime) {
        this.productionTime = productionTime;
    }

    public Float getProductionCost() {
        return productionCost;
    }

    public void setProductionCost(Float productionCost) {
        this.productionCost = productionCost;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }
    //public List<Product> getProducts() {
    //return products;
    //}

    //public void setProducts(List<Product> products) {
    //this.products = products;
    //}
}
//class Product {
//
//    private Long productID;
//    private Integer quantity;
//
//    public Product(Long productID, Integer quantity) {
//        this.productID = productID;
//        this.quantity = quantity;
//    }
//
//    public Long getProductID() {
//        return productID;
//    }
//
//    public void setProductID(Long productID) {
//        this.productID = productID;
//    }
//
//    public Integer getQuantity() {
//        return quantity;
//    }
//
//    public void setQuantity(Integer quantity) {
//        this.quantity = quantity;
//    }
//}
