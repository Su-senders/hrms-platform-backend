package com.hrms.repository;

import com.hrms.entity.AdministrativeStructure;
import com.hrms.entity.AdministrativeStructure.StructureType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for AdministrativeStructure entity
 */
@Repository
public interface AdministrativeStructureRepository extends JpaRepository<AdministrativeStructure, Long> {

    // Find by code
    Optional<AdministrativeStructure> findByCode(String code);

    // Check if code exists
    boolean existsByCode(String code);

    // Find by type
    List<AdministrativeStructure> findByTypeAndActiveTrue(StructureType type);

    // Find by parent structure
    @Query("SELECT s FROM AdministrativeStructure s WHERE s.parentStructure.id = :parentId " +
           "AND s.active = true AND s.deleted = false")
    List<AdministrativeStructure> findByParentStructureId(@Param("parentId") Long parentId);

    // Find root structures (no parent)
    @Query("SELECT s FROM AdministrativeStructure s WHERE s.parentStructure IS NULL " +
           "AND s.active = true AND s.deleted = false")
    List<AdministrativeStructure> findRootStructures();

    // Find by level
    List<AdministrativeStructure> findByLevelAndActiveTrue(Integer level);

    // Find by region (for Gouvernorats)
    List<AdministrativeStructure> findByRegionAndActiveTrue(String region);

    // Find all active structures
    List<AdministrativeStructure> findByActiveTrue();

    // Search structures
    @Query("SELECT s FROM AdministrativeStructure s WHERE " +
           "(LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.code) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND s.active = true AND s.deleted = false")
    Page<AdministrativeStructure> searchStructures(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Get structure hierarchy (children)
    @Query("SELECT s FROM AdministrativeStructure s WHERE s.parentStructure.id = :structureId " +
           "AND s.deleted = false ORDER BY s.level, s.name")
    List<AdministrativeStructure> findChildren(@Param("structureId") Long structureId);

    // Get all descendants (recursive)
    @Query(value = "WITH RECURSIVE structure_tree AS ( " +
           "SELECT id, parent_structure_id, name, code, level FROM administrative_structures WHERE id = :structureId " +
           "UNION ALL " +
           "SELECT s.id, s.parent_structure_id, s.name, s.code, s.level " +
           "FROM administrative_structures s " +
           "INNER JOIN structure_tree st ON s.parent_structure_id = st.id " +
           ") SELECT * FROM structure_tree", nativeQuery = true)
    List<AdministrativeStructure> findAllDescendants(@Param("structureId") Long structureId);

    // Count by type
    @Query("SELECT s.type, COUNT(s) FROM AdministrativeStructure s " +
           "WHERE s.active = true AND s.deleted = false GROUP BY s.type")
    List<Object[]> countByType();

    // Count by level
    @Query("SELECT s.level, COUNT(s) FROM AdministrativeStructure s " +
           "WHERE s.active = true AND s.deleted = false GROUP BY s.level")
    List<Object[]> countByLevel();

    // Advanced search
    @Query("SELECT s FROM AdministrativeStructure s WHERE " +
           "(:code IS NULL OR s.code = :code) AND " +
           "(:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:type IS NULL OR s.type = :type) AND " +
           "(:level IS NULL OR s.level = :level) AND " +
           "(:parentId IS NULL OR s.parentStructure.id = :parentId) AND " +
           "(:region IS NULL OR s.region = :region) AND " +
           "s.active = true AND s.deleted = false")
    Page<AdministrativeStructure> advancedSearch(@Param("code") String code,
                                                 @Param("name") String name,
                                                 @Param("type") StructureType type,
                                                 @Param("level") Integer level,
                                                 @Param("parentId") Long parentId,
                                                 @Param("region") String region,
                                                 Pageable pageable);
}
