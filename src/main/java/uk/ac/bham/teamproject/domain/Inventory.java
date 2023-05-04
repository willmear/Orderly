package uk.ac.bham.teamproject.domain;

import javax.validation.constraints.*;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;


/**
 * A Inventory.
 */
@Entity
@Table(name = "inventory")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Inventory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @Column(name = "status", nullable = false)
    private String status;


    @ManyToOne
    private User user;


    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Inventory id(Long id) {
        this.setId(id);
        return this;
    }

        public void setId(Long id) {
        this.id = id;
    }
  
    public String getName() {
        return this.name;
    }

    public Inventory name(String name) {
        this.setName(name);
        return this;
    }

        public void setName(String name) {
        this.name = name;
    }
  
    public Integer getQuantity() {
        return this.quantity;
    }

    public Inventory quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

        public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
  
    public String getStatus() {
        return this.status;
    }

    public Inventory status(String status) {
        this.setStatus(status);
        return this;
    }

        public void setStatus(String status) {
        this.status = status;
    }
  
    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Inventory user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Inventory)) {
            return false;
        }
        return id != null && id.equals(((Inventory) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Inventory{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", quantity=" + getQuantity() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
