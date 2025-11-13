# HRMS Platform - Quick Start Guide

## 🎯 Démarrage Rapide

Ce guide vous aide à démarrer rapidement avec le système HRMS.

## 📋 Prérequis

### Logiciels Requis
- **Java 17** ou supérieur
- **PostgreSQL 14** ou supérieur
- **Redis 7** ou supérieur
- **Maven 3.8** ou supérieur

### Vérification
```bash
java -version    # Devrait afficher Java 17+
mvn -version     # Devrait afficher Maven 3.8+
psql --version   # Devrait afficher PostgreSQL 14+
redis-cli --version  # Devrait afficher Redis 7+
```

## 🗄️ Configuration de la Base de Données

### 1. Créer la base de données PostgreSQL
```sql
-- Connexion à PostgreSQL
psql -U postgres

-- Créer la base de données
CREATE DATABASE hrms_db;

-- Créer l'utilisateur
CREATE USER hrms_user WITH PASSWORD 'hrms_password';

-- Accorder les privilèges
GRANT ALL PRIVILEGES ON DATABASE hrms_db TO hrms_user;

-- Se connecter à la base
\c hrms_db

-- Accorder les privilèges sur le schéma
GRANT ALL ON SCHEMA public TO hrms_user;
```

### 2. Configurer application.properties
Créer/modifier: `src/main/resources/application.properties`

```properties
# Base de données
spring.datasource.url=jdbc:postgresql://localhost:5432/hrms_db
spring.datasource.username=hrms_user
spring.datasource.password=hrms_password
spring.jpa.hibernate.ddl-auto=validate

# Liquibase
spring.liquibase.enabled=true
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.xml

# Redis
spring.redis.host=localhost
spring.redis.port=6379

# Profil actif
spring.profiles.active=dev

# Serveur
server.port=8080

# Logs
logging.level.com.hrms=DEBUG
logging.level.liquibase=INFO
```

## 🚀 Lancer l'Application

### 1. Compiler le projet
```bash
mvn clean install -DskipTests
```

### 2. Démarrer Redis
```bash
redis-server
```

### 3. Lancer l'application
```bash
mvn spring-boot:run
```

### 4. Vérifier le démarrage
L'application devrait:
1. Démarrer sur le port **8080**
2. Exécuter les migrations Liquibase (8 changesets)
3. Initialiser automatiquement (profil `dev`):
   - **80+ structures** MINAT (MinatStructureInitializer)
   - **10 régions + 58 départements** (CameroonTerritoriesInitializer)
   - **45+ modèles de postes** (PositionTemplateInitializer)

### 5. Vérifier les logs
Recherchez ces messages dans les logs:
```
INFO  - Initializing MINAT organizational structure...
INFO  - MINAT organizational structure initialized successfully!
INFO  - Initializing Cameroon territorial structures...
INFO  - Cameroon territorial structures initialized successfully!
INFO  - Initializing position templates...
INFO  - Position templates initialized successfully!
```

## 📊 Vérification de l'Installation

### 1. Accéder à Swagger UI
```
http://localhost:8080/swagger-ui.html
```

### 2. Vérifier les structures créées
```bash
# Nombre total de structures
curl http://localhost:8080/api/structures/count

# Obtenir toutes les structures
curl http://localhost:8080/api/structures

# Obtenir le MINAT
curl http://localhost:8080/api/structures/code/MINAT

# Obtenir un Gouvernorat
curl http://localhost:8080/api/structures/code/GOUV-CE
```

### 3. Vérifier les modèles de postes
```bash
# Tous les modèles actifs
curl http://localhost:8080/api/position-templates

# Modèles pour Gouvernorats
curl http://localhost:8080/api/position-templates/applicable/GOUVERNORAT

# Modèles pour Préfectures
curl http://localhost:8080/api/position-templates/applicable/PREFECTURE
```

## 🧪 Tests de Base

### 1. Créer un personnel
```bash
curl -X POST http://localhost:8080/api/personnel \
  -H "Content-Type: application/json" \
  -d '{
    "matricule": "TEST001",
    "firstName": "Jean",
    "lastName": "Mbarga",
    "dateOfBirth": "1985-06-15",
    "sex": "MALE",
    "cniNumber": "123456789",
    "phoneNumber": "+237677123456",
    "email": "jean.mbarga@minat.cm",
    "status": "ACTIVE",
    "grade": "Administrateur Civil Principal",
    "corps": "Administrateurs Civils"
  }'
```

### 2. Rechercher un personnel
```bash
curl -X POST http://localhost:8080/api/personnel/search \
  -H "Content-Type: application/json" \
  -d '{
    "searchTerm": "Mbarga"
  }'
```

### 3. Créer des postes automatiquement
```bash
# Pour toutes les structures actives
curl -X POST http://localhost:8080/api/position-templates/bulk-create-positions

# Pour un Gouvernorat spécifique
curl -X POST http://localhost:8080/api/position-templates/create-auto-positions/123
```

### 4. Obtenir les statistiques
```bash
curl http://localhost:8080/api/reports/statistics
```

## 📁 Structure du Projet

