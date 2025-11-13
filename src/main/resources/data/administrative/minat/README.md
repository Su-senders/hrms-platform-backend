# Structures Administratives du MINAT

Données organisationnelles du Ministère de l'Administration Territoriale (MINAT).

## 📁 Structure des Fichiers

```
data/administrative/
└── minat/
    ├── structure.json                       # Structure complète du MINAT
    └── README.md                            # Cette documentation
```

## 📋 Format des Fichiers

### structure.json

Contient la hiérarchie complète des structures organisationnelles du MINAT.

```json
{
  "organization": {
    "code": "MINAT",
    "name": "Ministère de l'Administration Territoriale",
    "type": "MINISTERE",
    "structures": [
      {
        "code": "MINAT-SG",
        "name": "Secrétariat Général",
        "type": "DIRECTION",
        "structures": [...]
      }
    ]
  }
}
```

## 🏗️ Types de Structures

- **MINISTERE** : Ministère (niveau racine)
- **DIRECTION** : Directions, Divisions, Sous-Directions
- **SERVICE** : Services, Cellules

## 🔄 Chargement

Les données sont chargées via `AdministrativeStructureLoader` et initialisées par `MinatStructureInitializer`.

## 📝 Note

Ces données concernent uniquement les **structures organisationnelles** du MINAT.  
Pour les données géographiques (régions, départements), voir `data/geographic/`.

