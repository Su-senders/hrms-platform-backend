# Export de Fiche Individuelle Complète du Personnel

## 📋 Vue d'Ensemble

Le système d'export de fiche individuelle permet de générer des documents complets regroupant **toutes les informations** d'un personnel dans un format professionnel (PDF ou Excel).

---

## 🎯 Objectif Atteint

✅ **OBJECTIF 6 - Export Fiche de Renseignement : 100% COMPLET**

Le système permet maintenant d'exporter une fiche de renseignement complète d'un personnel avec **toutes les informations disponibles** :
- Section A : Identification
- Section B : Qualifications
- Section C : Carrière
- Historique des mouvements de carrière
- Historique des formations
- Historique des congés
- Postes antérieurs

---

## 🚀 Nouveaux Endpoints

### 1. Export Fiche PDF

```http
GET /api/reports/export/personnel/{id}/fiche/pdf
```

**Description** : Exporte la fiche complète d'un personnel en PDF

**Paramètres** :
- `id` (path) : ID du personnel

**Réponse** :
- Type : `application/pdf`
- Nom du fichier : `fiche_personnel_{id}_{date}.pdf`

**Exemple** :
```bash
curl -X GET "http://localhost:8080/api/reports/export/personnel/123/fiche/pdf" \
  -H "accept: application/pdf" \
  --output fiche_personnel_123.pdf
```

---

### 2. Export Fiche Excel

```http
GET /api/reports/export/personnel/{id}/fiche/excel
```

**Description** : Exporte la fiche complète d'un personnel en Excel (multi-feuilles)

**Paramètres** :
- `id` (path) : ID du personnel

**Réponse** :
- Type : `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
- Nom du fichier : `fiche_personnel_{id}_{date}.xlsx`

**Exemple** :
```bash
curl -X GET "http://localhost:8080/api/reports/export/personnel/123/fiche/excel" \
  -H "accept: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" \
  --output fiche_personnel_123.xlsx
