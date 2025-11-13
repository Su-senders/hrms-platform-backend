# Données du Système HRMS

Structure organisée des données du système HRMS, séparant les données géographiques des données administratives.

## 📁 Structure Globale

```
data/
├── geographic/                    # Données géographiques (territoires)
│   └── cameroon/
│       ├── regions.json          # Régions et départements
│       └── arrondissements/      # Arrondissements par région
│
└── administrative/                # Données administratives (structures)
    └── minat/
        └── structure.json         # Structure organisationnelle du MINAT
```

## 🗺️ Données Géographiques

**Emplacement:** `data/geographic/`

Contient les données territoriales du Cameroun :
- **10 Régions** (Gouvernorats)
- **58 Départements** (Préfectures)
- **~360 Arrondissements** (Sous-Préfectures)

**Chargement:** `TerritorialDataLoader`  
**Initialisation:** `CameroonTerritoriesInitializer`

Voir [geographic/cameroon/README.md](./geographic/cameroon/README.md) pour plus de détails.

## 🏛️ Données Administratives

**Emplacement:** `data/administrative/`

Contient les structures organisationnelles :
- **MINAT** : Ministère de l'Administration Territoriale
  - Directions
  - Divisions
  - Services
  - Cellules

**Chargement:** `AdministrativeStructureLoader`  
**Initialisation:** `MinatStructureInitializer`

Voir [administrative/minat/README.md](./administrative/minat/README.md) pour plus de détails.

## 🔄 Séparation des Préoccupations

### Pourquoi cette séparation ?

1. **Données Géographiques** (`geographic/`)
   - Concernent la **géographie administrative** du pays
   - Structure hiérarchique : Région → Département → Arrondissement
   - Peuvent être utilisées pour d'autres systèmes
   - Relativement stables dans le temps

2. **Données Administratives** (`administrative/`)
   - Concernent l'**organisation interne** du MINAT
   - Structure hiérarchique : Ministère → Direction → Service
   - Spécifiques au MINAT
   - Peuvent évoluer avec les réorganisations

## 📝 Avantages

✅ **Séparation claire** des préoccupations  
✅ **Maintenabilité** améliorée  
✅ **Réutilisabilité** des données géographiques  
✅ **Flexibilité** pour ajouter d'autres pays/organisations  
✅ **Lisibilité** de la structure  

## 🔧 Utilisation

Les données sont chargées automatiquement au démarrage de l'application (profil `dev`) :

1. **MinatStructureInitializer** (Order=1) - Charge les structures administratives
2. **CameroonTerritoriesInitializer** (Order=2) - Charge les données géographiques

Les fichiers JSON sont dans le classpath et peuvent être modifiés sans recompiler le code.

