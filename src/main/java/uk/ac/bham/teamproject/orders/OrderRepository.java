package uk.ac.bham.teamproject.orders;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // What this does: Retrieves number of each product sold. For Finance page.
    @Query(value = "SELECT product, COUNT(*) as count FROM order GROUP_BY product", nativeQuery = true)
    List<Object[]> countByProduct();
}
