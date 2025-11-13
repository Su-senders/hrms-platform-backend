package com.hrms.repository;

import com.hrms.entity.PersonnelLeave;
import com.hrms.entity.PersonnelLeave.LeaveReason;
import com.hrms.entity.PersonnelLeave.LeaveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PersonnelLeaveRepository extends JpaRepository<PersonnelLeave, Long> {

    @Query("SELECT l FROM PersonnelLeave l WHERE l.personnel.id = :personnelId " +
           "AND l.deleted = false ORDER BY l.effectiveDate DESC")
    List<PersonnelLeave> findByPersonnelId(@Param("personnelId") Long personnelId);

    @Query("SELECT l FROM PersonnelLeave l WHERE l.personnel.id = :personnelId " +
           "AND l.deleted = false ORDER BY l.effectiveDate DESC")
    Page<PersonnelLeave> findByPersonnelId(@Param("personnelId") Long personnelId, Pageable pageable);

    @Query("SELECT l FROM PersonnelLeave l WHERE l.personnel.id = :personnelId " +
           "AND l.leaveReason = :reason AND l.deleted = false ORDER BY l.effectiveDate DESC")
    List<PersonnelLeave> findByPersonnelIdAndReason(@Param("personnelId") Long personnelId,
                                                     @Param("reason") LeaveReason reason);

    @Query("SELECT l FROM PersonnelLeave l WHERE l.personnel.id = :personnelId " +
           "AND l.status = :status AND l.deleted = false ORDER BY l.effectiveDate DESC")
    List<PersonnelLeave> findByPersonnelIdAndStatus(@Param("personnelId") Long personnelId,
                                                    @Param("status") LeaveStatus status);

    @Query("SELECT l FROM PersonnelLeave l WHERE l.effectiveDate BETWEEN :startDate AND :endDate " +
           "AND l.deleted = false ORDER BY l.effectiveDate DESC")
    List<PersonnelLeave> findByDateRange(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    @Query("SELECT l FROM PersonnelLeave l WHERE l.status = 'IN_PROGRESS' " +
           "AND l.deleted = false ORDER BY l.effectiveDate DESC")
    List<PersonnelLeave> findInProgressLeaves();

    @Query("SELECT l FROM PersonnelLeave l WHERE l.effectiveDate <= :date AND l.expiryDate >= :date " +
           "AND l.deleted = false ORDER BY l.effectiveDate DESC")
    List<PersonnelLeave> findActiveLeavesOnDate(@Param("date") LocalDate date);
}

