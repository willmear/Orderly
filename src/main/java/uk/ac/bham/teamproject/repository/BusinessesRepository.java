package uk.ac.bham.teamproject.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uk.ac.bham.teamproject.domain.Businesses;

/**
 * Spring Data JPA repository for the Businesses entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BusinessesRepository extends JpaRepository<Businesses, Long> {}
