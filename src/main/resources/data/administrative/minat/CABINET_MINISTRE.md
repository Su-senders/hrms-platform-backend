# Cabinet du Ministre

## 📋 Structure

Le **Cabinet du Ministre** (MINAT-CABINET) est la structure de direction immédiate du Ministre de l'Administration Territoriale.

## 🏗️ Hiérarchie

```
MINAT
└─ Cabinet du Ministre (MINAT-CABINET)
   ├─ Poste: Chef de Cabinet
   ├─ Poste: Ministre (1)
   ├─ Secrétariat Particulier (MINAT-CABINET-SP)
   │  └─ Poste: Chef de Secrétariat Particulier
   │
   └─ Postes du Cabinet:
      ├─ Secrétaire N°1 à N°4 (4 postes)
      ├─ Chargé de la Comptabilité N°1 à N°2 (2 postes)
      ├─ Chauffeur N°1 à N°2 (2 postes)
      ├─ Personnel d'appui N°1 à N°5 (5 postes)
      └─ Personnel d'escorte N°1 à N°5 (5 postes)
```

## 👥 Postes du Cabinet

### Poste de Direction
- **1 Chef de Cabinet** : Responsable du Cabinet du Ministre

### Poste Ministériel
- **1 Ministre** : Ministre de l'Administration Territoriale

### Secrétariat Particulier
- **1 Chef de Secrétariat Particulier** : Responsable du Secrétariat Particulier

### Personnel du Cabinet

#### Secrétaires (4 postes)
- **Secrétaire N°1**
- **Secrétaire N°2**
- **Secrétaire N°3**
- **Secrétaire N°4**

#### Chargés de la Comptabilité (2 postes)
- **Chargé de la Comptabilité N°1**
- **Chargé de la Comptabilité N°2**

#### Chauffeurs (2 postes)
- **Chauffeur N°1**
- **Chauffeur N°2**

#### Personnel d'appui (5 postes)
- **Personnel d'appui N°1**
- **Personnel d'appui N°2**
- **Personnel d'appui N°3**
- **Personnel d'appui N°4**
- **Personnel d'appui N°5**

#### Personnel d'escorte (5 postes)
- **Personnel d'escorte N°1**
- **Personnel d'escorte N°2**
- **Personnel d'escorte N°3**
- **Personnel d'escorte N°4**
- **Personnel d'escorte N°5**

## 📊 Résumé

| Type de Poste | Nombre | Numérotation |
|---------------|--------|--------------|
| Chef de Cabinet | 1 | - |
| Ministre | 1 | - |
| Chef de Secrétariat Particulier | 1 | - |
| Secrétaire | 4 | N°1 à N°4 |
| Chargé de la Comptabilité | 2 | N°1 à N°2 |
| Chauffeur | 2 | N°1 à N°2 |
| Personnel d'appui | 5 | N°1 à N°5 |
| Personnel d'escorte | 5 | N°1 à N°5 |
| **TOTAL** | **21** | |

## 🔧 Création Automatique

Tous les postes du Cabinet sont créés automatiquement par `MinatPositionsInitializer` :
- ✅ Poste de direction (Chef de Cabinet)
- ✅ Poste ministériel (Ministre)
- ✅ Postes multiples numérotés selon les spécifications

## 📝 Notes

- Le **Secrétariat Particulier** est une sous-structure du Cabinet
- Le **Ministre** a un poste au niveau MINAT et également dans le Cabinet
- Tous les postes sont créés avec le statut **VACANT** par défaut
- Les postes multiples sont numérotés de N°1 à N°X selon le nombre spécifié

