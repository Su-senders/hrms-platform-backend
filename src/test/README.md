# Tests - HRMS Platform Backend

**Branche** : `feature/tests`  
**Statut** : En cours d'implémentation

---

## 📊 Vue d'Ensemble

Cette branche contient l'implémentation des tests unitaires et d'intégration pour le backend HRMS.

### Tests Créés

| Type | Nombre | Fichiers |
|------|--------|----------|
| **Tests Unitaires (Services)** | 3 | GeographicServiceTest, GeographicValidationServiceTest, PersonnelServiceTest |
| **Tests d'Intégration (Controllers)** | 2 | RegionControllerTest, PersonnelControllerTest |
| **Tests d'Intégration (Repositories)** | 1 | RegionRepositoryTest |
| **Tests Utilitaires** | 1 | DateUtilTest |
| **Tests Application** | 1 | HrmsPlatformApplicationTests |
| **Total** | **8** | |

---

## 🏗️ Structure des Tests

```
src/test/
├── java/com/hrms/
│   ├── config/
│   │   └── TestConfig.java              # Configuration de test (sécurité désactivée)
│   ├── service/
│   │   ├── GeographicServiceTest.java
│   │   ├── GeographicValidationServiceTest.java
│   │   └── PersonnelServiceTest.java
│   ├── controller/
│   │   ├── RegionControllerTest.java
│   │   └── PersonnelControllerTest.java
│   ├── repository/
│   │   └── RegionRepositoryTest.java
│   ├── util/
│   │   └── DateUtilTest.java
│   └── HrmsPlatformApplicationTests.java
└── resources/
    └── application-test.yml             # Configuration de test (H2, Liquibase désactivé)
```

---

## 🧪 Types de Tests

### 1. Tests Unitaires (Services)

**GeographicServiceTest** :
- ✅ Récupération de toutes les régions
- ✅ Récupération par ID
- ✅ Récupération par code
- ✅ Recherche de régions
- ✅ Récupération des départements d'une région
- ✅ Récupération des arrondissements d'un département
- ✅ Gestion des erreurs (ResourceNotFoundException)

**GeographicValidationServiceTest** :
- ✅ Validation de cohérence géographique
- ✅ Validation région seule
- ✅ Validation région + département
- ✅ Validation région + département + arrondissement
- ✅ Détection d'incohérences
- ✅ Méthodes utilitaires (isDepartmentInRegion, etc.)

**PersonnelServiceTest** :
- ✅ Création de personnel
- ✅ Récupération par ID
- ✅ Récupération par matricule
- ✅ Mise à jour
- ✅ Suppression logique
- ✅ Détection de doublons
- ✅ Gestion des erreurs

### 2. Tests d'Intégration (Controllers)

**RegionControllerTest** :
- ✅ GET /api/geography/regions
- ✅ GET /api/geography/regions/{id}
- ✅ GET /api/geography/regions/code/{code}
- ✅ GET /api/geography/regions/search
- ✅ GET /api/geography/regions/{id}/departments
- ✅ GET /api/geography/regions/{id}/statistics

**PersonnelControllerTest** :
- ✅ POST /api/personnel (création)
- ✅ GET /api/personnel/{id}
- ✅ GET /api/personnel/matricule/{matricule}
- ✅ GET /api/personnel (liste paginée)
- ✅ PUT /api/personnel/{id} (mise à jour)
- ✅ DELETE /api/personnel/{id} (suppression)

### 3. Tests d'Intégration (Repositories)

**RegionRepositoryTest** :
- ✅ Sauvegarde et récupération
- ✅ Recherche par code
- ✅ Filtrage par statut actif
- ✅ Vérification d'existence
- ✅ Recherche par nom/chef-lieu

### 4. Tests Utilitaires

**DateUtilTest** :
- ✅ Calcul d'âge
- ✅ Calcul de période
- ✅ Formatage de dates
- ✅ Formatage de périodes
- ✅ Vérification année courante

---

## ⚙️ Configuration

### application-test.yml

- **Base de données** : H2 en mémoire
- **Liquibase** : Désactivé
- **Cache** : Simple (pas de Redis)
- **Sécurité** : Désactivée pour les tests
- **Logging** : DEBUG pour les tests

### TestConfig

- Désactive la sécurité Spring Security pour les tests
- Permet l'accès à tous les endpoints sans authentification

---

## 🚀 Exécution des Tests

### Avec Maven

```bash
# Tous les tests
mvn test

# Tests spécifiques
mvn test -Dtest=GeographicServiceTest
mvn test -Dtest=PersonnelServiceTest

# Tests d'une classe spécifique
mvn test -Dtest=*ServiceTest

# Avec couverture de code (si JaCoCo configuré)
mvn test jacoco:report
```

### Avec IDE

- **IntelliJ IDEA** : Clic droit sur le fichier de test → "Run Tests"
- **Eclipse** : Clic droit sur le fichier → "Run As" → "JUnit Test"
- **VS Code** : Extension Java Test Runner

---

## 📈 Couverture de Code

### Services Testés

- ✅ GeographicService (100% des méthodes publiques)
- ✅ GeographicValidationService (100% des méthodes)
- 🟡 PersonnelService (principales méthodes)

### Contrôleurs Testés

- ✅ RegionController (tous les endpoints)
- 🟡 PersonnelController (endpoints principaux)

### Repositories Testés

- ✅ RegionRepository (méthodes principales)

---

## 🔄 Prochaines Étapes

### Tests à Ajouter

1. **Services** :
   - [ ] CareerMovementServiceTest
   - [ ] AdministrativeStructureServiceTest
   - [ ] PositionServiceTest
   - [ ] TrainingServiceTest

2. **Contrôleurs** :
   - [ ] DepartmentControllerTest
   - [ ] ArrondissementControllerTest
   - [ ] CareerMovementControllerTest
   - [ ] TrainingControllerTest

3. **Repositories** :
   - [ ] DepartmentRepositoryTest
   - [ ] ArrondissementRepositoryTest
   - [ ] PersonnelRepositoryTest
   - [ ] PositionRepositoryTest

4. **Tests d'Intégration Complets** :
   - [ ] Tests avec base de données réelle (TestContainers)
   - [ ] Tests de sécurité
   - [ ] Tests de performance

---

## 📝 Notes

- Les tests utilisent **Mockito** pour le mocking
- **AssertJ** pour les assertions
- **JUnit 5** comme framework de test
- **H2** en mémoire pour les tests de repository
- **@WebMvcTest** pour les tests de contrôleurs

---

**Date de création** : Branche feature/tests  
**Statut** : ✅ Tests de base implémentés

