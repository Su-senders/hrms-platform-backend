# API Test Examples - HRMS Platform

## 📌 Collection de Tests API Complets

Ce document contient des exemples de requêtes pour tester toutes les fonctionnalités du HRMS.

## 🔧 Configuration

**Base URL:** `http://localhost:8080`

**Headers par défaut:**
```
Content-Type: application/json
Accept: application/json
```

---

## 1️⃣ ADMINISTRATIVE STRUCTURES

### 1.1 Obtenir toutes les structures
```bash
curl -X GET http://localhost:8080/api/structures
```

### 1.2 Obtenir une structure par ID
```bash
curl -X GET http://localhost:8080/api/structures/1
```

### 1.3 Obtenir une structure par code
```bash
curl -X GET http://localhost:8080/api/structures/code/MINAT
```

### 1.4 Obtenir la hiérarchie d'une structure
```bash
curl -X GET http://localhost:8080/api/structures/1/hierarchy
```

### 1.5 Obtenir les sous-structures
```bash
curl -X GET http://localhost:8080/api/structures/1/children
```

### 1.6 Rechercher des structures
```bash
curl -X GET "http://localhost:8080/api/structures/search?searchTerm=Gouvernorat"
```

### 1.7 Obtenir structures par type
```bash
curl -X GET http://localhost:8080/api/structures/type/GOUVERNORAT
```

### 1.8 Créer une nouvelle structure
```bash
curl -X POST http://localhost:8080/api/structures \
  -H "Content-Type: application/json" \
  -d '{
    "code": "MINAT-TEST",
    "name": "Service Test",
    "description": "Service de test",
    "type": "SERVICE",
    "parentId": 2,
    "level": 3,
    "active": true
  }'
```

### 1.9 Modifier une structure
```bash
curl -X PUT http://localhost:8080/api/structures/100 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Service Test Modifié",
    "description": "Description mise à jour",
    "active": true
  }'
```

### 1.10 Supprimer une structure (soft delete)
```bash
curl -X DELETE http://localhost:8080/api/structures/100
```

---

## 2️⃣ POSITION TEMPLATES

### 2.1 Obtenir tous les modèles de postes
```bash
curl -X GET http://localhost:8080/api/position-templates
```

### 2.2 Obtenir modèles applicables à un type de structure
```bash
# Pour Gouvernorat
curl -X GET http://localhost:8080/api/position-templates/applicable/GOUVERNORAT

# Pour Préfecture
curl -X GET http://localhost:8080/api/position-templates/applicable/PREFECTURE

# Pour Ministère
curl -X GET http://localhost:8080/api/position-templates/applicable/MINISTERE
```

### 2.3 Créer des postes à partir des modèles
```bash
# Pour une structure spécifique (ID = 10)
curl -X POST http://localhost:8080/api/position-templates/create-positions/10
```

### 2.4 Créer uniquement les postes auto-créés
```bash
curl -X POST http://localhost:8080/api/position-templates/create-auto-positions/10
```

### 2.5 Créer des postes pour toutes les structures (bulk)
```bash
curl -X POST http://localhost:8080/api/position-templates/bulk-create-positions
```

**⚠️ Attention:** Cette opération peut prendre du temps et créer plus de 1000 postes!

---

## 3️⃣ POSITIONS

### 3.1 Obtenir toutes les positions
```bash
curl -X GET http://localhost:8080/api/positions
```

### 3.2 Obtenir une position par ID
```bash
curl -X GET http://localhost:8080/api/positions/1
```

### 3.3 Obtenir position par code
```bash
curl -X GET http://localhost:8080/api/positions/code/GOUV-CE-POST-GOUV
```

### 3.4 Rechercher des positions
```bash
curl -X POST http://localhost:8080/api/positions/search \
  -H "Content-Type: application/json" \
  -d '{
    "searchTerm": "Gouverneur",
    "status": "VACANT",
    "structureId": 10
  }'
```

### 3.5 Obtenir positions vacantes
```bash
curl -X GET http://localhost:8080/api/positions/vacant
```

