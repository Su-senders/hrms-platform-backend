# Postes du MINAT

## 📋 Hiérarchie des Postes

Selon la structure organisationnelle du MINAT, chaque structure a un poste de direction spécifique :

### Niveau Ministère
- **1 Ministre** : À la tête du MINAT

### Niveau Direction
- **1 Directeur** : À la tête de chaque Direction principale
  - Direction des Affaires Politiques (MINAT-DAP)
  - Direction de l'Organisation du Territoire (MINAT-DOT)
  - Direction de la Protection Civile (MINAT-DPC)
  - Direction des Ressources Humaines (MINAT-DRH)
  - Direction des Ressources Financières et Matérielles (MINAT-DRFM)

- **1 Secrétaire Général** : À la tête du Secrétariat Général (MINAT-SG)
- **1 Chef de Secrétariat Particulier** : À la tête du Secrétariat Particulier (MINAT-SP)
- **1 Inspecteur Général** : À la tête de l'Inspection Générale (MINAT-IG)

### Niveau Division
- **1 Chef de Division** : À la tête de chaque Division
  - Toutes les divisions du Secrétariat Général (MINAT-SG-D*)
  - Exemples :
    - Chef de Division des Affaires Juridiques (MINAT-SG-DAJ)
    - Chef de Division des Études, Statistiques, Planification et Coopération (MINAT-SG-DESPC)
    - Chef de Division du Suivi et du Contrôle de Gestion (MINAT-SG-DSCG)
    - etc.

### Niveau Sous-Direction
- **1 Sous-Directeur** : À la tête de chaque Sous-Direction
  - Toutes les sous-directions (codes contenant -SD)
  - Exemples :
    - Sous-Directeur de l'Accueil, du Courrier et de Liaison (MINAT-SG-SDACL)
    - Sous-Directeur des Libertés Publiques (MINAT-DAP-SDLP)
    - Sous-Directeur de la Coordination et des Interventions (MINAT-DPC-SDCI)
    - etc.

### Niveau Service/Cellule
- **1 Chef de Service** : À la tête de chaque Service
  - Tous les services (codes contenant -S suivi d'une lettre)
  - Exemples :
    - Chef de Service de l'Accueil et de l'Orientation (MINAT-SG-SDACL-SAO)
    - Chef de Service du Courrier et de Liaison (MINAT-SG-SDACL-SCL)
    - Chef de Service des Associations (MINAT-DAP-SDLP-SA)
    - etc.

- **1 Chef de Cellule** : À la tête de chaque Cellule
  - Toutes les cellules (codes contenant -C suivi d'une lettre)
  - Exemples :
    - Chef de Cellule des Études et de la Réglementation (MINAT-SG-DAJ-CER)
    - Chef de Cellule des Requêtes et du Contentieux (MINAT-SG-DAJ-CRC)
    - Chef de Cellule de Coordination (MINAT-DOT-CC)
    - etc.

## 🔧 Règles de Création Automatique

Les postes sont créés automatiquement selon les règles suivantes :

1. **Ministre** : Créé pour MINAT uniquement
2. **Directeur** : Créé pour chaque Direction principale (MINAT-D*)
3. **Secrétaire Général** : Créé pour MINAT-SG uniquement
4. **Chef de Secrétariat Particulier** : Créé pour MINAT-SP uniquement
5. **Inspecteur Général** : Créé pour MINAT-IG uniquement
6. **Chef de Division** : Créé pour chaque Division (MINAT-SG-D*)
7. **Sous-Directeur** : Créé pour chaque Sous-Direction (codes contenant -SD)
8. **Chef de Service** : Créé pour chaque Service (codes contenant -S[A-Z])
9. **Chef de Cellule** : Créé pour chaque Cellule (codes contenant -C[A-Z])

## 📊 Exemple de Structure avec Postes

```
MINAT
└─ Poste: Ministre
│
├─ Secrétariat Général (MINAT-SG)
│  └─ Poste: Secrétaire Général
│     │
│     ├─ Division des Affaires Juridiques (MINAT-SG-DAJ)
│     │  └─ Poste: Chef de Division
│     │     │
│     │     ├─ Cellule des Études et de la Réglementation (MINAT-SG-DAJ-CER)
│     │     │  └─ Poste: Chef de Cellule
│     │     │
│     │     └─ Cellule des Requêtes et du Contentieux (MINAT-SG-DAJ-CRC)
│     │        └─ Poste: Chef de Cellule
│     │
│     └─ Sous-Direction de l'Accueil, du Courrier et de Liaison (MINAT-SG-SDACL)
│        └─ Poste: Sous-Directeur
│           │
│           ├─ Service de l'Accueil et de l'Orientation (MINAT-SG-SDACL-SAO)
│           │  └─ Poste: Chef de Service
│           │
│           └─ Service du Courrier et de Liaison (MINAT-SG-SDACL-SCL)
│              └─ Poste: Chef de Service
│
└─ Direction des Affaires Politiques (MINAT-DAP)
   └─ Poste: Directeur
      │
      └─ Sous-Direction des Libertés Publiques (MINAT-DAP-SDLP)
         └─ Poste: Sous-Directeur
            │
            └─ Service des Associations (MINAT-DAP-SDLP-SA)
               └─ Poste: Chef de Service
```

## 🚀 Initialisation

Les postes sont créés automatiquement par `MinatPositionsInitializer` qui :
1. Parcourt récursivement toutes les structures du MINAT
2. Identifie le type de poste approprié selon le code et le type de structure
3. Crée le poste avec le statut VACANT
4. Assure qu'un seul poste de direction est créé par structure

## 📝 Notes

- Tous les postes de direction sont **nominatifs** (isNominative = true)
- Tous les postes de direction sont **uniques par structure** (isUniquePerStructure = true)
- Tous les postes de direction sont **créés automatiquement** (autoCreate = true)
- Les postes sont créés avec le statut **VACANT** et doivent être pourvus ultérieurement

