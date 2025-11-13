package com.hrms.repository;

import com.hrms.entity.PreviousPosition;
import com.hrms.entity.PreviousPosition.EndType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PreviousPositionRepository extends JpaRepository<PreviousPosition, Long> {

    @Query("SELECT p FROM PreviousPosition p WHERE p.personnel.id = :personnelId " +
           "AND p.deleted = false ORDER BY p.endDate DESC")
    List<PreviousPosition> findByPersonnelId(@Param("personnelId") Long personnelId);

    @Query("SELECT p FROM PreviousPosition p WHERE p.personnel.id = :personnelId " +
           "AND p.deleted = false ORDER BY p.endDate DESC")
    Page<PreviousPosition> findByPersonnelId(@Param("personnelId") Long personnelId, Pageable pageable);

    /**
     * Trouve les postes antérieurs dans les 3 dernières années
     */
    @Query("SELECT p FROM PreviousPosition p WHERE p.personnel.id = :personnelId " +
           "AND p.endDate >= :threeYearsAgo AND p.deleted = false ORDER BY p.endDate DESC")
    List<PreviousPosition> findLastThreeYearsByPersonnelId(@Param("personnelId") Long personnelId,
                                                           @Param("threeYearsAgo") LocalDate threeYearsAgo);

    @Query("SELECT p FROM PreviousPosition p WHERE p.personnel.id = :personnelId " +
           "AND p.endType = :endType AND p.deleted = false ORDER BY p.endDate DESC")
    List<PreviousPosition> findByPersonnelIdAndEndType(@Param("personnelId") Long personnelId,
                                                       @Param("endType") EndType endType);

    @Query("SELECT p FROM PreviousPosition p WHERE p.structure.id = :structureId " +
           "AND p.deleted = false ORDER BY p.endDate DESC")
    List<PreviousPosition> findByStructureId(@Param("structureId") Long structureId);

    @Query("SELECT p FROM PreviousPosition p WHERE p.endDate BETWEEN :startDate AND :endDate " +
           "AND p.deleted = false ORDER BY p.endDate DESC")
    List<PreviousPosition> findByDateRange(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);
}