### 3.6 Obtenir positions occupées
```bash
curl -X GET http://localhost:8080/api/positions/occupied
```

### 3.7 Obtenir positions par rang
```bash
curl -X GET "http://localhost:8080/api/positions/rank?rank=Gouverneur"
```

### 3.8 Obtenir positions par structure
```bash
curl -X GET http://localhost:8080/api/positions/structure/10
```

### 3.9 Créer un poste
```bash
curl -X POST http://localhost:8080/api/positions \
  -H "Content-Type: application/json" \
  -d '{
    "code": "GOUV-CE-CHEF-SERV-001",
    "title": "Chef de Service Administratif",
    "description": "Responsable du service administratif",
    "structureId": 10,
    "rank": "Chef de Service",
    "category": "A",
    "requiredGrade": "Administrateur Civil",
    "requiredCorps": "Administrateurs Civils",
    "status": "VACANT",
    "isManagerial": true
  }'
```

### 3.10 Modifier un poste
```bash
curl -X PUT http://localhost:8080/api/positions/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Gouverneur de Région (Modifié)",
    "description": "Description mise à jour",
    "status": "VACANT"
  }'
```

### 3.11 Affecter un personnel à un poste
```bash
curl -X POST http://localhost:8080/api/positions/1/assign/5
```

### 3.12 Libérer un poste
```bash
curl -X POST http://localhost:8080/api/positions/1/release
```

### 3.13 Supprimer un poste
```bash
curl -X DELETE http://localhost:8080/api/positions/1
```

---

## 4️⃣ PERSONNEL

### 4.1 Créer un personnel
```bash
curl -X POST http://localhost:8080/api/personnel \
  -H "Content-Type: application/json" \
  -d '{
    "matricule": "MIN2024001",
    "firstName": "Jean-Claude",
    "lastName": "Mbarga Atangana",
    "dateOfBirth": "1975-03-15",
    "placeOfBirth": "Yaoundé",
    "sex": "MALE",
    "maritalStatus": "MARRIED",
    "numberOfChildren": 3,
    "cniNumber": "123456789CM",
    "passportNumber": "P0123456",
    "phoneNumber": "+237677123456",
    "alternatePhoneNumber": "+237655987654",
    "email": "jc.mbarga@minat.cm",
    "personalEmail": "jcmbarga@gmail.com",
    "address": "Bastos, Yaoundé",
    "city": "Yaoundé",
    "region": "Centre",
    "nationality": "Camerounaise",
    "status": "ACTIVE",
    "grade": "Administrateur Civil Principal",
    "corps": "Administrateurs Civils",
    "category": "A",
    "echelon": "5ème échelon",
    "indice": "1200",
    "recruitmentDate": "2005-01-10",
    "integrationDate": "2005-03-01",
    "firstAppointmentDate": "2005-03-01",
    "currentPositionStartDate": "2020-06-15"
  }'
```

### 4.2 Obtenir un personnel par ID
```bash
curl -X GET http://localhost:8080/api/personnel/1
```

### 4.3 Obtenir un personnel par matricule
```bash
curl -X GET http://localhost:8080/api/personnel/matricule/MIN2024001
```

### 4.4 Rechercher du personnel
```bash
curl -X POST http://localhost:8080/api/personnel/search \
  -H "Content-Type: application/json" \
  -d '{
    "searchTerm": "Mbarga",
    "status": "ACTIVE",
    "grade": "Administrateur Civil Principal",
    "structureId": 10
  }'
```

### 4.5 Obtenir le personnel actif
```bash
curl -X GET http://localhost:8080/api/personnel/active
```

### 4.6 Obtenir le personnel retraitable (année en cours)
```bash
curl -X GET http://localhost:8080/api/personnel/retirable/current-year
```

### 4.7 Obtenir le personnel retraitable (année prochaine)
```bash
curl -X GET http://localhost:8080/api/personnel/retirable/next-year
```

### 4.8 Obtenir personnel par structure
```bash
curl -X GET http://localhost:8080/api/personnel/structure/10
```

### 4.9 Obtenir personnel par poste
```bash
curl -X GET http://localhost:8080/api/personnel/position/5
```

