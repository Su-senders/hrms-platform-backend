package com.hrms.controller;

import com.hrms.dto.ExportConfigurationDTO;
import com.hrms.dto.PersonnelSearchDTO;
import com.hrms.dto.StatisticsDTO;
import com.hrms.entity.Personnel;
import com.hrms.repository.PersonnelRepository;
import com.hrms.repository.PositionRepository;
import com.hrms.service.ExportService;
import com.hrms.service.PersonnelService;
import com.hrms.service.PersonnelFicheExportService;
import com.hrms.service.PositionService;
import com.hrms.service.AdministrativeStructureService;
import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "API de génération de rapports et statistiques")
@CrossOrigin(origins = "*")
public class ReportController {

    private final PersonnelRepository personnelRepository;
    private final PersonnelService personnelService;
    private final PositionService positionService;
    private final AdministrativeStructureService structureService;
    private final ExportService exportService;
    private final PersonnelFicheExportService personnelFicheExportService;
    private final com.hrms.service.PersonnelCustomExportService customExportService;

    /**
     * Get overall statistics
     */
    @GetMapping("/statistics")
    @Operation(summary = "Obtenir les statistiques globales")
    public ResponseEntity<StatisticsDTO> getStatistics() {
        log.info("Fetching overall statistics");

        StatisticsDTO stats = StatisticsDTO.builder()
                .totalPersonnel((long) personnelRepository.findAll().size())
                .retirableThisYear((long) personnelRepository.findRetirableThisYear().size())
                .retirableNextYear((long) personnelRepository.findRetirableNextYear().size())
                .build();

        // Get personnel by situation
        Map<String, Long> bySituation = new HashMap<>();
        for (Personnel.PersonnelSituation situation : Personnel.PersonnelSituation.values()) {
            long count = personnelRepository.countBySituationAndDeletedFalse(situation);
            bySituation.put(situation.name(), count);
        }
        stats.setPersonnelBySituation(bySituation);

        // Get personnel by grade
        Map<String, Long> byGrade = new HashMap<>();
        personnelRepository.countByGrade().forEach(obj -> {
            byGrade.put((String) obj[0], (Long) obj[1]);
        });
        stats.setPersonnelByGrade(byGrade);

        // Get personnel by corps
        Map<String, Long> byCorps = new HashMap<>();
        personnelRepository.countByCorps().forEach(obj -> {
            byCorps.put((String) obj[0], (Long) obj[1]);
        });
        stats.setPersonnelByCorps(byCorps);

        // Get personnel by structure
        Map<String, Long> byStructure = new HashMap<>();
        personnelRepository.countByStructure().forEach(obj -> {
            byStructure.put((String) obj[0], (Long) obj[1]);
        });
        stats.setPersonnelByStructure(byStructure);

        return ResponseEntity.ok(stats);
    }

