# Structure Organisationnelle du MINAT

## Vue d'ensemble
Ce document décrit la structure organisationnelle complète du Ministère de l'Administration Territoriale (MINAT) du Cameroun, telle qu'implémentée dans le système HRMS.

## Implémentation

### Initialisation automatique
La structure est automatiquement créée au démarrage de l'application via la classe:
- **`MinatStructureInitializer.java`** (Active uniquement en mode `dev`)
- S'exécute si la base de données est vide
- Crée plus de **80 structures** hiérarchiques

### Comment désactiver l'initialisation automatique
Pour désactiver en production, supprimer l'annotation `@Profile("dev")` ou changer le profil actif.

## Hiérarchie Complète

### NIVEAU 1: Ministère
```
MINAT - Ministère de l'Administration Territoriale
```

### NIVEAU 2: Structures Principales

#### 1. Secrétariat Particulier (MINAT-SP)
- Chef de Secrétariat Particulier

#### 2. Conseillers Techniques (MINAT-CT)
- 3 Conseillers Techniques

#### 3. Inspection Générale (MINAT-IG)
- **IG de l'Administration Territoriale** (MINAT-IG-IGAT)
  - Inspecteur Général + 3 Inspecteurs
- **IG des Questions Électorales** (MINAT-IG-IGQE)
  - Inspecteur Général + 3 Inspecteurs
- **IG des Services** (MINAT-IG-IGS)
  - Inspecteur Général + 3 Inspecteurs

#### 4. Secrétariat Général (MINAT-SG)
Dirigé par le Secrétaire Général

## Divisions rattachées au Secrétariat Général

### 1. Division des Affaires Juridiques (MINAT-SG-DAJ)
- **Cellule des Études et de la Réglementation** (MINAT-SG-DAJ-CER)
  - 3 Chargés d'Études Assistants
- **Cellule des Requêtes et du Contentieux** (MINAT-SG-DAJ-CRC)
  - 3 Chargés d'Études Assistants

### 2. Division des Études, Statistiques, Planification et Coopération (MINAT-SG-DESPC)
- **Cellule des Études et des Statistiques** (MINAT-SG-DESPC-CES)
  - 2 Chargés d'Études Assistants
- **Cellule de la Planification et des Projets** (MINAT-SG-DESPC-CPP)
  - 2 Chargés d'Études Assistants
- **Cellule de la Coopération** (MINAT-SG-DESPC-CC)
  - 2 Chargés d'Études Assistants

### 3. Division du Suivi et du Contrôle de Gestion (MINAT-SG-DSCG)
- **Cellule de Suivi** (MINAT-SG-DSCG-CS)
  - 2 Chargés d'Études Assistants
- **Cellule du Contrôle de Gestion** (MINAT-SG-DSCG-CCG)
  - 3 Chargés d'Études Assistants

### 4. Division des Systèmes d'Information (MINAT-SG-DSI)
- **Cellule des Études et des Développements** (MINAT-SG-DSI-CED)
  - 3 Chargés d'Études Assistants
- **Cellule du Suivi de l'Exploitation et de la Maintenance** (MINAT-SG-DSI-CSEM)
  - 4 Chargés d'Études Assistants

### 5. Division de la Communication et des Relations Publiques (MINAT-SG-DCRP)
- **Cellule de Communication** (MINAT-SG-DCRP-CC)
  - 2 Chargés d'Études Assistants
- **Cellule des Relations Publiques** (MINAT-SG-DCRP-CRP)
  - 2 Chargés d'Études Assistants

### 6. Division de la Traduction et de la Promotion du Bilinguisme (MINAT-SG-DTPB)
- **Cellule de Traduction** (MINAT-SG-DTPB-CT)
  - 2 Chargés d'Études Assistants
- **Cellule de la Promotion du Bilinguisme** (MINAT-SG-DTPB-CPB)
  - 2 Chargés d'Études Assistants

