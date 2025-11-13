package com.hrms.service;

import com.hrms.entity.*;
import com.hrms.repository.*;
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

/**
 * Service d'export de fiche individuelle complète du personnel
 * Génère des fiches de renseignement détaillées en PDF et Excel
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PersonnelFicheExportService {

    private final PersonnelRepository personnelRepository;
    private final CareerMovementRepository careerMovementRepository;
    private final ProfessionalTrainingRepository professionalTrainingRepository;
    private final TrainingEnrollmentRepository trainingEnrollmentRepository;
    private final PersonnelLeaveRepository personnelLeaveRepository;
    private final PreviousPositionRepository previousPositionRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Export fiche complète d'un personnel en PDF
     */
    public ByteArrayOutputStream exportPersonnelFicheToPDF(Long personnelId) throws DocumentException {
        log.info("Exporting complete fiche for personnel ID: {} to PDF", personnelId);

        Personnel personnel = personnelRepository.findById(personnelId)
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé avec ID: " + personnelId));

        // Récupérer toutes les données associées
        List<CareerMovement> careerMovements = careerMovementRepository.findByPersonnelId(personnelId);
        List<ProfessionalTraining> trainings = professionalTrainingRepository.findByPersonnelId(personnelId);
        List<TrainingEnrollment> modernEnrollments = trainingEnrollmentRepository.findByPersonnelId(personnelId);
        List<PersonnelLeave> leaves = personnelLeaveRepository.findByPersonnelId(personnelId);
        List<PreviousPosition> previousPositions = previousPositionRepository.findByPersonnelId(personnelId);

        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, outputStream);
        document.open();

        // === EN-TÊTE DU DOCUMENT ===
        addPDFHeader(document, personnel);

        // === SECTION A : IDENTIFICATION ===
        addPDFSectionA(document, personnel);

        // === SECTION B : QUALIFICATIONS ===
        addPDFSectionB(document, personnel);

        // === SECTION C : CARRIÈRE ===
        addPDFSectionC(document, personnel);

        // === HISTORIQUE DES MOUVEMENTS DE CARRIÈRE ===
        if (!careerMovements.isEmpty()) {
            addPDFCareerMovementsHistory(document, careerMovements);
        }

        // === HISTORIQUE DES FORMATIONS ===
        if (!trainings.isEmpty() || !modernEnrollments.isEmpty()) {
            addPDFTrainingsHistory(document, trainings, modernEnrollments);
        }

        // === HISTORIQUE DES CONGÉS ===
        if (!leaves.isEmpty()) {
            addPDFLeavesHistory(document, leaves);
        }

        // === POSTES ANTÉRIEURS ===
        if (!previousPositions.isEmpty()) {
            addPDFPreviousPositions(document, previousPositions);
        }

        // === PIED DE PAGE ===
        addPDFFooter(document);

        document.close();

        log.info("PDF fiche export completed successfully for personnel ID: {}", personnelId);
        return outputStream;
    }

    /**
     * Export fiche complète d'un personnel en Excel
     */
    public ByteArrayOutputStream exportPersonnelFicheToExcel(Long personnelId) throws IOException {
        log.info("Exporting complete fiche for personnel ID: {} to Excel", personnelId);

        Personnel personnel = personnelRepository.findById(personnelId)
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé avec ID: " + personnelId));

        // Récupérer toutes les données associées
        List<CareerMovement> careerMovements = careerMovementRepository.findByPersonnelId(personnelId);
        List<ProfessionalTraining> trainings = professionalTrainingRepository.findByPersonnelId(personnelId);
        List<TrainingEnrollment> modernEnrollments = trainingEnrollmentRepository.findByPersonnelId(personnelId);
        List<PersonnelLeave> leaves = personnelLeaveRepository.findByPersonnelId(personnelId);
        List<PreviousPosition> previousPositions = previousPositionRepository.findByPersonnelId(personnelId);

        Workbook workbook = new XSSFWorkbook();

        // === FEUILLE 1 : INFORMATIONS PRINCIPALES ===
        addExcelMainInfoSheet(workbook, personnel);

        // === FEUILLE 2 : HISTORIQUE CARRIÈRE ===
        if (!careerMovements.isEmpty()) {
            addExcelCareerMovementsSheet(workbook, careerMovements);
        }

        // === FEUILLE 3 : FORMATIONS ===
        if (!trainings.isEmpty() || !modernEnrollments.isEmpty()) {
            addExcelTrainingsSheet(workbook, trainings, modernEnrollments);
        }

        // === FEUILLE 4 : CONGÉS ===
        if (!leaves.isEmpty()) {
            addExcelLeavesSheet(workbook, leaves);
        }

        // === FEUILLE 5 : POSTES ANTÉRIEURS ===
        if (!previousPositions.isEmpty()) {
            addExcelPreviousPositionsSheet(workbook, previousPositions);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        log.info("Excel fiche export completed successfully for personnel ID: {}", personnelId);
        return outputStream;
    }

    // ==================== PDF HELPER METHODS ====================

    private void addPDFHeader(Document document, Personnel personnel) throws DocumentException {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.DARK_GRAY);

        Paragraph title = new Paragraph("FICHE DE RENSEIGNEMENT DU PERSONNEL", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);

        Paragraph subtitle = new Paragraph("MINISTÈRE DE L'ADMINISTRATION TERRITORIALE (MINAT)", subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(20);
        document.add(subtitle);

        // Informations principales
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.BLACK);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);

        Paragraph matricule = new Paragraph();
        matricule.add(new Chunk("Matricule : ", labelFont));
        matricule.add(new Chunk(safeString(personnel.getMatricule()), valueFont));
        matricule.setSpacingAfter(5);
        document.add(matricule);

        Paragraph fullName = new Paragraph();
        fullName.add(new Chunk("Nom Complet : ", labelFont));
        fullName.add(new Chunk(personnel.getFullName(), valueFont));
        fullName.setSpacingAfter(5);
        document.add(fullName);

        Paragraph genDate = new Paragraph();
        genDate.add(new Chunk("Généré le : ", labelFont));
        genDate.add(new Chunk(LocalDate.now().format(DATE_FORMATTER), valueFont));
        genDate.setSpacingAfter(20);
        document.add(genDate);

        // Ligne de séparation
        addSeparator(document);
    }

    private void addPDFSectionA(Document document, Personnel personnel) throws DocumentException {
        addSectionTitle(document, "SECTION A : IDENTIFICATION DU PERSONNEL");

        // A.1 ÉTAT CIVIL
        addSubSectionTitle(document, "A.1 - État Civil");
        PdfPTable table = createInfoTable();

        addTableRow(table, "Nom", safeString(personnel.getLastName()));
        addTableRow(table, "Prénom", safeString(personnel.getFirstName()));
        addTableRow(table, "Sexe", safeString(personnel.getGender()));
        addTableRow(table, "Date de Naissance", formatDate(personnel.getDateOfBirth()));
        addTableRow(table, "Âge", personnel.getAge() != null ? personnel.getAge() + " ans" : "N/A");
        addTableRow(table, "Nationalité", safeString(personnel.getNationalityType()));
        if (personnel.getNaturalizationDate() != null) {
            addTableRow(table, "Date de Naturalisation", formatDate(personnel.getNaturalizationDate()));
        }
        addTableRow(table, "N° CNI", safeString(personnel.getCniNumber()));

        document.add(table);
        document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 8)));

        // A.2 UNITÉ ADMINISTRATIVE D'ORIGINE
        addSubSectionTitle(document, "A.2 - Unité Administrative d'Origine");
        PdfPTable origineTable = createInfoTable();

        addTableRow(origineTable, "Région d'Origine", safeRelation(personnel.getRegionOrigine(), Region::getName));
        addTableRow(origineTable, "Département d'Origine", safeRelation(personnel.getDepartementOrigine(), Departement::getName));
        addTableRow(origineTable, "Arrondissement d'Origine", safeRelation(personnel.getArrondissementOrigine(), Arrondissement::getName));
        addTableRow(origineTable, "Village", safeString(personnel.getVillageOrigine()));
        addTableRow(origineTable, "Tribu", safeString(personnel.getTribuOrigine()));

        document.add(origineTable);
        document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 8)));

        // A.3 FILIATION
        addSubSectionTitle(document, "A.3 - Filiation");
        PdfPTable filiationTable = createInfoTable();

        addTableRow(filiationTable, "Nom du Père", safeString(personnel.getFatherName()));
        addTableRow(filiationTable, "Nom de la Mère", safeString(personnel.getMotherName()));

        document.add(filiationTable);
        document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 10)));
    }

    private void addPDFSectionB(Document document, Personnel personnel) throws DocumentException {
        addSectionTitle(document, "SECTION B : QUALIFICATIONS");

        // B.1 DIPLÔME DE RECRUTEMENT
        addSubSectionTitle(document, "B.1 - Diplôme de Recrutement");
        PdfPTable recruitmentTable = createInfoTable();

        addTableRow(recruitmentTable, "Intitulé", safeString(personnel.getRecruitmentDiplomaTitle()));
        addTableRow(recruitmentTable, "Type de Diplôme", safeString(personnel.getRecruitmentDiplomaType()));
        addTableRow(recruitmentTable, "Date d'Obtention", formatDate(personnel.getRecruitmentDiplomaDate()));
        addTableRow(recruitmentTable, "Lieu d'Obtention", safeString(personnel.getRecruitmentDiplomaPlace()));
        addTableRow(recruitmentTable, "Niveau d'Instruction", safeString(personnel.getRecruitmentEducationLevel()));
        addTableRow(recruitmentTable, "Spécialité", safeString(personnel.getRecruitmentDiplomaSpecialty()));
        addTableRow(recruitmentTable, "Domaine d'Étude", safeString(personnel.getRecruitmentStudyField()));

        document.add(recruitmentTable);
        document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 8)));

        // B.2 DIPLÔME LE PLUS ÉLEVÉ
        addSubSectionTitle(document, "B.2 - Diplôme le Plus Élevé");
        PdfPTable highestTable = createInfoTable();

        addTableRow(highestTable, "Intitulé", safeString(personnel.getHighestDiplomaTitle()));
        addTableRow(highestTable, "Type de Diplôme", safeString(personnel.getHighestDiplomaType()));
        addTableRow(highestTable, "Date d'Obtention", formatDate(personnel.getHighestDiplomaDate()));
        addTableRow(highestTable, "Lieu d'Obtention", safeString(personnel.getHighestDiplomaPlace()));
        addTableRow(highestTable, "Niveau d'Instruction", safeString(personnel.getHighestEducationLevel()));
        addTableRow(highestTable, "Spécialité", safeString(personnel.getHighestDiplomaSpecialty()));
        addTableRow(highestTable, "Domaine d'Étude", safeString(personnel.getHighestStudyField()));

        document.add(highestTable);
        document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 10)));
    }

    private void addPDFSectionC(Document document, Personnel personnel) throws DocumentException {
        addSectionTitle(document, "SECTION C : CARRIÈRE");

        // C.2 SITUATION AU RECRUTEMENT
        addSubSectionTitle(document, "C.2 - Situation au Recrutement");
        PdfPTable recruitmentTable = createInfoTable();

        addTableRow(recruitmentTable, "N° Acte de Recrutement", safeString(personnel.getRecruitmentActNumber()));
        addTableRow(recruitmentTable, "Nature de l'Acte", safeString(personnel.getRecruitmentActNature()));
        addTableRow(recruitmentTable, "Date de Signature", formatDate(personnel.getRecruitmentActSignatureDate()));
        addTableRow(recruitmentTable, "Signataire", safeString(personnel.getRecruitmentActSignatory()));
        addTableRow(recruitmentTable, "Date de Prise d'Effet", formatDate(personnel.getRecruitmentActEffectiveDate()));
        addTableRow(recruitmentTable, "Mode de Recrutement", safeString(personnel.getRecruitmentMode()));
        addTableRow(recruitmentTable, "Profession", safeString(personnel.getRecruitmentProfession()));
        addTableRow(recruitmentTable, "Catégorie", safeString(personnel.getRecruitmentCategory()));
        addTableRow(recruitmentTable, "Administration d'Origine", safeString(personnel.getOriginAdministration()));
        addTableRow(recruitmentTable, "Ancienneté dans l'Admin. Publique",
                    personnel.getYearsInPublicService() != null ? personnel.getYearsInPublicService() + " ans" : "N/A");

        document.add(recruitmentTable);
        document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 8)));

        // C.3 SITUATION ACTUELLE
        addSubSectionTitle(document, "C.3 - Situation Actuelle");
        PdfPTable currentTable = createInfoTable();

        addTableRow(currentTable, "Type de Personnel", safeString(personnel.getPersonnelType()));
        addTableRow(currentTable, "Corps de Métier", safeString(personnel.getCorps()));
        addTableRow(currentTable, "Grade", safeString(personnel.getGrade()));
        addTableRow(currentTable, "Catégorie", safeString(personnel.getCategory()));
        addTableRow(currentTable, "Classe", safeString(personnel.getClasse()));
        addTableRow(currentTable, "Échelon", safeString(personnel.getEchelon()));
        addTableRow(currentTable, "Indice", safeString(personnel.getIndice()));
        addTableRow(currentTable, "Poste Actuel", safeRelation(personnel.getCurrentPosition(), Position::getTitle));
        addTableRow(currentTable, "Date d'Affectation", formatDate(personnel.getServiceStartDate()));
        addTableRow(currentTable, "N° Acte Actuel", safeString(personnel.getCurrentActNumber()));
        addTableRow(currentTable, "Nature Acte Actuel", safeString(personnel.getCurrentActNature()));
        addTableRow(currentTable, "Fonction 1", safeString(personnel.getCurrentOtherFunction1()));
        addTableRow(currentTable, "Fonction 2", safeString(personnel.getCurrentOtherFunction2()));
        addTableRow(currentTable, "Fonction 3", safeString(personnel.getCurrentOtherFunction3()));

        document.add(currentTable);
        document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 8)));

        // C.4 EMPLOYEUR
        addSubSectionTitle(document, "C.4 - Employeur");
        PdfPTable employerTable = createInfoTable();

        addTableRow(employerTable, "Structure", safeRelation(personnel.getStructure(), AdministrativeStructure::getName));
        addTableRow(employerTable, "Lieu d'Affectation", safeString(personnel.getWorkLocation()));
        addTableRow(employerTable, "Ville", safeString(personnel.getWorkCity()));
        addTableRow(employerTable, "Tél. Bureau", safeString(personnel.getOfficePhone()));
        addTableRow(employerTable, "Tél. Portable", safeString(personnel.getMobile()));
        addTableRow(employerTable, "Fax", safeString(personnel.getOfficeFax()));
        addTableRow(employerTable, "Email Professionnel", safeString(personnel.getProfessionalEmail()));

        document.add(employerTable);
        document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 10)));
    }

    private void addPDFCareerMovementsHistory(Document document, List<CareerMovement> movements) throws DocumentException {
        addSectionTitle(document, "HISTORIQUE DES MOUVEMENTS DE CARRIÈRE");

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);

        // En-tête
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.WHITE);
        addHeaderCell(table, "Date", headerFont);
        addHeaderCell(table, "Type de Mouvement", headerFont);
        addHeaderCell(table, "Structure Source", headerFont);
        addHeaderCell(table, "Structure Destination", headerFont);
        addHeaderCell(table, "Statut", headerFont);

        // Données
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);
        for (CareerMovement movement : movements) {
            table.addCell(new Phrase(formatDate(movement.getEffectiveDate()), dataFont));
            table.addCell(new Phrase(safeString(movement.getMovementType()), dataFont));
            table.addCell(new Phrase(safeRelation(movement.getSourceStructure(), AdministrativeStructure::getName), dataFont));
            table.addCell(new Phrase(safeRelation(movement.getDestinationStructure(), AdministrativeStructure::getName), dataFont));
            table.addCell(new Phrase(safeString(movement.getStatus()), dataFont));
        }

        document.add(table);
        document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 10)));
    }

    private void addPDFTrainingsHistory(Document document, List<ProfessionalTraining> trainings, List<TrainingEnrollment> modernEnrollments) throws DocumentException {
        addSectionTitle(document, "HISTORIQUE DES FORMATIONS");

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);

        // En-tête
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.WHITE);
        addHeaderCell(table, "Domaine", headerFont);
        addHeaderCell(table, "Formation", headerFont);
        addHeaderCell(table, "Formateur", headerFont);
        addHeaderCell(table, "Début", headerFont);
        addHeaderCell(table, "Fin", headerFont);
        addHeaderCell(table, "Statut", headerFont);

        // Données - Historique ancien (ProfessionalTraining)
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);
        for (ProfessionalTraining training : trainings) {
            table.addCell(new Phrase(safeString(training.getTrainingField()), dataFont));
            table.addCell(new Phrase(safeString(training.getDescription()), dataFont));
            table.addCell(new Phrase(safeString(training.getTrainer()), dataFont));
            table.addCell(new Phrase(formatDate(training.getStartDate()), dataFont));
            table.addCell(new Phrase(formatDate(training.getEndDate()), dataFont));
            table.addCell(new Phrase(safeString(training.getStatus()), dataFont));
        }

        // Données - Sessions modernes (TrainingEnrollment)
        for (TrainingEnrollment enrollment : modernEnrollments) {
            if (enrollment.getSession() != null && enrollment.getSession().getTraining() != null) {
                TrainingSession session = enrollment.getSession();
                Training training = session.getTraining();

                table.addCell(new Phrase(safeString(training.getTrainingField()), dataFont));
                table.addCell(new Phrase(safeString(training.getTitle()), dataFont));
                table.addCell(new Phrase(
                    session.getTrainer() != null ? session.getTrainer().getFullName() : "N/A",
                    dataFont
                ));
                table.addCell(new Phrase(formatDate(session.getStartDate()), dataFont));
                table.addCell(new Phrase(formatDate(session.getEndDate()), dataFont));
                table.addCell(new Phrase(safeString(enrollment.getStatus()), dataFont));
            }
        }

        document.add(table);
        document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 10)));
    }

    private void addPDFLeavesHistory(Document document, List<PersonnelLeave> leaves) throws DocumentException {
        addSectionTitle(document, "HISTORIQUE DES CONGÉS");

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);

        // En-tête
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.WHITE);
        addHeaderCell(table, "Motif", headerFont);
        addHeaderCell(table, "Début", headerFont);
        addHeaderCell(table, "Fin", headerFont);
        addHeaderCell(table, "Durée", headerFont);
        addHeaderCell(table, "Statut", headerFont);

        // Données
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);
        for (PersonnelLeave leave : leaves) {
            table.addCell(new Phrase(safeString(leave.getLeaveReason()), dataFont));
            table.addCell(new Phrase(formatDate(leave.getEffectiveDate()), dataFont));
            table.addCell(new Phrase(formatDate(leave.getExpiryDate()), dataFont));
            table.addCell(new Phrase(leave.getDurationDays() != null ? leave.getDurationDays() + " jours" : "N/A", dataFont));
            table.addCell(new Phrase(safeString(leave.getStatus()), dataFont));
        }

        document.add(table);
        document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 10)));
    }

    private void addPDFPreviousPositions(Document document, List<PreviousPosition> positions) throws DocumentException {
        addSectionTitle(document, "POSTES ANTÉRIEURS");

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);

        // En-tête
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.WHITE);
        addHeaderCell(table, "Poste", headerFont);
        addHeaderCell(table, "Structure", headerFont);
        addHeaderCell(table, "Début", headerFont);
        addHeaderCell(table, "Fin", headerFont);

        // Données
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);
        for (PreviousPosition position : positions) {
            table.addCell(new Phrase(safeString(position.getPositionTitle()), dataFont));
            table.addCell(new Phrase(safeString(position.getStructureName()), dataFont));
            table.addCell(new Phrase(formatDate(position.getStartDate()), dataFont));
            table.addCell(new Phrase(formatDate(position.getEndDate()), dataFont));
        }

        document.add(table);
        document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 10)));
    }

    private void addPDFFooter(Document document) throws DocumentException {
        addSeparator(document);

        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, BaseColor.GRAY);
        Paragraph footer = new Paragraph("Document généré automatiquement par le système HRMS - MINAT", footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(10);
        document.add(footer);
    }

    // ==================== EXCEL HELPER METHODS ====================

    private void addExcelMainInfoSheet(Workbook workbook, Personnel personnel) {
        Sheet sheet = workbook.createSheet("Informations Générales");

        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle sectionStyle = createSectionStyle(workbook);
        CellStyle labelStyle = createLabelStyle(workbook);
        CellStyle valueStyle = createValueStyle(workbook);

        int rowNum = 0;

        // Titre
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("FICHE DE RENSEIGNEMENT DU PERSONNEL - MINAT");
        titleCell.setCellStyle(titleStyle);
        rowNum++;

        // Matricule et nom
        addExcelRow(sheet, rowNum++, "Matricule", safeString(personnel.getMatricule()), labelStyle, valueStyle);
        addExcelRow(sheet, rowNum++, "Nom Complet", personnel.getFullName(), labelStyle, valueStyle);
        rowNum++;

        // SECTION A
        addExcelSectionTitle(sheet, rowNum++, "SECTION A : IDENTIFICATION", sectionStyle);
        addExcelRow(sheet, rowNum++, "Nom", safeString(personnel.getLastName()), labelStyle, valueStyle);
        addExcelRow(sheet, rowNum++, "Prénom", safeString(personnel.getFirstName()), labelStyle, valueStyle);
        addExcelRow(sheet, rowNum++, "Sexe", safeString(personnel.getGender()), labelStyle, valueStyle);
        addExcelRow(sheet, rowNum++, "Date de Naissance", formatDate(personnel.getDateOfBirth()), labelStyle, valueStyle);
        addExcelRow(sheet, rowNum++, "Âge", personnel.getAge() != null ? personnel.getAge() + " ans" : "N/A", labelStyle, valueStyle);
        addExcelRow(sheet, rowNum++, "Nationalité", safeString(personnel.getNationalityType()), labelStyle, valueStyle);
        addExcelRow(sheet, rowNum++, "N° CNI", safeString(personnel.getCniNumber()), labelStyle, valueStyle);
        addExcelRow(sheet, rowNum++, "Père", safeString(personnel.getFatherName()), labelStyle, valueStyle);
        addExcelRow(sheet, rowNum++, "Mère", safeString(personnel.getMotherName()), labelStyle, valueStyle);
        rowNum++;

        // SECTION B
        addExcelSectionTitle(sheet, rowNum++, "SECTION B : QUALIFICATIONS", sectionStyle);
        addExcelRow(sheet, rowNum++, "Diplôme de Recrutement", safeString(personnel.getRecruitmentDiplomaTitle()), labelStyle, valueStyle);
        addExcelRow(sheet, rowNum++, "Type", safeString(personnel.getRecruitmentDiplomaType()), labelStyle, valueStyle);
        addExcelRow(sheet, rowNum++, "Diplôme le Plus Élevé", safeString(personnel.getHighestDiplomaTitle()), labelStyle, valueStyle);
        addExcelRow(sheet, rowNum++, "Type", safeString(personnel.getHighestDiplomaType()), labelStyle, valueStyle);
        rowNum++;

        // SECTION C
        addExcelSectionTitle(sheet, rowNum++, "SECTION C : CARRIÈRE", sectionStyle);
        addExcelRow(sheet, rowNum++, "Corps", safeString(personnel.getCorps()), labelStyle, valueStyle);
        addExcelRow(sheet, rowNum++, "Grade", safeString(personnel.getGrade()), labelStyle, valueStyle);
        addExcelRow(sheet, rowNum++, "Catégorie", safeString(personnel.getCategory()), labelStyle, valueStyle);
        addExcelRow(sheet, rowNum++, "Échelon", safeString(personnel.getEchelon()), labelStyle, valueStyle);
        addExcelRow(sheet, rowNum++, "Indice", safeString(personnel.getIndice()), labelStyle, valueStyle);
        addExcelRow(sheet, rowNum++, "Structure", safeRelation(personnel.getStructure(), AdministrativeStructure::getName), labelStyle, valueStyle);
        addExcelRow(sheet, rowNum++, "Poste Actuel", safeRelation(personnel.getCurrentPosition(), Position::getTitle), labelStyle, valueStyle);
        addExcelRow(sheet, rowNum++, "Type de Personnel", safeString(personnel.getPersonnelType()), labelStyle, valueStyle);

        // Auto-size
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void addExcelCareerMovementsSheet(Workbook workbook, List<CareerMovement> movements) {
        Sheet sheet = workbook.createSheet("Mouvements de Carrière");

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createValueStyle(workbook);

        // En-tête
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Date", "Type de Mouvement", "Structure Source", "Structure Destination", "Poste Source", "Poste Destination", "Statut"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Données
        int rowNum = 1;
        for (CareerMovement movement : movements) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(formatDate(movement.getEffectiveDate()));
            row.createCell(1).setCellValue(safeString(movement.getMovementType()));
            row.createCell(2).setCellValue(safeRelation(movement.getSourceStructure(), AdministrativeStructure::getName));
            row.createCell(3).setCellValue(safeRelation(movement.getDestinationStructure(), AdministrativeStructure::getName));
            row.createCell(4).setCellValue(safeRelation(movement.getSourcePosition(), Position::getTitle));
            row.createCell(5).setCellValue(safeRelation(movement.getDestinationPosition(), Position::getTitle));
            row.createCell(6).setCellValue(safeString(movement.getStatus()));
        }

        // Auto-size
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void addExcelTrainingsSheet(Workbook workbook, List<ProfessionalTraining> trainings, List<TrainingEnrollment> modernEnrollments) {
        Sheet sheet = workbook.createSheet("Formations");

        CellStyle headerStyle = createHeaderStyle(workbook);

        // En-tête
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Source", "Domaine", "Formation", "Formateur", "Début", "Fin", "Durée (jours)", "Lieu", "Statut", "Certificat"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Données - Historique ancien (ProfessionalTraining)
        int rowNum = 1;
        for (ProfessionalTraining training : trainings) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue("Historique");
            row.createCell(1).setCellValue(safeString(training.getTrainingField()));
            row.createCell(2).setCellValue(safeString(training.getDescription()));
            row.createCell(3).setCellValue(safeString(training.getTrainer()));
            row.createCell(4).setCellValue(formatDate(training.getStartDate()));
            row.createCell(5).setCellValue(formatDate(training.getEndDate()));
            row.createCell(6).setCellValue(training.getDurationDays() != null ? training.getDurationDays().toString() : "N/A");
            row.createCell(7).setCellValue(safeString(training.getTrainingLocation()));
            row.createCell(8).setCellValue(safeString(training.getStatus()));
            row.createCell(9).setCellValue(safeString(training.getCertificateObtained()));
        }

        // Données - Sessions modernes (TrainingEnrollment)
        for (TrainingEnrollment enrollment : modernEnrollments) {
            if (enrollment.getSession() != null && enrollment.getSession().getTraining() != null) {
                TrainingSession session = enrollment.getSession();
                Training training = session.getTraining();

                int durationDays = 0;
                if (session.getStartDate() != null && session.getEndDate() != null) {
                    durationDays = (int) java.time.temporal.ChronoUnit.DAYS.between(
                        session.getStartDate(),
                        session.getEndDate()
                    ) + 1;
                }

                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue("Session Moderne");
                row.createCell(1).setCellValue(safeString(training.getTrainingField()));
                row.createCell(2).setCellValue(safeString(training.getTitle()));
                row.createCell(3).setCellValue(session.getTrainer() != null ? session.getTrainer().getFullName() : "N/A");
                row.createCell(4).setCellValue(formatDate(session.getStartDate()));
                row.createCell(5).setCellValue(formatDate(session.getEndDate()));
                row.createCell(6).setCellValue(String.valueOf(durationDays));
                row.createCell(7).setCellValue(safeString(session.getLocation()));
                row.createCell(8).setCellValue(safeString(enrollment.getStatus()));
                row.createCell(9).setCellValue(Boolean.TRUE.equals(enrollment.getCertificateIssued()) ? enrollment.getCertificateNumber() : "Non");
            }
        }

        // Auto-size
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void addExcelLeavesSheet(Workbook workbook, List<PersonnelLeave> leaves) {
        Sheet sheet = workbook.createSheet("Congés");

        CellStyle headerStyle = createHeaderStyle(workbook);

        // En-tête
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Motif", "Début", "Fin", "Durée (jours)", "Statut", "N° Décision", "Notes"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Données
        int rowNum = 1;
        for (PersonnelLeave leave : leaves) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(safeString(leave.getLeaveReason()));
            row.createCell(1).setCellValue(formatDate(leave.getEffectiveDate()));
            row.createCell(2).setCellValue(formatDate(leave.getExpiryDate()));
            row.createCell(3).setCellValue(leave.getDurationDays() != null ? leave.getDurationDays().toString() : "N/A");
            row.createCell(4).setCellValue(safeString(leave.getStatus()));
            row.createCell(5).setCellValue(safeString(leave.getDecisionNumber()));
            row.createCell(6).setCellValue(safeString(leave.getNotes()));
        }

        // Auto-size
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void addExcelPreviousPositionsSheet(Workbook workbook, List<PreviousPosition> positions) {
        Sheet sheet = workbook.createSheet("Postes Antérieurs");

        CellStyle headerStyle = createHeaderStyle(workbook);

        // En-tête
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Poste", "Structure", "Début", "Fin", "Durée"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Données
        int rowNum = 1;
        for (PreviousPosition position : positions) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(safeString(position.getPositionTitle()));
            row.createCell(1).setCellValue(safeString(position.getStructureName()));
            row.createCell(2).setCellValue(formatDate(position.getStartDate()));
            row.createCell(3).setCellValue(formatDate(position.getEndDate()));
            row.createCell(4).setCellValue(safeString(position.getDuration()));
        }

        // Auto-size
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // ==================== UTILITY METHODS ====================

    private PdfPTable createInfoTable() {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(5f);
        table.setSpacingAfter(5f);
        try {
            table.setWidths(new float[]{2f, 3f});
        } catch (DocumentException e) {
            log.error("Error setting table widths", e);
        }
        return table;
    }

    private void addTableRow(PdfPTable table, String label, String value) {
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.BLACK);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);

        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        labelCell.setPadding(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }

    private void addSectionTitle(Document document, String title) throws DocumentException {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, BaseColor.BLACK);
        Paragraph paragraph = new Paragraph(title, font);
        paragraph.setSpacingBefore(15);
        paragraph.setSpacingAfter(10);
        document.add(paragraph);
    }

    private void addSubSectionTitle(Document document, String title) throws DocumentException {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, new BaseColor(50, 50, 150));
        Paragraph paragraph = new Paragraph(title, font);
        paragraph.setSpacingBefore(8);
        paragraph.setSpacingAfter(5);
        document.add(paragraph);
    }

    private void addSeparator(Document document) throws DocumentException {
        LineSeparator separator = new LineSeparator();
        separator.setLineColor(BaseColor.GRAY);
        document.add(separator);
        document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 6)));
    }

    private void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(BaseColor.DARK_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private void addExcelRow(Sheet sheet, int rowNum, String label, String value, CellStyle labelStyle, CellStyle valueStyle) {
        Row row = sheet.createRow(rowNum);
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(labelStyle);

        Cell valueCell = row.createCell(1);
        valueCell.setCellValue(value);
        valueCell.setCellStyle(valueStyle);
    }

    private void addExcelSectionTitle(Sheet sheet, int rowNum, String title, CellStyle style) {
        Row row = sheet.createRow(rowNum);
        Cell cell = row.createCell(0);
        cell.setCellValue(title);
        cell.setCellStyle(style);
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createSectionStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.BLUE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createLabelStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createValueStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_80_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "N/A";
    }

    private String safeString(Object value) {
        return value != null ? value.toString() : "N/A";
    }

    private <T> String safeRelation(T entity, java.util.function.Function<T, String> getter) {
        return entity != null ? getter.apply(entity) : "N/A";
    }
}
