package com.hrms.service;

import com.hrms.dto.PersonnelCreateDTO;
import com.hrms.dto.PersonnelImportResultDTO;
import com.hrms.entity.*;
import com.hrms.exception.BusinessException;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.repository.*;
import com.hrms.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Service d'importation en masse de personnels depuis Excel/CSV
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PersonnelImportService {

    private final PersonnelService personnelService;
    private final PersonnelRepository personnelRepository;
    private final AdministrativeStructureRepository structureRepository;
    private final PositionRepository positionRepository;
    private final RegionRepository regionRepository;
    private final DepartmentRepository departmentRepository;
    private final ArrondissementRepository arrondissementRepository;
    private final GradeRepository gradeRepository;
    private final FileUtil fileUtil;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_FORMATTER_ALT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Importer des personnels depuis un fichier Excel
     */
    public PersonnelImportResultDTO importFromExcel(MultipartFile file, boolean validationOnly) {
        log.info("Import Excel de personnels - Mode validation: {}", validationOnly);

        if (!fileUtil.isValidFileType(file.getOriginalFilename(), new String[]{"xlsx", "xls"})) {
            throw new BusinessException("Le fichier doit être au format Excel (.xlsx ou .xls)");
        }

        List<PersonnelImportResultDTO.ImportErrorDTO> errors = new ArrayList<>();
        List<Long> createdIds = new ArrayList<>();
        int totalRows = 0;
        int successCount = 0;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // Lire les en-têtes (première ligne)
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new BusinessException("Le fichier Excel est vide ou mal formaté");
            }

            Map<String, Integer> columnMap = buildColumnMap(headerRow);

            // Lire les données (à partir de la ligne 2)
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                totalRows++;
                int currentRow = rowIndex + 1; // Pour l'affichage (1-indexed)

                try {
                    PersonnelCreateDTO dto = parseExcelRow(row, columnMap, currentRow, errors);
                    
                    if (dto != null && errors.stream().noneMatch(e -> e.getRowNumber().equals(currentRow))) {
                        if (!validationOnly) {
                            PersonnelDTO created = personnelService.createPersonnel(dto);
                            createdIds.add(created.getId());
                            successCount++;
                        } else {
                            // En mode validation, on vérifie juste que les données sont valides
                            successCount++;
                        }
                    }
                } catch (Exception e) {
                    log.error("Erreur lors du traitement de la ligne {}: {}", currentRow, e.getMessage());
                    errors.add(PersonnelImportResultDTO.ImportErrorDTO.builder()
                            .rowNumber(currentRow)
                            .errorMessage("Erreur lors du traitement: " + e.getMessage())
                            .errorType("PROCESSING")
                            .build());
                }
            }

        } catch (Exception e) {
            log.error("Erreur lors de la lecture du fichier Excel", e);
            throw new BusinessException("Erreur lors de la lecture du fichier Excel: " + e.getMessage());
        }

        int errorCount = errors.size();
        String summaryMessage = String.format(
                "Import terminé: %d ligne(s) traitée(s), %d succès, %d erreur(s)",
                totalRows, successCount, errorCount
        );

        return PersonnelImportResultDTO.builder()
                .totalRows(totalRows)
                .successCount(successCount)
                .errorCount(errorCount)
                .errors(errors)
                .createdPersonnelIds(createdIds)
                .validationOnly(validationOnly)
                .summaryMessage(summaryMessage)
                .build();
    }

    /**
     * Importer des personnels depuis un fichier CSV
     */
    public PersonnelImportResultDTO importFromCSV(MultipartFile file, boolean validationOnly) {
        log.info("Import CSV de personnels - Mode validation: {}", validationOnly);

        if (!fileUtil.isValidFileType(file.getOriginalFilename(), new String[]{"csv"})) {
            throw new BusinessException("Le fichier doit être au format CSV");
        }

        List<PersonnelImportResultDTO.ImportErrorDTO> errors = new ArrayList<>();
        List<Long> createdIds = new ArrayList<>();
        int totalRows = 0;
        int successCount = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"))) {
            String line;
            Map<String, Integer> columnMap = null;
            int rowIndex = 0;

            while ((line = reader.readLine()) != null) {
                rowIndex++;
                String[] values = parseCSVLine(line);

                if (rowIndex == 1) {
                    // Ligne d'en-têtes
                    columnMap = buildColumnMapFromHeaders(values);
                    continue;
                }

                totalRows++;
                int currentRow = rowIndex;

                try {
                    PersonnelCreateDTO dto = parseCSVRow(values, columnMap, currentRow, errors);
                    
                    if (dto != null && errors.stream().noneMatch(e -> e.getRowNumber().equals(currentRow))) {
                        if (!validationOnly) {
                            PersonnelDTO created = personnelService.createPersonnel(dto);
                            createdIds.add(created.getId());
                            successCount++;
                        } else {
                            successCount++;
                        }
                    }
                } catch (Exception e) {
                    log.error("Erreur lors du traitement de la ligne {}: {}", currentRow, e.getMessage());
                    errors.add(PersonnelImportResultDTO.ImportErrorDTO.builder()
                            .rowNumber(currentRow)
                            .errorMessage("Erreur lors du traitement: " + e.getMessage())
                            .errorType("PROCESSING")
                            .build());
                }
            }

        } catch (Exception e) {
            log.error("Erreur lors de la lecture du fichier CSV", e);
            throw new BusinessException("Erreur lors de la lecture du fichier CSV: " + e.getMessage());
        }

        int errorCount = errors.size();
        String summaryMessage = String.format(
                "Import terminé: %d ligne(s) traitée(s), %d succès, %d erreur(s)",
                totalRows, successCount, errorCount
        );

        return PersonnelImportResultDTO.builder()
                .totalRows(totalRows)
                .successCount(successCount)
                .errorCount(errorCount)
                .errors(errors)
                .createdPersonnelIds(createdIds)
                .validationOnly(validationOnly)
                .summaryMessage(summaryMessage)
                .build();
    }

    // ==================== MÉTHODES PRIVÉES ====================

    /**
     * Construire la map des colonnes depuis les en-têtes Excel
     */
    private Map<String, Integer> buildColumnMap(Row headerRow) {
        Map<String, Integer> map = new HashMap<>();
        for (Cell cell : headerRow) {
            String header = getCellValueAsString(cell).toLowerCase().trim();
            map.put(header, cell.getColumnIndex());
        }
        return map;
    }

    /**
     * Construire la map des colonnes depuis les en-têtes CSV
     */
    private Map<String, Integer> buildColumnMapFromHeaders(String[] headers) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i].toLowerCase().trim();
            map.put(header, i);
        }
        return map;
    }

    /**
     * Parser une ligne Excel
     */
    private PersonnelCreateDTO parseExcelRow(Row row, Map<String, Integer> columnMap, 
                                             int rowNumber, List<PersonnelImportResultDTO.ImportErrorDTO> errors) {
        PersonnelCreateDTO.PersonnelCreateDTOBuilder builder = PersonnelCreateDTO.builder();

        // Champs obligatoires
        String matricule = getCellValue(row, columnMap, "matricule", rowNumber, errors, true);
        String firstName = getCellValue(row, columnMap, "prenom", rowNumber, errors, true);
        String lastName = getCellValue(row, columnMap, "nom", rowNumber, errors, true);
        LocalDate dateOfBirth = getDateValue(row, columnMap, "date de naissance", rowNumber, errors, true);
        String gender = getCellValue(row, columnMap, "genre", rowNumber, errors, true);
        LocalDate hireDate = getDateValue(row, columnMap, "date d'embauche", rowNumber, errors, true);
        String grade = getCellValue(row, columnMap, "grade", rowNumber, errors, true);
        Long structureId = getLongValue(row, columnMap, "structure id", rowNumber, errors, true);

        if (errors.stream().anyMatch(e -> e.getRowNumber().equals(rowNumber))) {
            return null; // Erreurs critiques
        }

        builder.matricule(matricule)
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(dateOfBirth)
                .gender(gender)
                .hireDate(hireDate)
                .grade(grade)
                .structureId(structureId);

        // Champs optionnels
        builder.middleName(getCellValue(row, columnMap, "nom du milieu", rowNumber, errors, false));
        builder.placeOfBirth(getCellValue(row, columnMap, "lieu de naissance", rowNumber, errors, false));
        builder.maritalStatus(getCellValue(row, columnMap, "statut marital", rowNumber, errors, false));
        builder.nationality(getCellValue(row, columnMap, "nationalite", rowNumber, errors, false));
        builder.cniNumber(getCellValue(row, columnMap, "cni", rowNumber, errors, false));
        builder.cniIssueDate(getDateValue(row, columnMap, "date emission cni", rowNumber, errors, false));
        builder.cniExpiryDate(getDateValue(row, columnMap, "date expiration cni", rowNumber, errors, false));
        builder.phone(getCellValue(row, columnMap, "telephone", rowNumber, errors, false));
        builder.mobile(getCellValue(row, columnMap, "mobile", rowNumber, errors, false));
        builder.email(getCellValue(row, columnMap, "email", rowNumber, errors, false));
        builder.address(getCellValue(row, columnMap, "adresse", rowNumber, errors, false));
        builder.city(getCellValue(row, columnMap, "ville", rowNumber, errors, false));
        builder.corps(getCellValue(row, columnMap, "corps", rowNumber, errors, false));
        builder.category(getCellValue(row, columnMap, "categorie", rowNumber, errors, false));
        builder.echelon(getCellValue(row, columnMap, "echelon", rowNumber, errors, false));
        builder.indice(getCellValue(row, columnMap, "indice", rowNumber, errors, false));
        builder.status(getCellValue(row, columnMap, "statut", rowNumber, errors, false));
        builder.situation(getCellValue(row, columnMap, "situation", rowNumber, errors, false));
        
        Long positionId = getLongValue(row, columnMap, "poste id", rowNumber, errors, false);
        if (positionId != null) {
            builder.currentPositionId(positionId);
        }

        Long regionId = getLongValue(row, columnMap, "region origine id", rowNumber, errors, false);
        if (regionId != null) {
            builder.regionOrigineId(regionId);
        }

        Long departmentId = getLongValue(row, columnMap, "department origine id", rowNumber, errors, false);
        if (departmentId != null) {
            builder.departmentOrigineId(departmentId);
        }

        Long arrondissementId = getLongValue(row, columnMap, "arrondissement origine id", rowNumber, errors, false);
        if (arrondissementId != null) {
            builder.arrondissementOrigineId(arrondissementId);
        }

        return builder.build();
    }

    /**
     * Parser une ligne CSV
     */
    private PersonnelCreateDTO parseCSVRow(String[] values, Map<String, Integer> columnMap,
                                          int rowNumber, List<PersonnelImportResultDTO.ImportErrorDTO> errors) {
        PersonnelCreateDTO.PersonnelCreateDTOBuilder builder = PersonnelCreateDTO.builder();

        String matricule = getCSVValue(values, columnMap, "matricule", rowNumber, errors, true);
        String firstName = getCSVValue(values, columnMap, "prenom", rowNumber, errors, true);
        String lastName = getCSVValue(values, columnMap, "nom", rowNumber, errors, true);
        LocalDate dateOfBirth = getCSVDateValue(values, columnMap, "date de naissance", rowNumber, errors, true);
        String gender = getCSVValue(values, columnMap, "genre", rowNumber, errors, true);
        LocalDate hireDate = getCSVDateValue(values, columnMap, "date d'embauche", rowNumber, errors, true);
        String grade = getCSVValue(values, columnMap, "grade", rowNumber, errors, true);
        Long structureId = getCSVLongValue(values, columnMap, "structure id", rowNumber, errors, true);

        if (errors.stream().anyMatch(e -> e.getRowNumber().equals(rowNumber))) {
            return null;
        }

        builder.matricule(matricule)
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(dateOfBirth)
                .gender(gender)
                .hireDate(hireDate)
                .grade(grade)
                .structureId(structureId);

        // Champs optionnels (similaire à parseExcelRow)
        builder.middleName(getCSVValue(values, columnMap, "nom du milieu", rowNumber, errors, false));
        builder.placeOfBirth(getCSVValue(values, columnMap, "lieu de naissance", rowNumber, errors, false));
        builder.maritalStatus(getCSVValue(values, columnMap, "statut marital", rowNumber, errors, false));
        builder.nationality(getCSVValue(values, columnMap, "nationalite", rowNumber, errors, false));
        builder.cniNumber(getCSVValue(values, columnMap, "cni", rowNumber, errors, false));
        builder.cniIssueDate(getCSVDateValue(values, columnMap, "date emission cni", rowNumber, errors, false));
        builder.cniExpiryDate(getCSVDateValue(values, columnMap, "date expiration cni", rowNumber, errors, false));
        builder.phone(getCSVValue(values, columnMap, "telephone", rowNumber, errors, false));
        builder.mobile(getCSVValue(values, columnMap, "mobile", rowNumber, errors, false));
        builder.email(getCSVValue(values, columnMap, "email", rowNumber, errors, false));
        builder.address(getCSVValue(values, columnMap, "adresse", rowNumber, errors, false));
        builder.city(getCSVValue(values, columnMap, "ville", rowNumber, errors, false));
        builder.corps(getCSVValue(values, columnMap, "corps", rowNumber, errors, false));
        builder.category(getCSVValue(values, columnMap, "categorie", rowNumber, errors, false));
        builder.echelon(getCSVValue(values, columnMap, "echelon", rowNumber, errors, false));
        builder.indice(getCSVValue(values, columnMap, "indice", rowNumber, errors, false));
        builder.status(getCSVValue(values, columnMap, "statut", rowNumber, errors, false));
        builder.situation(getCSVValue(values, columnMap, "situation", rowNumber, errors, false));

        Long positionId = getCSVLongValue(values, columnMap, "poste id", rowNumber, errors, false);
        if (positionId != null) {
            builder.currentPositionId(positionId);
        }

        Long regionId = getCSVLongValue(values, columnMap, "region origine id", rowNumber, errors, false);
        if (regionId != null) {
            builder.regionOrigineId(regionId);
        }

        Long departmentId = getCSVLongValue(values, columnMap, "department origine id", rowNumber, errors, false);
        if (departmentId != null) {
            builder.departmentOrigineId(departmentId);
        }

        Long arrondissementId = getCSVLongValue(values, columnMap, "arrondissement origine id", rowNumber, errors, false);
        if (arrondissementId != null) {
            builder.arrondissementOrigineId(arrondissementId);
        }

        return builder.build();
    }

    // Méthodes utilitaires pour Excel
    private String getCellValue(Row row, Map<String, Integer> columnMap, String columnName,
                               int rowNumber, List<PersonnelImportResultDTO.ImportErrorDTO> errors, boolean required) {
        Integer colIndex = columnMap.get(columnName.toLowerCase());
        if (colIndex == null) {
            if (required) {
                errors.add(createError(rowNumber, columnName, null, "Colonne manquante: " + columnName, "VALIDATION"));
            }
            return null;
        }
        Cell cell = row.getCell(colIndex);
        String value = getCellValueAsString(cell);
        if (required && (value == null || value.trim().isEmpty())) {
            errors.add(createError(rowNumber, columnName, value, "Champ obligatoire vide", "VALIDATION"));
        }
        return value != null ? value.trim() : null;
    }

    private LocalDate getDateValue(Row row, Map<String, Integer> columnMap, String columnName,
                                  int rowNumber, List<PersonnelImportResultDTO.ImportErrorDTO> errors, boolean required) {
        String value = getCellValue(row, columnMap, columnName, rowNumber, errors, required);
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(value.trim(), DATE_FORMATTER_ALT);
            } catch (DateTimeParseException e2) {
                errors.add(createError(rowNumber, columnName, value, "Format de date invalide (attendu: dd/MM/yyyy)", "DATE"));
                return null;
            }
        }
    }

    private Long getLongValue(Row row, Map<String, Integer> columnMap, String columnName,
                             int rowNumber, List<PersonnelImportResultDTO.ImportErrorDTO> errors, boolean required) {
        String value = getCellValue(row, columnMap, columnName, rowNumber, errors, required);
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            errors.add(createError(rowNumber, columnName, value, "Valeur numérique invalide", "VALIDATION"));
            return null;
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate().format(DATE_FORMATTER);
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }

    // Méthodes utilitaires pour CSV
    private String getCSVValue(String[] values, Map<String, Integer> columnMap, String columnName,
                              int rowNumber, List<PersonnelImportResultDTO.ImportErrorDTO> errors, boolean required) {
        Integer colIndex = columnMap.get(columnName.toLowerCase());
        if (colIndex == null || colIndex >= values.length) {
            if (required) {
                errors.add(createError(rowNumber, columnName, null, "Colonne manquante: " + columnName, "VALIDATION"));
            }
            return null;
        }
        String value = values[colIndex] != null ? values[colIndex].trim() : null;
        if (required && (value == null || value.isEmpty())) {
            errors.add(createError(rowNumber, columnName, value, "Champ obligatoire vide", "VALIDATION"));
        }
        return value;
    }

    private LocalDate getCSVDateValue(String[] values, Map<String, Integer> columnMap, String columnName,
                                     int rowNumber, List<PersonnelImportResultDTO.ImportErrorDTO> errors, boolean required) {
        String value = getCSVValue(values, columnMap, columnName, rowNumber, errors, required);
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(value.trim(), DATE_FORMATTER_ALT);
            } catch (DateTimeParseException e2) {
                errors.add(createError(rowNumber, columnName, value, "Format de date invalide (attendu: dd/MM/yyyy)", "DATE"));
                return null;
            }
        }
    }

    private Long getCSVLongValue(String[] values, Map<String, Integer> columnMap, String columnName,
                                int rowNumber, List<PersonnelImportResultDTO.ImportErrorDTO> errors, boolean required) {
        String value = getCSVValue(values, columnMap, columnName, rowNumber, errors, required);
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            errors.add(createError(rowNumber, columnName, value, "Valeur numérique invalide", "VALIDATION"));
            return null;
        }
    }

    private String[] parseCSVLine(String line) {
        List<String> values = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentValue = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(currentValue.toString());
                currentValue = new StringBuilder();
            } else {
                currentValue.append(c);
            }
        }
        values.add(currentValue.toString());
        return values.toArray(new String[0]);
    }

    private PersonnelImportResultDTO.ImportErrorDTO createError(int rowNumber, String field, String value,
                                                               String message, String type) {
        return PersonnelImportResultDTO.ImportErrorDTO.builder()
                .rowNumber(rowNumber)
                .field(field)
                .value(value)
                .errorMessage(message)
                .errorType(type)
                .build();
    }
}

