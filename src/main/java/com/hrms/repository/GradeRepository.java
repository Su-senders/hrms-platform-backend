package com.hrms.repository;

import com.hrms.entity.CorpsMetier;
import com.hrms.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Grade
 */
@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    /**
     * Trouve un grade par son code
     */
    Optional<Grade> findByCode(String code);

    /**
     * Trouve un grade par son nom
     */
    Optional<Grade> findByName(String name);

    /**
     * Trouve tous les grades actifs
     */
    List<Grade> findByActiveTrue();

    /**
     * Trouve les grades d'un corps de métier
     */
    List<Grade> findByCorpsMetierAndActiveTrueOrderByLevelAsc(CorpsMetier corpsMetier);

    /**
     * Trouve les grades d'un corps de métier par code de corps
     */
    @Query("SELECT g FROM Grade g WHERE g.corpsMetier.code = :corpsCode AND g.active = true ORDER BY g.level ASC")
    List<Grade> findByCorpsMetierCodeOrderByLevelAsc(@Param("corpsCode") String corpsCode);

    /**
     * Trouve les grades par catégorie
     */
    List<Grade> findByCategoryAndActiveTrueOrderByLevelAsc(String category);

    /**
     * Trouve les grades d'un niveau donné
     */
    List<Grade> findByLevelAndActiveTrue(Integer level);

    /**
     * Trouve le grade le plus élevé d'un corps de métier (niveau 1)
     */
    @Query("SELECT g FROM Grade g WHERE g.corpsMetier = :corpsMetier AND g.level = 1 AND g.active = true")
    Optional<Grade> findTopGradeByCorpsMetier(@Param("corpsMetier") CorpsMetier corpsMetier);

    /**
     * Trouve les grades supérieurs ou égaux à un grade donné dans le même corps
     */
    @Query("SELECT g FROM Grade g WHERE g.corpsMetier = :corpsMetier AND g.level <= :level AND g.active = true ORDER BY g.level ASC")
    List<Grade> findSuperiorOrEqualGrades(
        @Param("corpsMetier") CorpsMetier corpsMetier,
        @Param("level") Integer level
    );

    /**
     * Vérifie si un grade avec ce code existe
     */
    boolean existsByCode(String code);

    /**
     * Compte le nombre de grades par catégorie
     */
    @Query("SELECT g.category, COUNT(g) FROM Grade g WHERE g.active = true GROUP BY g.category")
    List<Object[]> countGradesByCategory();
}
