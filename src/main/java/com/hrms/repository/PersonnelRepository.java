package com.hrms.repository;

import com.hrms.entity.*;
import com.hrms.entity.Personnel.PersonnelSituation;
import com.hrms.entity.Personnel.PersonnelStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Personnel entity
 * Version 2.0 - Avec support des grades/corps de métier et origines géographiques
 */
@Repository
public interface PersonnelRepository extends JpaRepository<Personnel, Long> {

    // ==================== RECHERCHE PAR IDENTIFIANTS ====================

    /**
     * Trouve un personnel par son matricule
     */
    Optional<Personnel> findByMatricule(String matricule);

    /**
     * Vérifie si un matricule existe déjà
     */
    boolean existsByMatricule(String matricule);

    /**
     * Trouve un personnel par son numéro CNI
     */
    Optional<Personnel> findByCniNumber(String cniNumber);

    /**
     * Vérifie si un numéro CNI existe déjà
     */
    boolean existsByCniNumber(String cniNumber);

    // ==================== VÉRIFICATION DOUBLONS ====================

    /**
     * Vérifie les doublons (matricule, CNI, ou nom+prénom+date de naissance)
     */
    @Query("SELECT COUNT(p) > 0 FROM Personnel p WHERE " +
           "(p.matricule = :matricule OR p.cniNumber = :cniNumber OR " +
           "(p.firstName = :firstName AND p.lastName = :lastName AND p.dateOfBirth = :dateOfBirth)) " +
           "AND (:excludeId IS NULL OR p.id <> :excludeId) " +
           "AND p.deleted = false")
    boolean checkDuplicate(@Param("matricule") String matricule,
                          @Param("cniNumber") String cniNumber,
                          @Param("firstName") String firstName,
                          @Param("lastName") String lastName,
                          @Param("dateOfBirth") LocalDate dateOfBirth,
                          @Param("excludeId") Long excludeId);

    // ==================== RECHERCHE PAR NOM ====================

