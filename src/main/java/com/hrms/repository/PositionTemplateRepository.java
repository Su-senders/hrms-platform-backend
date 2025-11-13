package com.hrms.repository;

import com.hrms.entity.PositionTemplate;
import com.hrms.entity.PositionTemplate.ApplicableStructureType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionTemplateRepository extends JpaRepository<PositionTemplate, Long> {

    Optional<PositionTemplate> findByCode(String code);

    boolean existsByCode(String code);

    List<PositionTemplate> findByActiveTrue();

    List<PositionTemplate> findByApplicableStructureType(ApplicableStructureType type);

    @Query("SELECT pt FROM PositionTemplate pt WHERE pt.autoCreate = true AND pt.active = true")
    List<PositionTemplate> findAutoCreateTemplates();

    @Query("SELECT pt FROM PositionTemplate pt WHERE " +
           "(pt.applicableStructureType = :type OR pt.applicableStructureType = 'ALL_STRUCTURES' " +
           "OR (pt.applicableStructureType = 'TERRITORIAL_ONLY' AND :type IN ('GOUVERNORAT', 'PREFECTURE', 'SOUS_PREFECTURE'))) " +
           "AND pt.active = true")
    List<PositionTemplate> findApplicableTemplates(@Param("type") String type);

    @Query("SELECT pt FROM PositionTemplate pt WHERE pt.isNominative = true AND pt.active = true")
    List<PositionTemplate> findNominativeTemplates();

    List<PositionTemplate> findByIsUniquePerStructureTrueAndActiveTrue();
}
