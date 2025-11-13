package com.hrms.service;

import com.hrms.entity.AdministrativeStructure;
import com.hrms.entity.AssignmentHistory;
import com.hrms.entity.Personnel;
import com.hrms.entity.Position;
import com.hrms.repository.AssignmentHistoryRepository;
import com.hrms.util.AuditUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service de gestion de l'historique des affectations
 * Enregistre automatiquement tous les changements d'affectation
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AssignmentHistoryService {

    private final AssignmentHistoryRepository assignmentHistoryRepository;
    private final AuditUtil auditUtil;

    /**
     * Enregistre une nouvelle affectation
     */
    public AssignmentHistory recordAssignment(
        Personnel personnel,
        Position oldPosition,
        Position newPosition,
        AdministrativeStructure oldStructure,
        AdministrativeStructure newStructure,
        LocalDate startDate,
        AssignmentHistory.MovementType movementType,
        String reason
    ) {
        log.info("Enregistrement d'une affectation - Personnel: {}, Type: {}",
            personnel.getMatricule(), movementType);

        // Terminer l'affectation active actuelle si elle existe
        AssignmentHistory activeAssignment = assignmentHistoryRepository
            .findActiveAssignmentByPersonnel(personnel.getId());

        if (activeAssignment != null) {
            activeAssignment.endAssignment(startDate.minusDays(1));
            assignmentHistoryRepository.save(activeAssignment);
            log.info("Affectation précédente terminée");
        }

        // Créer la nouvelle affectation
        AssignmentHistory newAssignment = AssignmentHistory.builder()
            .personnel(personnel)
            .positionOld(oldPosition)
            .positionNew(newPosition)
            .structureOld(oldStructure)
            .structureNew(newStructure)
            .startDate(startDate)
            .movementType(movementType)
            .reason(reason)
            .status(AssignmentHistory.AssignmentStatus.ACTIVE)
            .build();

        newAssignment.setCreatedBy(auditUtil.getCurrentUser());
        newAssignment.setCreatedDate(LocalDate.now());

        AssignmentHistory saved = assignmentHistoryRepository.save(newAssignment);
        log.info("Nouvelle affectation enregistrée avec ID: {}", saved.getId());

        return saved;
    }

    /**
     * Enregistre un document de décision pour une affectation
     */
    public AssignmentHistory attachDecisionDocument(
        Long assignmentId,
        String decisionDocument,
        String decisionNumber,
        LocalDate decisionDate
    ) {
        log.info("Ajout de document de décision à l'affectation {}", assignmentId);

        AssignmentHistory assignment = assignmentHistoryRepository.findById(assignmentId)
            .orElseThrow(() -> new RuntimeException("Affectation non trouvée"));

        assignment.setDecisionDocument(decisionDocument);
        assignment.setDecisionNumber(decisionNumber);
        assignment.setDecisionDate(decisionDate);
        assignment.setUpdatedBy(auditUtil.getCurrentUser());
        assignment.setUpdatedDate(LocalDate.now());

        return assignmentHistoryRepository.save(assignment);
    }

    /**
     * Obtient l'historique des affectations d'un personnel
     */
    @Transactional(readOnly = true)
    public List<AssignmentHistory> getPersonnelAssignmentHistory(Long personnelId) {
        log.info("Récupération de l'historique des affectations pour le personnel {}", personnelId);
        return assignmentHistoryRepository
            .findByPersonnelIdAndDeletedFalseOrderByStartDateDesc(personnelId);
    }

    /**
     * Obtient l'affectation active d'un personnel
     */
    @Transactional(readOnly = true)
    public AssignmentHistory getActiveAssignment(Long personnelId) {
        return assignmentHistoryRepository.findActiveAssignmentByPersonnel(personnelId);
    }

    /**
     * Obtient l'historique des affectations d'un poste
     */
    @Transactional(readOnly = true)
    public List<AssignmentHistory> getPositionAssignmentHistory(Long positionId) {
        log.info("Récupération de l'historique des affectations pour le poste {}", positionId);
        return assignmentHistoryRepository
            .findByPositionNewIdOrPositionOldIdAndDeletedFalseOrderByStartDateDesc(positionId, positionId);
    }

    /**
     * Obtient l'historique des affectations d'une structure
     */
    @Transactional(readOnly = true)
    public List<AssignmentHistory> getStructureAssignmentHistory(Long structureId) {
        log.info("Récupération de l'historique des affectations pour la structure {}", structureId);
        return assignmentHistoryRepository
            .findByStructureNewIdOrStructureOldIdAndDeletedFalseOrderByStartDateDesc(structureId, structureId);
    }

    /**
     * Obtient les affectations actives d'une structure
     */
    @Transactional(readOnly = true)
    public List<AssignmentHistory> getActiveAssignmentsByStructure(Long structureId) {
        return assignmentHistoryRepository.findActiveAssignmentsByStructure(structureId);
    }

    /**
     * Termine une affectation
     */
    public AssignmentHistory endAssignment(Long assignmentId, LocalDate endDate) {
        log.info("Fin de l'affectation {} à la date {}", assignmentId, endDate);

        AssignmentHistory assignment = assignmentHistoryRepository.findById(assignmentId)
            .orElseThrow(() -> new RuntimeException("Affectation non trouvée"));

        assignment.endAssignment(endDate);
        assignment.setUpdatedBy(auditUtil.getCurrentUser());
        assignment.setUpdatedDate(LocalDate.now());

        return assignmentHistoryRepository.save(assignment);
    }

    /**
     * Annule une affectation
     */
    public AssignmentHistory cancelAssignment(Long assignmentId, String reason) {
        log.info("Annulation de l'affectation {} - Raison: {}", assignmentId, reason);

        AssignmentHistory assignment = assignmentHistoryRepository.findById(assignmentId)
            .orElseThrow(() -> new RuntimeException("Affectation non trouvée"));

        assignment.cancel(reason);
        assignment.setUpdatedBy(auditUtil.getCurrentUser());
        assignment.setUpdatedDate(LocalDate.now());

        return assignmentHistoryRepository.save(assignment);
    }

    /**
     * Compte les affectations d'un personnel
     */
    @Transactional(readOnly = true)
    public long countPersonnelAssignments(Long personnelId) {
        return assignmentHistoryRepository.countByPersonnelIdAndDeletedFalse(personnelId);
    }

    /**
     * Obtient les affectations par type de mouvement
     */
    @Transactional(readOnly = true)
    public List<AssignmentHistory> getAssignmentsByMovementType(AssignmentHistory.MovementType movementType) {
        return assignmentHistoryRepository
            .findByMovementTypeAndDeletedFalseOrderByStartDateDesc(movementType);
    }

    /**
     * Obtient les affectations sur une période
     */
    @Transactional(readOnly = true)
    public List<AssignmentHistory> getAssignmentsByPeriod(LocalDate startDate, LocalDate endDate) {
        return assignmentHistoryRepository
            .findByStartDateBetweenAndDeletedFalseOrderByStartDateDesc(startDate, endDate);
    }

    /**
     * Obtient les statistiques globales des affectations
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAssignmentStatistics() {
        log.info("Calcul des statistiques globales des affectations");

        List<AssignmentHistory> allAssignments = assignmentHistoryRepository.findByDeletedFalse();

        long totalAssignments = allAssignments.size();
        long activeAssignments = allAssignments.stream()
            .filter(a -> a.getStatus() == AssignmentHistory.AssignmentStatus.ACTIVE)
            .count();
        long completedAssignments = allAssignments.stream()
            .filter(a -> a.getStatus() == AssignmentHistory.AssignmentStatus.COMPLETED)
            .count();
        long cancelledAssignments = allAssignments.stream()
            .filter(a -> a.getStatus() == AssignmentHistory.AssignmentStatus.CANCELLED)
            .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAssignments", totalAssignments);
        stats.put("activeAssignments", activeAssignments);
        stats.put("completedAssignments", completedAssignments);
        stats.put("cancelledAssignments", cancelledAssignments);
        stats.put("byMovementType", getStatisticsByMovementType());

        return stats;
    }

    /**
     * Obtient les statistiques par type de mouvement
     */
    @Transactional(readOnly = true)
    public Map<AssignmentHistory.MovementType, Long> getStatisticsByMovementType() {
        log.info("Calcul des statistiques par type de mouvement");

        List<AssignmentHistory> allAssignments = assignmentHistoryRepository.findByDeletedFalse();

        return allAssignments.stream()
            .filter(a -> a.getMovementType() != null)
            .collect(Collectors.groupingBy(
                AssignmentHistory::getMovementType,
                Collectors.counting()
            ));
    }
}
