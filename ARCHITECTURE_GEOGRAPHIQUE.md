# 🗺️ Architecture Géographique - HRMS Platform

**Date:** 2025-11-09
**Version:** 2.0

---

## 📋 Vue d'Ensemble

Cette architecture sépare clairement les **entités géographiques** (données de référence) des **structures administratives** (organisation du MINAT).

### ✅ Principe Fondamental

```
ENTITÉ GÉOGRAPHIQUE (Référence)  →  STRUCTURE ADMINISTRATIVE (Organisation)

Région "Centre"                  →  Gouvernorat du Centre
Département "Mfoundi"            →  Préfecture de Mfoundi
Arrondissement "Yaoundé 1"       →  Sous-Préfecture de Yaoundé 1
```

---

## 🏗️ Modèle de Données

### 1. Entités Géographiques (Référence)

#### **Region** (10 régions du Cameroun)
```java
@Entity
@Table(name = "regions")
public class Region {
    private Long id;
    private String code;           // "AD", "CE", "EN"
    private String name;           // "Adamaoua", "Centre", "Extrême-Nord"
    private String chefLieu;       // "Ngaoundéré", "Yaoundé", "Maroua"
    private Double superficieKm2;
    private Long population;

    // Relation inverse
    @OneToOne(mappedBy = "region")
    private AdministrativeStructure gouvernorat;
}
```

**Exemple:**
```json
{
  "id": 1,
  "code": "CE",
  "name": "Centre",
  "chefLieu": "Yaoundé",
  "superficieKm2": 68953.0,
  "population": 3098044
}
```

#### **Department** (58 départements)
```java
@Entity
@Table(name = "departments")
public class Department {
    private Long id;
    private String code;           // "CE-MFOU", "AD-DJER"
    private String name;           // "Mfoundi", "Djérem"
    private String chefLieu;       // "Yaoundé", "Tibati"

    // Région parente
    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region;

    // Relation inverse
    @OneToOne(mappedBy = "department")
    private AdministrativeStructure prefecture;
}
```

**Exemple:**
```json
{
  "id": 10,
  "code": "CE-MFOU",
  "name": "Mfoundi",
  "chefLieu": "Yaoundé",
  "regionId": 1,
  "regionName": "Centre"
}
```

#### **Arrondissement** (~360 arrondissements)
```java
@Entity
@Table(name = "arrondissements")
public class Arrondissement {
    private Long id;
    private String code;           // "CE-MFOU-YDE1"
    private String name;           // "Yaoundé 1er"
    private String chefLieu;       // "Yaoundé"
    private ArrondissementType type; // NORMAL, URBAIN, SPECIAL

    // Département parent
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    // Relation inverse
    @OneToOne(mappedBy = "arrondissement")
    private AdministrativeStructure sousPrefecture;

    public enum ArrondissementType {
        NORMAL,      // Arrondissement rural
        URBAIN,      // Arrondissement urbain (grandes villes)
        SPECIAL      // Statut spécial
    }
}
```

**Exemple:**
```json
{
  "id": 100,
  "code": "CE-MFOU-YDE1",
  "name": "Yaoundé 1er",
  "chefLieu": "Yaoundé",
  "type": "URBAIN",
  "departmentId": 10,
  "departmentName": "Mfoundi",
  "regionId": 1,
  "regionName": "Centre"
}
```

### 2. Structures Administratives (Organisation MINAT)

#### **AdministrativeStructure** (Modifié)
```java
@Entity
@Table(name = "administrative_structures")
public class AdministrativeStructure {
    private Long id;
    private String code;
    private String name;
    private StructureType type;

    // Relations vers les entités géographiques
    @OneToOne
    @JoinColumn(name = "region_id")
    private Region region;              // Pour GOUVERNORAT uniquement

    @OneToOne
    @JoinColumn(name = "department_id")
    private Department department;      // Pour PREFECTURE uniquement

    @OneToOne
    @JoinColumn(name = "arrondissement_id")
    private Arrondissement arrondissement; // Pour SOUS_PREFECTURE uniquement

    // Hiérarchie administrative
    @ManyToOne
    @JoinColumn(name = "parent_structure_id")
    private AdministrativeStructure parentStructure;

    // Méthodes utilitaires
    public String getRegionName() {
        if (region != null) return region.getName();
        if (department != null) return department.getRegion().getName();
        if (arrondissement != null) return arrondissement.getDepartment().getRegion().getName();
        return null;
    }
}
```

