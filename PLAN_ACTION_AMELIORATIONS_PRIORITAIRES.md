# Plan d'Action - Améliorations Prioritaires HRMS MINAT

**Date**: 2025-01-13
**Basé sur**: ANALYSE_COMPLETE_APPLICATION_HRMS.md
**Score actuel**: 88/100
**Objectif**: 95+/100

---

## 🎯 Vue d'Ensemble des Priorités

### Criticité des Problèmes Identifiés

| Priorité | Problème | Impact | Effort | Sprint |
|----------|----------|--------|--------|--------|
| **P0 - CRITIQUE** | Aucun test (0% couverture) | ⚠️ TRÈS ÉLEVÉ | 🔴 Élevé | 1-2 |
| **P0 - CRITIQUE** | Sécurité JWT hardcodée | ⚠️ TRÈS ÉLEVÉ | 🟢 Faible | 1 |
| **P0 - CRITIQUE** | Pas de contrôle d'accès | ⚠️ TRÈS ÉLEVÉ | 🟡 Moyen | 1 |
| **P1 - IMPORTANT** | Module Référentiels incomplet | ⚠️ ÉLEVÉ | 🟡 Moyen | 1-2 |
| **P1 - IMPORTANT** | Module Géographie sans API | ⚠️ ÉLEVÉ | 🟢 Faible | 2 |
| **P2 - MOYEN** | Incohérence Grade (String vs Entity) | ⚠️ MOYEN | 🔴 Élevé | 3 |
| **P2 - MOYEN** | Validation congés manquante | ⚠️ MOYEN | 🟡 Moyen | 3 |
| **P3 - FAIBLE** | Cache Redis non activé | ⚠️ FAIBLE | 🟢 Faible | 4 |
| **P3 - FAIBLE** | Monitoring/APM | ⚠️ FAIBLE | 🟡 Moyen | 5 |

---

## 📅 Planning par Sprint

### **SPRINT 1** (Semaines 1-2) - SÉCURITÉ ET FONDATIONS

#### Objectif: Sécuriser l'application et créer les bases de qualité

**Tâches P0**:

✅ **1.1 Externaliser les secrets (JWT, DB, etc.)**
- Fichier: `application.properties` / `application-prod.properties`
- Créer: `application-secrets.properties.template`
- Action: Remplacer hardcoded secrets par `${JWT_SECRET}`
- Temps: 2h

✅ **1.2 Implémenter contrôle d'accès basé sur rôles**
- Créer: `SecurityConfig.java` avec @PreAuthorize
- Ajouter: `@PreAuthorize("hasRole('ADMIN')")` sur endpoints sensibles
- Exemples:
  ```java
  // PersonnelController
  @PreAuthorize("hasAnyRole('ADMIN', 'RH')")
  @PostMapping
  public ResponseEntity<PersonnelDTO> create(...) {}

  @PreAuthorize("hasAnyRole('ADMIN', 'RH', 'MANAGER')")
  @GetMapping("/{id}")
  public ResponseEntity<PersonnelDTO> getById(...) {}
  ```
- Temps: 1 jour

✅ **1.3 Tests unitaires critiques (objectif: 30% couverture)**
- Services à tester en priorité:
  1. `PersonnelService` (createPersonnel, updatePersonnel)
  2. `CareerMovementService` (proposeMovement, approveMovement)
  3. `TrainingEnrollmentService` (enroll, approve)
  4. `GeographicValidationService` (ALL)
  5. `DateValidationService` (ALL)
- Frameworks: JUnit 5 + Mockito + AssertJ
- Temps: 3 jours

**Tâches P1**:

✅ **1.4 API Référentiels - Corps de métier**
- Créer: `CorpsMetierController.java`
- Endpoints:
  ```java
  GET    /api/corps
  GET    /api/corps/{id}
  POST   /api/corps (ADMIN only)
  PUT    /api/corps/{id} (ADMIN only)
  DELETE /api/corps/{id} (ADMIN only)
  GET    /api/corps/{id}/grades
  ```
- Temps: 1 jour

✅ **1.5 API Référentiels - Grades**
- Créer: `GradeController.java`
- Endpoints:
  ```java
  GET    /api/grades
  GET    /api/grades/{id}
  POST   /api/grades (ADMIN only)
  PUT    /api/grades/{id} (ADMIN only)
  DELETE /api/grades/{id} (ADMIN only)
  GET    /api/grades/corps/{corpsId}
  ```
