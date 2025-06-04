package com.toyotabackend.mainplatform.Repository;

import com.toyotabackend.mainplatform.Entity.Rate;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on the {@link Rate} entity.
 * <p>
 * This interface extends {@link JpaRepository}, providing built-in methods for saving,
 * updating, deleting, and querying {@link Rate} entities in the PostgreSQL database.
 */
@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {
    Optional<Rate> findTopByOrderByDbUpdateTimeDesc();
    Optional<Rate> findByRateName(String rateName);
}
