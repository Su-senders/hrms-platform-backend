package com.hrms.service;

import com.hrms.dto.GlobalMovementStatisticsDTO;
import com.hrms.dto.StructureMovementStatisticsDTO;
import com.hrms.entity.AdministrativeStructure;
import com.hrms.entity.CareerMovement;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.repository.AdministrativeStructureRepository;
import com.hrms.repository.CareerMovementRepository;
import com.hrms.repository.PersonnelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service de statistiques sur les mouvements de carrière
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareerMovementStatisticsService {

    private final CareerMovementRepository movementRepository;
    private final AdministrativeStructureRepository structureRepository;
    private final PersonnelRepository personnelRepository;

    /**
     * Statistiques globales pour une année donnée
     */
    public GlobalMovementStatisticsDTO getGlobalStatistics(Integer year) {
        log.info("Calcul des statistiques globales de mouvements pour l'année {}", year);

        LocalDate startDate = year != null ? LocalDate.of(year, 1, 1) : LocalDate.of(LocalDate.now().getYear(), 1, 1);
        LocalDate endDate = year != null ? LocalDate.of(year, 12, 31) : LocalDate.of(LocalDate.now().getYear(), 12, 31);

        List<CareerMovement> movements = movementRepository.findByDateRange(startDate, endDate);

        // Répartition par type
        Map<String, Long> byType = movements.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getMovementType().name(),
                        Collectors.counting()
                ));

        // Répartition par statut
        Map<String, Long> byStatus = movements.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getStatus().name(),
                        Collectors.counting()
                ));

        // Répartition par mois
        Map<String, Long> byMonth = movements.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getMovementDate().getMonth().name(),
                        Collectors.counting()
                ));

        // Répartition par trimestre
        Map<String, Long> byQuarter = movements.stream()
                .collect(Collectors.groupingBy(
                        m -> getQuarter(m.getMovementDate()),
                        Collectors.counting()
                ));

        // Évolution mensuelle (format: "2024-01", "2024-02", etc.)
        Map<String, Long> monthlyEvolution = movements.stream()
                .collect(Collectors.groupingBy(
                        m -> String.format("%d-%02d", m.getMovementDate().getYear(), m.getMovementDate().getMonthValue()),
                        Collectors.counting()
                ));

        // Délai moyen de traitement (création → exécution)
        double averageProcessingDays = movements.stream()
                .filter(m -> m.getStatus() == CareerMovement.MovementStatus.EXECUTED && 
                            m.getCreatedAt() != null && m.getApprovalDate() != null)
                .mapToLong(m -> ChronoUnit.DAYS.between(
                        m.getCreatedAt().toLocalDate(), 
                        m.getApprovalDate()))
                .average()
                .orElse(0.0);

        return GlobalMovementStatisticsDTO.builder()
                .year(year != null ? year : LocalDate.now().getYear())
                .totalMovements((long) movements.size())
                .pendingMovements(byStatus.getOrDefault("PENDING", 0L))
                .approvedMovements(byStatus.getOrDefault("APPROVED", 0L))
                .executedMovements(byStatus.getOrDefault("EXECUTED", 0L))
                .cancelledMovements(byStatus.getOrDefault("CANCELLED", 0L))
                .rejectedMovements(byStatus.getOrDefault("REJECTED", 0L))
                .movementsByType(byType)
                .movementsByStatus(byStatus)
                .movementsByMonth(byMonth)
                .movementsByQuarter(byQuarter)
                .monthlyEvolution(monthlyEvolution)
                .averageProcessingDays(averageProcessingDays)
                .build();
    }

    /**
     * Statistiques par structure pour une année donnée
     */
    public StructureMovementStatisticsDTO getStructureStatistics(Long structureId, Integer year) {
        log.info("Calcul des statistiques de mouvements pour la structure {} - Année {}", structureId, year);

        AdministrativeStructure structure = structureRepository.findById(structureId)
                .orElseThrow(() -> new ResourceNotFoundException("Structure", "id", structureId));

        LocalDate startDate = year != null ? LocalDate.of(year, 1, 1) : LocalDate.of(LocalDate.now().getYear(), 1, 1);
        LocalDate endDate = year != null ? LocalDate.of(year, 12, 31) : LocalDate.of(LocalDate.now().getYear(), 12, 31);

        // Tous les mouvements liés à cette structure
        List<CareerMovement> allMovements = movementRepository.findByDateRange(startDate, endDate).stream()
                .filter(m -> (m.getSourceStructure() != null && m.getSourceStructure().getId().equals(structureId)) ||
                            (m.getDestinationStructure() != null && m.getDestinationStructure().getId().equals(structureId)))
                .collect(Collectors.toList());

        // Mouvements entrants (vers cette structure)
        List<CareerMovement> incoming = allMovements.stream()
                .filter(m -> m.getDestinationStructure() != null && m.getDestinationStructure().getId().equals(structureId))
                .collect(Collectors.toList());

        // Mouvements sortants (depuis cette structure)
        List<CareerMovement> outgoing = allMovements.stream()
                .filter(m -> m.getSourceStructure() != null && m.getSourceStructure().getId().equals(structureId))
                .collect(Collectors.toList());

        // Répartition par type (tous mouvements)
        Map<String, Long> byType = allMovements.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getMovementType().name(),
                        Collectors.counting()
                ));

        // Répartition par statut
        Map<String, Long> byStatus = allMovements.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getStatus().name(),
                        Collectors.counting()
                ));

        // Mouvements entrants par type
        Map<String, Long> incomingByType = incoming.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getMovementType().name(),
                        Collectors.counting()
                ));

        // Mouvements sortants par type
        Map<String, Long> outgoingByType = outgoing.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getMovementType().name(),
                        Collectors.counting()
                ));

        // Taux de rotation (mouvements / effectif moyen)
        long structurePersonnelCount = personnelRepository.findByStructureId(structureId, 
                org.springframework.data.domain.Pageable.unpaged()).getContent().size();
        double rotationRate = structurePersonnelCount > 0 
                ? (double) allMovements.size() / structurePersonnelCount 
                : 0.0;

        // Durée moyenne d'affectation (pour mouvements EXECUTED)
        double averageDuration = allMovements.stream()
                .filter(m -> m.getStatus() == CareerMovement.MovementStatus.EXECUTED && 
                            m.getMovementDate() != null && m.getEndDate() != null)
                .mapToLong(m -> ChronoUnit.DAYS.between(m.getMovementDate(), m.getEndDate()))
                .average()
                .orElse(0.0);

        return StructureMovementStatisticsDTO.builder()
                .structureId(structureId)
                .structureName(structure.getName())
                .structureCode(structure.getCode())
                .year(year != null ? year : LocalDate.now().getYear())
                .incomingMovements((long) incoming.size())
                .outgoingMovements((long) outgoing.size())
                .totalMovements((long) allMovements.size())
                .movementsByType(byType)
                .movementsByStatus(byStatus)
                .rotationRate(rotationRate)
                .averageAssignmentDuration(averageDuration)
                .incomingByType(incomingByType)
                .outgoingByType(outgoingByType)
                .build();
    }

    /**
     * Statistiques par type de mouvement
     */
    public Map<String, Long> getStatisticsByType() {
        log.info("Calcul des statistiques par type de mouvement");
        List<Object[]> results = movementRepository.countByMovementType();
        return results.stream()
                .collect(Collectors.toMap(
                        obj -> obj[0].toString(),
                        obj -> (Long) obj[1]
                ));
    }

    /**
     * Statistiques par statut
     */
    public Map<String, Long> getStatisticsByStatus() {
        log.info("Calcul des statistiques par statut");
        List<Object[]> results = movementRepository.countByStatus();
        return results.stream()
                .collect(Collectors.toMap(
                        obj -> obj[0].toString(),
                        obj -> (Long) obj[1]
                ));
    }

    /**
     * Obtenir le trimestre d'une date
     */
    private String getQuarter(LocalDate date) {
        int month = date.getMonthValue();
        if (month <= 3) return "T1";
        if (month <= 6) return "T2";
        if (month <= 9) return "T3";
        return "T4";
    }
}

