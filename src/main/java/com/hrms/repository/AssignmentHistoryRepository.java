package com.hrms.repository;

import com.hrms.entity.AssignmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository pour l'historique des affectations
 */
@Repository
public interface AssignmentHistoryRepository extends JpaRepository<AssignmentHistory, Long> {

    /**
     * Trouve l'historique des affectations d'un personnel
     */
    List<AssignmentHistory> findByPersonnelIdAndDeletedFalseOrderByStartDateDesc(Long personnelId);

    /**
     * Trouve l'historique des affectations d'un poste
     */
    List<AssignmentHistory> findByPositionNewIdOrPositionOldIdAndDeletedFalseOrderByStartDateDesc(
        Long positionNewId, Long positionOldId);

    /**
     * Trouve l'historique des affectations d'une structure
     */
    List<AssignmentHistory> findByStructureNewIdOrStructureOldIdAndDeletedFalseOrderByStartDateDesc(
        Long structureNewId, Long structureOldId);

    /**
     * Trouve l'affectation active d'un personnel
     */
    @Query("SELECT ah FROM AssignmentHistory ah WHERE ah.personnel.id = :personnelId " +
           "AND ah.status = 'ACTIVE' AND ah.endDate IS NULL AND ah.deleted = false")
    AssignmentHistory findActiveAssignmentByPersonnel(@Param("personnelId") Long personnelId);

    /**
     * Trouve les affectations par type de mouvement
     */
    List<AssignmentHistory> findByMovementTypeAndDeletedFalseOrderByStartDateDesc(
        AssignmentHistory.MovementType movementType);

    /**
     * Trouve les affectations par période
     */
    List<AssignmentHistory> findByStartDateBetweenAndDeletedFalseOrderByStartDateDesc(
        LocalDate startDate, LocalDate endDate);

    /**
     * Compte les affectations d'un personnel
     */
    long countByPersonnelIdAndDeletedFalse(Long personnelId);

    /**
     * Trouve les affectations actives d'une structure
     */
    @Query("SELECT ah FROM AssignmentHistory ah WHERE ah.structureNew.id = :structureId " +
           "AND ah.status = 'ACTIVE' AND ah.endDate IS NULL AND ah.deleted = false")
    List<AssignmentHistory> findActiveAssignmentsByStructure(@Param("structureId") Long structureId);
}
