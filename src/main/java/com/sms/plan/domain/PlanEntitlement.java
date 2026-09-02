package com.sms.plan.domain;

import jakarta.persistence.*;

/**
 * Grants a Feature to a Plan with a concrete value, e.g.
 * Plan "PRO" v2 -> Feature "max_seats" -> value "25"
 * Plan "PRO" v2 -> Feature "api_access" -> value "true"
 */
@Entity
@Table(
        name = "plan_entitlements",
        uniqueConstraints = @UniqueConstraint(columnNames = {"plan_id", "feature_id"})
)
public class PlanEntitlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @ManyToOne(optional = false)
    @JoinColumn(name = "feature_id", nullable = false)
    private Feature feature;

    /** Raw value, interpreted according to feature.type ("true"/"false", a number, or a tier name). Use -1 for unlimited numeric features. */
    @Column(nullable = false)
    private String value;

    protected PlanEntitlement() {
        // JPA
    }

    public PlanEntitlement(Feature feature, String value) {
        this.feature = feature;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public Feature getFeature() {
        return feature;
    }

    public String getValue() {
        return value;
    }
}
