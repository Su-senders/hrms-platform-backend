package com.hrms.repository;

import com.hrms.entity.PersonnelDocument;
import com.hrms.entity.PersonnelDocument.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for PersonnelDocument entity
 */
@Repository
public interface PersonnelDocumentRepository extends JpaRepository<PersonnelDocument, Long> {

    // Find by personnel
    @Query("SELECT d FROM PersonnelDocument d WHERE d.personnel.id = :personnelId " +
           "AND d.deleted = false ORDER BY d.createdAt DESC")
    List<PersonnelDocument> findByPersonnelId(@Param("personnelId") Long personnelId);

    // Find by personnel and document type
    @Query("SELECT d FROM PersonnelDocument d WHERE d.personnel.id = :personnelId " +
           "AND d.documentType = :documentType AND d.deleted = false ORDER BY d.version DESC")
    List<PersonnelDocument> findByPersonnelIdAndDocumentType(@Param("personnelId") Long personnelId,
                                                             @Param("documentType") DocumentType documentType);

    // Find latest version of document
    @Query("SELECT d FROM PersonnelDocument d WHERE d.personnel.id = :personnelId " +
           "AND d.documentType = :documentType AND d.deleted = false " +
           "ORDER BY d.version DESC LIMIT 1")
    PersonnelDocument findLatestVersion(@Param("personnelId") Long personnelId,
                                       @Param("documentType") DocumentType documentType);

    // Find expired documents
    @Query("SELECT d FROM PersonnelDocument d WHERE d.expiryDate < :date " +
           "AND d.deleted = false ORDER BY d.expiryDate")
    List<PersonnelDocument> findExpiredDocuments(@Param("date") LocalDate date);

    // Find documents expiring soon
    @Query("SELECT d FROM PersonnelDocument d WHERE d.expiryDate BETWEEN :startDate AND :endDate " +
           "AND d.deleted = false ORDER BY d.expiryDate")
    List<PersonnelDocument> findDocumentsExpiringSoon(@Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

    // Find unverified documents
    @Query("SELECT d FROM PersonnelDocument d WHERE d.isVerified = false " +
           "AND d.deleted = false ORDER BY d.createdAt")
    List<PersonnelDocument> findUnverifiedDocuments();

    // Find mandatory documents missing for personnel
    @Query("SELECT dt FROM PersonnelDocument d RIGHT JOIN d.documentType dt " +
           "WHERE d.personnel.id = :personnelId AND d.isMandatory = true " +
           "AND d.id IS NULL")
    List<DocumentType> findMissingMandatoryDocuments(@Param("personnelId") Long personnelId);

    // Check if personnel has document type
    @Query("SELECT COUNT(d) > 0 FROM PersonnelDocument d WHERE d.personnel.id = :personnelId " +
           "AND d.documentType = :documentType AND d.deleted = false")
    boolean hasDocumentType(@Param("personnelId") Long personnelId,
                           @Param("documentType") DocumentType documentType);

    // Count documents by type
    @Query("SELECT d.documentType, COUNT(d) FROM PersonnelDocument d " +
           "WHERE d.deleted = false GROUP BY d.documentType")
    List<Object[]> countByDocumentType();

    // Find documents by personnel and verified status
    @Query("SELECT d FROM PersonnelDocument d WHERE d.personnel.id = :personnelId " +
           "AND d.isVerified = :isVerified AND d.deleted = false")
    List<PersonnelDocument> findByPersonnelIdAndVerified(@Param("personnelId") Long personnelId,
                                                         @Param("isVerified") Boolean isVerified);
}