```
backend/
├── src/main/java/com/hrms/
│   ├── config/              # Initializers (3 fichiers)
│   │   ├── MinatStructureInitializer.java
│   │   ├── CameroonTerritoriesInitializer.java
│   │   └── PositionTemplateInitializer.java
│   ├── controller/          # REST Controllers (7 fichiers)
│   ├── dto/                 # Data Transfer Objects (17 fichiers)
│   ├── entity/              # JPA Entities (8 fichiers)
│   ├── exception/           # Exception handling (6 fichiers)
│   ├── mapper/              # MapStruct Mappers (5 fichiers)
│   ├── repository/          # Spring Data Repositories (8 fichiers)
│   ├── service/             # Business Logic (7 fichiers)
│   └── util/                # Utilities (4 fichiers)
└── src/main/resources/
    ├── application.properties
    └── db/changelog/        # Liquibase migrations (8 fichiers)
```

## 🔑 Endpoints Principaux

### Personnel Management
- `POST /api/personnel` - Créer un personnel
- `GET /api/personnel/{id}` - Obtenir un personnel
- `PUT /api/personnel/{id}` - Modifier un personnel
- `DELETE /api/personnel/{id}` - Supprimer (soft delete)
- `POST /api/personnel/search` - Rechercher

### Position Management
- `POST /api/positions` - Créer un poste
- `GET /api/positions/vacant` - Postes vacants
- `GET /api/positions/occupied` - Postes occupés
- `POST /api/positions/{id}/assign/{personnelId}` - Affecter

### Career Movements
- `POST /api/career-movements` - Créer un mouvement
- `PUT /api/career-movements/{id}/approve` - Approuver
- `PUT /api/career-movements/{id}/execute` - Exécuter

### Documents
- `POST /api/personnel-documents` - Uploader un document
- `GET /api/personnel-documents/personnel/{personnelId}` - Documents d'un personnel

### Administrative Structures
- `GET /api/structures` - Toutes les structures
- `GET /api/structures/{id}/hierarchy` - Hiérarchie
- `GET /api/structures/code/{code}` - Par code

### Position Templates
- `GET /api/position-templates` - Tous les modèles
- `GET /api/position-templates/applicable/{structureType}` - Par type
- `POST /api/position-templates/create-positions/{structureId}` - Créer des postes

### Reports & Exports
- `GET /api/reports/statistics` - Statistiques générales
- `GET /api/reports/export/personnel/excel` - Export Excel
- `GET /api/reports/export/personnel/pdf` - Export PDF

## 📖 Documentation Complète

Consultez ces fichiers pour plus de détails:

1. **[IMPLEMENTATION_SUMMARY.md](./IMPLEMENTATION_SUMMARY.md)** - Vue d'ensemble complète du projet
2. **[MINAT_STRUCTURE.md](./MINAT_STRUCTURE.md)** - Structure organisationnelle du MINAT
3. **[TERRITORIAL_STRUCTURE.md](./TERRITORIAL_STRUCTURE.md)** - Structures territoriales et postes

## 🐛 Dépannage

### Erreur: "Table already exists"
```bash
# Supprimer et recréer la base
dropdb -U postgres hrms_db
createdb -U postgres hrms_db
```

### Erreur: "Redis connection refused"
```bash
# Démarrer Redis
redis-server

# Vérifier que Redis écoute
redis-cli ping  # Devrait répondre "PONG"
```

### Erreur: "Port 8080 already in use"
```bash
# Changer le port dans application.properties
server.port=8081
```

### Les initializers ne s'exécutent pas
Vérifiez que le profil `dev` est actif:
```properties
spring.profiles.active=dev
```

## 📊 Données Initialisées

Après le premier démarrage en mode `dev`:

| Type | Nombre | Source |
|------|--------|--------|
| Structures MINAT | ~80 | MinatStructureInitializer |
| Gouvernorats | 10 | CameroonTerritoriesInitializer |
| Préfectures | 58 | CameroonTerritoriesInitializer |
| Modèles de postes | ~45 | PositionTemplateInitializer |
| **Total structures** | **~150** | - |

## 🔄 Prochaines Étapes

1. **Créer des postes** pour les structures territoriales
   ```bash
   curl -X POST http://localhost:8080/api/position-templates/bulk-create-positions
   ```

2. **Enregistrer du personnel** via l'API ou l'interface

3. **Affecter le personnel** aux postes créés

4. **Gérer les mouvements de carrière** (affectations, mutations, etc.)

5. **Générer des rapports et exports**

## 📞 Support

Pour toute question ou problème:
- Vérifiez les logs: `tail -f logs/application.log`
- Consultez la documentation Swagger: http://localhost:8080/swagger-ui.html
- Vérifiez la base de données: `psql -U hrms_user -d hrms_db`

## ✅ Checklist de Démarrage

- [ ] Java 17+ installé
- [ ] PostgreSQL 14+ installé et démarré
- [ ] Redis 7+ installé et démarré
- [ ] Base de données `hrms_db` créée
- [ ] Utilisateur `hrms_user` créé
- [ ] `application.properties` configuré
- [ ] `mvn clean install` réussi
- [ ] Application démarrée sans erreur
- [ ] Swagger UI accessible
- [ ] Structures initialisées (vérifiées dans les logs)
- [ ] Modèles de postes créés
- [ ] Tests API réussis

---

**Version:** 1.0
**Date:** 2025-11-09
**Statut:** Production Ready 🚀
