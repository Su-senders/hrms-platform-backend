package com.hrms.repository;

import com.hrms.entity.AuditLog;
import com.hrms.entity.AuditLog.Action;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for AuditLog entity
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    // Find by entity
    @Query("SELECT a FROM AuditLog a WHERE a.entityName = :entityName AND a.entityId = :entityId " +
           "ORDER BY a.actionTimestamp DESC")
    List<AuditLog> findByEntity(@Param("entityName") String entityName,
                                @Param("entityId") Long entityId);

    // Find by entity with pagination
    @Query("SELECT a FROM AuditLog a WHERE a.entityName = :entityName AND a.entityId = :entityId")
    Page<AuditLog> findByEntity(@Param("entityName") String entityName,
                               @Param("entityId") Long entityId,
                               Pageable pageable);

    // Find by user
    Page<AuditLog> findByUserId(Long userId, Pageable pageable);

    // Find by performed by (username)
    Page<AuditLog> findByPerformedBy(String performedBy, Pageable pageable);

    // Find by action
    Page<AuditLog> findByAction(Action action, Pageable pageable);

    // Find by date range
    @Query("SELECT a FROM AuditLog a WHERE a.actionTimestamp BETWEEN :startDate AND :endDate " +
           "ORDER BY a.actionTimestamp DESC")
    List<AuditLog> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate);

    // Find by tenant
    Page<AuditLog> findByTenantId(String tenantId, Pageable pageable);

    // Find recent logs
    @Query("SELECT a FROM AuditLog a ORDER BY a.actionTimestamp DESC")
    List<AuditLog> findRecentLogs(Pageable pageable);

    // Count by action
    @Query("SELECT a.action, COUNT(a) FROM AuditLog a GROUP BY a.action")
    List<Object[]> countByAction();

    // Count by user
    @Query("SELECT a.performedBy, COUNT(a) FROM AuditLog a GROUP BY a.performedBy")
    List<Object[]> countByUser();

    // Count by entity
    @Query("SELECT a.entityName, COUNT(a) FROM AuditLog a GROUP BY a.entityName")
    List<Object[]> countByEntityName();

    // Advanced search
    @Query("SELECT a FROM AuditLog a WHERE " +
           "(:entityName IS NULL OR a.entityName = :entityName) AND " +
           "(:entityId IS NULL OR a.entityId = :entityId) AND " +
           "(:action IS NULL OR a.action = :action) AND " +
           "(:performedBy IS NULL OR LOWER(a.performedBy) LIKE LOWER(CONCAT('%', :performedBy, '%'))) AND " +
           "(:userId IS NULL OR a.userId = :userId) AND " +
           "(:startDate IS NULL OR a.actionTimestamp >= :startDate) AND " +
           "(:endDate IS NULL OR a.actionTimestamp <= :endDate) AND " +
           "(:tenantId IS NULL OR a.tenantId = :tenantId)")
    Page<AuditLog> advancedSearch(@Param("entityName") String entityName,
                                 @Param("entityId") Long entityId,
                                 @Param("action") Action action,
                                 @Param("performedBy") String performedBy,
                                 @Param("userId") Long userId,
                                 @Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate,
                                 @Param("tenantId") String tenantId,
                                 Pageable pageable);
}
