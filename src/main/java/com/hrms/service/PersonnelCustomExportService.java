package com.hrms.service;

import com.hrms.dto.ExportConfigurationDTO;
import com.hrms.dto.PersonnelDTO;
import com.hrms.dto.PersonnelSearchDTO;
import com.hrms.entity.Personnel;
import com.hrms.mapper.PersonnelMapper;
import com.hrms.repository.PersonnelRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service d'export personnalisé de personnels
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PersonnelCustomExportService {

    private final PersonnelRepository personnelRepository;
    private final PersonnelService personnelService;
    private final PersonnelMapper personnelMapper;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Mapping des colonnes disponibles
     */
    private static final Map<String, ColumnDefinition> AVAILABLE_COLUMNS = new HashMap<>();
    
    static {
        AVAILABLE_COLUMNS.put("matricule", new ColumnDefinition("Matricule", "matricule"));
        AVAILABLE_COLUMNS.put("nomComplet", new ColumnDefinition("Nom Complet", "fullName"));
        AVAILABLE_COLUMNS.put("prenom", new ColumnDefinition("Prénom", "firstName"));
        AVAILABLE_COLUMNS.put("nom", new ColumnDefinition("Nom", "lastName"));
        AVAILABLE_COLUMNS.put("dateNaissance", new ColumnDefinition("Date de Naissance", "dateOfBirth"));
        AVAILABLE_COLUMNS.put("age", new ColumnDefinition("Âge", "age"));
        AVAILABLE_COLUMNS.put("cni", new ColumnDefinition("CNI", "cniNumber"));
        AVAILABLE_COLUMNS.put("grade", new ColumnDefinition("Grade", "grade"));
        AVAILABLE_COLUMNS.put("corps", new ColumnDefinition("Corps", "corps"));
        AVAILABLE_COLUMNS.put("categorie", new ColumnDefinition("Catégorie", "category"));
        AVAILABLE_COLUMNS.put("echelon", new ColumnDefinition("Échelon", "echelon"));
        AVAILABLE_COLUMNS.put("indice", new ColumnDefinition("Indice", "indice"));
        AVAILABLE_COLUMNS.put("poste", new ColumnDefinition("Poste Actuel", "currentPosition"));
        AVAILABLE_COLUMNS.put("structure", new ColumnDefinition("Structure", "structure"));
        AVAILABLE_COLUMNS.put("situation", new ColumnDefinition("Situation", "situation"));
        AVAILABLE_COLUMNS.put("statut", new ColumnDefinition("Statut", "status"));
        AVAILABLE_COLUMNS.put("dateEmbauche", new ColumnDefinition("Date d'Embauche", "hireDate"));
        AVAILABLE_COLUMNS.put("ancienneteAdmin", new ColumnDefinition("Ancienneté Admin", "seniorityInAdministration"));
        AVAILABLE_COLUMNS.put("anciennetePoste", new ColumnDefinition("Ancienneté au Poste", "seniorityInPost"));
        AVAILABLE_COLUMNS.put("dateRetraite", new ColumnDefinition("Date de Retraite", "retirementDate"));
        AVAILABLE_COLUMNS.put("telephone", new ColumnDefinition("Téléphone", "phone"));
        AVAILABLE_COLUMNS.put("mobile", new ColumnDefinition("Mobile", "mobile"));
        AVAILABLE_COLUMNS.put("email", new ColumnDefinition("Email", "email"));
        AVAILABLE_COLUMNS.put("regionOrigine", new ColumnDefinition("Région d'Origine", "regionOrigine"));
        AVAILABLE_COLUMNS.put("departmentOrigine", new ColumnDefinition("Département d'Origine", "departmentOrigine"));
        AVAILABLE_COLUMNS.put("arrondissementOrigine", new ColumnDefinition("Arrondissement d'Origine", "arrondissementOrigine"));
    }

    /**
     * Export personnalisé en Excel
     */
    public ByteArrayOutputStream exportCustomToExcel(ExportConfigurationDTO config) throws IOException {
        log.info("Export personnalisé Excel avec {} colonnes", 
                config.getSelectedColumns() != null ? config.getSelectedColumns().size() : "toutes");

        // Récupérer les personnels avec filtres
        List<Personnel> personnelList = getFilteredPersonnel(config.getFilters(), config.getSortBy(), config.getSortDirection());

        // Déterminer les colonnes à exporter
        List<String> columnsToExport = determineColumns(config);

        // Créer le workbook
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Personnel");

        // Styles
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = workbook.createCellStyle();

        // Créer les en-têtes
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columnsToExport.size(); i++) {
            String columnKey = columnsToExport.get(i);
            ColumnDefinition colDef = AVAILABLE_COLUMNS.get(columnKey);
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(colDef != null ? colDef.getHeader() : columnKey);
            cell.setCellStyle(headerStyle);
        }

        // Remplir les données
        int rowNum = 1;
        for (Personnel personnel : personnelList) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < columnsToExport.size(); i++) {
                String columnKey = columnsToExport.get(i);
                Cell cell = row.createCell(i);
                setCellValue(cell, personnel, columnKey);
                cell.setCellStyle(dataStyle);
            }
        }

        // Auto-size columns
        for (int i = 0; i < columnsToExport.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        log.info("Export personnalisé Excel terminé : {} lignes", personnelList.size());
        return outputStream;
    }

    /**
     * Export personnalisé en PDF
     */
    public ByteArrayOutputStream exportCustomToPDF(ExportConfigurationDTO config) throws DocumentException {
        log.info("Export personnalisé PDF avec {} colonnes", 
                config.getSelectedColumns() != null ? config.getSelectedColumns().size() : "toutes");

        // Récupérer les personnels avec filtres
        List<Personnel> personnelList = getFilteredPersonnel(config.getFilters(), config.getSortBy(), config.getSortDirection());

        // Déterminer les colonnes à exporter
        List<String> columnsToExport = determineColumns(config);

        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        // Titre
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
        Paragraph title = new Paragraph("Liste du Personnel - MINAT", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Date de génération
        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
        Paragraph date = new Paragraph("Généré le: " + LocalDate.now().format(DATE_FORMATTER), dateFont);
        date.setAlignment(Element.ALIGN_RIGHT);
        date.setSpacingAfter(20);
        document.add(date);

        // Table
        PdfPTable table = new PdfPTable(columnsToExport.size());
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // En-têtes
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.WHITE);
        for (String columnKey : columnsToExport) {
            ColumnDefinition colDef = AVAILABLE_COLUMNS.get(columnKey);
            PdfPCell cell = new PdfPCell(new Phrase(colDef != null ? colDef.getHeader() : columnKey, headerFont));
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }

        // Données
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);
        for (Personnel personnel : personnelList) {
            for (String columnKey : columnsToExport) {
                String value = getCellValueAsString(personnel, columnKey);
                table.addCell(new Phrase(value, dataFont));
            }
        }

        document.add(table);

        // Footer
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, BaseColor.GRAY);
        Paragraph footer = new Paragraph("Total: " + personnelList.size() + " personnel(s)", footerFont);
        footer.setAlignment(Element.ALIGN_RIGHT);
        footer.setSpacingBefore(20);
        document.add(footer);

        document.close();

        log.info("Export personnalisé PDF terminé : {} lignes", personnelList.size());
        return outputStream;
    }

    /**
     * Export personnalisé en CSV
     */
    public ByteArrayOutputStream exportCustomToCSV(ExportConfigurationDTO config) throws IOException {
        log.info("Export personnalisé CSV avec {} colonnes", 
                config.getSelectedColumns() != null ? config.getSelectedColumns().size() : "toutes");

        // Récupérer les personnels avec filtres
        List<Personnel> personnelList = getFilteredPersonnel(config.getFilters(), config.getSortBy(), config.getSortDirection());

        // Déterminer les colonnes à exporter
        List<String> columnsToExport = determineColumns(config);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StringBuilder csv = new StringBuilder();

        // En-têtes
        for (int i = 0; i < columnsToExport.size(); i++) {
            String columnKey = columnsToExport.get(i);
            ColumnDefinition colDef = AVAILABLE_COLUMNS.get(columnKey);
            csv.append(colDef != null ? colDef.getHeader() : columnKey);
            if (i < columnsToExport.size() - 1) {
                csv.append(",");
            }
        }
        csv.append("\n");

        // Données
        for (Personnel personnel : personnelList) {
            for (int i = 0; i < columnsToExport.size(); i++) {
                String columnKey = columnsToExport.get(i);
                String value = getCellValueAsString(personnel, columnKey);
                // Échapper les virgules et guillemets
                if (value.contains(",") || value.contains("\"")) {
                    value = "\"" + value.replace("\"", "\"\"") + "\"";
                }
                csv.append(value);
                if (i < columnsToExport.size() - 1) {
                    csv.append(",");
                }
            }
            csv.append("\n");
        }

        outputStream.write(csv.toString().getBytes("UTF-8"));
        log.info("Export personnalisé CSV terminé : {} lignes", personnelList.size());
        return outputStream;
    }

    /**
     * Obtenir la liste des colonnes disponibles
     */
    public List<Map<String, String>> getAvailableColumns() {
        return AVAILABLE_COLUMNS.entrySet().stream()
                .map(entry -> {
                    Map<String, String> col = new HashMap<>();
                    col.put("key", entry.getKey());
                    col.put("header", entry.getValue().getHeader());
                    col.put("field", entry.getValue().getField());
                    return col;
                })
                .collect(Collectors.toList());
    }

    // ==================== MÉTHODES PRIVÉES ====================

    /**
     * Récupérer les personnels avec filtres et tri
     */
    private List<Personnel> getFilteredPersonnel(PersonnelSearchDTO filters, String sortBy, String sortDirection) {
        if (filters != null) {
            // Utiliser le service de recherche existant
            Sort sort = createSort(sortBy, sortDirection);
            return personnelService.searchPersonnel(filters, 
                    org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE, sort))
                    .getContent().stream()
                    .map(dto -> personnelRepository.findById(dto.getId()).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else {
            // Tous les personnels
            Sort sort = createSort(sortBy, sortDirection);
            return personnelRepository.findAll(sort);
        }
    }

    /**
     * Créer le tri
     */
    private Sort createSort(String sortBy, String sortDirection) {
        if (sortBy == null || sortBy.isEmpty()) {
            return Sort.by(Sort.Direction.ASC, "lastName", "firstName");
        }
        Sort.Direction direction = "DESC".equalsIgnoreCase(sortDirection) 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
        return Sort.by(direction, sortBy);
    }

    /**
     * Déterminer les colonnes à exporter
     */
    private List<String> determineColumns(ExportConfigurationDTO config) {
        if (config.getSelectedColumns() != null && !config.getSelectedColumns().isEmpty()) {
            // Vérifier que toutes les colonnes demandées existent
            return config.getSelectedColumns().stream()
                    .filter(AVAILABLE_COLUMNS::containsKey)
                    .collect(Collectors.toList());
        }
        // Toutes les colonnes par défaut
        List<String> allColumns = new ArrayList<>(AVAILABLE_COLUMNS.keySet());
        
        // Filtrer selon les options
        if (Boolean.FALSE.equals(config.getIncludeContactData())) {
            allColumns.removeAll(Arrays.asList("telephone", "mobile", "email"));
        }
        if (Boolean.FALSE.equals(config.getIncludeGeographicData())) {
            allColumns.removeAll(Arrays.asList("regionOrigine", "departmentOrigine", "arrondissementOrigine"));
        }
        if (Boolean.FALSE.equals(config.getIncludeCalculatedFields())) {
            allColumns.removeAll(Arrays.asList("age", "ancienneteAdmin", "anciennetePoste"));
        }
        
        return allColumns;
    }

    /**
     * Définir la valeur d'une cellule Excel
     */
    private void setCellValue(Cell cell, Personnel personnel, String columnKey) {
        String value = getCellValueAsString(personnel, columnKey);
        if (value != null && !value.equals("N/A")) {
            // Essayer de détecter le type
            if (value.matches("\\d+")) {
                cell.setCellValue(Long.parseLong(value));
            } else if (value.matches("\\d+\\.\\d+")) {
                cell.setCellValue(Double.parseDouble(value));
            } else {
                cell.setCellValue(value);
            }
        } else {
            cell.setCellValue("");
        }
    }

    /**
     * Obtenir la valeur d'une cellule comme String
     */
    private String getCellValueAsString(Personnel personnel, String columnKey) {
        switch (columnKey) {
            case "matricule": return personnel.getMatricule() != null ? personnel.getMatricule() : "E.C.I";
            case "nomComplet": return personnel.getFullName();
            case "prenom": return personnel.getFirstName();
            case "nom": return personnel.getLastName();
            case "dateNaissance": return formatDate(personnel.getDateOfBirth());
            case "age": return personnel.getAge() != null ? String.valueOf(personnel.getAge()) : "N/A";
            case "cni": return personnel.getCniNumber() != null ? personnel.getCniNumber() : "N/A";
            case "grade": return personnel.getGrade() != null ? personnel.getGrade() : "N/A";
            case "corps": return personnel.getCorps() != null ? personnel.getCorps() : "N/A";
            case "categorie": return personnel.getCategory() != null ? personnel.getCategory() : "N/A";
            case "echelon": return personnel.getEchelon() != null ? personnel.getEchelon() : "N/A";
            case "indice": return personnel.getIndice() != null ? personnel.getIndice() : "N/A";
            case "poste": return personnel.getCurrentPosition() != null ? personnel.getCurrentPosition().getTitle() : "N/A";
            case "structure": return personnel.getStructure() != null ? personnel.getStructure().getName() : "N/A";
            case "situation": return personnel.getSituation() != null ? personnel.getSituation().name() : "N/A";
            case "statut": return personnel.getStatus() != null ? personnel.getStatus().name() : "N/A";
            case "dateEmbauche": return formatDate(personnel.getHireDate());
            case "ancienneteAdmin": return formatPeriod(personnel.getSeniorityInAdministration());
            case "anciennetePoste": return formatPeriod(personnel.getSeniorityInPost());
            case "dateRetraite": return formatDate(personnel.getRetirementDate());
            case "telephone": return personnel.getPhone() != null ? personnel.getPhone() : "N/A";
            case "mobile": return personnel.getMobile() != null ? personnel.getMobile() : "N/A";
            case "email": return personnel.getEmail() != null ? personnel.getEmail() : "N/A";
            case "regionOrigine": return personnel.getRegionOrigine() != null ? personnel.getRegionOrigine().getName() : "N/A";
            case "departmentOrigine": return personnel.getDepartmentOrigine() != null ? personnel.getDepartmentOrigine().getName() : "N/A";
            case "arrondissementOrigine": return personnel.getArrondissementOrigine() != null ? personnel.getArrondissementOrigine().getName() : "N/A";
            default: return "N/A";
        }
    }

    /**
     * Créer le style d'en-tête Excel
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "N/A";
    }

    private String formatPeriod(java.time.Period period) {
        if (period == null) return "N/A";
        int years = period.getYears();
        int months = period.getMonths();
        if (years > 0 && months > 0) {
            return years + "a " + months + "m";
        } else if (years > 0) {
            return years + " ans";
        } else if (months > 0) {
            return months + " mois";
        }
        return "0";
    }

    /**
     * Classe interne pour définir une colonne
     */
    private static class ColumnDefinition {
        private final String header;
        private final String field;

        public ColumnDefinition(String header, String field) {
            this.header = header;
            this.field = field;
        }

        public String getHeader() {
            return header;
        }

        public String getField() {
            return field;
        }
    }
}

