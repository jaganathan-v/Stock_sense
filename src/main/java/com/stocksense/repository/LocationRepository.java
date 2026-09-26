package com.stocksense.repository;

import com.stocksense.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByName(String name);
    Optional<Location> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
    List<Location> findAllByOrderByNameAsc();
}