### 4.10 Obtenir personnel par statut
```bash
curl -X GET "http://localhost:8080/api/personnel/status?status=ACTIVE"
```

### 4.11 Modifier un personnel
```bash
curl -X PUT http://localhost:8080/api/personnel/1 \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "+237677999888",
    "email": "nouveau.email@minat.cm",
    "address": "Nouvelle adresse",
    "status": "ACTIVE"
  }'
```

### 4.12 Supprimer un personnel (soft delete)
```bash
curl -X DELETE http://localhost:8080/api/personnel/1
```

---

## 5️⃣ CAREER MOVEMENTS

### 5.1 Créer un mouvement de carrière
```bash
curl -X POST http://localhost:8080/api/career-movements \
  -H "Content-Type: application/json" \
  -d '{
    "personnelId": 1,
    "movementType": "AFFECTATION",
    "movementDate": "2024-01-15",
    "fromStructureId": 5,
    "toStructureId": 10,
    "fromPositionId": 8,
    "toPositionId": 15,
    "decisionNumber": "DEC/2024/001/MINAT",
    "decisionDate": "2024-01-10",
    "effectiveDate": "2024-02-01",
    "reason": "Renforcement des capacités du Gouvernorat",
    "status": "PENDING"
  }'
```

### 5.2 Obtenir un mouvement par ID
```bash
curl -X GET http://localhost:8080/api/career-movements/1
```

### 5.3 Obtenir mouvements d'un personnel
```bash
curl -X GET http://localhost:8080/api/career-movements/personnel/1
```

### 5.4 Obtenir mouvements par type
```bash
curl -X GET "http://localhost:8080/api/career-movements/type?type=AFFECTATION"
```

### 5.5 Obtenir mouvements par statut
```bash
curl -X GET "http://localhost:8080/api/career-movements/status?status=PENDING"
```

### 5.6 Obtenir mouvements par période
```bash
curl -X GET "http://localhost:8080/api/career-movements/date-range?startDate=2024-01-01&endDate=2024-12-31"
```

### 5.7 Approuver un mouvement
```bash
curl -X PUT http://localhost:8080/api/career-movements/1/approve \
  -H "Content-Type: application/json" \
  -d '{
    "approverComments": "Mouvement approuvé par le SG"
  }'
```

### 5.8 Rejeter un mouvement
```bash
curl -X PUT http://localhost:8080/api/career-movements/1/reject \
  -H "Content-Type: application/json" \
  -d '{
    "rejectionReason": "Documents incomplets"
  }'
```

### 5.9 Exécuter un mouvement
```bash
curl -X PUT http://localhost:8080/api/career-movements/1/execute
```

### 5.10 Annuler un mouvement
```bash
curl -X PUT http://localhost:8080/api/career-movements/1/cancel \
  -H "Content-Type: application/json" \
  -d '{
    "cancellationReason": "Changement de décision"
  }'
```

---

## 6️⃣ PERSONNEL DOCUMENTS

### 6.1 Uploader un document
```bash
curl -X POST http://localhost:8080/api/personnel-documents \
  -F "personnelId=1" \
  -F "documentType=CNI" \
  -F "documentNumber=123456789CM" \
  -F "issueDate=2020-01-15" \
  -F "expiryDate=2030-01-15" \
  -F "file=@/path/to/document.pdf"
```

### 6.2 Obtenir documents d'un personnel
```bash
curl -X GET http://localhost:8080/api/personnel-documents/personnel/1
```

### 6.3 Obtenir documents par type
```bash
curl -X GET "http://localhost:8080/api/personnel-documents/type?type=CNI"
```

### 6.4 Obtenir documents expirés
```bash
curl -X GET http://localhost:8080/api/personnel-documents/expired
```

### 6.5 Obtenir documents expirant bientôt (30 jours)
```bash
curl -X GET http://localhost:8080/api/personnel-documents/expiring-soon
```

### 6.6 Télécharger un document
```bash
curl -X GET http://localhost:8080/api/personnel-documents/1/download \
  -o document.pdf
```

