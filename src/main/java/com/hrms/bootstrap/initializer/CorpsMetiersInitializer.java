package com.hrms.bootstrap.initializer;

import com.hrms.bootstrap.loader.data.CorpsMetierData;
import com.hrms.bootstrap.loader.data.CorpsMetiersDataLoader;
import com.hrms.bootstrap.loader.data.GradeData;
import com.hrms.entity.CorpsMetier;
import com.hrms.entity.Grade;
import com.hrms.repository.CorpsMetierRepository;
import com.hrms.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Initialise les corps de métiers et leurs grades depuis le fichier JSON de référence.
 * Doit s'exécuter AVANT toute création de Personnel.
 */
@Slf4j
@Component
@Profile("dev")
@Order(4) // Après GeographicDataInitializer (3), avant PersonnelInitializer (5)
@RequiredArgsConstructor
public class CorpsMetiersInitializer implements CommandLineRunner {

    private final CorpsMetierRepository corpsMetierRepository;
    private final GradeRepository gradeRepository;
    private final CorpsMetiersDataLoader dataLoader;

    @Override
    @Transactional
    public void run(String... args) {
        // Vérifier si les données existent déjà
        if (corpsMetierRepository.count() > 0) {
            log.info("Corps de métiers already initialized, skipping...");
            return;
        }

        log.info("Initializing corps de métiers and grades...");

        try {
            // Charger les données depuis JSON
            List<CorpsMetierData> corpsMetiersData = dataLoader.loadCorpsMetiers();

            for (CorpsMetierData corpsMetierData : corpsMetiersData) {
                createCorpsMetierWithGrades(corpsMetierData);
            }

            long totalCorps = corpsMetierRepository.count();
            long totalGrades = gradeRepository.count();

            log.info("Corps de métiers and grades initialized successfully!");
            log.info("Created: {} corps de métiers, {} grades", totalCorps, totalGrades);

        } catch (Exception e) {
            log.error("Error initializing corps de métiers", e);
            throw e;
        }
    }

    /**
     * Crée un corps de métier avec tous ses grades
     */
    private void createCorpsMetierWithGrades(CorpsMetierData data) {
        log.info("Creating corps de métier: {} ({})", data.getName(), data.getCode());

        // 1. Créer le corps de métier
        CorpsMetier corpsMetier = CorpsMetier.builder()
            .code(data.getCode())
            .name(data.getName())
            .description(data.getDescription())
            .category(data.getCategory())
            .ministere(data.getMinistere())
            .active(true)
            .build();

        corpsMetier.setCreatedBy("system");
        corpsMetier.setCreatedDate(LocalDate.now());

        corpsMetier = corpsMetierRepository.save(corpsMetier);

        // 2. Créer tous les grades de ce corps de métier
        if (data.getGrades() != null) {
            for (GradeData gradeData : data.getGrades()) {
                createGrade(gradeData, corpsMetier);
            }
        }

        log.info("Created corps de métier {} with {} grades",
            data.getCode(),
            data.getGrades() != null ? data.getGrades().size() : 0);
    }

    /**
     * Crée un grade
     */
    private void createGrade(GradeData data, CorpsMetier corpsMetier) {
        Grade grade = Grade.builder()
            .code(data.getCode())
            .name(data.getName())
            .level(data.getLevel())
            .category(data.getCategory())
            .description(data.getDescription())
            .corpsMetier(corpsMetier)
            .active(true)
            .build();

        grade.setCreatedBy("system");
        grade.setCreatedDate(LocalDate.now());

        gradeRepository.save(grade);

        log.debug("Created grade: {} ({}) - Level {}, Category {}",
            data.getName(), data.getCode(), data.getLevel(), data.getCategory());
    }
}

