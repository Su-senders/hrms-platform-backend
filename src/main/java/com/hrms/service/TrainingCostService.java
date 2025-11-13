package com.hrms.service;

import com.hrms.dto.TrainingCostCreateDTO;
import com.hrms.dto.TrainingCostDTO;
import com.hrms.dto.TrainingCostUpdateDTO;
import com.hrms.entity.TrainingCost;
import com.hrms.entity.TrainingSession;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.mapper.TrainingCostMapper;
import com.hrms.repository.TrainingCostRepository;
import com.hrms.repository.TrainingSessionRepository;
import com.hrms.util.AuditUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TrainingCostService {

    private final TrainingCostRepository costRepository;
    private final TrainingSessionRepository sessionRepository;
    private final TrainingCostMapper costMapper;
    private final AuditUtil auditUtil;

    public TrainingCostDTO createCost(TrainingCostCreateDTO dto) {
        log.info("Creating cost for session ID: {}", dto.getSessionId());

        TrainingSession session = sessionRepository.findById(dto.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("TrainingSession", "id", dto.getSessionId()));

        TrainingCost cost = costMapper.toEntity(dto);
        cost.setSession(session);
        cost.setCreatedBy(auditUtil.getCurrentUser());
        cost.setCreatedAt(java.time.LocalDateTime.now());

        TrainingCost saved = costRepository.save(cost);

        // Recalculate session total cost
        recalculateSessionCosts(session);

        log.info("Training cost created with ID: {}", saved.getId());
        return costMapper.toDTO(saved);
    }

    public TrainingCostDTO updateCost(Long id, TrainingCostUpdateDTO dto) {
        log.info("Updating cost ID: {}", id);

        TrainingCost cost = costRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TrainingCost", "id", id));

        costMapper.updateEntity(dto, cost);
        cost.setUpdatedBy(auditUtil.getCurrentUser());
        cost.setUpdatedAt(java.time.LocalDateTime.now());

        TrainingCost updated = costRepository.save(cost);

        // Recalculate session total cost
        recalculateSessionCosts(updated.getSession());

        log.info("Training cost updated: {}", id);
        return costMapper.toDTO(updated);
    }

    @Transactional(readOnly = true)
    public TrainingCostDTO getCostById(Long id) {
        TrainingCost cost = costRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TrainingCost", "id", id));
        return costMapper.toDTO(cost);
    }

    @Transactional(readOnly = true)
    public List<TrainingCostDTO> getCostsBySession(Long sessionId) {
        return costRepository.findBySessionId(sessionId).stream()
                .map(costMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TrainingCostDTO> getCostsBySessionAndType(Long sessionId, String costType) {
        TrainingCost.CostType type = TrainingCost.CostType.valueOf(costType.toUpperCase());
        return costRepository.findBySessionIdAndCostType(sessionId, type).stream()
                .map(costMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalCostBySession(Long sessionId) {
        BigDecimal total = costRepository.calculateTotalCostBySessionId(sessionId);
        return total != null ? total : BigDecimal.ZERO;
    }

    public TrainingCostDTO markAsPaid(Long id) {
        log.info("Marking cost ID: {} as paid", id);
        TrainingCost cost = costRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TrainingCost", "id", id));

        cost.markAsPaid();
        cost.setUpdatedBy(auditUtil.getCurrentUser());
        cost.setUpdatedAt(java.time.LocalDateTime.now());

        return costMapper.toDTO(costRepository.save(cost));
    }

    /**
     * Recalculate session costs
     */
    private void recalculateSessionCosts(TrainingSession session) {
        BigDecimal totalCost = costRepository.calculateTotalCostBySessionId(session.getId());
        if (totalCost != null) {
            // Mettre à jour actualCost (nouveau champ)
            session.setActualCost(totalCost);
            session.updateActualCost(); // Appeler la méthode pour s'assurer de la cohérence
            
            // Garder totalCost pour compatibilité (déprécié)
            session.setTotalCost(totalCost);
            
            int enrolledCount = session.getEnrolledCount();
            if (enrolledCount > 0) {
                session.setCostPerParticipant(totalCost.divide(BigDecimal.valueOf(enrolledCount), 2, BigDecimal.ROUND_HALF_UP));
            }
            sessionRepository.save(session);
        }
    }

    public void deleteCost(Long id) {
        log.info("Soft deleting cost with ID: {}", id);
        TrainingCost cost = costRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TrainingCost", "id", id));
        
        TrainingSession session = cost.getSession();
        cost.softDelete(auditUtil.getCurrentUser());
        costRepository.save(cost);

        // Recalculate session costs
        recalculateSessionCosts(session);
        log.info("Training cost soft deleted: {}", id);
    }
}