---

## 🔗 Relations et Cardinalités

### Hiérarchie Géographique
```
Region (1) ──< (N) Department (1) ──< (N) Arrondissement
   ↓                    ↓                      ↓
  (1:1)               (1:1)                   (1:1)
   ↓                    ↓                      ↓
Gouvernorat        Préfecture          Sous-Préfecture
```

### Relations Détaillées

| Relation | Type | Description |
|---|---|---|
| `Region → Gouvernorat` | **1:1** | Une région = un gouvernorat |
| `Department → Préfecture` | **1:1** | Un département = une préfecture |
| `Arrondissement → Sous-Préfecture` | **1:1** | Un arrondissement = une sous-préfecture |
| `Region → Department` | **1:N** | Une région contient plusieurs départements |
| `Department → Arrondissement` | **1:N** | Un département contient plusieurs arrondissements |

---

## 📊 Schéma de Base de Données

### Tables Créées

#### **regions** (Nouvelle)
```sql
CREATE TABLE regions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(10) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    chef_lieu VARCHAR(100) NOT NULL,
    superficie_km2 DECIMAL(12,2),
    population BIGINT,
    description TEXT,
    active BOOLEAN DEFAULT TRUE,
    -- Audit fields
    created_by VARCHAR(100),
    created_date DATE,
    ...
);
```

#### **departments** (Nouvelle)
```sql
CREATE TABLE departments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    chef_lieu VARCHAR(100) NOT NULL,
    region_id BIGINT NOT NULL,
    superficie_km2 DECIMAL(12,2),
    population BIGINT,
    description TEXT,
    active BOOLEAN DEFAULT TRUE,
    -- Audit fields
    created_by VARCHAR(100),
    created_date DATE,
    ...
    FOREIGN KEY (region_id) REFERENCES regions(id) ON DELETE CASCADE
);
```

#### **arrondissements** (Nouvelle)
```sql
CREATE TABLE arrondissements (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(30) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    chef_lieu VARCHAR(100),
    department_id BIGINT NOT NULL,
    type VARCHAR(20) DEFAULT 'NORMAL',
    superficie_km2 DECIMAL(12,2),
    population BIGINT,
    description TEXT,
    active BOOLEAN DEFAULT TRUE,
    -- Audit fields
    created_by VARCHAR(100),
    created_date DATE,
    ...
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE CASCADE
);
```

#### **administrative_structures** (Modifiée)
```sql
ALTER TABLE administrative_structures
    ADD COLUMN region_id BIGINT,
    ADD COLUMN department_id BIGINT,
    ADD COLUMN arrondissement_id BIGINT,
    ADD FOREIGN KEY (region_id) REFERENCES regions(id),
    ADD FOREIGN KEY (department_id) REFERENCES departments(id),
    ADD FOREIGN KEY (arrondissement_id) REFERENCES arrondissements(id);

-- Suppression des anciennes colonnes VARCHAR
ALTER TABLE administrative_structures
    DROP COLUMN region,
    DROP COLUMN department,
    DROP COLUMN arrondissement;
```

---

## 🚀 Initialisation des Données

### Ordre d'Exécution

**GeographicDataInitializer.java** (@Order(2))

```
1. Charger les données JSON (TerritorialDataLoader)
   ├── regions.json → 10 régions
   └── arrondissements/by-region/*.json → ~360 arrondissements

2. Pour chaque région:
   a. Créer l'entité Region
   b. Créer la structure Gouvernorat (liée à Region)
   c. Pour chaque département:
      i.   Créer l'entité Department
      ii.  Créer la structure Préfecture (liée à Department)
      iii. Pour chaque arrondissement:
           - Créer l'entité Arrondissement
           - Créer la structure Sous-Préfecture (liée à Arrondissement)
```

### Exemple Concret: Région Centre

