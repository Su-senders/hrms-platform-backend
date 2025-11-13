package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Organization entity - represents a tenant organization (Ministry, Department, etc.)
 */
@Entity
@Table(name = "organizations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization extends BaseEntity {

    @Column(name = "tenant_id", unique = true, nullable = false)
    private String tenantId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "website")
    private String website;

    @Column(name = "registration_number")
    private String registrationNumber;

    @Column(name = "tax_id")
    private String taxId;

    @Column(name = "active")
    private boolean active = true;

    @Column(name = "max_employees")
    private Integer maxEmployees;

    @Column(name = "subscription_type")
    @Enumerated(EnumType.STRING)
    private SubscriptionType subscriptionType;

    @Column(name = "subscription_start_date")
    private java.time.LocalDate subscriptionStartDate;

    @Column(name = "subscription_end_date")
    private java.time.LocalDate subscriptionEndDate;

    public enum SubscriptionType {
        TRIAL,
        BASIC,
        PROFESSIONAL,
        ENTERPRISE,
        GOVERNMENT
    }
}
