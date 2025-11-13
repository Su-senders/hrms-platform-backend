package com.hrms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test d'intégration pour vérifier que l'application Spring Boot démarre correctement
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Tests de démarrage de l'application")
class HrmsPlatformApplicationTests {

    @Test
    @DisplayName("Devrait charger le contexte Spring Boot")
    void contextLoads() {
        // Ce test vérifie que le contexte Spring Boot se charge sans erreur
        // Si cette méthode s'exécute sans exception, le contexte est valide
    }
}

