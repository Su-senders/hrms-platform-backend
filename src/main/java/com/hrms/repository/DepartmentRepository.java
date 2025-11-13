package com.hrms.repository;

import com.hrms.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByCode(String code);

    Optional<Department> findByName(String name);

    boolean existsByCode(String code);

    boolean existsByName(String name);

    List<Department> findByActiveTrue();

    List<Department> findByRegionId(Long regionId);

    @Query("SELECT d FROM Department d WHERE d.region.id = :regionId AND d.active = true ORDER BY d.name")
    List<Department> findByRegionIdAndActiveTrue(@Param("regionId") Long regionId);

    @Query("SELECT d FROM Department d WHERE d.region.code = :regionCode AND d.active = true ORDER BY d.name")
    List<Department> findByRegionCodeAndActiveTrue(@Param("regionCode") String regionCode);

    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.prefecture WHERE d.id = :id")
    Optional<Department> findByIdWithPrefecture(Long id);

    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.region WHERE d.id = :id")
    Optional<Department> findByIdWithRegion(Long id);

    @Query("SELECT d FROM Department d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(d.chefLieu) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Department> searchDepartments(String searchTerm);

    @Query("SELECT COUNT(d) FROM Department d WHERE d.region.id = :regionId")
    Long countByRegionId(@Param("regionId") Long regionId);
}