- Temps: 1 jour

**Livrables Sprint 1**:
- ✅ Secrets externalisés
- ✅ Sécurité des endpoints (roles)
- ✅ 30% couverture tests
- ✅ API Corps/Grades complète
- ✅ Documentation Swagger mise à jour

---

### **SPRINT 2** (Semaines 3-4) - API COMPLÈTES ET QUALITÉ

#### Objectif: Compléter les modules manquants et augmenter la couverture tests

**Tâches P1**:

✅ **2.1 API Géographie complète**
- Créer: `GeographyController.java`
- Endpoints:
  ```java
  // Régions
  GET    /api/geography/regions
  GET    /api/geography/regions/{id}
  POST   /api/geography/regions (ADMIN)
  PUT    /api/geography/regions/{id} (ADMIN)

  // Départements
  GET    /api/geography/departments
  GET    /api/geography/departments/region/{regionId}
  GET    /api/geography/departments/{id}
  POST   /api/geography/departments (ADMIN)
  PUT    /api/geography/departments/{id} (ADMIN)

  // Arrondissements
  GET    /api/geography/arrondissements
  GET    /api/geography/arrondissements/department/{deptId}
  GET    /api/geography/arrondissements/{id}
  POST   /api/geography/arrondissements (ADMIN)
  PUT    /api/geography/arrondissements/{id} (ADMIN)

  // Validation
  POST   /api/geography/validate
  ```
- Temps: 2 jours

✅ **2.2 Tests unitaires modules clés (objectif: 50% couverture)**
- Ajouter tests pour:
  1. `PersonnelLeaveService`
  2. `PositionService`
  3. `AdministrativeStructureService`
  4. `TrainingService`
  5. `SeniorityCalculationService`
- Temps: 3 jours

✅ **2.3 Tests d'intégration (objectif: 20% couverture)**
- Tests bout-en-bout:
  1. Création Personnel complet (avec validations)
  2. Workflow mouvement carrière complet
  3. Workflow inscription formation
  4. Workflow demande congé
- Configuration: TestContainers + PostgreSQL
- Temps: 2 jours

**Tâches P2**:

✅ **2.4 Validation congés - Détection chevauchements**
- Modifier: `PersonnelLeaveService.java`
- Ajouter méthode:
  ```java
  private void validateNoOverlap(Personnel personnel, LocalDate startDate, LocalDate endDate) {
      List<PersonnelLeave> existingLeaves = leaveRepository
          .findOverlappingLeaves(personnel.getId(), startDate, endDate);

      if (!existingLeaves.isEmpty()) {
          throw new BusinessException(
              "Chevauchement avec congé existant: " +
              existingLeaves.get(0).getStartDate() + " - " +
              existingLeaves.get(0).getEndDate()
          );
      }
  }
  ```
- Ajouter query dans `PersonnelLeaveRepository`:
  ```java
  @Query("SELECT pl FROM PersonnelLeave pl WHERE pl.personnel.id = :personnelId " +
         "AND pl.status IN ('PENDING', 'APPROVED') " +
         "AND ((pl.startDate <= :endDate AND pl.endDate >= :startDate)) " +
         "AND pl.deleted = false")
  List<PersonnelLeave> findOverlappingLeaves(
      @Param("personnelId") Long personnelId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate
  );
  ```
- Temps: 0.5 jour

**Livrables Sprint 2**:
- ✅ API Géographie complète
- ✅ 50% couverture tests unitaires
- ✅ 20% couverture tests intégration
- ✅ Validation congés robuste

---

### **SPRINT 3** (Semaines 5-6) - CORRECTIONS STRUCTURELLES

#### Objectif: Corriger les incohérences de design

**Tâches P2**:

✅ **3.1 Correction stockage Grade dans CareerMovement**

**Problème actuel**:
```java
// CareerMovement.java - AVANT (INCORRECT)
@Column(name = "new_grade")
private String newGrade; // ❌ String au lieu d'entité
```

**Solution**:
```java
// CareerMovement.java - APRÈS (CORRECT)
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "new_grade_id")
private Grade newGrade; // ✅ Relation vers l'entité

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "old_grade_id")
private Grade oldGrade; // ✅ Ancien grade également
```

