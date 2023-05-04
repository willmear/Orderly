package uk.ac.bham.teamproject.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.ac.bham.teamproject.domain.Target;

/**
 * Spring Data JPA repository for the Target entity.
 */
@Repository
public interface TargetRepository extends JpaRepository<Target, Long> {
    @Query("select target from Target target where target.user.login = ?#{principal.username}")
    List<Target> findByUserIsCurrentUser();

    default Optional<Target> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Target> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Target> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct target from Target target left join fetch target.user",
        countQuery = "select count(distinct target) from Target target"
    )
    Page<Target> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct target from Target target left join fetch target.user")
    List<Target> findAllWithToOneRelationships();

    @Query("select target from Target target left join fetch target.user where target.id =:id")
    Optional<Target> findOneWithToOneRelationships(@Param("id") Long id);
}
