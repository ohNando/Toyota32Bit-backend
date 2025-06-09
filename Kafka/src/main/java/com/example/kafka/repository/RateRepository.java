package com.example.kafka.repository;

import com.example.kafka.entity.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link Rate} entities in the database.
 * <p>
 * Extends {@link JpaRepository} to provide CRUD operations and additional query methods.
 */
@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {
    /**
     * Retrieves a {@link Rate} entity by its rate name.
     *
     * @param rateName the name of the rate to search for
     * @return an {@link Optional} containing the found Rate, or empty if not found
     */
    Optional<Rate> findByRateName(String rateName);
}