**Migration Liquibase nécessaire**:
```xml
<changeSet id="fix-career-movement-grade-relation" author="system">
    <!-- Créer nouvelles colonnes -->
    <addColumn tableName="career_movements">
        <column name="new_grade_id" type="BIGINT">
            <constraints foreignKeyName="fk_movement_grade_new"
                        references="grades(id)"/>
        </column>
        <column name="old_grade_id" type="BIGINT">
            <constraints foreignKeyName="fk_movement_grade_old"
                        references="grades(id)"/>
        </column>
    </addColumn>

    <!-- Migrer données existantes (manuel ou script) -->
    <!-- UPDATE career_movements SET new_grade_id = (SELECT id FROM grades WHERE name = new_grade) -->

    <!-- Supprimer ancienne colonne -->
    <dropColumn tableName="career_movements" columnName="new_grade"/>
</changeSet>
```

**Adapter le service**:
```java
// CareerMovementService.java
public CareerMovementDTO proposeMovement(CareerMovementCreateDTO dto) {
    // AVANT
    // movement.setNewGrade(dto.getNewGrade()); // String

    // APRÈS
    Grade newGrade = gradeRepository.findById(dto.getNewGradeId())
        .orElseThrow(() -> new ResourceNotFoundException("Grade", "id", dto.getNewGradeId()));
    movement.setNewGrade(newGrade);

    // Valider cohérence Grade avec Corps
    if (!newGrade.getCorps().getId().equals(personnel.getCorps().getId())) {
        throw new BusinessException("Le grade ne correspond pas au corps du personnel");
    }
}
```

- Temps: 2 jours (migration + tests)

✅ **3.2 Améliorer workflow congés**

**Fonctionnalités manquantes à ajouter**:

1. **Solde de congés**:
```java
// PersonnelLeave.java - Ajouter champ
@Column(name = "remaining_balance_before")
private Integer remainingBalanceBefore; // Solde avant cette demande

// PersonnelLeaveService.java
public int calculateRemainingBalance(Long personnelId, int year) {
    // Congés acquis dans l'année
    int entitlement = seniorityCalculationService
        .calculateAnnualLeaveEntitlement(personnel);

    // Congés pris (approuvés et terminés)
    int taken = leaveRepository.countTakenLeavesByPersonnelAndYear(personnelId, year);

    // Congés en attente/approuvés mais pas encore pris
    int pending = leaveRepository.countPendingLeavesByPersonnelAndYear(personnelId, year);

    return entitlement - taken - pending;
}
```

2. **Validation solde suffisant**:
```java
private void validateSufficientBalance(Personnel personnel, LocalDate startDate, LocalDate endDate) {
    int daysRequested = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
    int remainingBalance = calculateRemainingBalance(personnel.getId(), startDate.getYear());

    if (daysRequested > remainingBalance) {
        throw new BusinessException(
            String.format("Solde insuffisant: %d jours demandés, %d jours disponibles",
                daysRequested, remainingBalance)
        );
    }
}
```

3. **Report de congés non pris**:
```java
// Permettre report max 10 jours sur année N+1
public void carryOverUnusedLeave(int year) {
    List<Personnel> allPersonnel = personnelRepository.findByDeletedFalse();

    for (Personnel p : allPersonnel) {
        int unused = calculateRemainingBalance(p.getId(), year);
        if (unused > 0) {
            int carriedOver = Math.min(unused, 10); // Max 10 jours reportables
            // Créer entrée "CARRIED_OVER" pour année N+1
        }
    }
}
```

- Temps: 2 jours

✅ **3.3 Tests des corrections**
- Tests unitaires nouvelles fonctionnalités
- Tests de régression (non-régression des fonctionnalités existantes)
- Temps: 1 jour

**Livrables Sprint 3**:
- ✅ Grade stocké comme relation (migration réussie)
- ✅ Workflow congés amélioré (solde, validation, report)
- ✅ Tests de régression passés

---

### **SPRINT 4** (Semaines 7-8) - PERFORMANCE ET CACHE

#### Objectif: Optimiser les performances

**Tâches P3**:

✅ **4.1 Activer cache Redis**

**Configuration actuelle** (`application.properties`):
```properties
# Cache (désactivé)
# spring.cache.type=redis
# spring.redis.host=localhost
# spring.redis.port=6379
```