```

---

## 📄 Contenu de la Fiche PDF

### En-tête
- Titre : "FICHE DE RENSEIGNEMENT DU PERSONNEL"
- Sous-titre : "MINISTÈRE DE L'ADMINISTRATION TERRITORIALE (MINAT)"
- Matricule du personnel
- Nom complet
- Date de génération

### Section A : Identification du Personnel

#### A.1 - État Civil
| Champ | Description |
|-------|-------------|
| Nom | Nom de famille |
| Prénom | Prénom(s) |
| Sexe | Masculin/Féminin |
| Date de Naissance | JJ/MM/AAAA |
| Âge | Calculé automatiquement |
| Nationalité | ORIGINE ou NATURALISATION |
| Date de Naturalisation | Si applicable |
| N° CNI | Numéro de Carte Nationale d'Identité |

#### A.2 - Unité Administrative d'Origine
| Champ | Description |
|-------|-------------|
| Région d'Origine | Région d'origine (10 régions) |
| Département d'Origine | Département d'origine (58 départements) |
| Arrondissement d'Origine | Arrondissement d'origine (360 arrondissements) |
| Village | Village d'origine |
| Tribu | Tribu d'origine |

#### A.3 - Filiation
| Champ | Description |
|-------|-------------|
| Nom du Père | Nom complet du père |
| Nom de la Mère | Nom complet de la mère |

---

### Section B : Qualifications

#### B.1 - Diplôme de Recrutement
| Champ | Description |
|-------|-------------|
| Intitulé | Titre du diplôme |
| Type de Diplôme | CEP, BEPC, BAC, Licence, Master, etc. |
| Date d'Obtention | JJ/MM/AAAA |
| Lieu d'Obtention | Établissement/Ville |
| Niveau d'Instruction | Niveau scolaire |
| Spécialité | Spécialité du diplôme |
| Domaine d'Étude | 45+ domaines disponibles |

#### B.2 - Diplôme le Plus Élevé
| Champ | Description |
|-------|-------------|
| Intitulé | Titre du diplôme |
| Type de Diplôme | CEP, BEPC, BAC, Licence, Master, etc. |
| Date d'Obtention | JJ/MM/AAAA |
| Lieu d'Obtention | Établissement/Ville |
| Niveau d'Instruction | Niveau scolaire |
| Spécialité | Spécialité du diplôme |
| Domaine d'Étude | 45+ domaines disponibles |

---

### Section C : Carrière

#### C.2 - Situation au Recrutement
| Champ | Description |
|-------|-------------|
| N° Acte de Recrutement | Numéro de l'acte |
| Nature de l'Acte | DECRET, ARRETE, DECISION, CONTRAT_TRAVAIL |
| Date de Signature | JJ/MM/AAAA |
| Signataire | Autorité signataire |
| Date de Prise d'Effet | JJ/MM/AAAA |
| Mode de Recrutement | SUR_CONCOURS, SUR_TITRE, SPECIAL, etc. |
| Profession | Profession au recrutement |
| Catégorie | CATEGORIE_A, B, C, D |
| Administration d'Origine | Administration de provenance |
| Ancienneté dans l'Admin. Publique | Calculé automatiquement (années) |

#### C.3 - Situation Actuelle
| Champ | Description |
|-------|-------------|
| Type de Personnel | FONCTIONNAIRE, CONTRACTUEL, DECISIONNAIRE |
| Corps de Métier | Corps métier actuel |
| Grade | Grade actuel |
| Catégorie | CATEGORIE_A, B, C, D |
| Classe | Classe actuelle |
| Échelon | Échelon actuel |
| Indice | Indice salarial |
| Poste Actuel | Titre du poste |
| Date d'Affectation | JJ/MM/AAAA |
| N° Acte Actuel | Numéro de l'acte |
| Nature Acte Actuel | ACTE_INTEGRATION, AVANCE_GRADE, etc. |
| Fonction 1 | Autre fonction actuelle 1 |
| Fonction 2 | Autre fonction actuelle 2 |
| Fonction 3 | Autre fonction actuelle 3 |

#### C.4 - Employeur
| Champ | Description |
|-------|-------------|
| Structure | Structure d'affectation |
| Lieu d'Affectation | Lieu précis (bureau, bâtiment) |
| Ville | Ville du bureau |
| Tél. Bureau | Téléphone de bureau |
| Tél. Portable | Téléphone portable |
| Fax | Numéro de fax |
| Email Professionnel | Email professionnel (unique) |

---

### Historique des Mouvements de Carrière

Tableau avec les colonnes :
- **Date** : Date effective du mouvement
- **Type de Mouvement** : AFFECTATION, MUTATION, PROMOTION, etc.
- **Structure Source** : Structure de départ
- **Structure Destination** : Structure d'arrivée
- **Statut** : PENDING, APPROVED, EXECUTED, CANCELLED

**15 types de mouvements** supportés :
1. AFFECTATION
2. MUTATION
3. PROMOTION
4. DETACHEMENT
5. MISE_A_DISPOSITION
6. FORMATION
7. STAGE
8. INTEGRATION
9. RETRAITE
10. DECES
11. SUSPENSION
12. REVOCATION
13. DEMISSION
14. DISPONIBILITE
15. REINTEGRATION

---

### Historique des Formations

Tableau avec les colonnes :
- **Domaine** : Domaine de la formation
- **Formateur** : Organisme formateur
- **Début** : Date de début
- **Fin** : Date de fin
- **Statut** : PLANNED, IN_PROGRESS, COMPLETED, CANCELLED, SUSPENDED

---

### Historique des Congés

Tableau avec les colonnes :
- **Motif** : ADMINISTRATIF, ANNUEL, MALADIE, MATERNITE, etc.
- **Début** : Date de début (effective_date)
- **Fin** : Date de fin (expiry_date)
- **Durée** : Durée en jours
- **Statut** : REQUESTED, APPROVED, IN_PROGRESS, COMPLETED, CANCELLED

---

### Postes Antérieurs

Tableau avec les colonnes :
- **Poste** : Titre du poste
- **Structure** : Structure d'affectation
- **Début** : Date de début
- **Fin** : Date de fin

---

### Pied de Page
- Message : "Document généré automatiquement par le système HRMS - MINAT"

---

## 📊 Contenu de la Fiche Excel

Le fichier Excel contient **5 feuilles** :

### Feuille 1 : Informations Générales
- Toutes les sections A, B, C regroupées
- Format clé-valeur avec styles
- Sections clairement identifiées

### Feuille 2 : Mouvements de Carrière
- Colonnes : Date, Type, Structure Source, Structure Destination, Poste Source, Poste Destination, Statut
- Trié par date (plus récent en premier)

### Feuille 3 : Formations
- Colonnes : Domaine, Formateur, Début, Fin, Durée (jours), Lieu, Statut, Certificat
- Trié par date de début (plus récent en premier)

### Feuille 4 : Congés
- Colonnes : Motif, Début, Fin, Durée (jours), Statut, N° Décision, Notes
- Trié par date effective (plus récent en premier)

### Feuille 5 : Postes Antérieurs
- Colonnes : Poste, Structure, Début, Fin, Durée
- Trié par date de fin (plus récent en premier)

---

## 🛠️ Implémentation Technique

### Fichiers Créés

#### 1. PersonnelFicheExportService.java
**Localisation** : `src/main/java/com/hrms/service/PersonnelFicheExportService.java`

**Responsabilités** :
- Export PDF complet avec iText
- Export Excel multi-feuilles avec Apache POI
- Formatage professionnel des documents
- Gestion des relations (lazy loading)

**Méthodes Principales** :
```java
// Export PDF
public ByteArrayOutputStream exportPersonnelFicheToPDF(Long personnelId) throws DocumentException