### 6.7 Supprimer un document
```bash
curl -X DELETE http://localhost:8080/api/personnel-documents/1
```

---

## 7️⃣ REPORTS & STATISTICS

### 7.1 Obtenir statistiques générales
```bash
curl -X GET http://localhost:8080/api/reports/statistics
```

### 7.2 Statistiques par structure
```bash
curl -X GET http://localhost:8080/api/reports/statistics/structure/10
```

### 7.3 Statistiques par grade
```bash
curl -X GET http://localhost:8080/api/reports/statistics/grade
```

### 7.4 Statistiques par corps
```bash
curl -X GET http://localhost:8080/api/reports/statistics/corps
```

### 7.5 Statistiques par statut
```bash
curl -X GET http://localhost:8080/api/reports/statistics/status
```

### 7.6 Export Excel - Tout le personnel
```bash
curl -X GET http://localhost:8080/api/reports/export/personnel/excel \
  -o personnel_export.xlsx
```

### 7.7 Export Excel - Personnel filtré
```bash
curl -X GET "http://localhost:8080/api/reports/export/personnel/excel?status=ACTIVE&grade=Gouverneur" \
  -o personnel_gouverneurs.xlsx
```

### 7.8 Export PDF - Tout le personnel
```bash
curl -X GET http://localhost:8080/api/reports/export/personnel/pdf \
  -o personnel_export.pdf
```

### 7.9 Export PDF - Personnel filtré
```bash
curl -X GET "http://localhost:8080/api/reports/export/personnel/pdf?structureId=10" \
  -o personnel_gouvernorat_centre.pdf
```

### 7.10 Export Excel - Positions
```bash
curl -X GET http://localhost:8080/api/reports/export/positions/excel \
  -o positions_export.xlsx
```

### 7.11 Export PDF - Positions
```bash
curl -X GET http://localhost:8080/api/reports/export/positions/pdf \
  -o positions_export.pdf
```

---

## 8️⃣ SCÉNARIOS DE TEST COMPLETS

### Scénario 1: Création complète d'un Gouvernorat avec postes et personnel

#### Étape 1: Vérifier le Gouvernorat existe
```bash
curl -X GET http://localhost:8080/api/structures/code/GOUV-CE
```

#### Étape 2: Créer les postes pour ce Gouvernorat
```bash
# Obtenir l'ID du Gouvernorat (ex: 11)
GOUV_ID=11
curl -X POST http://localhost:8080/api/position-templates/create-auto-positions/$GOUV_ID
```

#### Étape 3: Vérifier les postes créés
```bash
curl -X GET http://localhost:8080/api/positions/structure/$GOUV_ID
```

#### Étape 4: Créer le Gouverneur
```bash
curl -X POST http://localhost:8080/api/personnel \
  -H "Content-Type: application/json" \
  -d '{
    "matricule": "GOUV-CE-2024",
    "firstName": "Paul",
    "lastName": "Atanga Nji",
    "dateOfBirth": "1965-07-22",
    "sex": "MALE",
    "cniNumber": "987654321CM",
    "phoneNumber": "+237677888999",
    "email": "gouverneur.centre@minat.cm",
    "status": "ACTIVE",
    "grade": "Gouverneur",
    "corps": "Corps Préfectoral",
    "category": "A",
    "recruitmentDate": "1990-01-10"
  }'
```

#### Étape 5: Affecter le Gouverneur à son poste
```bash
# Obtenir l'ID du poste de Gouverneur (ex: 101)
# Obtenir l'ID du personnel créé (ex: 1)
curl -X POST http://localhost:8080/api/positions/101/assign/1
```

#### Étape 6: Vérifier l'affectation
```bash
curl -X GET http://localhost:8080/api/positions/101
curl -X GET http://localhost:8080/api/personnel/1
```

### Scénario 2: Mutation d'un personnel

