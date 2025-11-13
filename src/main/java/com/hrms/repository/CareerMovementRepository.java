package com.hrms.repository;

import com.hrms.entity.CareerMovement;
import com.hrms.entity.CareerMovement.MovementStatus;
import com.hrms.entity.CareerMovement.MovementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for CareerMovement entity
 */
@Repository
public interface CareerMovementRepository extends JpaRepository<CareerMovement, Long> {

    // Find by personnel
    @Query("SELECT cm FROM CareerMovement cm WHERE cm.personnel.id = :personnelId " +
           "AND cm.deleted = false ORDER BY cm.movementDate DESC")
    List<CareerMovement> findByPersonnelId(@Param("personnelId") Long personnelId);

    // Find by personnel with pagination
    @Query("SELECT cm FROM CareerMovement cm WHERE cm.personnel.id = :personnelId " +
           "AND cm.deleted = false")
    Page<CareerMovement> findByPersonnelId(@Param("personnelId") Long personnelId, Pageable pageable);

    // Find by movement type
    Page<CareerMovement> findByMovementTypeAndDeletedFalse(MovementType movementType, Pageable pageable);

    // Find by status
    Page<CareerMovement> findByStatusAndDeletedFalse(MovementStatus status, Pageable pageable);

    // Find pending movements
    @Query("SELECT cm FROM CareerMovement cm WHERE cm.status = 'PENDING' AND cm.deleted = false")
    List<CareerMovement> findPendingMovements();

    // Find movements by date range
    @Query("SELECT cm FROM CareerMovement cm WHERE cm.movementDate BETWEEN :startDate AND :endDate " +
           "AND cm.deleted = false ORDER BY cm.movementDate DESC")
    List<CareerMovement> findByDateRange(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);

    // Find movements by structure
    @Query("SELECT cm FROM CareerMovement cm WHERE " +
           "(cm.sourceStructure.id = :structureId OR cm.destinationStructure.id = :structureId) " +
           "AND cm.deleted = false")
    Page<CareerMovement> findByStructureId(@Param("structureId") Long structureId, Pageable pageable);

    // Find movements by position
    @Query("SELECT cm FROM CareerMovement cm WHERE " +
           "(cm.sourcePosition.id = :positionId OR cm.destinationPosition.id = :positionId) " +
           "AND cm.deleted = false")
    List<CareerMovement> findByPositionId(@Param("positionId") Long positionId);

    // Count by movement type
    @Query("SELECT cm.movementType, COUNT(cm) FROM CareerMovement cm " +
           "WHERE cm.deleted = false GROUP BY cm.movementType")
    List<Object[]> countByMovementType();

    // Count by status
    @Query("SELECT cm.status, COUNT(cm) FROM CareerMovement cm " +
           "WHERE cm.deleted = false GROUP BY cm.status")
    List<Object[]> countByStatus();

    // Find latest movement for personnel
    @Query("SELECT cm FROM CareerMovement cm WHERE cm.personnel.id = :personnelId " +
           "AND cm.deleted = false ORDER BY cm.movementDate DESC, cm.createdAt DESC")
    List<CareerMovement> findLatestByPersonnelId(@Param("personnelId") Long personnelId);

    // Check if personnel has pending movement
    @Query("SELECT COUNT(cm) > 0 FROM CareerMovement cm WHERE cm.personnel.id = :personnelId " +
           "AND cm.status = 'PENDING' AND cm.deleted = false")
    boolean hasPendingMovement(@Param("personnelId") Long personnelId);

    // Find temporary movements (detachment, formation, etc.) expiring soon
    @Query("SELECT cm FROM CareerMovement cm WHERE cm.endDate IS NOT NULL " +
           "AND cm.endDate BETWEEN :startDate AND :endDate " +
           "AND cm.status = 'EXECUTED' AND cm.deleted = false")
    List<CareerMovement> findExpiringSoon(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    // Advanced search
    @Query("SELECT cm FROM CareerMovement cm WHERE " +
           "(:personnelId IS NULL OR cm.personnel.id = :personnelId) AND " +
           "(:movementType IS NULL OR cm.movementType = :movementType) AND " +
           "(:status IS NULL OR cm.status = :status) AND " +
           "(:startDate IS NULL OR cm.movementDate >= :startDate) AND " +
           "(:endDate IS NULL OR cm.movementDate <= :endDate) AND " +
           "(:structureId IS NULL OR cm.sourceStructure.id = :structureId OR cm.destinationStructure.id = :structureId) AND " +
           "cm.deleted = false")
    Page<CareerMovement> advancedSearch(@Param("personnelId") Long personnelId,
                                       @Param("movementType") MovementType movementType,
                                       @Param("status") MovementStatus status,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate,
                                       @Param("structureId") Long structureId,
                                       Pageable pageable);
}
