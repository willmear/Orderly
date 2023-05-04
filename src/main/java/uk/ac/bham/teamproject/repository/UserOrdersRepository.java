package uk.ac.bham.teamproject.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.ac.bham.teamproject.domain.UserOrders;

/**
 * Spring Data JPA repository for the UserOrders entity.
 */
@Repository
public interface UserOrdersRepository extends JpaRepository<UserOrders, Long> {
    @Query("select userOrders from UserOrders userOrders where userOrders.user.login = ?#{principal.username}")
    List<UserOrders> findByUserIsCurrentUser();

    default Optional<UserOrders> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<UserOrders> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<UserOrders> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct userOrders from UserOrders userOrders left join fetch userOrders.user",
        countQuery = "select count(distinct userOrders) from UserOrders userOrders"
    )
    Page<UserOrders> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct userOrders from UserOrders userOrders left join fetch userOrders.user")
    List<UserOrders> findAllWithToOneRelationships();

    @Query("select userOrders from UserOrders userOrders left join fetch userOrders.user where userOrders.id =:id")
    Optional<UserOrders> findOneWithToOneRelationships(@Param("id") Long id);

    /*
     * FOR FINANCES PAGE
     */
    @Query(
        "select u.orderDescription, count(*) as count FROM UserOrders u where u.user.login = ?#{principal.username} group by u.orderDescription"
    )
    List<Object[]> countByUserDescription();

    @Query(
        "select u.orderDescription, count(*) as count FROM UserOrders u where u.user.login = ?#{principal.username} and u.dateOrdered > :startDate group by u.orderDescription"
    )
    List<Object[]> countByUserDescriptionOneMonth(@Param("startDate") LocalDate starDate);

    @Query(
        "select u.orderDescription, count(*) as count FROM UserOrders u where u.user.login = ?#{principal.username} and u.dateOrdered > :startDate group by u.orderDescription"
    )
    List<Object[]> countByUserDescriptionSixMonths(@Param("startDate") LocalDate starDate);

    @Query(
        "select u.orderDescription, count(*) as count FROM UserOrders u where u.user.login = ?#{principal.username} and u.dateOrdered > :startDate group by u.orderDescription"
    )
    List<Object[]> countByUserDescriptionOneYear(@Param("startDate") LocalDate starDate);

    @Query(
        "select u.orderDescription, sum(u.price) as sum from UserOrders u where u.user.login = ?#{principal.username} group by u.orderDescription"
    )
    List<Object[]> revenueByProduct();

    @Query(
        "select u.orderDescription, sum(u.price) as sum from UserOrders u where u.user.login = ?#{principal.username} and u.dateOrdered > :startDate group by u.orderDescription"
    )
    List<Object[]> revenueByProductOneMonth(@Param("startDate") LocalDate starDate);

    @Query(
        "select u.orderDescription, sum(u.price) as sum from UserOrders u where u.user.login = ?#{principal.username} and u.dateOrdered > :startDate group by u.orderDescription"
    )
    List<Object[]> revenueByProductSixMonths(@Param("startDate") LocalDate starDate);

    @Query(
        "select u.orderDescription, sum(u.price) as sum from UserOrders u where u.user.login = ?#{principal.username} and u.dateOrdered > :startDate group by u.orderDescription"
    )
    List<Object[]> revenueByProductOneYear(@Param("startDate") LocalDate starDate);

    @Query("select u.dateOrdered, sum(u.price) as sum from UserOrders u where u.user.login = ?#{principal.username} group by u.dateOrdered")
    List<Object[]> revenueByMonth();

    @Query(
        "select u.dateOrdered, sum(u.price) as priceSum, sum(productionCost) as costSum from UserOrders u where u.user.login = ?#{principal.username} group by u.dateOrdered"
    )
    List<Object[]> revenueAndLoss();
}
