# Documentation : Cartographie des Personnels

## 📋 Vue d'Ensemble

La cartographie permet de visualiser la mise en place des personnels par structure et par poste, avec des filtres multiples pour répondre à tous les besoins d'analyse.

---

## 🎯 Fonctionnalités

### Critères de Filtrage Disponibles

1. **Par Structure**
   - Structure spécifique (avec ou sans enfants)
   - Type de structure (MINISTERE, DIRECTION, SERVICE, etc.)
   - Vue hiérarchique (parent → enfants)

2. **Par Poste**
   - Statut (VACANT, OCCUPE)
   - Rang
   - Catégorie
   - Poste spécifique

3. **Par Personnel**
   - Grade
   - Corps de métier
   - Situation (EN_FONCTION, EN_STAGE, etc.)
   - Statut (ACTIVE, RETIRED, etc.)

4. **Options d'Affichage**
   - Postes occupés uniquement
   - Postes vacants uniquement
   - Structures vides incluses ou non

---

## 📡 Endpoints API

### 1. Cartographie Complète

**GET** `/api/cartography`

Obtenir la cartographie de toutes les structures.

**Réponse :**
```json
{
  "statistics": {
    "totalStructures": 150,
    "totalPositions": 500,
    "totalOccupiedPositions": 450,
    "totalVacantPositions": 50,
    "totalPersonnel": 480,
    "totalPersonnelWithPosition": 450,
    "totalPersonnelWithoutPosition": 30
  },
  "structures": [
    {
      "structure": {
        "id": 1,
        "code": "MINAT",
        "name": "Ministère de l'Administration Territoriale",
        "type": "MINISTERE",
        "totalPositions": 50,
        "occupiedPositions": 48,
        "vacantPositions": 2,
        "totalPersonnel": 48
      },
      "positions": [
        {
          "position": {
            "id": 10,
            "code": "POST-MINAT-001",
            "title": "Ministre",
            "rank": "A1",
            "status": "OCCUPE"
          },
          "personnel": {
            "id": 100,
            "matricule": "MINAT-2020-00001",
            "fullName": "Jean DUPONT",
            "grade": "Ministre",
            "corps": "Administration Générale",
            "situation": "EN_FONCTION",
            "age": 55,
            "seniorityInPost": "2 an(s), 3 mois",
            "seniorityInAdministration": "25 an(s), 6 mois"
          }
        }
      ],
      "children": []
    }
  ],
  "filters": null
}
```

---

### 2. Cartographie avec Filtres Personnalisés

**POST** `/api/cartography`

Obtenir la cartographie avec des filtres personnalisés.

**Corps de la Requête :**
```json
{
  "structureId": 1,
  "includeChildren": true,
  "positionStatus": "OCCUPE",
  "grade": "Directeur",
  "situation": "EN_FONCTION",
  "onlyOccupied": true
}
```

**Paramètres Disponibles :**
- `structureId` : ID de la structure
- `structureType` : Type de structure (MINISTERE, DIRECTION, etc.)
- `includeChildren` : Inclure les structures enfants
- `positionId` : ID du poste
- `positionStatus` : Statut du poste (VACANT, OCCUPE)
- `rank` : Rang du poste
- `category` : Catégorie du poste
- `personnelId` : ID du personnel
- `grade` : Grade (ID ou nom)
- `corps` : Corps de métier (ID ou nom)
- `situation` : Situation du personnel
- `status` : Statut du personnel
- `onlyOccupied` : Afficher uniquement les postes occupés
- `onlyVacant` : Afficher uniquement les postes vacants
- `includeEmptyStructures` : Inclure les structures sans postes
- `hierarchical` : Vue hiérarchique

---

### 3. Cartographie d'une Structure Spécifique

**GET** `/api/cartography/structure/{structureId}?includeChildren=true`

Obtenir la cartographie d'une structure spécifique.

**Paramètres :**
- `structureId` : ID de la structure
- `includeChildren` (optionnel) : Inclure les structures enfants (défaut: false)

**Exemple :**
```bash
GET /api/cartography/structure/1?includeChildren=true
```

---

### 4. Cartographie Hiérarchique

**GET** `/api/cartography/hierarchical/{rootStructureId}`

Obtenir la cartographie hiérarchique à partir d'une structure racine.

**Exemple :**
```bash
GET /api/cartography/hierarchical/1
```

Retourne la structure racine avec toutes ses structures enfants et leurs postes/personnels.

---

### 5. Cartographie par Type de Structure

**GET** `/api/cartography/type/{structureType}`

Obtenir la cartographie filtrée par type de structure.

**Types Disponibles :**
- `MINISTERE`
- `DIRECTION`
- `DIVISION`
- `SERVICE`
- `CELLULE`
- `GOUVERNORAT`
- `PREFECTURE`
- `SOUS_PREFECTURE`

**Exemple :**
```bash
GET /api/cartography/type/DIRECTION
```

---

### 6. Cartographie par Statut de Poste

**GET** `/api/cartography/positions/{status}`

Obtenir la cartographie filtrée par statut de poste.

**Statuts :**
- `VACANT` : Postes vacants
- `OCCUPE` : Postes occupés

**Exemple :**
```bash
GET /api/cartography/positions/VACANT
```

---

### 7. Cartographie par Grade

**GET** `/api/cartography/grade/{grade}`

Obtenir la cartographie filtrée par grade.

**Paramètres :**
- `grade` : ID du grade (nombre) ou nom du grade (texte)

**Exemple :**
```bash
GET /api/cartography/grade/Directeur
GET /api/cartography/grade/1
```

---

### 8. Cartographie par Corps de Métier

**GET** `/api/cartography/corps/{corps}`

Obtenir la cartographie filtrée par corps de métier.

