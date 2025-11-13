package com.hrms.service;

import com.hrms.dto.PersonnelDTO;
import com.hrms.entity.Personnel;
import com.hrms.repository.PersonnelRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportService {

    private final PersonnelRepository personnelRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Export personnel list to Excel
     */
    public ByteArrayOutputStream exportPersonnelToExcel(List<Personnel> personnelList) throws IOException {
        log.info("Exporting {} personnel records to Excel", personnelList.size());

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Personnel");

        // Create header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {
            "Matricule", "Nom Complet", "Date de Naissance", "Âge", "CNI",
            "Grade", "Corps", "Catégorie", "Échelon", "Indice",
            "Poste Actuel", "Structure", "Situation", "Statut",
            "Date d'Embauche", "Ancienneté Admin", "Ancienneté au Poste",
            "Date de Retraite", "Téléphone", "Email"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Fill data
        int rowNum = 1;
        for (Personnel personnel : personnelList) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(personnel.getMatricule());
            row.createCell(1).setCellValue(personnel.getFullName());
            row.createCell(2).setCellValue(formatDate(personnel.getDateOfBirth()));
            row.createCell(3).setCellValue(personnel.getAge() != null ? personnel.getAge() : 0);
            row.createCell(4).setCellValue(personnel.getCniNumber());
            row.createCell(5).setCellValue(personnel.getGrade());
            row.createCell(6).setCellValue(personnel.getCorps());
            row.createCell(7).setCellValue(personnel.getCategory());
            row.createCell(8).setCellValue(personnel.getEchelon());
            row.createCell(9).setCellValue(personnel.getIndice());
            row.createCell(10).setCellValue(personnel.getCurrentPosition() != null ?
                personnel.getCurrentPosition().getTitle() : "N/A");
            row.createCell(11).setCellValue(personnel.getStructure() != null ?
                personnel.getStructure().getName() : "N/A");
            row.createCell(12).setCellValue(personnel.getSituation() != null ?
                personnel.getSituation().name() : "N/A");
            row.createCell(13).setCellValue(personnel.getStatus() != null ?
                personnel.getStatus().name() : "N/A");
            row.createCell(14).setCellValue(formatDate(personnel.getHireDate()));
            row.createCell(15).setCellValue(formatPeriod(personnel.getSeniorityInAdministration()));
            row.createCell(16).setCellValue(formatPeriod(personnel.getSeniorityInPost()));
            row.createCell(17).setCellValue(formatDate(personnel.getRetirementDate()));
            row.createCell(18).setCellValue(personnel.getMobile());
            row.createCell(19).setCellValue(personnel.getEmail());
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        log.info("Excel export completed successfully");
        return outputStream;
    }

    /**
     * Export personnel list to PDF
     */
    public ByteArrayOutputStream exportPersonnelToPDF(List<Personnel> personnelList) throws DocumentException {
        log.info("Exporting {} personnel records to PDF", personnelList.size());

        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, outputStream);
        document.open();

        // Add title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
        Paragraph title = new Paragraph("Liste du Personnel - MINAT", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Add generation date
        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
        Paragraph date = new Paragraph("Généré le: " + LocalDate.now().format(DATE_FORMATTER), dateFont);
        date.setAlignment(Element.ALIGN_RIGHT);
        date.setSpacingAfter(20);
        document.add(date);

        // Create table
        PdfPTable table = new PdfPTable(10);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Set column widths
        float[] columnWidths = {1.2f, 2f, 1.5f, 1f, 1.5f, 1.5f, 2f, 1.5f, 1.5f, 1.5f};
        table.setWidths(columnWidths);

        // Table header
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.WHITE);
        String[] headers = {
            "Matricule", "Nom Complet", "Grade", "Corps", "Poste", "Structure",
            "Situation", "Date Embauche", "Ancienneté", "Retraite"
        };

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }

        // Table data
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);
        for (Personnel personnel : personnelList) {
            table.addCell(new Phrase(personnel.getMatricule(), dataFont));
            table.addCell(new Phrase(personnel.getFullName(), dataFont));
            table.addCell(new Phrase(personnel.getGrade(), dataFont));
            table.addCell(new Phrase(personnel.getCorps(), dataFont));
            table.addCell(new Phrase(personnel.getCurrentPosition() != null ?
                personnel.getCurrentPosition().getTitle() : "N/A", dataFont));
            table.addCell(new Phrase(personnel.getStructure() != null ?
                personnel.getStructure().getName() : "N/A", dataFont));
            table.addCell(new Phrase(personnel.getSituation() != null ?
                personnel.getSituation().name() : "N/A", dataFont));
            table.addCell(new Phrase(formatDate(personnel.getHireDate()), dataFont));
            table.addCell(new Phrase(formatPeriod(personnel.getSeniorityInAdministration()), dataFont));
            table.addCell(new Phrase(formatDate(personnel.getRetirementDate()), dataFont));
        }

        document.add(table);

        // Add footer
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, BaseColor.GRAY);
        Paragraph footer = new Paragraph("Total: " + personnelList.size() + " personnel(s)", footerFont);
        footer.setAlignment(Element.ALIGN_RIGHT);
        footer.setSpacingBefore(20);
        document.add(footer);

        document.close();

        log.info("PDF export completed successfully");
        return outputStream;
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
}
