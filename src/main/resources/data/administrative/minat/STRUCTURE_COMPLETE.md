# Structure Organisationnelle Complète du MINAT

## 📊 Vue d'Ensemble

Le Ministère de l'Administration Territoriale (MINAT) est structuré en **hiérarchie récursive** avec 3 niveaux principaux :

1. **MINISTERE** : Niveau racine (MINAT)
2. **DIRECTION** : Directions, Divisions, Sous-Directions
3. **SERVICE** : Services, Cellules

## 🏗️ Hiérarchie Complète

```
MINAT (Ministère de l'Administration Territoriale)
│
├─ Cabinet du Ministre (MINAT-CABINET)
│  └─ Type: DIRECTION
│     ├─ Poste: Chef de Cabinet
│     ├─ Poste: Ministre
│     ├─ Secrétaires (4 postes numérotés)
│     ├─ Chargés de la Comptabilité (2 postes numérotés)
│     ├─ Chauffeurs (2 postes numérotés)
│     ├─ Personnel d'appui (5 postes numérotés)
│     ├─ Personnel d'escorte (5 postes numérotés)
│     │
│     └─ Secrétariat Particulier (MINAT-CABINET-SP)
│        └─ Poste: Chef de Secrétariat Particulier
│
├─ Secrétariat Particulier (MINAT-SP)
│  └─ Type: DIRECTION
│     └─ Poste: Chef de Secrétariat Particulier
│
├─ Conseillers Techniques (MINAT-CT)
│  └─ Type: DIRECTION
│
├─ Inspection Générale (MINAT-IG)
│  └─ Type: DIRECTION
│     ├─ IG de l'Administration Territoriale (MINAT-IG-IGAT)
│     ├─ IG des Questions Électorales (MINAT-IG-IGQE)
│     └─ IG des Services (MINAT-IG-IGS)
│
├─ Secrétariat Général (MINAT-SG)
│  └─ Type: DIRECTION
│     │
│     ├─ Division des Affaires Juridiques (MINAT-SG-DAJ)
│     │  ├─ Cellule des Études et de la Réglementation (MINAT-SG-DAJ-CER)
│     │  └─ Cellule des Requêtes et du Contentieux (MINAT-SG-DAJ-CRC)
│     │
│     ├─ Division des Études, Statistiques, Planification et Coopération (MINAT-SG-DESPC)
│     │  ├─ Cellule des Études et des Statistiques (MINAT-SG-DESPC-CES)
│     │  ├─ Cellule de la Planification et des Projets (MINAT-SG-DESPC-CPP)
│     │  └─ Cellule de la Coopération (MINAT-SG-DESPC-CC)
│     │
│     ├─ Division du Suivi et du Contrôle de Gestion (MINAT-SG-DSCG)
│     │  ├─ Cellule de Suivi (MINAT-SG-DSCG-CS)
│     │  └─ Cellule du Contrôle de Gestion (MINAT-SG-DSCG-CCG)
│     │
│     ├─ Division des Systèmes d'Information (MINAT-SG-DSI)
│     │  ├─ Cellule des Études et des Développements (MINAT-SG-DSI-CED)
│     │  └─ Cellule du Suivi de l'Exploitation et de la Maintenance (MINAT-SG-DSI-CSEM)
│     │
│     ├─ Division de la Communication et des Relations Publiques (MINAT-SG-DCRP)
│     │  ├─ Cellule de Communication (MINAT-SG-DCRP-CC)
│     │  └─ Cellule des Relations Publiques (MINAT-SG-DCRP-CRP)
│     │
│     ├─ Division de la Traduction et de la Promotion du Bilinguisme (MINAT-SG-DTPB)
│     │  ├─ Cellule de Traduction (MINAT-SG-DTPB-CT)
│     │  └─ Cellule de la Promotion du Bilinguisme (MINAT-SG-DTPB-CPB)
│     │
│     ├─ Sous-Direction de l'Accueil, du Courrier et de Liaison (MINAT-SG-SDACL)
│     │  ├─ Service de l'Accueil et de l'Orientation (MINAT-SG-SDACL-SAO)
│     │  ├─ Service du Courrier et de Liaison (MINAT-SG-SDACL-SCL)
│     │  └─ Service de la Relance (MINAT-SG-SDACL-SR)
│     │
│     └─ Centre de Documentation et des Archives (MINAT-SG-CDA)
│        ├─ Service de la Documentation (MINAT-SG-CDA-SD)
│        ├─ Service du Fichier et des Archives (MINAT-SG-CDA-SFA)
│        └─ Bibliothèque (MINAT-SG-CDA-BIB)
│
├─ Direction des Affaires Politiques (MINAT-DAP)
│  └─ Type: DIRECTION
│     │
│     ├─ Sous-Direction des Libertés Publiques (MINAT-DAP-SDLP)
│     │  ├─ Service des Associations (MINAT-DAP-SDLP-SA)
│     │  ├─ Service des Organisations Non Gouvernementales (MINAT-DAP-SDLP-SONG)
│     │  ├─ Service des Jeux (MINAT-DAP-SDLP-SJ)
│     │  └─ Service des Cultes (MINAT-DAP-SDLP-SC)
│     │
│     ├─ Sous-Direction des Affaires Administratives et Électorales (MINAT-DAP-SDAAE)
│     │  ├─ Service des Affaires Administratives (MINAT-DAP-SDAAE-SAA)
│     │  └─ Service des Affaires Électorales (MINAT-DAP-SDAAE-SAE)
│     │
│     └─ Sous-Direction de l'Exploitation et de la Sécurité (MINAT-DAP-SDES)
│        ├─ Service des Rapports et des Synthèses (MINAT-DAP-SDES-SRS)
│        ├─ Service des Activités Privées de Gardiennage (MINAT-DAP-SDES-SAPG)
│        └─ Service des Armes et Munitions (MINAT-DAP-SDES-SAM)
│
├─ Direction de l'Organisation du Territoire (MINAT-DOT)
│  └─ Type: DIRECTION
│     │
│     ├─ Cellule de Coordination (MINAT-DOT-CC)
│     ├─ Cellule des Questions Frontalières (MINAT-DOT-CQF)
│     │
│     ├─ Sous-Direction de l'Organisation Administrative (MINAT-DOT-SDOA)
│     │  ├─ Service des Circonscriptions Administratives (MINAT-DOT-SDOA-SCA)
│     │  └─ Service du Suivi des Litiges entre Unités Administratives (MINAT-DOT-SDOA-SSLUA)
│     │
│     └─ Sous-Direction des Chefferies Traditionnelles (MINAT-DOT-SDCT)
│        ├─ Service des Chefferies de 1er et 2e degré (MINAT-DOT-SDCT-SC12)
│        └─ Service des Chefferies de 3e degré (MINAT-DOT-SDCT-SC3)
│
├─ Direction de la Protection Civile (MINAT-DPC)
│  └─ Type: DIRECTION
│     │
│     ├─ Cellule des Études et de la Prévention (MINAT-DPC-CEP)
│     │
│     └─ Sous-Direction de la Coordination et des Interventions (MINAT-DPC-SDCI)
│        ├─ Service de la Coordination (MINAT-DPC-SDCI-SC)
│        └─ Service de l'Assistance et des Interventions (MINAT-DPC-SDCI-SAI)
│
├─ Direction des Ressources Humaines (MINAT-DRH)
│  └─ Type: DIRECTION
│     │
│     ├─ Cellule de Gestion du Projet SIGIPES (MINAT-DRH-CGPS)
│     │
│     ├─ Sous-Direction du Personnel, de la Solde et des Pensions (MINAT-DRH-SDPSP)
│     │  ├─ Service du Personnel (MINAT-DRH-SDPSP-SP)
│     │  ├─ Service de la Formation et des Stages (MINAT-DRH-SDPSP-SFS)
│     │  └─ Service de la Solde et des Pensions (MINAT-DRH-SDPSP-SSP)
│     │
│     └─ Sous-Direction de l'Assistance (MINAT-DRH-SDA)
│        ├─ Service de l'Assistance au Personnel des Services Centraux (MINAT-DRH-SDA-SAPSC)
│        └─ Service de l'Assistance aux Autorités Administratives (MINAT-DRH-SDA-SAAA)
│
└─ Direction des Ressources Financières et Matérielles (MINAT-DRFM)
   └─ Type: DIRECTION
      │
      ├─ Sous-Direction du Budget (MINAT-DRFM-SDB)
      │  ├─ Service du Budget (MINAT-DRFM-SDB-SB)
      │  └─ Service des Marchés Publics (MINAT-DRFM-SDB-SMP)
      │
      └─ Sous-Direction de l'Équipement et de la Maintenance (MINAT-DRFM-SDEM)
         ├─ Service du Matériel et de la Maintenance (MINAT-DRFM-SDEM-SMM)
         ├─ Service des Infrastructures (MINAT-DRFM-SDEM-SI)
         └─ Service des Moyens de Transport (MINAT-DRFM-SDEM-SMT)
```

