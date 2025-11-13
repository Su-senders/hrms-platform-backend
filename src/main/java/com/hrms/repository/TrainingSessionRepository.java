package com.hrms.repository;

import com.hrms.entity.TrainingSession;
import com.hrms.entity.TrainingSession.SessionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {

    Optional<TrainingSession> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT s FROM TrainingSession s WHERE s.training.id = :trainingId AND s.deleted = false")
    List<TrainingSession> findByTrainingId(@Param("trainingId") Long trainingId);

    @Query("SELECT s FROM TrainingSession s WHERE s.trainer.id = :trainerId AND s.deleted = false")
    List<TrainingSession> findByTrainerId(@Param("trainerId") Long trainerId);

    @Query("SELECT s FROM TrainingSession s WHERE s.status = :status AND s.deleted = false")
    Page<TrainingSession> findByStatus(@Param("status") SessionStatus status, Pageable pageable);

    @Query("SELECT s FROM TrainingSession s WHERE s.status = 'PLANNED' AND s.deleted = false ORDER BY s.startDate ASC")
    List<TrainingSession> findPlannedSessions();

    @Query("SELECT s FROM TrainingSession s WHERE s.status = 'OPEN' AND s.deleted = false")
    List<TrainingSession> findOpenSessions();

    @Query("SELECT s FROM TrainingSession s WHERE s.status = 'IN_PROGRESS' AND s.deleted = false")
    List<TrainingSession> findInProgressSessions();

    @Query("SELECT s FROM TrainingSession s WHERE s.status = 'COMPLETED' AND s.deleted = false")
    List<TrainingSession> findCompletedSessions();

    @Query("SELECT s FROM TrainingSession s WHERE " +
           "s.startDate >= :startDate AND s.endDate <= :endDate AND s.deleted = false")
    List<TrainingSession> findByDateRange(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    @Query("SELECT s FROM TrainingSession s WHERE " +
           "s.organizingStructure.id = :structureId AND s.deleted = false")
    List<TrainingSession> findByOrganizingStructureId(@Param("structureId") Long structureId);

    @Query("SELECT s FROM TrainingSession s WHERE " +
           "(LOWER(s.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.code) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND s.deleted = false")
    Page<TrainingSession> searchSessions(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT s FROM TrainingSession s WHERE " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:trainingId IS NULL OR s.training.id = :trainingId) AND " +
           "(:trainerId IS NULL OR s.trainer.id = :trainerId) AND " +
           "(:startDate IS NULL OR s.startDate >= :startDate) AND " +
           "(:endDate IS NULL OR s.endDate <= :endDate) AND " +
           "s.deleted = false")
    Page<TrainingSession> advancedSearch(@Param("status") SessionStatus status,
                                        @Param("trainingId") Long trainingId,
                                        @Param("trainerId") Long trainerId,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate,
                                        Pageable pageable);

    @Query("SELECT COUNT(s) FROM TrainingSession s WHERE s.status = :status AND s.deleted = false")
    long countByStatus(@Param("status") SessionStatus status);

    /**
     * Trouve les sessions dont la date de début est dans une période donnée
     * Utile pour les statistiques annuelles
     */
    @Query("SELECT s FROM TrainingSession s WHERE " +
           "s.startDate BETWEEN :startDate AND :endDate AND s.deleted = false")
    List<TrainingSession> findByStartDateBetween(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}