**Configuration cible**:
```properties
# Cache Redis activé
spring.cache.type=redis
spring.redis.host=${REDIS_HOST:localhost}
spring.redis.port=${REDIS_PORT:6379}
spring.redis.password=${REDIS_PASSWORD:}
spring.cache.redis.time-to-live=3600000
spring.cache.redis.cache-null-values=false
```

**Ajouter annotations cache**:
```java
// PersonnelService.java
@Cacheable(value = "personnel", key = "#id")
public PersonnelDTO getById(Long id) { ... }

@CacheEvict(value = "personnel", key = "#id")
public PersonnelDTO updatePersonnel(Long id, PersonnelUpdateDTO dto) { ... }

// AdministrativeStructureService.java
@Cacheable(value = "structures", key = "'tree'")
public StructureTreeNodeDTO getCompleteTree() { ... }

// ReferentielService (à créer)
@Cacheable(value = "corps", key = "'all'")
public List<CorpsMetierDTO> getAllCorps() { ... }

@Cacheable(value = "grades", key = "'all'")
public List<GradeDTO> getAllGrades() { ... }
```

- Temps: 1 jour

✅ **4.2 Optimiser requêtes N+1**

**Problèmes identifiés**:
```java
// AVANT (N+1 queries)
List<Personnel> personnelList = personnelRepository.findAll();
for (Personnel p : personnelList) {
    p.getStructure().getName(); // Query supplémentaire
    p.getGrade().getName();      // Query supplémentaire
}
```

**Solution - Entity Graphs**:
```java
// PersonnelRepository.java
@EntityGraph(attributePaths = {"structure", "grade", "corps", "currentPosition"})
@Query("SELECT p FROM Personnel p WHERE p.deleted = false")
List<Personnel> findAllWithDetails();

@EntityGraph(attributePaths = {"structure", "grade", "corps", "currentPosition",
                                "regionOrigine", "departmentOrigine"})
@Query("SELECT p FROM Personnel p WHERE p.id = :id")
Optional<Personnel> findByIdWithFullDetails(@Param("id") Long id);
```

- Temps: 1 jour

✅ **4.3 Indexation base de données**

**Indices manquants identifiés**:
```sql
-- Personnel
CREATE INDEX idx_personnel_matricule ON personnel(matricule);
CREATE INDEX idx_personnel_structure ON personnel(structure_id);
CREATE INDEX idx_personnel_grade ON personnel(grade_id);
CREATE INDEX idx_personnel_birth_date ON personnel(date_of_birth); -- Pour calculs retraite
CREATE INDEX idx_personnel_recruitment_date ON personnel(recruitment_date); -- Pour ancienneté

-- CareerMovement
CREATE INDEX idx_movement_personnel ON career_movements(personnel_id);
CREATE INDEX idx_movement_effective_date ON career_movements(effective_date);
CREATE INDEX idx_movement_status ON career_movements(status);

-- TrainingEnrollment
CREATE INDEX idx_enrollment_session ON training_enrollments(session_id);
CREATE INDEX idx_enrollment_personnel ON training_enrollments(personnel_id);
CREATE INDEX idx_enrollment_status ON training_enrollments(status);

-- PersonnelLeave
CREATE INDEX idx_leave_personnel ON personnel_leaves(personnel_id);
CREATE INDEX idx_leave_dates ON personnel_leaves(start_date, end_date);
CREATE INDEX idx_leave_status ON personnel_leaves(status);
```

**Migration Liquibase**:
```xml
<changeSet id="add-performance-indexes" author="system">
    <createIndex tableName="personnel" indexName="idx_personnel_matricule">
        <column name="matricule"/>
    </createIndex>
    <!-- ... autres indexes ... -->
</changeSet>
```

- Temps: 0.5 jour

**Livrables Sprint 4**:
- ✅ Cache Redis opérationnel
- ✅ Requêtes N+1 éliminées
- ✅ Indexation optimisée
- ✅ Temps de réponse API < 200ms (95e percentile)

---

### **SPRINT 5** (Semaines 9-10) - MONITORING ET OBSERVABILITÉ

#### Objectif: Ajouter monitoring et alerting

**Tâches P3**:

✅ **5.1 Spring Boot Actuator**

**Ajouter dépendance**:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

**Configuration**:
```properties
# Actuator
management.endpoints.web.exposure.include=health,metrics,info,prometheus
management.endpoint.health.show-details=when-authorized
management.metrics.export.prometheus.enabled=true

# Custom metrics
management.metrics.tags.application=${spring.application.name}
management.metrics.tags.environment=${spring.profiles.active}
```

