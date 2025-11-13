package com.hrms.service;

import com.hrms.dto.CartographyDTO;
import com.hrms.dto.CartographyFilterDTO;
import com.hrms.entity.AdministrativeStructure;
import com.hrms.entity.Personnel;
import com.hrms.entity.Position;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.repository.AdministrativeStructureRepository;
import com.hrms.repository.CorpsMetierRepository;
import com.hrms.repository.GradeRepository;
import com.hrms.repository.PersonnelRepository;
import com.hrms.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartographyService {

    private final AdministrativeStructureRepository structureRepository;
    private final PositionRepository positionRepository;
    private final PersonnelRepository personnelRepository;
    private final GradeRepository gradeRepository;
    private final CorpsMetierRepository corpsMetierRepository;

    /**
     * Obtenir la cartographie complète avec filtres
     */
    public CartographyDTO getCartography(CartographyFilterDTO filters) {
        log.info("Generating cartography with filters: {}", filters);

        // Construire les statistiques
        CartographyDTO.Statistics statistics = buildStatistics(filters);

        // Construire la liste des structures
        List<CartographyDTO.StructureMapping> structures = buildStructures(filters);

        return CartographyDTO.builder()
                .statistics(statistics)
                .structures(structures)
                .filters(filters)
                .build();
    }

    /**
     * Obtenir la cartographie d'une structure spécifique
     */
    public CartographyDTO getCartographyByStructure(Long structureId, CartographyFilterDTO filters) {
        log.info("Generating cartography for structure ID: {}", structureId);

        AdministrativeStructure structure = structureRepository.findById(structureId)
                .orElseThrow(() -> new RuntimeException("Structure not found: " + structureId));

        // Appliquer le filtre de structure
        if (filters == null) {
            filters = CartographyFilterDTO.builder().build();
        }
        filters.setStructureId(structureId);

        return getCartography(filters);
    }

    /**
     * Obtenir la cartographie hiérarchique (structure parent → enfants)
     */
    public CartographyDTO getHierarchicalCartography(Long rootStructureId, CartographyFilterDTO filters) {
        log.info("Generating hierarchical cartography from root structure ID: {}", rootStructureId);

        if (filters == null) {
            filters = CartographyFilterDTO.builder().build();
        }
        filters.setStructureId(rootStructureId);
        filters.setHierarchical(true);
        filters.setIncludeChildren(true);

        return getCartography(filters);
    }

    /**
     * Construire les statistiques
     */
    private CartographyDTO.Statistics buildStatistics(CartographyFilterDTO filters) {
        int totalStructures = 0;
        int totalPositions = 0;
        int totalOccupiedPositions = 0;
        int totalVacantPositions = 0;
        int totalPersonnel = 0;
        int totalPersonnelWithPosition = 0;
        int totalPersonnelWithoutPosition = 0;

        // Filtrer les structures
        List<AdministrativeStructure> structures = getFilteredStructures(filters);

        totalStructures = structures.size();

        for (AdministrativeStructure structure : structures) {
            // Compter les postes
            List<Position> positions = getFilteredPositions(structure.getId(), filters);
            totalPositions += positions.size();

            for (Position position : positions) {
                if (position.getStatus() == Position.PositionStatus.OCCUPE) {
                    totalOccupiedPositions++;
                } else if (position.getStatus() == Position.PositionStatus.VACANT) {
                    totalVacantPositions++;
                }
            }

            // Compter le personnel
            List<Personnel> personnel = getFilteredPersonnel(structure.getId(), filters);
            totalPersonnel += personnel.size();

            for (Personnel p : personnel) {
                if (p.getCurrentPosition() != null) {
                    totalPersonnelWithPosition++;
                } else {
                    totalPersonnelWithoutPosition++;
                }
            }
        }

        return CartographyDTO.Statistics.builder()
                .totalStructures(totalStructures)
                .totalPositions(totalPositions)
                .totalOccupiedPositions(totalOccupiedPositions)
                .totalVacantPositions(totalVacantPositions)
                .totalPersonnel(totalPersonnel)
                .totalPersonnelWithPosition(totalPersonnelWithPosition)
                .totalPersonnelWithoutPosition(totalPersonnelWithoutPosition)
                .build();
    }

    /**
     * Construire la liste des structures avec leurs postes et personnels
     */
    private List<CartographyDTO.StructureMapping> buildStructures(CartographyFilterDTO filters) {
        List<AdministrativeStructure> structures = getFilteredStructures(filters);

        return structures.stream()
                .map(structure -> buildStructureMapping(structure, filters))
                .collect(Collectors.toList());
    }

    /**
     * Construire le mapping d'une structure
     */
    private CartographyDTO.StructureMapping buildStructureMapping(
            AdministrativeStructure structure, CartographyFilterDTO filters) {

        // Informations sur la structure
        CartographyDTO.StructureInfo structureInfo = buildStructureInfo(structure, filters);

        // Postes de la structure
        List<CartographyDTO.PositionMapping> positions = buildPositions(structure.getId(), filters);

        // Structures enfants (si hiérarchique)
        List<CartographyDTO.StructureMapping> children = new ArrayList<>();
        if (Boolean.TRUE.equals(filters.getHierarchical()) && Boolean.TRUE.equals(filters.getIncludeChildren())) {
            List<AdministrativeStructure> childStructures = structureRepository.findByParentStructureId(structure.getId());
            children = childStructures.stream()
                    .map(child -> buildStructureMapping(child, filters))
                    .collect(Collectors.toList());
        }

        return CartographyDTO.StructureMapping.builder()
                .structure(structureInfo)
                .positions(positions)
                .children(children)
                .build();
    }

    /**
     * Construire les informations sur une structure
     */
    private CartographyDTO.StructureInfo buildStructureInfo(
            AdministrativeStructure structure, CartographyFilterDTO filters) {

        // Compter les postes
        List<Position> positions = getFilteredPositions(structure.getId(), filters);
        long occupiedCount = positions.stream()
                .filter(p -> p.getStatus() == Position.PositionStatus.OCCUPE)
                .count();
        long vacantCount = positions.stream()
                .filter(p -> p.getStatus() == Position.PositionStatus.VACANT)
                .count();

        // Compter le personnel
        List<Personnel> personnel = getFilteredPersonnel(structure.getId(), filters);

        return CartographyDTO.StructureInfo.builder()
                .id(structure.getId())
                .code(structure.getCode())
                .name(structure.getName())
                .type(structure.getType() != null ? structure.getType().name() : null)
                .parentStructureId(structure.getParentStructure() != null ? structure.getParentStructure().getId() : null)
                .parentStructureName(structure.getParentStructure() != null ? structure.getParentStructure().getName() : null)
                .totalPositions(positions.size())
                .occupiedPositions((int) occupiedCount)
                .vacantPositions((int) vacantCount)
                .totalPersonnel(personnel.size())
                .build();
    }

    /**
     * Construire la liste des postes avec leurs personnels
     */
    private List<CartographyDTO.PositionMapping> buildPositions(Long structureId, CartographyFilterDTO filters) {
        List<Position> positions = getFilteredPositions(structureId, filters);

        return positions.stream()
                .map(position -> {
                    CartographyDTO.PositionInfo positionInfo = buildPositionInfo(position);
                    CartographyDTO.PersonnelInfo personnelInfo = null;

                    if (position.getCurrentPersonnel() != null) {
                        Personnel personnel = position.getCurrentPersonnel();
                        // Appliquer les filtres de personnel si nécessaire
                        if (matchesPersonnelFilters(personnel, filters)) {
                            personnelInfo = buildPersonnelInfo(personnel);
                        }
                    }

                    return CartographyDTO.PositionMapping.builder()
                            .position(positionInfo)
                            .personnel(personnelInfo)
                            .build();
                })
                .filter(pm -> {
                    // Filtrer selon les options d'affichage
                    if (filters != null) {
                        if (Boolean.TRUE.equals(filters.getOnlyOccupied())) {
                            return pm.getPersonnel() != null;
                        }
                        if (Boolean.TRUE.equals(filters.getOnlyVacant())) {
                            return pm.getPersonnel() == null;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    /**
     * Construire les informations sur un poste
     */
    private CartographyDTO.PositionInfo buildPositionInfo(Position position) {
        return CartographyDTO.PositionInfo.builder()
                .id(position.getId())
                .code(position.getCode())
                .title(position.getTitle())
                .rank(position.getRank())
                .category(position.getCategory())
                .status(position.getStatus() != null ? position.getStatus().name() : null)
                .requiredGrade(position.getRequiredGrade())
                .requiredCorps(position.getRequiredCorps())
                .build();
    }

    /**
     * Construire les informations sur un personnel
     */
    private CartographyDTO.PersonnelInfo buildPersonnelInfo(Personnel personnel) {
        String seniorityInPost = formatPeriod(personnel.getSeniorityInPost());
        String seniorityInAdmin = formatPeriod(personnel.getSeniorityInAdministration());

        return CartographyDTO.PersonnelInfo.builder()
                .id(personnel.getId())
                .matricule(personnel.getMatricule())
                .fullName(personnel.getFullName())
                .grade(personnel.getGradeName())
                .corps(personnel.getCorpsMetier() != null ? personnel.getCorpsMetier().getName() : null)
                .situation(personnel.getSituation() != null ? personnel.getSituation().name() : null)
                .status(personnel.getStatus() != null ? personnel.getStatus().name() : null)
                .age(personnel.getAge())
                .seniorityInPost(seniorityInPost)
                .seniorityInAdministration(seniorityInAdmin)
                .build();
    }

    /**
     * Obtenir les structures filtrées
     */
    private List<AdministrativeStructure> getFilteredStructures(CartographyFilterDTO filters) {
        if (filters == null) {
            return structureRepository.findByActiveTrue();
        }

        if (filters.getStructureId() != null) {
            AdministrativeStructure structure = structureRepository.findById(filters.getStructureId())
                    .orElseThrow(() -> new ResourceNotFoundException("Structure", "id", filters.getStructureId()));
            
            List<AdministrativeStructure> result = new ArrayList<>();
            result.add(structure);
            
            // Inclure les enfants si demandé
            if (Boolean.TRUE.equals(filters.getIncludeChildren())) {
                result.addAll(structureRepository.findAllDescendants(filters.getStructureId()));
            }
            
            return result;
        }

        if (filters.getStructureType() != null) {
            AdministrativeStructure.StructureType type = AdministrativeStructure.StructureType.valueOf(
                    filters.getStructureType().toUpperCase());
            return structureRepository.findByTypeAndActiveTrue(type);
        }

        return structureRepository.findByActiveTrue();
    }

    /**
     * Obtenir les postes filtrés pour une structure
     */
    private List<Position> getFilteredPositions(Long structureId, CartographyFilterDTO filters) {
        Page<Position> positionPage = positionRepository.findByStructureId(
                structureId, PageRequest.of(0, Integer.MAX_VALUE));
        List<Position> positions = positionPage.getContent();

        if (filters == null) {
            return positions;
        }

        return positions.stream()
                .filter(position -> {
                    // Filtre par statut
                    if (filters.getPositionStatus() != null) {
                        Position.PositionStatus status = Position.PositionStatus.valueOf(
                                filters.getPositionStatus().toUpperCase());
                        if (position.getStatus() != status) {
                            return false;
                        }
                    }

                    // Filtre par rang
                    if (filters.getRank() != null && !filters.getRank().equals(position.getRank())) {
                        return false;
                    }

                    // Filtre par catégorie
                    if (filters.getCategory() != null && !filters.getCategory().equals(position.getCategory())) {
                        return false;
                    }

                    // Filtre par ID de poste
                    if (filters.getPositionId() != null && !filters.getPositionId().equals(position.getId())) {
                        return false;
                    }

                    return true;
                })
                .collect(Collectors.toList());
    }

    /**
     * Obtenir le personnel filtré pour une structure
     */
    private List<Personnel> getFilteredPersonnel(Long structureId, CartographyFilterDTO filters) {
        Page<Personnel> personnelPage = personnelRepository.findByStructureId(
                structureId, PageRequest.of(0, Integer.MAX_VALUE));
        List<Personnel> personnel = personnelPage.getContent();

        if (filters == null) {
            return personnel;
        }

        return personnel.stream()
                .filter(p -> matchesPersonnelFilters(p, filters))
                .collect(Collectors.toList());
    }

    /**
     * Vérifier si un personnel correspond aux filtres
     */
    private boolean matchesPersonnelFilters(Personnel personnel, CartographyFilterDTO filters) {
        if (filters == null) {
            return true;
        }

        // Filtre par ID de personnel
        if (filters.getPersonnelId() != null && !filters.getPersonnelId().equals(personnel.getId())) {
            return false;
        }

        // Filtre par grade
        if (filters.getGrade() != null) {
            if (personnel.getCurrentGrade() == null) {
                return false;
            }
            try {
                Long gradeId = Long.parseLong(filters.getGrade());
                if (!personnel.getCurrentGrade().getId().equals(gradeId)) {
                    return false;
                }
            } catch (NumberFormatException e) {
                // Si ce n'est pas un ID, comparer par nom
                if (!personnel.getGradeName().equalsIgnoreCase(filters.getGrade())) {
                    return false;
                }
            }
        }

        // Filtre par corps
        if (filters.getCorps() != null) {
            if (personnel.getCorpsMetier() == null) {
                return false;
            }
            try {
                Long corpsId = Long.parseLong(filters.getCorps());
                if (!personnel.getCorpsMetier().getId().equals(corpsId)) {
                    return false;
                }
            } catch (NumberFormatException e) {
                // Si ce n'est pas un ID, comparer par nom
                if (!personnel.getCorpsMetier().getName().equalsIgnoreCase(filters.getCorps())) {
                    return false;
                }
            }
        }

        // Filtre par situation
        if (filters.getSituation() != null) {
            Personnel.PersonnelSituation situation = Personnel.PersonnelSituation.valueOf(
                    filters.getSituation().toUpperCase());
            if (personnel.getSituation() != situation) {
                return false;
            }
        }

        // Filtre par statut
        if (filters.getStatus() != null) {
            Personnel.PersonnelStatus status = Personnel.PersonnelStatus.valueOf(
                    filters.getStatus().toUpperCase());
            if (personnel.getStatus() != status) {
                return false;
            }
        }

        return true;
    }

    /**
     * Formater une période
     */
    private String formatPeriod(Period period) {
        if (period == null) {
            return "N/A";
        }
        int years = period.getYears();
        int months = period.getMonths();
        if (years > 0 && months > 0) {
            return years + " an(s), " + months + " mois";
        } else if (years > 0) {
            return years + " an(s)";
        } else if (months > 0) {
            return months + " mois";
        }
        return "0";
    }
}

