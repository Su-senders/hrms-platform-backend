package com.hrms.dto.reports;

import com.hrms.entity.TrainingEnrollment;
import lombok.*;
import java.time.LocalDate;

/**
 * DTO pour les participants d'une session de formation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantDTO {
    private Long personnelId;
    private String matricule;
    private String firstName;
    private String lastName;
    private String position;
    private String structure;
    private LocalDate enrollmentDate;
    private TrainingEnrollment.EnrollmentStatus status;
}
