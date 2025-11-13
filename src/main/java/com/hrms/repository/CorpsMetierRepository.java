package com.hrms.repository;

import com.hrms.entity.CorpsMetier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité CorpsMetier
 */
@Repository
public interface CorpsMetierRepository extends JpaRepository<CorpsMetier, Long> {

    /**
     * Trouve un corps de métier par son code
     */
    Optional<CorpsMetier> findByCode(String code);

    /**
     * Trouve un corps de métier par son nom
     */
    Optional<CorpsMetier> findByName(String name);

    /**
     * Trouve tous les corps de métiers actifs
     */
    List<CorpsMetier> findByActiveTrue();

    /**
     * Trouve les corps de métiers par catégorie
     */
    List<CorpsMetier> findByCategoryAndActiveTrue(String category);

    /**
     * Trouve les corps de métiers par ministère
     */
    List<CorpsMetier> findByMinistereAndActiveTrue(String ministere);

    /**
     * Vérifie si un corps de métier avec ce code existe
     */
    boolean existsByCode(String code);

    /**
     * Compte le nombre de grades dans un corps de métier
     */
    @Query("SELECT COUNT(g) FROM Grade g WHERE g.corpsMetier.id = :corpsMetierId AND g.active = true")
    long countActiveGradesByCorpsMetierId(@Param("corpsMetierId") Long corpsMetierId);

    /**
     * Trouve les corps de métiers avec leurs grades (fetch eager)
     */
    @Query("SELECT DISTINCT c FROM CorpsMetier c LEFT JOIN FETCH c.grades WHERE c.active = true")
    List<CorpsMetier> findAllWithGrades();
}
