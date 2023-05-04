package uk.ac.bham.teamproject.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.ac.bham.teamproject.domain.Targets;

/**
 * Spring Data JPA repository for the Targets entity.
 */
@Repository
public interface TargetsRepository extends JpaRepository<Targets, Long> {
    @Query("select targets from Targets targets where targets.user.login = ?#{principal.username}")
    List<Targets> findByUserIsCurrentUser();

    default Optional<Targets> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Targets> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Targets> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct targets from Targets targets left join fetch targets.user",
        countQuery = "select count(distinct targets) from Targets targets"
    )
    Page<Targets> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct targets from Targets targets left join fetch targets.user")
    List<Targets> findAllWithToOneRelationships();

    @Query("select targets from Targets targets left join fetch targets.user where targets.id =:id")
    Optional<Targets> findOneWithToOneRelationships(@Param("id") Long id);
}
