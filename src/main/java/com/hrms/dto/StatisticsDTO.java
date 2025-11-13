package com.hrms.dto;

import lombok.*;
import java.util.Map;

/**
 * DTO for various statistics and reports
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsDTO {

    // Personnel statistics
    private Long totalPersonnel;
    private Long activePersonnel;
    private Long retiredPersonnel;
    private Long retirableThisYear;
    private Long retirableNextYear;

    // By situation
    private Map<String, Long> personnelBySituation;

    // By grade
    private Map<String, Long> personnelByGrade;

    // By corps
    private Map<String, Long> personnelByCorps;

    // By structure
    private Map<String, Long> personnelByStructure;

    // Position statistics
    private Long totalPositions;
    private Long occupiedPositions;
    private Long vacantPositions;

    // By rank
    private Map<String, Long> positionsByRank;

    // By structure
    private Map<String, Long> positionsByStructure;
}
