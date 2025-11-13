package com.hrms.repository;

import com.hrms.entity.Personnel;
import com.hrms.entity.TrainingEnrollment;
import com.hrms.entity.TrainingEnrollment.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TrainingEnrollmentRepository extends JpaRepository<TrainingEnrollment, Long> {

    @Query("SELECT e FROM TrainingEnrollment e WHERE e.session.id = :sessionId AND e.deleted = false")
    List<TrainingEnrollment> findBySessionId(@Param("sessionId") Long sessionId);

    @Query("SELECT e FROM TrainingEnrollment e WHERE e.session.id = :sessionId AND e.deleted = false")
    Page<TrainingEnrollment> findBySessionId(@Param("sessionId") Long sessionId, Pageable pageable);

    @Query("SELECT e FROM TrainingEnrollment e WHERE e.personnel.id = :personnelId AND e.deleted = false " +
           "ORDER BY e.enrollmentDate DESC")
    List<TrainingEnrollment> findByPersonnelId(@Param("personnelId") Long personnelId);

    @Query("SELECT e FROM TrainingEnrollment e WHERE e.personnel.id = :personnelId AND e.deleted = false " +
           "ORDER BY e.enrollmentDate DESC")
    Page<TrainingEnrollment> findByPersonnelId(@Param("personnelId") Long personnelId, Pageable pageable);

    @Query("SELECT e FROM TrainingEnrollment e WHERE " +
           "e.session.id = :sessionId AND e.status = :status AND e.deleted = false")
    List<TrainingEnrollment> findBySessionIdAndStatus(@Param("sessionId") Long sessionId,
                                                      @Param("status") EnrollmentStatus status);

    @Query("SELECT e FROM TrainingEnrollment e WHERE " +
           "e.personnel.id = :personnelId AND e.status = :status AND e.deleted = false")
    List<TrainingEnrollment> findByPersonnelIdAndStatus(@Param("personnelId") Long personnelId,
                                                        @Param("status") EnrollmentStatus status);

    @Query("SELECT COUNT(e) FROM TrainingEnrollment e WHERE " +
           "e.session.id = :sessionId AND e.status = 'APPROVED' AND e.deleted = false")
    long countApprovedBySessionId(@Param("sessionId") Long sessionId);

    @Query("SELECT COUNT(e) FROM TrainingEnrollment e WHERE " +
           "e.session.id = :sessionId AND e.status = 'ATTENDED' AND e.deleted = false")
    long countAttendedBySessionId(@Param("sessionId") Long sessionId);

    @Query("SELECT e FROM TrainingEnrollment e WHERE " +
           "e.session.id = :sessionId AND e.personnel.id = :personnelId AND e.deleted = false")
    TrainingEnrollment findBySessionIdAndPersonnelId(@Param("sessionId") Long sessionId,
                                                     @Param("personnelId") Long personnelId);

    @Query("SELECT e.status, COUNT(e) FROM TrainingEnrollment e " +
           "WHERE e.session.id = :sessionId AND e.deleted = false GROUP BY e.status")
    List<Object[]> countByStatusForSession(@Param("sessionId") Long sessionId);

    /**
     * Trouve les inscriptions d'un personnel dans une période donnée
     * Utile pour les statistiques annuelles
     */
    @Query("SELECT e FROM TrainingEnrollment e " +
           "WHERE e.personnel = :personnel " +
           "AND e.session.startDate BETWEEN :startDate AND :endDate " +
           "AND e.deleted = false " +
           "ORDER BY e.session.startDate DESC")
    List<TrainingEnrollment> findByPersonnelAndSessionStartDateBetween(
        @Param("personnel") Personnel personnel,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Trouve les inscriptions par domaine de formation et période
     * Utile pour exporter le personnel formé par domaine
     */
    @Query("SELECT e FROM TrainingEnrollment e " +
           "JOIN e.session s " +
           "JOIN s.training t " +
           "WHERE t.trainingField = :field " +
           "AND s.startDate >= :startDate " +
           "AND s.endDate <= :endDate " +
           "AND e.deleted = false " +
           "ORDER BY s.startDate DESC")
    List<TrainingEnrollment> findByTrainingFieldAndPeriod(
        @Param("field") String trainingField,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}




