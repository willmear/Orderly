package uk.ac.bham.teamproject.repository;

import uk.ac.bham.teamproject.domain.Inventory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Inventory entity.
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query("select inventory from Inventory inventory where inventory.user.login = ?#{principal.username}")
    List<Inventory> findByUserIsCurrentUser();

    default Optional<Inventory> findOneWithEagerRelationships(Long id) {
        return 
            this.findOneWithToOneRelationships(id)
    ;
    }

    default List<Inventory> findAllWithEagerRelationships() {
        return 
            this.findAllWithToOneRelationships()
    ;
    }

    default Page<Inventory> findAllWithEagerRelationships(Pageable pageable) {
        return 
            this.findAllWithToOneRelationships(pageable)
    ;
    }

    @Query(value = "select distinct inventory from Inventory inventory left join fetch inventory.user",
        countQuery = "select count(distinct inventory) from Inventory inventory")
    Page<Inventory> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct inventory from Inventory inventory left join fetch inventory.user")
    List<Inventory> findAllWithToOneRelationships();

    @Query("select inventory from Inventory inventory left join fetch inventory.user where inventory.id =:id")
    Optional<Inventory> findOneWithToOneRelationships(@Param("id") Long id);
}
