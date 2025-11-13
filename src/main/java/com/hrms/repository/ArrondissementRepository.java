package com.hrms.repository;

import com.hrms.entity.Arrondissement;
import com.hrms.entity.Arrondissement.ArrondissementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArrondissementRepository extends JpaRepository<Arrondissement, Long> {

    Optional<Arrondissement> findByCode(String code);

    Optional<Arrondissement> findByName(String name);

    boolean existsByCode(String code);

    List<Arrondissement> findByActiveTrue();

    List<Arrondissement> findByDepartmentId(Long departmentId);

    @Query("SELECT a FROM Arrondissement a WHERE a.department.id = :departmentId AND a.active = true ORDER BY a.name")
    List<Arrondissement> findByDepartmentIdAndActiveTrue(@Param("departmentId") Long departmentId);

    @Query("SELECT a FROM Arrondissement a WHERE a.department.code = :departmentCode AND a.active = true ORDER BY a.name")
    List<Arrondissement> findByDepartmentCodeAndActiveTrue(@Param("departmentCode") String departmentCode);

    @Query("SELECT a FROM Arrondissement a WHERE a.department.region.id = :regionId AND a.active = true ORDER BY a.name")
    List<Arrondissement> findByRegionIdAndActiveTrue(@Param("regionId") Long regionId);

    List<Arrondissement> findByType(ArrondissementType type);

    @Query("SELECT a FROM Arrondissement a LEFT JOIN FETCH a.sousPrefecture WHERE a.id = :id")
    Optional<Arrondissement> findByIdWithSousPrefecture(Long id);

    @Query("SELECT a FROM Arrondissement a LEFT JOIN FETCH a.department d LEFT JOIN FETCH d.region WHERE a.id = :id")
    Optional<Arrondissement> findByIdWithDepartmentAndRegion(Long id);

    @Query("SELECT a FROM Arrondissement a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(a.chefLieu) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Arrondissement> searchArrondissements(String searchTerm);

    @Query("SELECT COUNT(a) FROM Arrondissement a WHERE a.department.id = :departmentId")
    Long countByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT COUNT(a) FROM Arrondissement a WHERE a.department.region.id = :regionId")
    Long countByRegionId(@Param("regionId") Long regionId);

    @Query("SELECT a FROM Arrondissement a " +
           "WHERE a.department.name = :departmentName " +
           "AND a.department.region.code = :regionCode " +
           "AND a.active = true " +
           "ORDER BY a.name")
    List<Arrondissement> findByDepartmentNameAndRegionCode(
        @Param("departmentName") String departmentName,
        @Param("regionCode") String regionCode
    );
}