**Endpoints exposés**:
- `/actuator/health` - État de santé
- `/actuator/metrics` - Métriques
- `/actuator/prometheus` - Métriques format Prometheus

- Temps: 0.5 jour

✅ **5.2 Métriques métier custom**

**Exemples à implémenter**:
```java
// MetricsService.java
@Service
public class MetricsService {

    private final MeterRegistry registry;

    public void recordPersonnelCreation() {
        registry.counter("hrms.personnel.created").increment();
    }

    public void recordCareerMovement(String movementType) {
        registry.counter("hrms.movement.processed",
                        "type", movementType).increment();
    }

    public void recordTrainingEnrollment() {
        registry.counter("hrms.training.enrollment").increment();
    }

    public void recordLeaveRequest() {
        registry.counter("hrms.leave.requested").increment();
    }

    @Scheduled(fixedRate = 60000) // Toutes les minutes
    public void updateGauges() {
        // Nombre de personnels actifs
        long activePersonnel = personnelRepository.countByAdministrativeStatusAndDeletedFalse(
            Personnel.AdministrativeStatus.ACTIVE);
        registry.gauge("hrms.personnel.active", activePersonnel);

        // Mouvements en attente
        long pendingMovements = movementRepository.countByStatusAndDeletedFalse(
            CareerMovement.MovementStatus.PENDING);
        registry.gauge("hrms.movement.pending", pendingMovements);

        // Congés en attente
        long pendingLeaves = leaveRepository.countByStatusAndDeletedFalse(
            PersonnelLeave.LeaveStatus.PENDING);
        registry.gauge("hrms.leave.pending", pendingLeaves);
    }
}
```

- Temps: 1 jour

✅ **5.3 Logging structuré**

**Configuration Logback**:
```xml
<!-- logback-spring.xml -->
<configuration>
    <appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <includeMdcKeyName>traceId</includeMdcKeyName>
            <includeMdcKeyName>userId</includeMdcKeyName>
            <customFields>{"application":"hrms-minat"}</customFields>
        </encoder>
    </appender>

    <logger name="com.hrms" level="INFO"/>
    <logger name="org.hibernate.SQL" level="DEBUG"/>

    <root level="INFO">
        <appender-ref ref="JSON"/>
    </root>
</configuration>
```

**Ajouter correlation ID**:
```java
// CorrelationIdFilter.java
@Component
@Order(1)
public class CorrelationIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String correlationId = httpRequest.getHeader("X-Correlation-ID");
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put("traceId", correlationId);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
```

- Temps: 0.5 jour

✅ **5.4 Health checks custom**