// Export Excel
public ByteArrayOutputStream exportPersonnelFicheToExcel(Long personnelId) throws IOException
```

**Méthodes Helper PDF** :
- `addPDFHeader()` - En-tête du document
- `addPDFSectionA()` - Section A : Identification
- `addPDFSectionB()` - Section B : Qualifications
- `addPDFSectionC()` - Section C : Carrière
- `addPDFCareerMovementsHistory()` - Historique mouvements
- `addPDFTrainingsHistory()` - Historique formations
- `addPDFLeavesHistory()` - Historique congés
- `addPDFPreviousPositions()` - Postes antérieurs
- `addPDFFooter()` - Pied de page

**Méthodes Helper Excel** :
- `addExcelMainInfoSheet()` - Feuille informations principales
- `addExcelCareerMovementsSheet()` - Feuille mouvements
- `addExcelTrainingsSheet()` - Feuille formations
- `addExcelLeavesSheet()` - Feuille congés
- `addExcelPreviousPositionsSheet()` - Feuille postes antérieurs

**Utilitaires** :
- `formatDate()` - Formatage des dates (dd/MM/yyyy)
- `safeString()` - Gestion des valeurs null
- `safeRelation()` - Gestion des relations null
- `createInfoTable()` - Création de tables PDF
- `addTableRow()` - Ajout de lignes dans tables PDF
- `createTitleStyle()`, `createSectionStyle()`, etc. - Styles Excel

#### 2. ReportController.java (Mise à jour)
**Localisation** : `src/main/java/com/hrms/controller/ReportController.java`

**Nouveaux endpoints ajoutés** :
```java
@GetMapping("/export/personnel/{id}/fiche/pdf")
public ResponseEntity<byte[]> exportPersonnelFicheToPDF(@PathVariable Long id)

@GetMapping("/export/personnel/{id}/fiche/excel")
public ResponseEntity<byte[]> exportPersonnelFicheToExcel(@PathVariable Long id)
```

**Gestion des erreurs** :
- `DocumentException` → HTTP 500 (Internal Server Error)
- `IOException` → HTTP 500 (Internal Server Error)
- `RuntimeException` (Personnel non trouvé) → HTTP 404 (Not Found)

---

### Dépendances Utilisées

#### iText (PDF)
```xml
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itextpdf</artifactId>
    <version>5.5.13.3</version>
