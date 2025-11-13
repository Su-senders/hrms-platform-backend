package com.hrms.repository;

import com.hrms.entity.Trainer;
import com.hrms.entity.Trainer.TrainerType;
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
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    Optional<Trainer> findByCode(String code);

    boolean existsByCode(String code);

    List<Trainer> findByTypeAndActiveTrue(TrainerType type);

    List<Trainer> findByActiveTrue();

    @Query("SELECT t FROM Trainer t WHERE " +
           "(LOWER(t.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.code) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND t.active = true AND t.deleted = false")
    Page<Trainer> searchTrainers(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT t FROM Trainer t WHERE " +
           "(:type IS NULL OR t.type = :type) AND " +
           "(:specialization IS NULL OR LOWER(t.specialization) LIKE LOWER(CONCAT('%', :specialization, '%'))) AND " +
           "t.active = true AND t.deleted = false")
    Page<Trainer> advancedSearch(@Param("type") TrainerType type,
                                 @Param("specialization") String specialization,
                                 Pageable pageable);

    @Query("SELECT t FROM Trainer t WHERE t.personnel.id = :personnelId AND t.deleted = false")
    Optional<Trainer> findByPersonnelId(@Param("personnelId") Long personnelId);

    @Query("SELECT COUNT(t) FROM Trainer t WHERE t.type = :type AND t.active = true AND t.deleted = false")
    long countByType(@Param("type") TrainerType type);

    @Query("SELECT t.specialization, COUNT(t) FROM Trainer t " +
           "WHERE t.active = true AND t.deleted = false GROUP BY t.specialization")
    List<Object[]> countBySpecialization();
}