### 7. Sous-Direction de l'Accueil, du Courrier et de Liaison (MINAT-SG-SDACL)
- **Service de l'Accueil et de l'Orientation** (MINAT-SG-SDACL-SAO)
- **Service du Courrier et de Liaison** (MINAT-SG-SDACL-SCL)
- **Service de la Relance** (MINAT-SG-SDACL-SR)

### 8. Centre de Documentation et des Archives (MINAT-SG-CDA)
- **Service de la Documentation** (MINAT-SG-CDA-SD)
- **Service du Fichier et des Archives** (MINAT-SG-CDA-SFA)
- **Bibliothèque** (MINAT-SG-CDA-BIB)

## Directions NON rattachées au Secrétariat Général

### 1. Direction des Affaires Politiques (MINAT-DAP)

#### Sous-Direction des Libertés Publiques (MINAT-DAP-SDLP)
- Service des Associations (MINAT-DAP-SDLP-SA)
- Service des Organisations Non Gouvernementales (MINAT-DAP-SDLP-SONG)
- Service des Jeux (MINAT-DAP-SDLP-SJ)
- Service des Cultes (MINAT-DAP-SDLP-SC)

#### Sous-Direction des Affaires Administratives et Électorales (MINAT-DAP-SDAAE)
- Service des Affaires Administratives (MINAT-DAP-SDAAE-SAA)
- Service des Affaires Électorales (MINAT-DAP-SDAAE-SAE)

#### Sous-Direction de l'Exploitation et de la Sécurité (MINAT-DAP-SDES)
- Service des Rapports et des Synthèses (MINAT-DAP-SDES-SRS)
- Service des Activités Privées de Gardiennage (MINAT-DAP-SDES-SAPG)
- Service des Armes et Munitions (MINAT-DAP-SDES-SAM)

### 2. Direction de l'Organisation du Territoire (MINAT-DOT)
- **Cellule de Coordination** (MINAT-DOT-CC)
  - 5 Chargés d'Études Assistants
- **Cellule des Questions Frontalières** (MINAT-DOT-CQF)
  - 4 Chargés d'Études Assistants

#### Sous-Direction de l'Organisation Administrative (MINAT-DOT-SDOA)
- Service des Circonscriptions Administratives (MINAT-DOT-SDOA-SCA)
- Service du Suivi des Litiges entre Unités Administratives (MINAT-DOT-SDOA-SSLUA)

#### Sous-Direction des Chefferies Traditionnelles (MINAT-DOT-SDCT)
- Service des Chefferies de 1er et 2e degré (MINAT-DOT-SDCT-SC12)
- Service des Chefferies de 3e degré (MINAT-DOT-SDCT-SC3)

### 3. Direction de la Protection Civile (MINAT-DPC)
- **Cellule des Études et de la Prévention** (MINAT-DPC-CEP)
  - 3 Chargés d'Études Assistants

#### Sous-Direction de la Coordination et des Interventions (MINAT-DPC-SDCI)
- Service de la Coordination (MINAT-DPC-SDCI-SC)
- Service de l'Assistance et des Interventions (MINAT-DPC-SDCI-SAI)

### 4. Direction des Ressources Humaines (MINAT-DRH)
- **Cellule de Gestion du Projet SIGIPES** (MINAT-DRH-CGPS)
  - 2 Chargés d'Études Assistants

#### Sous-Direction du Personnel, de la Solde et des Pensions (MINAT-DRH-SDPSP)
- Service du Personnel (MINAT-DRH-SDPSP-SP)
- Service de la Formation et des Stages (MINAT-DRH-SDPSP-SFS)
- Service de la Solde et des Pensions (MINAT-DRH-SDPSP-SSP)

#### Sous-Direction de l'Assistance (MINAT-DRH-SDA)
- Service de l'Assistance au Personnel des Services Centraux (MINAT-DRH-SDA-SAPSC)
- Service de l'Assistance aux Autorités Administratives (MINAT-DRH-SDA-SAAA)

