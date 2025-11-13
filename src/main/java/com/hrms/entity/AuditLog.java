package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Audit Log entity
 * Tracks all operations performed in the system for traceability
 */
@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_entity", columnList = "entity_name"),
    @Index(name = "idx_audit_action", columnList = "action"),
    @Index(name = "idx_audit_user", columnList = "performed_by"),
    @Index(name = "idx_audit_timestamp", columnList = "action_timestamp")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_name", nullable = false)
    private String entityName; // Nom de l'entité (Personnel, Position, etc.)

    @Column(name = "entity_id", nullable = false)
    private Long entityId; // ID de l'entité concernée

    @Column(name = "action", nullable = false)
    @Enumerated(EnumType.STRING)
    private Action action;

    @Column(name = "performed_by", nullable = false)
    private String performedBy; // Nom de l'opérateur

    @Column(name = "user_id")
    private Long userId; // ID de l'utilisateur

    @Column(name = "action_timestamp", nullable = false)
    private LocalDateTime actionTimestamp;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "old_values", columnDefinition = "TEXT")
    private String oldValues; // Anciennes valeurs (JSON)

    @Column(name = "new_values", columnDefinition = "TEXT")
    private String newValues; // Nouvelles valeurs (JSON)

    @Column(name = "changes_summary", columnDefinition = "TEXT")
    private String changesSummary; // Résumé des changements

    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "additional_info", columnDefinition = "TEXT")
    private String additionalInfo;

    public enum Action {
        CREATE,     // Création
        UPDATE,     // Modification
        DELETE,     // Suppression
        VIEW,       // Consultation
        EXPORT,     // Export
        IMPORT,     // Import
        APPROVE,    // Approbation
        REJECT,     // Rejet
        ASSIGN,     // Affectation
        UNASSIGN,   // Désaffectation
        LOGIN,      // Connexion
        LOGOUT      // Déconnexion
    }

    /**
     * Create audit log entry
     */
    public static AuditLog createLog(String entityName, Long entityId, Action action,
                                     String performedBy, Long userId, String tenantId) {
        return AuditLog.builder()
                .entityName(entityName)
                .entityId(entityId)
                .action(action)
                .performedBy(performedBy)
                .userId(userId)
                .tenantId(tenantId)
                .actionTimestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Get action label in French
     */
    @Transient
    public String getActionLabel() {
        switch (action) {
            case CREATE: return "Création";
            case UPDATE: return "Modification";
            case DELETE: return "Suppression";
            case VIEW: return "Consultation";
            case EXPORT: return "Export";
            case IMPORT: return "Import";
            case APPROVE: return "Approbation";
            case REJECT: return "Rejet";
            case ASSIGN: return "Affectation";
            case UNASSIGN: return "Désaffectation";
            case LOGIN: return "Connexion";
            case LOGOUT: return "Déconnexion";
            default: return action.name();
        }
    }
}
