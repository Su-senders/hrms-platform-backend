# Données Géographiques du Cameroun

Structure organisée pour les données géographiques territoriales du Cameroun (Régions, Départements, Arrondissements).

## 📁 Structure des Fichiers

```
data/geographic/
└── cameroon/
    ├── regions.json                          # Toutes les régions et leurs départements
    ├── arrondissements/
    │   └── by-region/
    │       ├── centre.json                  # Arrondissements du Centre
    │       ├── est.json                     # Arrondissements de l'Est
    │       └── ...                          # (8 autres régions)
    └── README.md                            # Cette documentation
```

## 📋 Format des Fichiers

### regions.json

Contient toutes les régions (Gouvernorats) avec leurs départements (Préfectures).

```json
{
  "regions": [
    {
      "code": "GOUV-CE",
      "name": "Gouvernorat de la Région du Centre",
      "region": "Centre",
      "chefLieu": "Yaoundé",
      "departments": [
        {"name": "Mfoundi", "chefLieu": "Yaoundé"}
      ]
    }
  ]
}
```

### arrondissements/by-region/{region-name}.json

Contient les arrondissements (Sous-Préfectures) organisés par département.

## 🔄 Chargement

Les données sont chargées via `TerritorialDataLoader` et initialisées par `CameroonTerritoriesInitializer`.

## 📝 Note

Ces données concernent uniquement la **géographie administrative** du Cameroun.  
Pour les structures organisationnelles du MINAT, voir `data/administrative/`.