### 5. Direction des Ressources Financières et Matérielles (MINAT-DRFM)

#### Sous-Direction du Budget (MINAT-DRFM-SDB)
- Service du Budget (MINAT-DRFM-SDB-SB)
- Service des Marchés Publics (MINAT-DRFM-SDB-SMP)

#### Sous-Direction de l'Équipement et de la Maintenance (MINAT-DRFM-SDEM)
- Service du Matériel et de la Maintenance (MINAT-DRFM-SDEM-SMM)
- Service des Infrastructures (MINAT-DRFM-SDEM-SI)
- Service des Moyens de Transport (MINAT-DRFM-SDEM-SMT)

## Statistiques de la Structure

### Par Type
- **MINISTERE:** 1
- **DIRECTION:** ~20
- **SERVICE:** ~60+
- **CELLULES:** ~20+

### Par Niveau Hiérarchique
- **Niveau 1 (Ministère):** 1
- **Niveau 2 (Directions principales):** 10
- **Niveau 3 (Divisions/Sous-directions):** ~30
- **Niveau 4 (Services/Cellules):** ~50+

## Convention de Nommage des Codes

### Format
`MINAT-[PARENT]-[STRUCTURE]-[SOUS-STRUCTURE]`

### Exemples
- `MINAT` - Ministère
- `MINAT-SG` - Secrétariat Général
- `MINAT-SG-DAJ` - Division des Affaires Juridiques (rattachée au SG)
- `MINAT-SG-DAJ-CER` - Cellule des Études et Réglementation (rattachée à la DAJ)
- `MINAT-DRH` - Direction des Ressources Humaines (non rattachée au SG)

### Abréviations Courantes
- **SG** - Secrétariat Général
- **DAJ** - Division des Affaires Juridiques
- **DSI** - Division des Systèmes d'Information
- **DRH** - Direction des Ressources Humaines
- **DRFM** - Direction des Ressources Financières et Matérielles
- **DAP** - Direction des Affaires Politiques
- **DOT** - Direction de l'Organisation du Territoire
- **DPC** - Direction de la Protection Civile
- **SD** - Sous-Direction
- **C** - Cellule
- **S** - Service

## Utilisation dans l'API

### Obtenir toute la structure
```
GET /api/structures
```

### Obtenir la hiérarchie d'une structure
```
GET /api/structures/{id}/hierarchy
```

### Obtenir les sous-structures
```
GET /api/structures/{parentId}/children
```

### Rechercher une structure
```
GET /api/structures/search?searchTerm=DAJ
```

### Obtenir par code
```
GET /api/structures/code/MINAT-SG-DAJ
```

## Notes Importantes

1. **Chargés d'Études Assistants** - Le nombre indiqué dans la description représente les postes prévus par cellule

2. **Inspection Générale** - Chaque Inspecteur Général est assisté de 3 Inspecteurs

3. **Conseillers Techniques** - 3 postes prévus directement rattachés au Ministre

4. **Structure évolutive** - La structure peut être modifiée via l'API sans redéploiement

5. **Soft Delete** - Toutes les structures peuvent être désactivées sans suppression physique

## Prochaines Étapes

Pour compléter l'implémentation:
1. Créer les **postes** (positions) pour chaque structure
2. Définir les **rangs hiérarchiques** des postes
3. Créer les **profils de poste** avec qualifications requises
4. Mapper les **grades** aux différents postes
5. Initialiser les **postes nominatifs** (Ministre, SG, Directeurs, etc.)

## Référence Technique

- **Classe:** `MinatStructureInitializer.java`
- **Package:** `com.hrms.config`
- **Profil:** `dev` (activation automatique)
- **Base de données:** Table `administrative_structures`
- **API Controller:** `AdministrativeStructureController.java`
