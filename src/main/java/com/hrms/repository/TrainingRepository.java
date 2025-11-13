package com.hrms.repository;

import com.hrms.entity.Training;
import com.hrms.entity.Training.TrainingCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {

    Optional<Training> findByCode(String code);

    boolean existsByCode(String code);

    List<Training> findByActiveTrue();

    @Query("SELECT t FROM Training t WHERE " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.code) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND t.active = true AND t.deleted = false")
    Page<Training> searchTrainings(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT t FROM Training t WHERE " +
           "(:category IS NULL OR t.category = :category) AND " +
           "(:trainingField IS NULL OR LOWER(t.trainingField) LIKE LOWER(CONCAT('%', :trainingField, '%'))) AND " +
           "t.active = true AND t.deleted = false")
    Page<Training> advancedSearch(@Param("category") TrainingCategory category,
                                  @Param("trainingField") String trainingField,
                                  Pageable pageable);

    @Query("SELECT t FROM Training t WHERE t.category = :category AND t.active = true AND t.deleted = false")
    List<Training> findByCategory(@Param("category") TrainingCategory category);

    @Query("SELECT COUNT(t) FROM Training t WHERE t.category = :category AND t.active = true AND t.deleted = false")
    long countByCategory(@Param("category") TrainingCategory category);

    @Query("SELECT t.category, COUNT(t) FROM Training t " +
           "WHERE t.active = true AND t.deleted = false GROUP BY t.category")
    List<Object[]> countByCategory();
}




