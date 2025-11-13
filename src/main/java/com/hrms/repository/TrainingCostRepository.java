package com.hrms.repository;

import com.hrms.entity.TrainingCost;
import com.hrms.entity.TrainingCost.CostType;
import com.hrms.entity.TrainingCost.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TrainingCostRepository extends JpaRepository<TrainingCost, Long> {

    @Query("SELECT c FROM TrainingCost c WHERE c.session.id = :sessionId AND c.deleted = false " +
           "ORDER BY c.expenseDate DESC")
    List<TrainingCost> findBySessionId(@Param("sessionId") Long sessionId);

    @Query("SELECT c FROM TrainingCost c WHERE " +
           "c.session.id = :sessionId AND c.costType = :costType AND c.deleted = false")
    List<TrainingCost> findBySessionIdAndCostType(@Param("sessionId") Long sessionId,
                                                   @Param("costType") CostType costType);

    @Query("SELECT c FROM TrainingCost c WHERE " +
           "c.session.id = :sessionId AND c.paymentStatus = :paymentStatus AND c.deleted = false")
    List<TrainingCost> findBySessionIdAndPaymentStatus(@Param("sessionId") Long sessionId,
                                                       @Param("paymentStatus") PaymentStatus paymentStatus);

    @Query("SELECT SUM(c.amount) FROM TrainingCost c WHERE " +
           "c.session.id = :sessionId AND c.deleted = false")
    BigDecimal calculateTotalCostBySessionId(@Param("sessionId") Long sessionId);

    @Query("SELECT SUM(c.amount) FROM TrainingCost c WHERE " +
           "c.session.id = :sessionId AND c.costType = :costType AND c.deleted = false")
    BigDecimal calculateTotalCostBySessionIdAndType(@Param("sessionId") Long sessionId,
                                                    @Param("costType") CostType costType);

    @Query("SELECT c.costType, SUM(c.amount) FROM TrainingCost c " +
           "WHERE c.session.id = :sessionId AND c.deleted = false GROUP BY c.costType")
    List<Object[]> sumByCostTypeForSession(@Param("sessionId") Long sessionId);

    @Query("SELECT c FROM TrainingCost c WHERE " +
           "c.expenseDate >= :startDate AND c.expenseDate <= :endDate AND c.deleted = false")
    List<TrainingCost> findByDateRange(@Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(c) FROM TrainingCost c WHERE " +
           "c.session.id = :sessionId AND c.paymentStatus = 'PENDING' AND c.deleted = false")
    long countPendingPaymentsBySessionId(@Param("sessionId") Long sessionId);
}




