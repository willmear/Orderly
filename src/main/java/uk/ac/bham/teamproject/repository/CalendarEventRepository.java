package uk.ac.bham.teamproject.repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.ac.bham.teamproject.domain.CalendarEvent;
import uk.ac.bham.teamproject.domain.User;

/**
 * Spring Data JPA repository for the CalendarEvent entity.
 */
@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {
    @Query("select calendarEvent from CalendarEvent calendarEvent where calendarEvent.user.login = ?#{principal.username}")
    List<CalendarEvent> findByUserIsCurrentUser();

    default Optional<CalendarEvent> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<CalendarEvent> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<CalendarEvent> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct calendarEvent from CalendarEvent calendarEvent left join fetch calendarEvent.user",
        countQuery = "select count(distinct calendarEvent) from CalendarEvent calendarEvent"
    )
    Page<CalendarEvent> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct calendarEvent from CalendarEvent calendarEvent left join fetch calendarEvent.user")
    List<CalendarEvent> findAllWithToOneRelationships();

    @Query("select calendarEvent from CalendarEvent calendarEvent left join fetch calendarEvent.user where calendarEvent.id =:id")
    Optional<CalendarEvent> findOneWithToOneRelationships(@Param("id") Long id);

    List<CalendarEvent> findTop5ByUserAndStartAfterOrderByStart(User user, ZonedDateTime currentTime);
}
