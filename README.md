# HRMS Platform - MINAT (Backend)

Système de Gestion des Ressources Humaines pour le Ministère de l'Administration Territoriale du Cameroun.

---

## 🎯 Vue d'Ensemble

Plateforme complète de gestion du personnel pour le MINAT avec :
- ✅ Gestion complète du personnel (59 champs, 3 sections)
- ✅ 15 types de mouvements de carrière
- ✅ Traçabilité totale et historique complet
- ✅ Export PDF et Excel de fiches complètes
- ✅ API REST documentée (Swagger)

**Statut** : ✅ **100% Complet et Opérationnel**

---

## 📚 Documentation

### 🌟 Point d'Entrée Recommandé

**[PLATEFORME_HRMS_RESUME_FINAL.md](PLATEFORME_HRMS_RESUME_FINAL.md)**
→ Document récapitulatif complet de toute la plateforme (18K)

---

### 📖 Documentation par Thème

#### 1️⃣ Personnel (Entité Centrale)

| Document | Description | Taille |
|----------|-------------|--------|
| [SYSTEME_PERSONNEL_FINALISE.md](SYSTEME_PERSONNEL_FINALISE.md) | Système personnel complet | 25K |
| [SECTION_A_IDENTIFICATION_PERSONNEL.md](SECTION_A_IDENTIFICATION_PERSONNEL.md) | Section A : Identification | 18K |
| [SECTION_B_QUALIFICATIONS_COMPLETE.md](SECTION_B_QUALIFICATIONS_COMPLETE.md) | Section B : Qualifications | 9.9K |
| [SECTION_C_CARRIERE_FINALE.md](SECTION_C_CARRIERE_FINALE.md) | Section C : Carrière | 13K |

**Section A** : État civil, origines géographiques, filiation
**Section B** : Diplômes (recrutement + le plus élevé)
**Section C** : Recrutement, situation actuelle, employeur

#### 2️⃣ Systèmes de Base

| Document | Description | Taille |
|----------|-------------|--------|
| [SYSTEME_CORPS_METIERS_FINALISE.md](SYSTEME_CORPS_METIERS_FINALISE.md) | 9 corps métiers + 80 grades | 20K |
| [SYSTEME_ECI_FINALISE.md](SYSTEME_ECI_FINALISE.md) | Système En Cours d'Intégration | 25K |
| [ARCHITECTURE_GEOGRAPHIQUE.md](ARCHITECTURE_GEOGRAPHIQUE.md) | Données géographiques (10 régions, 58 dép., 360 arr.) | 15K |

#### 3️⃣ Export et Rapports

| Document | Description | Taille |
|----------|-------------|--------|
| [EXPORT_FICHE_PERSONNEL_COMPLETE.md](EXPORT_FICHE_PERSONNEL_COMPLETE.md) | Export PDF/Excel de fiches complètes | 19K |
| [ANALYSE_FONCTIONNALITES_PERSONNEL.md](ANALYSE_FONCTIONNALITES_PERSONNEL.md) | Analyse des objectifs (100% atteints) | 10K |

#### 4️⃣ Guides Pratiques

| Document | Description | Taille |
|----------|-------------|--------|
| [QUICK_START_GUIDE.md](QUICK_START_GUIDE.md) | Guide de démarrage rapide | 9.3K |
| [API_TEST_EXAMPLES.md](API_TEST_EXAMPLES.md) | Exemples de tests API | 17K |

#### 5️⃣ Référence

| Document | Description | Taille |
|----------|-------------|--------|
| [MINAT_STRUCTURE.md](MINAT_STRUCTURE.md) | Structure officielle du MINAT | 9.1K |

---

## 🚀 Démarrage Rapide

### Prérequis

- **Java 17+**
- **Maven 3.8+**
- **PostgreSQL 14+**

### Installation

```bash
# 1. Cloner le projet
git clone <repo-url>
cd hrms-platform/backend

# 2. Configurer la base de données
# Modifier src/main/resources/application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/hrms_db
spring.datasource.username=votre_user
spring.datasource.password=votre_password

# 3. Compiler et lancer
mvn clean install
mvn spring-boot:run
```

### Accès

- **API** : http://localhost:8080
- **Swagger UI** : http://localhost:8080/swagger-ui.html
- **H2 Console** (si H2) : http://localhost:8080/h2-console

---

## 📊 Architecture

### Technologies

- **Backend** : Spring Boot 3.2.1
- **Base de données** : PostgreSQL
- **Migrations** : Liquibase
- **Documentation API** : Swagger/OpenAPI
- **Export PDF** : iText 5.5.13
- **Export Excel** : Apache POI 5.2.3

### Structure du Projet

