package com.hrms.repository;

import com.hrms.entity.Personnel;
import com.hrms.entity.ProfessionalTraining;
import com.hrms.entity.ProfessionalTraining.TrainingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProfessionalTrainingRepository extends JpaRepository<ProfessionalTraining, Long> {

    @Query("SELECT t FROM ProfessionalTraining t WHERE t.personnel.id = :personnelId " +
           "AND t.deleted = false ORDER BY t.startDate DESC")
    List<ProfessionalTraining> findByPersonnelId(@Param("personnelId") Long personnelId);

    @Query("SELECT t FROM ProfessionalTraining t WHERE t.personnel.id = :personnelId " +
           "AND t.deleted = false ORDER BY t.startDate DESC")
    Page<ProfessionalTraining> findByPersonnelId(@Param("personnelId") Long personnelId, Pageable pageable);

    @Query("SELECT t FROM ProfessionalTraining t WHERE t.personnel.id = :personnelId " +
           "AND t.status = :status AND t.deleted = false ORDER BY t.startDate DESC")
    List<ProfessionalTraining> findByPersonnelIdAndStatus(@Param("personnelId") Long personnelId,
                                                          @Param("status") TrainingStatus status);

    @Query("SELECT t FROM ProfessionalTraining t WHERE t.trainingField LIKE %:field% " +
           "AND t.deleted = false ORDER BY t.startDate DESC")
    List<ProfessionalTraining> findByTrainingField(@Param("field") String field);

    @Query("SELECT t FROM ProfessionalTraining t WHERE t.startDate BETWEEN :startDate AND :endDate " +
           "AND t.deleted = false ORDER BY t.startDate DESC")
    List<ProfessionalTraining> findByDateRange(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    @Query("SELECT t FROM ProfessionalTraining t WHERE t.status = 'IN_PROGRESS' " +
           "AND t.deleted = false ORDER BY t.startDate DESC")
    List<ProfessionalTraining> findInProgressTrainings();

    /**
     * Vérifie si un ProfessionalTraining existe déjà pour un personnel et des dates données
     * Utile pour éviter les doublons lors de la synchronisation avec TrainingEnrollment
     */
    boolean existsByPersonnelAndStartDateAndEndDate(
        Personnel personnel,
        LocalDate startDate,
        LocalDate endDate
    );
}

