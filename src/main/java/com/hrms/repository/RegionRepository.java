package com.hrms.repository;

import com.hrms.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

    Optional<Region> findByCode(String code);

    Optional<Region> findByName(String name);

    boolean existsByCode(String code);

    boolean existsByName(String name);

    List<Region> findByActiveTrue();

    @Query("SELECT r FROM Region r WHERE r.active = true ORDER BY r.name")
    List<Region> findAllActiveOrderByName();

    @Query("SELECT r FROM Region r LEFT JOIN FETCH r.gouvernorat WHERE r.id = :id")
    Optional<Region> findByIdWithGouvernorat(Long id);

    @Query("SELECT r FROM Region r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(r.chefLieu) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Region> searchRegions(String searchTerm);
}