**Paramètres :**
- `corps` : ID du corps (nombre) ou nom du corps (texte)

**Exemple :**
```bash
GET /api/cartography/corps/Administration%20Générale
GET /api/cartography/corps/1
```

---

### 9. Cartographie par Situation du Personnel

**GET** `/api/cartography/situation/{situation}`

Obtenir la cartographie filtrée par situation du personnel.

**Situations Disponibles :**
- `EN_FONCTION`
- `EN_STAGE`
- `EN_FORMATION`
- `EN_DETACHEMENT`
- `EN_MISE_A_DISPOSITION`
- `EN_ATTENTE_AFFECTATION`
- `RETRAITE`
- `DECEDE`
- `SUSPENDU`
- `DISPONIBILITE`

**Exemple :**
```bash
GET /api/cartography/situation/EN_FONCTION
```

---

### 10. Cartographie par Rang de Poste

**GET** `/api/cartography/rank/{rank}`

Obtenir la cartographie filtrée par rang de poste.

**Exemple :**
```bash
GET /api/cartography/rank/A1
```

---

### 11. Cartographie des Postes Occupés

**GET** `/api/cartography/occupied`

Obtenir uniquement la cartographie des postes occupés.

---

### 12. Cartographie des Postes Vacants

**GET** `/api/cartography/vacant`

Obtenir uniquement la cartographie des postes vacants.

---

## 🔍 Exemples d'Utilisation

### Exemple 1 : Cartographie Complète du MINAT

```bash
GET /api/cartography/structure/1?includeChildren=true
```

Retourne toutes les structures du MINAT avec leurs postes et personnels.

---

### Exemple 2 : Postes Vacants par Direction

```bash
POST /api/cartography
Content-Type: application/json

{
  "structureType": "DIRECTION",
  "positionStatus": "VACANT",
  "onlyVacant": true
}
```

---

### Exemple 3 : Personnel d'un Grade Spécifique

```bash
GET /api/cartography/grade/Directeur
```

Retourne toutes les structures avec les postes occupés par des Directeurs.

---

### Exemple 4 : Cartographie Hiérarchique Complète

```bash
GET /api/cartography/hierarchical/1
```

Retourne la structure racine (MINAT) avec toutes ses sous-structures de manière hiérarchique.

---

### Exemple 5 : Filtres Multiples

```bash
POST /api/cartography
Content-Type: application/json

{
  "structureType": "SERVICE",
  "grade": "Chef de Service",
  "situation": "EN_FONCTION",
  "onlyOccupied": true
}
```

Retourne tous les Services avec les postes occupés par des Chefs de Service en fonction.

---

## 📊 Structure des Données

### StructureInfo
```json
{
  "id": 1,
  "code": "MINAT",
  "name": "Ministère de l'Administration Territoriale",
  "type": "MINISTERE",
  "parentStructureId": null,
  "parentStructureName": null,
  "totalPositions": 50,
  "occupiedPositions": 48,
  "vacantPositions": 2,
  "totalPersonnel": 48
}
```

### PositionInfo
```json
{
  "id": 10,
  "code": "POST-MINAT-001",
  "title": "Ministre",
  "rank": "A1",
  "category": "A",
  "status": "OCCUPE",
  "requiredGrade": "Ministre",
  "requiredCorps": "Administration Générale"
}
```

### PersonnelInfo
```json
{
  "id": 100,
  "matricule": "MINAT-2020-00001",
  "fullName": "Jean DUPONT",
  "grade": "Ministre",
  "corps": "Administration Générale",
  "situation": "EN_FONCTION",
  "status": "ACTIVE",
  "age": 55,
  "seniorityInPost": "2 an(s), 3 mois",
  "seniorityInAdministration": "25 an(s), 6 mois"
}
```

### Statistics
```json
{
  "totalStructures": 150,
  "totalPositions": 500,
  "totalOccupiedPositions": 450,
  "totalVacantPositions": 50,
  "totalPersonnel": 480,
  "totalPersonnelWithPosition": 450,
  "totalPersonnelWithoutPosition": 30
}
```

---

## ✅ Avantages

1. **Flexibilité** : Filtres multiples pour répondre à tous les besoins
2. **Performance** : Requêtes optimisées avec pagination
3. **Complétude** : Statistiques détaillées incluses
4. **Hiérarchie** : Support de la vue hiérarchique des structures
5. **Traçabilité** : Informations complètes sur chaque poste et personnel

---

## 🎯 Cas d'Usage

### 1. Audit Organisationnel
Visualiser la répartition complète des personnels par structure et poste.

### 2. Planification des Ressources
Identifier les postes vacants et les besoins en recrutement.

### 3. Analyse des Compétences
Visualiser la répartition par grade et corps de métier.

### 4. Gestion des Carrières
Suivre les mouvements et les affectations.

### 5. Reporting Exécutif
Générer des vues synthétiques pour la direction.

---

## 📝 Notes Techniques

- **Performance** : Les requêtes sont optimisées pour gérer de grandes quantités de données
- **Pagination** : Non implémentée actuellement (peut être ajoutée si nécessaire)
- **Cache** : Peut être ajouté pour améliorer les performances sur les grandes structures
- **Export** : Les données peuvent être exportées en Excel/PDF via les endpoints d'export existants

---

## 🔄 Évolutions Futures Possibles

1. **Export Excel/PDF** : Ajouter des endpoints d'export spécifiques pour la cartographie
2. **Vues Pré-définies** : Créer des vues prédéfinies pour les besoins courants
3. **Graphiques** : Ajouter des visualisations graphiques (organigrammes)
4. **Comparaisons** : Comparer la cartographie à différentes dates
5. **Alertes** : Notifications sur les postes vacants critiques

