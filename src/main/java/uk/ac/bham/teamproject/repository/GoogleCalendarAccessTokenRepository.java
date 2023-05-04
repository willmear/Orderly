package uk.ac.bham.teamproject.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.ac.bham.teamproject.domain.GoogleCalendarAccessToken;
import uk.ac.bham.teamproject.domain.User;

/**
 * Spring Data JPA repository for the GoogleCalendarAccessToken entity.
 */
@Repository
public interface GoogleCalendarAccessTokenRepository extends JpaRepository<GoogleCalendarAccessToken, Long> {
    default Optional<GoogleCalendarAccessToken> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<GoogleCalendarAccessToken> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<GoogleCalendarAccessToken> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct googleCalendarAccessToken from GoogleCalendarAccessToken googleCalendarAccessToken left join fetch googleCalendarAccessToken.user",
        countQuery = "select count(distinct googleCalendarAccessToken) from GoogleCalendarAccessToken googleCalendarAccessToken"
    )
    Page<GoogleCalendarAccessToken> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct googleCalendarAccessToken from GoogleCalendarAccessToken googleCalendarAccessToken left join fetch googleCalendarAccessToken.user"
    )
    List<GoogleCalendarAccessToken> findAllWithToOneRelationships();

    @Query(
        "select googleCalendarAccessToken from GoogleCalendarAccessToken googleCalendarAccessToken left join fetch googleCalendarAccessToken.user where googleCalendarAccessToken.id =:id"
    )
    Optional<GoogleCalendarAccessToken> findOneWithToOneRelationships(@Param("id") Long id);

    Optional<GoogleCalendarAccessToken> findGoogleCalendarAccessTokenByUser(User user);
}