#### Étape 1: Créer le mouvement de mutation
```bash
curl -X POST http://localhost:8080/api/career-movements \
  -H "Content-Type: application/json" \
  -d '{
    "personnelId": 1,
    "movementType": "MUTATION",
    "movementDate": "2024-06-01",
    "fromStructureId": 11,
    "toStructureId": 15,
    "fromPositionId": 101,
    "toPositionId": 201,
    "decisionNumber": "MUT/2024/001",
    "decisionDate": "2024-05-20",
    "effectiveDate": "2024-06-01",
    "reason": "Mutation pour nécessité de service",
    "status": "PENDING"
  }'
```

#### Étape 2: Approuver le mouvement
```bash
curl -X PUT http://localhost:8080/api/career-movements/1/approve \
  -H "Content-Type: application/json" \
  -d '{"approverComments": "Approuvé par le Ministre"}'
```

#### Étape 3: Exécuter le mouvement
```bash
curl -X PUT http://localhost:8080/api/career-movements/1/execute
```

#### Étape 4: Vérifier la mutation
```bash
# Vérifier que l'ancien poste est vacant
curl -X GET http://localhost:8080/api/positions/101

# Vérifier que le nouveau poste est occupé
curl -X GET http://localhost:8080/api/positions/201

# Vérifier l'historique du personnel
curl -X GET http://localhost:8080/api/career-movements/personnel/1
```

### Scénario 3: Génération de rapports complets

#### Étape 1: Statistiques globales
```bash
curl -X GET http://localhost:8080/api/reports/statistics
```

#### Étape 2: Export Excel du personnel actif
```bash
curl -X GET "http://localhost:8080/api/reports/export/personnel/excel?status=ACTIVE" \
  -o personnel_actif.xlsx
```

#### Étape 3: Export PDF des retraitables
```bash
curl -X GET http://localhost:8080/api/reports/export/personnel/pdf?retirable=true \
  -o personnel_retraitable.pdf
```

#### Étape 4: Liste des postes vacants
```bash
curl -X GET http://localhost:8080/api/positions/vacant
```

---

## 9️⃣ TESTS DE VALIDATION

### Test 1: Prévention des doublons
```bash
# Créer un personnel
curl -X POST http://localhost:8080/api/personnel -H "Content-Type: application/json" \
  -d '{"matricule": "TEST001", "firstName": "Test", "lastName": "User", ...}'

# Essayer de créer à nouveau avec le même matricule (devrait échouer)
curl -X POST http://localhost:8080/api/personnel -H "Content-Type: application/json" \
  -d '{"matricule": "TEST001", "firstName": "Autre", "lastName": "Personne", ...}'
```

### Test 2: Validation des affectations
```bash
# Affecter un personnel à un poste
curl -X POST http://localhost:8080/api/positions/10/assign/1

# Essayer d'affecter le même personnel à un autre poste (devrait échouer sans cumul)
curl -X POST http://localhost:8080/api/positions/11/assign/1
```

### Test 3: Workflow des mouvements
```bash
# Créer un mouvement
curl -X POST http://localhost:8080/api/career-movements -H "Content-Type: application/json" \
  -d '{...,"status":"PENDING"}'

# Essayer d'exécuter sans approuver (devrait échouer)
curl -X PUT http://localhost:8080/api/career-movements/1/execute
```

---

## 🔟 VARIABLES D'ENVIRONNEMENT (pour scripts)

```bash
# Configuration
export API_URL="http://localhost:8080"
export CONTENT_TYPE="application/json"

# IDs de test (à adapter)
export MINAT_ID=1
export GOUV_CE_ID=11
export PERSONNEL_ID=1
export POSITION_ID=101

# Exemples d'utilisation
curl -X GET $API_URL/api/structures/$MINAT_ID
curl -X GET $API_URL/api/personnel/$PERSONNEL_ID
```

---

## 📊 Codes de Statut HTTP Attendus

- `200 OK` - Succès (GET, PUT)
- `201 Created` - Ressource créée (POST)
- `204 No Content` - Suppression réussie (DELETE)
- `400 Bad Request` - Données invalides
- `404 Not Found` - Ressource introuvable
- `409 Conflict` - Conflit (doublon, etc.)
- `500 Internal Server Error` - Erreur serveur

---

**Version:** 1.0
**Date:** 2025-11-09
**Total Endpoints:** 80+