```java
// DatabaseHealthIndicator.java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    @Autowired
    private PersonnelRepository personnelRepository;

    @Override
    public Health health() {
        try {
            long count = personnelRepository.count();
            return Health.up()
                .withDetail("database", "PostgreSQL")
                .withDetail("personnelCount", count)
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}

// RedisHealthIndicator.java
@Component
public class RedisHealthIndicator implements HealthIndicator {

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Health health() {
        if (redisTemplate == null) {
            return Health.unknown()
                .withDetail("redis", "Not configured")
                .build();
        }

        try {
            redisTemplate.opsForValue().set("health:check", "ok", 5, TimeUnit.SECONDS);
            return Health.up()
                .withDetail("redis", "Connected")
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

- Temps: 0.5 jour

**Livrables Sprint 5**:
- ✅ Actuator configuré et exposé
- ✅ Métriques métier custom
- ✅ Logging structuré JSON
- ✅ Health checks custom
- ✅ Dashboard Grafana (optionnel)

---

### **SPRINT 6** (Semaines 11-12) - FINALISATION ET DOCUMENTATION

#### Objectif: Peaufiner et documenter

✅ **6.1 Documentation API Swagger enrichie**
- Ajouter exemples de requêtes/réponses
- Documenter codes d'erreur
- Ajouter schémas d'authentification

✅ **6.2 Guide de déploiement**
- Docker Compose pour environnement complet
- Scripts de migration
- Procédure de backup/restore

✅ **6.3 Tests de charge**
- JMeter ou Gatling
- Scénarios: 100 utilisateurs simultanés
- Identifier goulots d'étranglement

✅ **6.4 Audit de sécurité**
- OWASP Dependency Check
- SQL Injection tests
- XSS tests

---

## 📊 Métriques de Succès

| Métrique | Actuel | Objectif Sprint 6 |
|----------|--------|-------------------|
| **Score global** | 88% | 95% |
| **Couverture tests** | 0% | 70% |
| **Endpoints sécurisés** | 0% | 100% |
| **API complètes** | 85% | 100% |
| **Temps réponse moyen** | ~500ms | <200ms |
| **Documentation API** | 70% | 95% |

---

## 💰 Estimation Effort Total

| Sprint | Jours/Homme | Coût Estimé* |
|--------|-------------|--------------|
| Sprint 1 | 10 | 10,000€ |
| Sprint 2 | 10 | 10,000€ |
| Sprint 3 | 8 | 8,000€ |
| Sprint 4 | 4 | 4,000€ |
| Sprint 5 | 4 | 4,000€ |
| Sprint 6 | 5 | 5,000€ |
| **TOTAL** | **41 j/h** | **41,000€** |

*Basé sur 1000€/jour développeur senior

---

## 🎓 Compétences Requises

- ✅ Java 17 / Spring Boot 3
- ✅ PostgreSQL / Liquibase
- ✅ JUnit 5 / Mockito / TestContainers
- ✅ Spring Security / JWT
- ✅ Redis / Cache
- ✅ Prometheus / Grafana (monitoring)
- ✅ Docker / Docker Compose

---

## 🚀 Commencer Immédiatement

### Actions Rapides (< 1 jour)

1. **Externaliser secrets** (2h)
2. **Ajouter @PreAuthorize sur 10 endpoints critiques** (2h)
3. **Créer CorpsMetierController** (3h)
4. **Créer GradeController** (3h)

### Template Premier Test Unitaire

```java
// PersonnelServiceTest.java
@ExtendWith(MockitoExtension.class)
class PersonnelServiceTest {

    @Mock
    private PersonnelRepository personnelRepository;

    @Mock
    private GeographicValidationService geoValidationService;

    @Mock
    private DateValidationService dateValidationService;

    @InjectMocks
    private PersonnelService personnelService;

    @Test
    @DisplayName("Création personnel avec validation géographique réussie")
    void createPersonnel_WithValidGeography_ShouldSucceed() {
        // Given
        PersonnelCreateDTO dto = PersonnelCreateDTO.builder()
            .matricule("MINAT-2025-001")
            .firstName("Jean")
            .lastName("DUPONT")
            .dateOfBirth(LocalDate.of(1990, 1, 1))
            .regionOrigineId(1L)
            .departmentOrigineId(1L)
            .arrondissementOrigineId(1L)
            .build();

        Personnel personnel = new Personnel();
        personnel.setId(1L);

        when(personnelRepository.save(any())).thenReturn(personnel);
        doNothing().when(geoValidationService).validateGeographicCoherence(any(), any(), any());
        doNothing().when(dateValidationService).validatePersonnelDates(any());

        // When
        PersonnelDTO result = personnelService.createPersonnel(dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        verify(geoValidationService).validateGeographicCoherence(1L, 1L, 1L);
        verify(dateValidationService).validatePersonnelDates(any());
        verify(personnelRepository).save(any());
    }

    @Test
    @DisplayName("Création personnel avec géographie incohérente doit échouer")
    void createPersonnel_WithInvalidGeography_ShouldThrowException() {
        // Given
        PersonnelCreateDTO dto = PersonnelCreateDTO.builder()
            .regionOrigineId(1L)
            .departmentOrigineId(99L) // Département n'appartient pas à la région
            .build();

        doThrow(new BusinessException("Incohérence géographique"))
            .when(geoValidationService).validateGeographicCoherence(1L, 99L, null);

        // When & Then
        assertThatThrownBy(() -> personnelService.createPersonnel(dto))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("géographique");
    }
}
```

---

## 📞 Support

Pour toute question sur ce plan d'action:
- Documentation complète: `ANALYSE_COMPLETE_APPLICATION_HRMS.md`
- Services créés: `IMPLEMENTATION_COMPLETE_SERVICES.md`
- Architecture: `PLAN_AMELIORATIONS_BACKEND.md`

---

**Dernière mise à jour**: 2025-01-13
**Version**: 1.0
**Statut**: Prêt pour exécution