## 📊 Statistiques

### Par Niveau

| Niveau | Type | Nombre | Description |
|--------|------|--------|-------------|
| **Niveau 1** | MINISTERE | 1 | MINAT |
| **Niveau 2** | DIRECTION | 7 | Structures principales rattachées au Ministre |
| **Niveau 3** | DIRECTION | ~20 | Divisions, Sous-Directions |
| **Niveau 4** | SERVICE | ~50+ | Services, Cellules |

### Structures Principales

1. **Cabinet du Ministre** (MINAT-CABINET) - 1 sous-structure (Secrétariat Particulier)
2. **Secrétariat Particulier** (MINAT-SP)
3. **Conseillers Techniques** (MINAT-CT)
4. **Inspection Générale** (MINAT-IG) - 3 sous-structures
5. **Secrétariat Général** (MINAT-SG) - 9 divisions/sous-directions
5. **Direction des Affaires Politiques** (MINAT-DAP) - 3 sous-directions
6. **Direction de l'Organisation du Territoire** (MINAT-DOT) - 2 sous-directions
7. **Direction de la Protection Civile** (MINAT-DPC) - 1 sous-direction
8. **Direction des Ressources Humaines** (MINAT-DRH) - 2 sous-directions
9. **Direction des Ressources Financières et Matérielles** (MINAT-DRFM) - 2 sous-directions

## 🔧 Caractéristiques Techniques

### Types de Structures

- **MINISTERE** : Niveau racine (MINAT uniquement)
- **DIRECTION** : Utilisé pour Directions, Divisions, Sous-Directions
- **SERVICE** : Utilisé pour Services et Cellules

### Relations Parent-Enfant

- Chaque structure a un `parentStructure` (sauf MINAT)
- La hiérarchie est **récursive** (une structure peut avoir plusieurs niveaux de sous-structures)
- Le code est généré de manière hiérarchique (ex: `MINAT-SG-DAJ-CER`)

### Chargement

- **Fichier source** : `data/administrative/minat/structure.json`
- **Loader** : `AdministrativeStructureLoader`
- **Initializer** : `MinatStructureInitializer` (Order=1, s'exécute en premier)
- **Mode** : Chargement récursif automatique au démarrage (profil `dev`)

## 📝 Notes

- Cette structure représente l'**organisation interne** du MINAT
- Elle est **distincte** des structures géographiques (régions, départements, arrondissements)
- Les structures géographiques sont gérées par `CameroonTerritoriesInitializer` (Order=2)
- La structure peut être modifiée en éditant le fichier JSON sans recompiler le code