</dependency>
```

**Classes utilisées** :
- `Document` - Document PDF
- `PdfWriter` - Écriture du PDF
- `PdfPTable` - Tables
- `PdfPCell` - Cellules de table
- `Paragraph` - Paragraphes
- `Phrase` - Texte simple
- `Font`, `FontFactory` - Polices
- `BaseColor` - Couleurs

#### Apache POI (Excel)
```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.3</version>
</dependency>
```

**Classes utilisées** :
- `XSSFWorkbook` - Classeur Excel
- `Sheet` - Feuille Excel
- `Row` - Ligne
- `Cell` - Cellule
- `CellStyle` - Styles de cellule
- `Font` - Polices
- `IndexedColors` - Couleurs

---

### Repositories Utilisés

Le service utilise 5 repositories :

1. **PersonnelRepository**
   - `findById(Long id)` - Récupérer le personnel

2. **CareerMovementRepository**
   - `findByPersonnelId(Long personnelId)` - Historique des mouvements

3. **ProfessionalTrainingRepository**
   - `findByPersonnelId(Long personnelId)` - Historique des formations

4. **PersonnelLeaveRepository**
   - `findByPersonnelId(Long personnelId)` - Historique des congés

5. **PreviousPositionRepository**
   - `findByPersonnelId(Long personnelId)` - Postes antérieurs

---

## 🎨 Formatage et Styles

### PDF

#### Polices
- **Titre** : Helvetica Bold, 18pt, Noir
- **Sous-titre** : Helvetica, 12pt, Gris foncé
- **Section** : Helvetica Bold, 13pt, Noir
- **Sous-section** : Helvetica Bold, 11pt, Bleu (50, 50, 150)
- **Label** : Helvetica Bold, 9pt, Noir (fond gris clair)
- **Valeur** : Helvetica, 9pt, Noir
- **En-tête de table** : Helvetica Bold, 9pt, Blanc (fond gris foncé)
- **Données de table** : Helvetica, 8pt, Noir

#### Mise en page
- Format : A4 (210 x 297 mm)
- Tables : 100% de largeur
- Espacement avant sections : 15pt
- Espacement après sections : 10pt
- Padding cellules : 5pt
- Bordures : Toutes les cellules

### Excel

#### Polices
- **Titre** : Bold, 14pt, Centré
- **Section** : Bold, 12pt, Bleu, Fond gris 25%
- **Label** : Bold, 10pt, Fond gris 25%
- **Valeur** : Normal, 10pt
- **En-tête de table** : Bold, 11pt, Blanc, Fond gris 80%, Centré

#### Mise en page
- Auto-sizing des colonnes
- Bordures sur toutes les cellules
- Alignement horizontal : Centre pour en-têtes

---

## 🔍 Gestion des Données Manquantes

Le système gère intelligemment les valeurs null ou manquantes :

### Méthodes de Sécurité

```java
// Pour les champs simples
private String safeString(Object value) {
    return value != null ? value.toString() : "N/A";
}

// Pour les relations (avec lazy loading)
private <T> String safeRelation(T entity, Function<T, String> getter) {
    return entity != null ? getter.apply(entity) : "N/A";
}
```

### Exemples

```java
// Champ simple null
safeString(personnel.getVillageOrigine())
// → Affiche "N/A" si null

// Relation null
safeRelation(personnel.getStructure(), AdministrativeStructure::getName)
// → Affiche "N/A" si structure est null

// Date null
formatDate(personnel.getNaturalizationDate())
// → Affiche "N/A" si date est null
```

---

## 📝 Exemples d'Utilisation

### 1. Export PDF via API REST

```bash
# Export fiche PDF pour personnel ID 123
curl -X GET "http://localhost:8080/api/reports/export/personnel/123/fiche/pdf" \
  -H "Authorization: Bearer {token}" \
  -H "accept: application/pdf" \
  --output fiche_personnel_123.pdf
```

### 2. Export Excel via API REST

```bash
# Export fiche Excel pour personnel ID 456
curl -X GET "http://localhost:8080/api/reports/export/personnel/456/fiche/excel" \
  -H "Authorization: Bearer {token}" \
  -H "accept: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" \
  --output fiche_personnel_456.xlsx
```

### 3. Utilisation dans un Frontend (React/Angular/Vue)

```javascript
// Fonction pour télécharger la fiche PDF
async function downloadPersonnelFichePDF(personnelId) {
  const response = await fetch(
    `http://localhost:8080/api/reports/export/personnel/${personnelId}/fiche/pdf`,
    {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Accept': 'application/pdf'
      }
    }
  );

  const blob = await response.blob();
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `fiche_personnel_${personnelId}_${new Date().toISOString().split('T')[0]}.pdf`;
  document.body.appendChild(a);
  a.click();
  window.URL.revokeObjectURL(url);
  document.body.removeChild(a);
}