```
1. CREATE Region (id=1, code="CE", name="Centre", chefLieu="Yaoundé")

2. CREATE AdministrativeStructure
   (code="GOUV-CE", type=GOUVERNORAT, region_id=1)

3. CREATE Department (id=10, code="CE-MFOU", name="Mfoundi", region_id=1)

4. CREATE AdministrativeStructure
   (code="PREF-CE-MFOU", type=PREFECTURE, department_id=10, parent=GOUV-CE)

5. CREATE Arrondissement (id=100, code="CE-MFOU-YDE1", name="Yaoundé 1er", department_id=10)

6. CREATE AdministrativeStructure
   (code="SPREF-CE-MFOU-YDE1", type=SOUS_PREFECTURE, arrondissement_id=100, parent=PREF-CE-MFOU)
```

---

## 📁 Fichiers Créés/Modifiés

### Nouvelles Entités
```
src/main/java/com/hrms/entity/
├── Region.java                 ✅ NEW
├── Department.java             ✅ NEW
└── Arrondissement.java         ✅ NEW
```

### Nouveaux Repositories
```
src/main/java/com/hrms/repository/
├── RegionRepository.java       ✅ NEW
├── DepartmentRepository.java   ✅ NEW
└── ArrondissementRepository.java ✅ NEW
```

### Nouveaux DTOs
```
src/main/java/com/hrms/dto/
├── RegionDTO.java              ✅ NEW
├── DepartmentDTO.java          ✅ NEW
└── ArrondissementDTO.java      ✅ NEW
```

### Initializer Refactorisé
```
src/main/java/com/hrms/config/
├── GeographicDataInitializer.java  ✅ NEW (remplace CameroonTerritoriesInitializer)
```

### Migrations Liquibase
```
src/main/resources/db/changelog/v1.0/
├── 011-create-geographic-entities.xml              ✅ NEW
├── 012-add-geographic-references-to-structures.xml ✅ NEW
```

### Entités Modifiées
```
src/main/java/com/hrms/entity/
└── AdministrativeStructure.java ✅ MODIFIED
    - Ajout: @OneToOne Region region
    - Ajout: @OneToOne Department department
    - Ajout: @OneToOne Arrondissement arrondissement
    - Supprimé: String region, department, arrondissement
```

---

## 🎯 Avantages de Cette Architecture

### 1. ✅ Séparation des Préoccupations
- **Géographie** = Données de référence (immuables)
- **Administration** = Organisation MINAT (évolutive)

### 2. ✅ Réutilisabilité
```java
// Même région peut être référencée par :
- Gouvernorat (MINAT)
- Direction Régionale de la Santé
- Direction Régionale de l'Éducation
- etc.
```

### 3. ✅ Intégrité des Données
```sql
-- Cascade DELETE: Si on supprime une région,
-- ses départements et structures sont aussi supprimés
FOREIGN KEY (region_id) REFERENCES regions(id) ON DELETE CASCADE
```

### 4. ✅ Requêtes Optimisées
```java
// Recherche toutes les structures dans une région
List<AdministrativeStructure> structures = structureRepository
    .findByRegionId(regionId);

// Statistiques géographiques
Long nbDepartments = departmentRepository.countByRegionId(regionId);
Long nbArrondissements = arrondissementRepository.countByRegionId(regionId);
```

### 5. ✅ Flexibilité
- Ajout de nouvelles structures administratives sans modifier la géographie
- Plusieurs structures peuvent gérer la même zone géographique
- Support futur d'autres ministères

---

## 📊 Statistiques

### Données Géographiques
| Type | Nombre | Table |
|---|---|---|
| Régions | 10 | `regions` |
| Départements | 58 | `departments` |
| Arrondissements | ~360 | `arrondissements` |
| **TOTAL** | **~428** | **3 tables** |

### Structures Administratives MINAT
| Type | Nombre | Référence |
|---|---|---|
| Gouvernorats | 10 | `region_id` |
| Préfectures | 58 | `department_id` |
| Sous-Préfectures | ~360 | `arrondissement_id` |
| Structures MINAT centrales | ~80 | - |
| **TOTAL** | **~508** | `administrative_structures` |

---

## 🔍 Exemples de Requêtes

### 1. Obtenir tous les départements d'une région
```java
List<Department> departments = departmentRepository
    .findByRegionIdAndActiveTrue(regionId);
```

