package com.sms.plan.domain;

import jakarta.persistence.*;

/**
 * A Feature is a reusable capability or limit that plans can grant,
 * e.g. "api_access" (BOOLEAN), "max_seats" (NUMERIC), "support_level" (TIERED).
 * Features are defined once per organization and referenced by many plans
 * within that organization via PlanEntitlement.
 */
@Entity
@Table(
        name = "features",
        uniqueConstraints = @UniqueConstraint(columnNames = {"organizationId", "code"})
)
public class Feature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String organizationId;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeatureType type;

    protected Feature() {
        // JPA
    }

    public Feature(String organizationId, String code, String name, String description, FeatureType type) {
        this.organizationId = organizationId;
        this.code = code;
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public FeatureType getType() {
        return type;
    }
}