```
src/main/
├── java/com/hrms/
│   ├── entity/              # Entités JPA (Personnel, CareerMovement, etc.)
│   ├── repository/          # Repositories Spring Data JPA
│   ├── service/             # Logique métier
│   │   ├── PersonnelService.java
│   │   ├── CareerMovementService.java
│   │   ├── ExportService.java
│   │   └── PersonnelFicheExportService.java  ⭐
│   ├── controller/          # Controllers REST
│   ├── dto/                 # Data Transfer Objects
│   └── config/              # Configuration
│
└── resources/
    ├── application.properties
    ├── db/changelog/        # 22 migrations Liquibase
    └── data/                # Données de référence (géographie, structures)
```

---

## 🎯 Fonctionnalités Principales

### 1. Gestion du Personnel (CRUD Complet)

**Endpoints** :
```
POST   /api/personnel           - Créer un personnel
GET    /api/personnel/{id}      - Obtenir un personnel
PUT    /api/personnel/{id}      - Modifier un personnel
DELETE /api/personnel/{id}      - Supprimer (soft delete)
GET    /api/personnel           - Liste paginée
```

**59 champs** organisés en 3 sections (A, B, C)

### 2. Mouvements de Carrière (15 Types)

**Types** : AFFECTATION, MUTATION, PROMOTION, DETACHEMENT, MISE_A_DISPOSITION, FORMATION, STAGE, INTEGRATION, RETRAITE, DECES, SUSPENSION, REVOCATION, DEMISSION, DISPONIBILITE, REINTEGRATION

**Workflow** : PENDING → APPROVED → EXECUTED

**Endpoints** :
```
POST   /api/career-movements              - Créer un mouvement
POST   /api/career-movements/{id}/approve - Approuver
POST   /api/career-movements/{id}/execute - Exécuter
GET    /api/career-movements/personnel/{id} - Historique complet
```

### 3. Export de Fiches Complètes ⭐

**Nouveauté** : Export PDF et Excel avec toutes les sections

**Endpoints** :
```
GET /api/reports/export/personnel/{id}/fiche/pdf   - Fiche PDF
GET /api/reports/export/personnel/{id}/fiche/excel - Fiche Excel (5 feuilles)
```

**Contenu** :
- Sections A, B, C complètes
- Historique des mouvements de carrière
- Historique des formations
- Historique des congés
- Postes antérieurs

### 4. Formations et Congés

**Formations** :
```
POST /api/professional-trainings              - Créer une formation
GET  /api/professional-trainings/personnel/{id} - Historique formations
```

**Congés** :
```
POST /api/personnel-leaves              - Créer un congé
GET  /api/personnel-leaves/personnel/{id} - Historique congés
```

---

## 📈 Statistiques du Projet

### Base de Données

- **22 migrations** Liquibase
- **15+ entités** JPA
- **15+ enums** (148+ valeurs)
- **10+ repositories** avec 50+ queries personnalisées

### Personnel (Entité Centrale)

- **59 colonnes** au total
- **Section A** : 14 champs (Identification)
- **Section B** : 14 champs (Qualifications)
- **Section C** : 31 champs (Carrière)

### Données de Référence

- **10 régions** du Cameroun
- **58 départements**
- **360 arrondissements**
- **9 corps de métiers**
- **80+ grades métiers**

---

## 🧪 Tests

### Lancer les Tests

```bash
# Tests unitaires
mvn test

# Tests d'intégration
mvn verify

# Couverture de code
mvn jacoco:report
```

### Exemples de Tests API

Voir [API_TEST_EXAMPLES.md](API_TEST_EXAMPLES.md) pour des exemples complets avec curl et Postman.

---

## 📝 Changelog

### Version 1.0.0 (Novembre 2025)

✅ **Système Personnel Complet**
- Section A : Identification (14 champs)
- Section B : Qualifications (14 champs)
- Section C : Carrière (31 champs)

✅ **Mouvements de Carrière**
- 15 types de mouvements
- Workflow d'approbation complet
- Validation automatique

✅ **Export de Fiches**
- Export PDF professionnel
- Export Excel multi-feuilles
- Toutes les sections et historiques

✅ **Systèmes de Base**
- Corps métiers et grades
- Données géographiques complètes
- Système ECI (En Cours d'Intégration)

---

## 🤝 Contribution

Ce projet est développé pour le MINAT (Ministère de l'Administration Territoriale du Cameroun).

---

## 📞 Support

Pour toute question ou assistance :
1. Consulter la documentation dans les fichiers .md
2. Vérifier le Swagger UI pour les endpoints API
3. Contacter l'équipe de développement

---

## 📜 Licence

Propriété du MINAT - Tous droits réservés

---

## 🎉 Statut du Projet

**✅ 100% COMPLET ET OPÉRATIONNEL**

Tous les objectifs ont été atteints :
1. ✅ Personnel comme élément central
2. ✅ Opérations CRUD complètes
3. ✅ Traçabilité des mouvements de carrière
4. ✅ Historique de carrière complet
5. ✅ Historisation des formations
6. ✅ Export de fiche de renseignement
7. ✅ Tous les mouvements de carrière possibles

**La plateforme est prête pour la production ! 🚀**

---

**Date de finalisation** : Novembre 2025
**Version** : 1.0.0
**Équipe** : MINAT HRMS Development Team