### 2. Obtenir le gouvernorat d'une région
```java
Region region = regionRepository.findById(regionId).orElseThrow();
AdministrativeStructure gouvernorat = region.getGouvernorat();
```

### 3. Obtenir toutes les structures d'un département
```java
Department department = departmentRepository.findById(deptId).orElseThrow();
AdministrativeStructure prefecture = department.getPrefecture();
```

### 4. Recherche géographique
```java
// Chercher une région
List<Region> regions = regionRepository.searchRegions("Centre");

// Chercher un département
List<Department> departments = departmentRepository.searchDepartments("Mfoundi");

// Chercher un arrondissement
List<Arrondissement> arrondissements = arrondissementRepository
    .searchArrondissements("Yaoundé");
```

### 5. Navigation hiérarchique
```java
// De l'arrondissement vers la région
Arrondissement arr = arrondissementRepository.findById(id).orElseThrow();
Department dept = arr.getDepartment();
Region region = dept.getRegion();

// Ou directement
String regionName = arr.getRegionName(); // Méthode helper
```

---

## 🚀 Migration depuis l'Ancienne Architecture

### Avant (Ancien Modèle)
```java
AdministrativeStructure gouvernorat = ...;
gouvernorat.setRegion("Centre");        // VARCHAR
gouvernorat.setDepartment("Mfoundi");   // VARCHAR
gouvernorat.setArrondissement("Yaoundé 1er"); // VARCHAR
```

### Après (Nouveau Modèle)
```java
Region region = regionRepository.findByName("Centre").orElseThrow();
Department dept = departmentRepository.findByName("Mfoundi").orElseThrow();
Arrondissement arr = arrondissementRepository.findByName("Yaoundé 1er").orElseThrow();

AdministrativeStructure gouvernorat = ...;
gouvernorat.setRegion(region);          // Entity reference
gouvernorat.setDepartment(dept);        // Entity reference
gouvernorat.setArrondissement(arr);     // Entity reference
```

---

## ✅ Checklist de Migration

- [x] Créer entités géographiques (Region, Department, Arrondissement)
- [x] Créer repositories géographiques
- [x] Modifier AdministrativeStructure (ajouter relations)
- [x] Créer migrations Liquibase (2 fichiers)
- [x] Créer GeographicDataInitializer (remplace CameroonTerritoriesInitializer)
- [x] Créer DTOs géographiques
- [ ] Créer Services géographiques (optionnel)
- [ ] Créer Controllers géographiques (optionnel)
- [x] Mettre à jour db.changelog-master.xml
- [x] Documenter architecture

---

## 📝 Notes Importantes

### 1. Suppression en Cascade
```sql
-- Si on supprime une région, TOUS ses départements et arrondissements sont supprimés
-- Ainsi que TOUTES les structures administratives liées
DELETE FROM regions WHERE id = 1;
-- Supprime automatiquement:
-- - 10 departments de cette région
-- - ~30 arrondissements de ces départements
-- - 1 gouvernorat
-- - 10 préfectures
-- - ~30 sous-préfectures
```

### 2. Soft Delete
Toutes les entités héritent de `BaseEntity` avec soft delete:
```java
region.setDeleted(true);
region.setDeletedAt(LocalDateTime.now());
region.setDeletedBy("admin");
// L'entité reste en base mais est marquée comme supprimée
```

### 3. Performance
```java
// Utiliser les fetch joins pour optimiser
@Query("SELECT a FROM Arrondissement a " +
       "LEFT JOIN FETCH a.department d " +
       "LEFT JOIN FETCH d.region " +
       "WHERE a.id = :id")
Optional<Arrondissement> findByIdWithDepartmentAndRegion(Long id);
```

---

**Version:** 2.0
**Date:** 2025-11-09
**Statut:** ✅ Architecture refactorisée et prête

## 🎉 Conclusion

Cette nouvelle architecture offre:
- ✅ **Clarté** - Séparation géographie vs administration
- ✅ **Flexibilité** - Support multi-ministères
- ✅ **Intégrité** - Relations avec contraintes FK
- ✅ **Performance** - Requêtes optimisées
- ✅ **Maintenabilité** - Code plus propre et logique