// Fonction pour télécharger la fiche Excel
async function downloadPersonnelFicheExcel(personnelId) {
  const response = await fetch(
    `http://localhost:8080/api/reports/export/personnel/${personnelId}/fiche/excel`,
    {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Accept': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      }
    }
  );

  const blob = await response.blob();
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `fiche_personnel_${personnelId}_${new Date().toISOString().split('T')[0]}.xlsx`;
  document.body.appendChild(a);
  a.click();
  window.URL.revokeObjectURL(url);
  document.body.removeChild(a);
}
```

---

## ✅ Tests Recommandés

### Tests Unitaires

```java
@Test
void testExportPersonnelFicheToPDF_Success() {
    // Given
    Long personnelId = 1L;
    Personnel personnel = createTestPersonnel();
    when(personnelRepository.findById(personnelId)).thenReturn(Optional.of(personnel));

    // When
    ByteArrayOutputStream result = personnelFicheExportService.exportPersonnelFicheToPDF(personnelId);

    // Then
    assertNotNull(result);
    assertTrue(result.size() > 0);
}

@Test
void testExportPersonnelFicheToExcel_Success() {
    // Given
    Long personnelId = 1L;
    Personnel personnel = createTestPersonnel();
    when(personnelRepository.findById(personnelId)).thenReturn(Optional.of(personnel));

    // When
    ByteArrayOutputStream result = personnelFicheExportService.exportPersonnelFicheToExcel(personnelId);

    // Then
    assertNotNull(result);
    assertTrue(result.size() > 0);
}

@Test
void testExportPersonnelFiche_PersonnelNotFound() {
    // Given
    Long personnelId = 999L;
    when(personnelRepository.findById(personnelId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(RuntimeException.class, () -> {
        personnelFicheExportService.exportPersonnelFicheToPDF(personnelId);
    });
}
```

### Tests d'Intégration

```java
@Test
@WithMockUser
void testExportEndpoint_PDF() throws Exception {
    mockMvc.perform(get("/api/reports/export/personnel/1/fiche/pdf"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_PDF))
        .andExpect(header().exists("Content-Disposition"));
}

@Test
@WithMockUser
void testExportEndpoint_Excel() throws Exception {
    mockMvc.perform(get("/api/reports/export/personnel/1/fiche/excel"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .andExpect(header().exists("Content-Disposition"));
}
```

---

## 🚀 Améliorations Futures Possibles

1. **Ajout d'un logo MINAT** dans l'en-tête PDF
2. **QR Code** avec URL de vérification du document
3. **Watermark** "CONFIDENTIEL" sur les pages PDF
4. **Signature numérique** des documents PDF
5. **Export multi-personnels** (sélection de plusieurs personnels)
6. **Templates personnalisables** par structure
7. **Génération asynchrone** avec notification email
8. **Cache** des fiches fréquemment demandées
9. **Compression** des fichiers Excel volumineux
10. **Aperçu en ligne** avant téléchargement

---

## 📊 Résumé des Objectifs

| Objectif | État | Taux de Complétion |
|----------|------|-------------------|
| 1. CRUD Personnel | ✅ Complet | 100% |
| 2. Traçabilité Mouvements | ✅ Complet | 100% |
| 3. Historique Formations | ✅ Complet | 100% |
| 4. Mouvements de Carrière | ✅ Complet | 100% |
| 5. Export Listes | ✅ Complet | 100% |
| **6. Export Fiche Individuelle** | **✅ Complet** | **100%** |

---

## 🎉 Conclusion

Le système d'export de fiche individuelle est maintenant **100% fonctionnel** et permet :

✅ Export PDF professionnel avec toutes les sections
✅ Export Excel multi-feuilles avec toutes les données
✅ Gestion intelligente des valeurs manquantes
✅ Formatage professionnel et lisible
✅ API REST complète et documentée
✅ Traçabilité complète de toutes les informations

**La plateforme HRMS atteint maintenant 100% des objectifs définis !**