    /**
     * Export all personnel to Excel
     */
    @GetMapping("/export/personnel/excel")
    @Operation(summary = "Exporter tous les personnels en Excel (triés par ordre alphabétique)")
    public ResponseEntity<byte[]> exportAllPersonnelToExcel() {
        try {
            log.info("Exporting all personnel to Excel (sorted alphabetically)");
            List<Personnel> personnelList = personnelRepository.findAll(
                org.springframework.data.domain.Sort.by(
                    org.springframework.data.domain.Sort.Direction.ASC, 
                    "lastName", 
                    "firstName"
                )
            );
            ByteArrayOutputStream outputStream = exportService.exportPersonnelToExcel(personnelList);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment",
                    "personnel_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx");

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Error exporting to Excel", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Export all personnel to PDF
     */
    @GetMapping("/export/personnel/pdf")
    @Operation(summary = "Exporter tous les personnels en PDF (triés par ordre alphabétique)")
    public ResponseEntity<byte[]> exportAllPersonnelToPDF() {
        try {
            log.info("Exporting all personnel to PDF (sorted alphabetically)");
            List<Personnel> personnelList = personnelRepository.findAll(
                org.springframework.data.domain.Sort.by(
                    org.springframework.data.domain.Sort.Direction.ASC, 
                    "lastName", 
                    "firstName"
                )
            );
            ByteArrayOutputStream outputStream = exportService.exportPersonnelToPDF(personnelList);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "personnel_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf");

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (DocumentException e) {
            log.error("Error exporting to PDF", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Export personnel by situation to Excel
     */
    @GetMapping("/export/personnel/situation/{situation}/excel")
    @Operation(summary = "Exporter les personnels par situation en Excel")
    public ResponseEntity<byte[]> exportPersonnelBySituationToExcel(@PathVariable String situation) {
        try {
            log.info("Exporting personnel with situation {} to Excel", situation);
            Personnel.PersonnelSituation enumSituation = Personnel.PersonnelSituation.valueOf(situation.toUpperCase());
            List<Personnel> personnelList = personnelRepository.findBySituationAndDeletedFalse(
                    enumSituation, org.springframework.data.domain.Pageable.unpaged()).getContent();

            ByteArrayOutputStream outputStream = exportService.exportPersonnelToExcel(personnelList);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment",
                    "personnel_" + situation.toLowerCase() + "_" +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx");

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Error exporting to Excel", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Export personnel by situation to PDF
     */
    @GetMapping("/export/personnel/situation/{situation}/pdf")
    @Operation(summary = "Exporter les personnels par situation en PDF")
    public ResponseEntity<byte[]> exportPersonnelBySituationToPDF(@PathVariable String situation) {
        try {
            log.info("Exporting personnel with situation {} to PDF", situation);
            Personnel.PersonnelSituation enumSituation = Personnel.PersonnelSituation.valueOf(situation.toUpperCase());
            List<Personnel> personnelList = personnelRepository.findBySituationAndDeletedFalse(
                    enumSituation, org.springframework.data.domain.Pageable.unpaged()).getContent();

            ByteArrayOutputStream outputStream = exportService.exportPersonnelToPDF(personnelList);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "personnel_" + situation.toLowerCase() + "_" +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf");

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (DocumentException e) {
            log.error("Error exporting to PDF", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Export retirable personnel this year to Excel
     */
    @GetMapping("/export/retirable/current-year/excel")
    @Operation(summary = "Exporter les personnels retraitables cette année en Excel")
    public ResponseEntity<byte[]> exportRetirableThisYearToExcel() {
        try {
            log.info("Exporting retirable personnel (current year) to Excel");
            List<Personnel> personnelList = personnelRepository.findRetirableThisYear();
            ByteArrayOutputStream outputStream = exportService.exportPersonnelToExcel(personnelList);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment",
                    "retraitables_" + LocalDate.now().getYear() + ".xlsx");

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Error exporting to Excel", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Export retirable personnel next year to Excel
     */
    @GetMapping("/export/retirable/next-year/excel")
    @Operation(summary = "Exporter les personnels retraitables l'année prochaine en Excel")
    public ResponseEntity<byte[]> exportRetirableNextYearToExcel() {
        try {
            log.info("Exporting retirable personnel (next year) to Excel");
            List<Personnel> personnelList = personnelRepository.findRetirableNextYear();
            ByteArrayOutputStream outputStream = exportService.exportPersonnelToExcel(personnelList);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment",
                    "retraitables_" + (LocalDate.now().getYear() + 1) + ".xlsx");

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Error exporting to Excel", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Export personnel by structure to Excel
     */
    @GetMapping("/export/personnel/structure/{structureId}/excel")
    @Operation(summary = "Exporter les personnels par structure en Excel")
    public ResponseEntity<byte[]> exportPersonnelByStructureToExcel(@PathVariable Long structureId) {
        try {
            log.info("Exporting personnel from structure {} to Excel", structureId);
            List<Personnel> personnelList = personnelRepository.findByStructureId(
                    structureId, org.springframework.data.domain.Pageable.unpaged()).getContent();

            ByteArrayOutputStream outputStream = exportService.exportPersonnelToExcel(personnelList);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment",
                    "personnel_structure_" + structureId + "_" +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx");

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Error exporting to Excel", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Export complete fiche for a single personnel to PDF
     */
    @GetMapping("/export/personnel/{id}/fiche/pdf")
    @Operation(summary = "Exporter la fiche complète d'un personnel en PDF")
    public ResponseEntity<byte[]> exportPersonnelFicheToPDF(@PathVariable Long id) {
        try {
            log.info("Exporting complete fiche for personnel ID: {} to PDF", id);
            ByteArrayOutputStream outputStream = personnelFicheExportService.exportPersonnelFicheToPDF(id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "fiche_personnel_" + id + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf");

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (DocumentException e) {
            log.error("Error exporting personnel fiche to PDF", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e) {
            log.error("Personnel not found: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Export complete fiche for a single personnel to Excel
     */
    @GetMapping("/export/personnel/{id}/fiche/excel")
    @Operation(summary = "Exporter la fiche complète d'un personnel en Excel")
    public ResponseEntity<byte[]> exportPersonnelFicheToExcel(@PathVariable Long id) {
        try {
            log.info("Exporting complete fiche for personnel ID: {} to Excel", id);
            ByteArrayOutputStream outputStream = personnelFicheExportService.exportPersonnelFicheToExcel(id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment",
                    "fiche_personnel_" + id + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx");

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Error exporting personnel fiche to Excel", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e) {
            log.error("Personnel not found: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // ==================== EXPORT PERSONNALISÉ ====================

    @PostMapping("/export/personnel/custom/excel")
    @Operation(summary = "Exporter des personnels avec configuration personnalisée en Excel")
    public ResponseEntity<byte[]> exportCustomToExcel(@RequestBody ExportConfigurationDTO config) {
        try {
            log.info("Export personnalisé Excel avec {} colonnes", 
                    config.getSelectedColumns() != null ? config.getSelectedColumns().size() : "toutes");
            ByteArrayOutputStream outputStream = customExportService.exportCustomToExcel(config);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment",
                    "personnel_custom_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx");

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Error exporting custom Excel", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/export/personnel/custom/pdf")
    @Operation(summary = "Exporter des personnels avec configuration personnalisée en PDF")
    public ResponseEntity<byte[]> exportCustomToPDF(@RequestBody ExportConfigurationDTO config) {
        try {
            log.info("Export personnalisé PDF avec {} colonnes", 
                    config.getSelectedColumns() != null ? config.getSelectedColumns().size() : "toutes");
            ByteArrayOutputStream outputStream = customExportService.exportCustomToPDF(config);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "personnel_custom_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf");

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (DocumentException e) {
            log.error("Error exporting custom PDF", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/export/personnel/custom/csv")
    @Operation(summary = "Exporter des personnels avec configuration personnalisée en CSV")
    public ResponseEntity<byte[]> exportCustomToCSV(@RequestBody ExportConfigurationDTO config) {
        try {
            log.info("Export personnalisé CSV avec {} colonnes", 
                    config.getSelectedColumns() != null ? config.getSelectedColumns().size() : "toutes");
            ByteArrayOutputStream outputStream = customExportService.exportCustomToCSV(config);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment",
                    "personnel_custom_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv");

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Error exporting custom CSV", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/export/personnel/available-columns")
    @Operation(summary = "Obtenir la liste des colonnes disponibles pour l'export personnalisé")
    public ResponseEntity<List<Map<String, String>>> getAvailableColumns() {
        List<Map<String, String>> columns = customExportService.getAvailableColumns();
        return ResponseEntity.ok(columns);
    }
}
