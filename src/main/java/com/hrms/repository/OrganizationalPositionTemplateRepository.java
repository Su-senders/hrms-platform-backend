package com.hrms.repository;

import com.hrms.entity.OrganizationalPositionTemplate;
import com.hrms.entity.OrganizationalTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationalPositionTemplateRepository extends JpaRepository<OrganizationalPositionTemplate, Long> {

    Optional<OrganizationalPositionTemplate> findByCode(String code);

    List<OrganizationalPositionTemplate> findByOrganizationalTemplate(OrganizationalTemplate template);

    List<OrganizationalPositionTemplate> findByOrganizationalTemplateAndActiveTrue(OrganizationalTemplate template);

    List<OrganizationalPositionTemplate> findByOrganizationalTemplateIdAndActiveTrue(Long templateId);

    List<OrganizationalPositionTemplate> findByCategory(OrganizationalPositionTemplate.PositionCategory category);

    List<OrganizationalPositionTemplate> findByIsNominativeTrue();

    List<OrganizationalPositionTemplate> findByIsManagerialTrue();

    @Query("SELECT p FROM OrganizationalPositionTemplate p WHERE p.organizationalTemplate = :template AND p.level = :level")
    List<OrganizationalPositionTemplate> findByTemplateAndLevel(
        @Param("template") OrganizationalTemplate template,
        @Param("level") Integer level
    );

    @Query("SELECT p FROM OrganizationalPositionTemplate p WHERE p.organizationalTemplate = :template ORDER BY p.level ASC, p.code ASC")
    List<OrganizationalPositionTemplate> findByTemplateOrderedByLevel(@Param("template") OrganizationalTemplate template);

    @Query("SELECT SUM(p.count) FROM OrganizationalPositionTemplate p WHERE p.organizationalTemplate = :template")
    Long countTotalPositionsByTemplate(@Param("template") OrganizationalTemplate template);

    long countByOrganizationalTemplate(OrganizationalTemplate template);

    boolean existsByCode(String code);
}
