package com.hrms.repository;

import com.hrms.entity.Position;
import com.hrms.entity.Position.PositionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Position entity
 */
@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {

    // Find by code
    Optional<Position> findByCode(String code);

    // Check if code exists
    boolean existsByCode(String code);

    // Find by structure
    @Query("SELECT p FROM Position p WHERE p.structure.id = :structureId AND p.deleted = false")
    Page<Position> findByStructureId(@Param("structureId") Long structureId, Pageable pageable);

    // Find by status
    Page<Position> findByStatusAndDeletedFalse(PositionStatus status, Pageable pageable);

    // Find vacant positions
    @Query("SELECT p FROM Position p WHERE p.status = 'VACANT' AND p.active = true AND p.deleted = false")
    List<Position> findVacantPositions();

    // Find vacant positions by structure
    @Query("SELECT p FROM Position p WHERE p.structure.id = :structureId " +
           "AND p.status = 'VACANT' AND p.active = true AND p.deleted = false")
    List<Position> findVacantPositionsByStructure(@Param("structureId") Long structureId);

    // Find occupied positions
    @Query("SELECT p FROM Position p WHERE p.status = 'OCCUPE' AND p.deleted = false")
    List<Position> findOccupiedPositions();

    // Find by rank
    Page<Position> findByRankAndDeletedFalse(String rank, Pageable pageable);

    // Find by category
    Page<Position> findByCategoryAndDeletedFalse(String category, Pageable pageable);

    // Count by structure
    @Query("SELECT COUNT(p) FROM Position p WHERE p.structure.id = :structureId AND p.deleted = false")
    long countByStructureId(@Param("structureId") Long structureId);

    // Count vacant positions by structure
    @Query("SELECT COUNT(p) FROM Position p WHERE p.structure.id = :structureId " +
           "AND p.status = 'VACANT' AND p.active = true AND p.deleted = false")
    long countVacantByStructureId(@Param("structureId") Long structureId);

    // Count occupied positions by structure
    @Query("SELECT COUNT(p) FROM Position p WHERE p.structure.id = :structureId " +
           "AND p.status = 'OCCUPE' AND p.deleted = false")
    long countOccupiedByStructureId(@Param("structureId") Long structureId);

    // Statistics by status
    @Query("SELECT p.status, COUNT(p) FROM Position p WHERE p.deleted = false GROUP BY p.status")
    List<Object[]> countByStatus();

    // Search positions
    @Query("SELECT p FROM Position p WHERE " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.code) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND p.deleted = false")
    Page<Position> searchPositions(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Advanced search
    @Query("SELECT p FROM Position p WHERE " +
           "(:code IS NULL OR p.code = :code) AND " +
           "(:title IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:rank IS NULL OR p.rank = :rank) AND " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:structureId IS NULL OR p.structure.id = :structureId) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "p.deleted = false")
    Page<Position> advancedSearch(@Param("code") String code,
                                  @Param("title") String title,
                                  @Param("rank") String rank,
                                  @Param("category") String category,
                                  @Param("structureId") Long structureId,
                                  @Param("status") PositionStatus status,
                                  Pageable pageable);
}