    /**
     * Recherche par nom ou matricule (insensible à la casse)
     */
    @Query("SELECT p FROM Personnel p WHERE " +
           "(LOWER(p.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.matricule) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND p.deleted = false")
    Page<Personnel> searchByNameOrMatricule(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Trouve par nom de famille (insensible à la casse)
     */
    List<Personnel> findByLastNameContainingIgnoreCaseAndDeletedFalse(String lastName);

    /**
     * Trouve par prénom (insensible à la casse)
     */
    List<Personnel> findByFirstNameContainingIgnoreCaseAndDeletedFalse(String firstName);

    // ==================== RECHERCHE PAR GRADE ET CORPS ====================

    /**
     * Trouve les personnels par grade actuel
     */
    @Query("SELECT p FROM Personnel p WHERE p.currentGrade = :grade AND p.deleted = false")
    Page<Personnel> findByCurrentGrade(@Param("grade") Grade grade, Pageable pageable);

    /**
     * Trouve les personnels par grade ID
     */
    @Query("SELECT p FROM Personnel p WHERE p.currentGrade.id = :gradeId AND p.deleted = false")
    List<Personnel> findByCurrentGradeId(@Param("gradeId") Long gradeId);

    /**
     * Trouve les personnels par corps de métier
     */
    @Query("SELECT p FROM Personnel p WHERE p.currentGrade.corpsMetier = :corps AND p.deleted = false")
    Page<Personnel> findByCorpsMetier(@Param("corps") CorpsMetier corps, Pageable pageable);

    /**
     * Trouve les personnels par code du corps de métier
     */
    @Query("SELECT p FROM Personnel p WHERE p.currentGrade.corpsMetier.code = :corpsCode AND p.deleted = false")
    List<Personnel> findByCorpsMetierCode(@Param("corpsCode") String corpsCode);

    /**
     * Trouve les personnels par catégorie (A1, A2, B1, etc.)
     */
    @Query("SELECT p FROM Personnel p WHERE p.currentGrade.category = :category AND p.deleted = false")
    List<Personnel> findByCategory(@Param("category") String category);

    /**
     * Trouve les personnels par échelon
     */
    List<Personnel> findByEchelonAndDeletedFalse(Integer echelon);

    // ==================== RECHERCHE PAR STRUCTURE ====================

    /**
     * Trouve les personnels d'une structure
     */
    @Query("SELECT p FROM Personnel p WHERE p.structure.id = :structureId AND p.deleted = false")
    Page<Personnel> findByStructureId(@Param("structureId") Long structureId, Pageable pageable);

    /**
     * Trouve les personnels d'une structure (liste)
     */
    @Query("SELECT p FROM Personnel p WHERE p.structure = :structure AND p.deleted = false")
    List<Personnel> findByStructure(@Param("structure") AdministrativeStructure structure);

    // ==================== RECHERCHE PAR ORIGINES GÉOGRAPHIQUES ====================

    /**
     * Trouve les personnels par région d'origine
     */
    @Query("SELECT p FROM Personnel p WHERE p.regionOrigine = :region AND p.deleted = false")
    List<Personnel> findByRegionOrigine(@Param("region") Region region);

    /**
     * Trouve les personnels par région d'origine ID
     */
    @Query("SELECT p FROM Personnel p WHERE p.regionOrigine.id = :regionId AND p.deleted = false")
    Page<Personnel> findByRegionOrigineId(@Param("regionId") Long regionId, Pageable pageable);

    /**
     * Trouve les personnels par département d'origine
     */
    @Query("SELECT p FROM Personnel p WHERE p.departmentOrigine = :department AND p.deleted = false")
    List<Personnel> findByDepartmentOrigine(@Param("department") Department department);

    /**
     * Trouve les personnels par département d'origine ID
     */
    @Query("SELECT p FROM Personnel p WHERE p.departmentOrigine.id = :departmentId AND p.deleted = false")
    Page<Personnel> findByDepartmentOrigineId(@Param("departmentId") Long departmentId, Pageable pageable);

    /**
     * Trouve les personnels par arrondissement d'origine
     */
    @Query("SELECT p FROM Personnel p WHERE p.arrondissementOrigine = :arrondissement AND p.deleted = false")
    List<Personnel> findByArrondissementOrigine(@Param("arrondissement") Arrondissement arrondissement);

    /**
     * Trouve les personnels par arrondissement d'origine ID
     */
    @Query("SELECT p FROM Personnel p WHERE p.arrondissementOrigine.id = :arrondissementId AND p.deleted = false")
    Page<Personnel> findByArrondissementOrigineId(@Param("arrondissementId") Long arrondissementId, Pageable pageable);

    /**
     * Trouve les personnels affectés dans leur région d'origine
     */
    @Query("SELECT p FROM Personnel p " +
           "WHERE p.regionOrigine = p.structure.region " +
           "AND p.deleted = false")
    List<Personnel> findAffectedInTheirOriginRegion();

    /**
     * Trouve les personnels affectés hors de leur région d'origine
     */
    @Query("SELECT p FROM Personnel p " +
           "WHERE p.regionOrigine != p.structure.region " +
           "AND p.deleted = false")
    List<Personnel> findAffectedOutsideTheirOriginRegion();

    // ==================== RECHERCHE PAR SITUATION ET STATUT ====================

    /**
     * Trouve les personnels par situation
     */
    Page<Personnel> findBySituationAndDeletedFalse(PersonnelSituation situation, Pageable pageable);

    /**
     * Trouve les personnels par statut
     */
    Page<Personnel> findByStatusAndDeletedFalse(PersonnelStatus status, Pageable pageable);

    /**
     * Trouve les personnels actifs
     */
    @Query("SELECT p FROM Personnel p WHERE p.status = 'ACTIVE' AND p.deleted = false")
    List<Personnel> findActivePersonnel();

    // ==================== RETRAITE ====================

    /**
     * Trouve les personnels retraitables cette année
     */
    @Query("SELECT p FROM Personnel p WHERE p.isRetirableThisYear = true " +
           "AND p.status = 'ACTIVE' AND p.deleted = false")
    List<Personnel> findRetirableThisYear();

    /**
     * Trouve les personnels retraitables l'année prochaine
     */
    @Query("SELECT p FROM Personnel p WHERE p.isRetirableNextYear = true " +
           "AND p.status = 'ACTIVE' AND p.deleted = false")
    List<Personnel> findRetirableNextYear();

    /**
     * Trouve les personnels par plage de dates de retraite
     */
    @Query("SELECT p FROM Personnel p WHERE p.retirementDate BETWEEN :startDate AND :endDate " +
           "AND p.status = 'ACTIVE' AND p.deleted = false")
    List<Personnel> findByRetirementDateBetween(@Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);

    /**
     * Trouve les personnels par année de retraite
     */
    @Query("SELECT p FROM Personnel p WHERE YEAR(p.retirementDate) = :year " +
           "AND p.status = 'ACTIVE' AND p.deleted = false")
    List<Personnel> findRetirableInYear(@Param("year") int year);

    /**
     * Trouve les personnels éligibles à la retraite (âge >= 60)
     */
    @Query("SELECT p FROM Personnel p WHERE " +
           "YEAR(CURRENT_DATE) - YEAR(p.dateOfBirth) >= p.retirementAge " +
           "AND p.status = 'ACTIVE' AND p.deleted = false")
    List<Personnel> findEligibleForRetirement();

    // ==================== POSTES ====================

    /**
     * Trouve les personnels sans poste
     */
    @Query("SELECT p FROM Personnel p WHERE p.currentPosition IS NULL " +
           "AND p.status = 'ACTIVE' AND p.deleted = false")
    List<Personnel> findPersonnelWithoutPosition();

    /**
     * Trouve le personnel occupant un poste spécifique
     */
    @Query("SELECT p FROM Personnel p WHERE p.currentPosition.id = :positionId AND p.deleted = false")
    Optional<Personnel> findByPositionId(@Param("positionId") Long positionId);

    /**
     * Trouve les personnels avec cumul officiel
     */
    @Query("SELECT p FROM Personnel p WHERE p.officialCumul = true AND p.deleted = false")
    List<Personnel> findWithOfficialCumul();

    // ==================== STATISTIQUES ====================

    /**
     * Compte les personnels par structure
     */
    @Query("SELECT COUNT(p) FROM Personnel p WHERE p.structure.id = :structureId " +
           "AND p.deleted = false")
    long countByStructureId(@Param("structureId") Long structureId);

    /**
     * Compte les personnels par situation
     */
    long countBySituationAndDeletedFalse(PersonnelSituation situation);

    /**
     * Compte les personnels par statut
     */
    long countByStatusAndDeletedFalse(PersonnelStatus status);

    /**
     * Compte les personnels par grade
     */
    @Query("SELECT g.name, COUNT(p) FROM Personnel p " +
           "JOIN p.currentGrade g " +
           "WHERE p.deleted = false " +
           "GROUP BY g.name " +
           "ORDER BY COUNT(p) DESC")
    List<Object[]> countByGrade();

    /**
     * Compte les personnels par corps de métier
     */
    @Query("SELECT cm.name, COUNT(p) FROM Personnel p " +
           "JOIN p.currentGrade g " +
           "JOIN g.corpsMetier cm " +
           "WHERE p.deleted = false " +
           "GROUP BY cm.name " +
           "ORDER BY COUNT(p) DESC")
    List<Object[]> countByCorpsMetier();

    /**
     * Compte les personnels par catégorie
     */
    @Query("SELECT g.category, COUNT(p) FROM Personnel p " +
           "JOIN p.currentGrade g " +
           "WHERE p.deleted = false " +
           "GROUP BY g.category " +
           "ORDER BY g.category")
    List<Object[]> countByCategory();

    /**
     * Compte les personnels par structure
     */
    @Query("SELECT s.name, COUNT(p) FROM Personnel p JOIN p.structure s " +
           "WHERE p.deleted = false GROUP BY s.name ORDER BY COUNT(p) DESC")
    List<Object[]> countByStructure();

    /**
     * Compte les personnels par région d'origine
     */
    @Query("SELECT r.name, COUNT(p) FROM Personnel p " +
           "JOIN p.regionOrigine r " +
           "WHERE p.deleted = false " +
           "GROUP BY r.name " +
           "ORDER BY COUNT(p) DESC")
    List<Object[]> countByRegionOrigine();

    /**
     * Compte les personnels par département d'origine
     */
    @Query("SELECT d.name, COUNT(p) FROM Personnel p " +
           "JOIN p.departmentOrigine d " +
           "WHERE p.deleted = false " +
           "GROUP BY d.name " +
           "ORDER BY COUNT(p) DESC")
    List<Object[]> countByDepartmentOrigine();

    /**
     * Compte les personnels par arrondissement d'origine
     */
    @Query("SELECT a.name, COUNT(p) FROM Personnel p " +
           "JOIN p.arrondissementOrigine a " +
           "WHERE p.deleted = false " +
           "GROUP BY a.name " +
           "ORDER BY COUNT(p) DESC")
    List<Object[]> countByArrondissementOrigine();

    /**
     * Compte les personnels affectés vs hors région d'origine
     */
    @Query("SELECT " +
           "SUM(CASE WHEN p.regionOrigine = p.structure.region THEN 1 ELSE 0 END) as inOriginRegion, " +
           "SUM(CASE WHEN p.regionOrigine != p.structure.region THEN 1 ELSE 0 END) as outsideOriginRegion " +
           "FROM Personnel p WHERE p.deleted = false")
    Object[] countByOriginRegionAffectation();

    // ==================== RECHERCHE AVANCÉE ====================

    /**
     * Recherche avancée avec critères multiples
     */
    @Query("SELECT p FROM Personnel p WHERE " +
           "(:matricule IS NULL OR p.matricule = :matricule) AND " +
           "(:firstName IS NULL OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
           "(:lastName IS NULL OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
           "(:positionId IS NULL OR p.currentPosition.id = :positionId) AND " +
           "(:gradeId IS NULL OR p.currentGrade.id = :gradeId) AND " +
           "(:corpsId IS NULL OR p.currentGrade.corpsMetier.id = :corpsId) AND " +
           "(:structureId IS NULL OR p.structure.id = :structureId) AND " +
           "(:situation IS NULL OR p.situation = :situation) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:regionOrigineId IS NULL OR p.regionOrigine.id = :regionOrigineId) AND " +
           "(:departmentOrigineId IS NULL OR p.departmentOrigine.id = :departmentOrigineId) AND " +
           "p.deleted = false")
    Page<Personnel> advancedSearch(@Param("matricule") String matricule,
                                   @Param("firstName") String firstName,
                                   @Param("lastName") String lastName,
                                   @Param("positionId") Long positionId,
                                   @Param("gradeId") Long gradeId,
                                   @Param("corpsId") Long corpsId,
                                   @Param("structureId") Long structureId,
                                   @Param("situation") PersonnelSituation situation,
                                   @Param("status") PersonnelStatus status,
                                   @Param("regionOrigineId") Long regionOrigineId,
                                   @Param("departmentOrigineId") Long departmentOrigineId,
                                   Pageable pageable);

    // ==================== DATES ====================

    /**
     * Trouve les personnels embauchés dans une plage de dates
     */
    @Query("SELECT p FROM Personnel p WHERE p.hireDate BETWEEN :startDate AND :endDate " +
           "AND p.deleted = false")
    List<Personnel> findByHireDateBetween(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    /**
     * Trouve les personnels par année d'embauche
     */
    @Query("SELECT p FROM Personnel p WHERE YEAR(p.hireDate) = :year AND p.deleted = false")
    List<Personnel> findByHireYear(@Param("year") int year);

    // ==================== SEXE ET ÂGE ====================

    /**
     * Compte les personnels par sexe
     */
    @Query("SELECT p.gender, COUNT(p) FROM Personnel p " +
           "WHERE p.deleted = false GROUP BY p.gender")
    List<Object[]> countByGender();

    /**
     * Trouve les personnels par tranche d'âge
     */
    @Query("SELECT p FROM Personnel p WHERE " +
           "YEAR(CURRENT_DATE) - YEAR(p.dateOfBirth) BETWEEN :minAge AND :maxAge " +
           "AND p.deleted = false")
    List<Personnel> findByAgeRange(@Param("minAge") int minAge, @Param("maxAge") int maxAge);
}
