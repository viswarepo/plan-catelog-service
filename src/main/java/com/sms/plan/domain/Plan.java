package com.sms.plan.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * A Plan represents one version of a sellable subscription tier
 * (e.g. planCode="PRO", version=3).
 *
 * Versioning model: planCode is the stable business identifier a subscriber's
 * entitlement points to. Each time the plan is re-priced or re-scoped, a new
 * Plan row is created with the same planCode and an incremented version.
 * Only one version per planCode may be ACTIVE at a time; publishing a new
 * version automatically deprecates the previous ACTIVE version so existing
 * subscribers keep the terms they signed up under (grandfathering).
 *
 * organizationId is denormalized from the owning Product so this table can be
 * queried and indexed by tenant directly (e.g. "all versions of planCode PRO
 * for this organization") without always joining through Product.
 */
@Entity
@Table(
        name = "plans",
        uniqueConstraints = @UniqueConstraint(columnNames = {"organizationId", "planCode", "version"})
)
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String organizationId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String planCode;

    @Column(nullable = false)
    private int version;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanStatus status = PlanStatus.DRAFT;

    private Instant effectiveFrom;

    private Instant effectiveTo;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PricePoint> pricePoints = new ArrayList<>();

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanEntitlement> entitlements = new ArrayList<>();

    protected Plan() {
        // JPA
    }

    public Plan(String organizationId, String planCode, int version, String name, String description) {
        this.organizationId = organizationId;
        this.planCode = planCode;
        this.version = version;
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getPlanCode() {
        return planCode;
    }

    public int getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PlanStatus getStatus() {
        return status;
    }

    public void setStatus(PlanStatus status) {
        this.status = status;
    }

    public Instant getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(Instant effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public Instant getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(Instant effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<PricePoint> getPricePoints() {
        return pricePoints;
    }

    public void addPricePoint(PricePoint pricePoint) {
        pricePoints.add(pricePoint);
        pricePoint.setPlan(this);
    }

    public List<PlanEntitlement> getEntitlements() {
        return entitlements;
    }

    public void addEntitlement(PlanEntitlement entitlement) {
        entitlements.add(entitlement);
        entitlement.setPlan(this);
    }
}
