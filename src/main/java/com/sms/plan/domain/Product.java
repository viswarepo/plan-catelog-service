package com.sms.plan.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * A Product is the top-level sellable offering (e.g. "Acme Cloud").
 * Each Product owns one or more Plans (e.g. "Free", "Pro", "Enterprise"),
 * and each Plan may have multiple versions over time.
 *
 * organizationId identifies the tenant that owns this product. Catalogs are
 * fully tenant-isolated: productCode is unique within an organization, not
 * globally, so two organizations can both define a "PRO" plan under
 * different products without colliding.
 */
@Entity
@Table(
        name = "products",
        uniqueConstraints = @UniqueConstraint(columnNames = {"organizationId", "productCode"})
)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    @Column(nullable = false)
    private String organizationId;

    @Column(nullable = false)
    private String productCode;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Plan> plans = new ArrayList<>();

    protected Product() {
        // JPA
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public Product(String organizationId, String productCode, String name, String description) {
        this.organizationId = organizationId;
        this.productCode = productCode;
        this.name = name;
        this.description = description;
    }

    public Product(Long id, boolean active, String name, String description) {
        this.name = name;
        this.description = description;
        this.active = active;
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<Plan> getPlans() {
        return plans;
    }

    public void addPlan(Plan plan) {
        plans.add(plan);
        plan.setProduct(this);
    }
}
