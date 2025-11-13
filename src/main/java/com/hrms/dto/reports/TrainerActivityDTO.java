package com.hrms.dto.reports;

import lombok.*;

/**
 * DTO pour les statistiques d'activité des formateurs
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerActivityDTO {
    private Long trainerId;
    private String trainerName;
    private String organization;
    private String specialization;
    private Integer totalSessions;
    private Integer totalParticipants;
    private Integer totalDays;
}
