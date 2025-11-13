package com.hrms.repository;

import com.hrms.entity.AdministrativeStructure;
import com.hrms.entity.OrganizationalTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationalTemplateRepository extends JpaRepository<OrganizationalTemplate, Long> {

    Optional<OrganizationalTemplate> findByCode(String code);

    List<OrganizationalTemplate> findByAppliesTo(AdministrativeStructure.StructureType appliesTo);

    List<OrganizationalTemplate> findByActiveTrue();

    Optional<OrganizationalTemplate> findByCodeAndActiveTrue(String code);

    List<OrganizationalTemplate> findByAppliesToAndActiveTrue(AdministrativeStructure.StructureType appliesTo);

    boolean existsByCode(String code);

    long countByAppliesTo(AdministrativeStructure.StructureType appliesTo);
}
