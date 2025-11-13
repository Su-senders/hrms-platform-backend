package com.hrms.service;

import com.hrms.dto.PersonnelLeaveDTO;
import com.hrms.dto.PersonnelLeaveCreateDTO;
import com.hrms.dto.PersonnelLeaveUpdateDTO;
import com.hrms.entity.Personnel;
import com.hrms.entity.PersonnelLeave;
import com.hrms.entity.PersonnelLeave.LeaveReason;
import com.hrms.entity.PersonnelLeave.LeaveStatus;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.mapper.PersonnelLeaveMapper;
import com.hrms.repository.PersonnelRepository;
import com.hrms.repository.PersonnelLeaveRepository;
import com.hrms.util.AuditUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PersonnelLeaveService {

    private final PersonnelLeaveRepository leaveRepository;
    private final PersonnelRepository personnelRepository;
    private final PersonnelLeaveMapper leaveMapper;
    private final AuditUtil auditUtil;

    public PersonnelLeaveDTO createLeave(PersonnelLeaveCreateDTO dto) {
        log.info("Creating leave for personnel ID: {}", dto.getPersonnelId());

        Personnel personnel = personnelRepository.findById(dto.getPersonnelId())
                .orElseThrow(() -> new ResourceNotFoundException("Personnel", "id", dto.getPersonnelId()));

        PersonnelLeave leave = leaveMapper.toEntity(dto);
        leave.setPersonnel(personnel);
        leave.setCreatedBy(auditUtil.getCurrentUser());
        leave.setCreatedDate(LocalDate.now());

        PersonnelLeave saved = leaveRepository.save(leave);
        log.info("Personnel leave created with ID: {}", saved.getId());

        return leaveMapper.toDTO(saved);
    }

    public PersonnelLeaveDTO updateLeave(Long id, PersonnelLeaveUpdateDTO dto) {
        log.info("Updating personnel leave with ID: {}", id);

        PersonnelLeave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PersonnelLeave", "id", id));

        leaveMapper.updateEntity(dto, leave);
        leave.setUpdatedBy(auditUtil.getCurrentUser());
        leave.setUpdatedDate(LocalDate.now());

        PersonnelLeave updated = leaveRepository.save(leave);
        log.info("Personnel leave updated: {}", id);

        return leaveMapper.toDTO(updated);
    }

    @Transactional(readOnly = true)
    public PersonnelLeaveDTO getLeaveById(Long id) {
        PersonnelLeave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PersonnelLeave", "id", id));
        return leaveMapper.toDTO(leave);
    }

    @Transactional(readOnly = true)
    public List<PersonnelLeaveDTO> getLeavesByPersonnel(Long personnelId) {
        log.info("Fetching leaves for personnel ID: {}", personnelId);
        return leaveRepository.findByPersonnelId(personnelId).stream()
                .map(leaveMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<PersonnelLeaveDTO> getLeavesByPersonnel(Long personnelId, Pageable pageable) {
        return leaveRepository.findByPersonnelId(personnelId, pageable)
                .map(leaveMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<PersonnelLeaveDTO> getLeavesByReason(Long personnelId, String reason) {
        LeaveReason leaveReason = LeaveReason.valueOf(reason.toUpperCase());
        return leaveRepository.findByPersonnelIdAndReason(personnelId, leaveReason).stream()
                .map(leaveMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PersonnelLeaveDTO> getInProgressLeaves() {
        return leaveRepository.findInProgressLeaves().stream()
                .map(leaveMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteLeave(Long id) {
        log.info("Soft deleting personnel leave with ID: {}", id);

        PersonnelLeave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PersonnelLeave", "id", id));

        leave.setDeleted(true);
        leave.setDeletedAt(java.time.LocalDateTime.now());
        leave.setDeletedBy(auditUtil.getCurrentUser());

        leaveRepository.save(leave);
        log.info("Personnel leave soft deleted: {}", id);
    }
}

