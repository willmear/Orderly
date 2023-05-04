package uk.ac.bham.teamproject.domain;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A GoogleCalendarAccessToken.
 */
@Entity
@Table(name = "google_calendar_access_token")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GoogleCalendarAccessToken implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 2048)
    @Column(name = "encrypted_token", length = 2048, nullable = false)
    private String encryptedToken;

    @NotNull
    @Column(name = "expires", nullable = false)
    private Instant expires;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public GoogleCalendarAccessToken id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEncryptedToken() {
        return this.encryptedToken;
    }

    public GoogleCalendarAccessToken encryptedToken(String encryptedToken) {
        this.setEncryptedToken(encryptedToken);
        return this;
    }

    public void setEncryptedToken(String encryptedToken) {
        this.encryptedToken = encryptedToken;
    }

    public Instant getExpires() {
        return this.expires;
    }

    public GoogleCalendarAccessToken expires(Instant expires) {
        this.setExpires(expires);
        return this;
    }

    public void setExpires(Instant expires) {
        this.expires = expires;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public GoogleCalendarAccessToken user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GoogleCalendarAccessToken)) {
            return false;
        }
        return id != null && id.equals(((GoogleCalendarAccessToken) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GoogleCalendarAccessToken{" +
            "id=" + getId() +
            ", encryptedToken='" + getEncryptedToken() + "'" +
            ", expires='" + getExpires() + "'" +
            "}";
    }
}
