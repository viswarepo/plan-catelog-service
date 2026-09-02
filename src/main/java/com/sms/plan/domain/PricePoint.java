package com.sms.plan.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * A single price for a Plan in one currency, at one billing cadence.
 * A plan typically has several PricePoints (e.g. USD/MONTHLY, USD/ANNUAL, EUR/MONTHLY...).
 */
@Entity
@Table(
        name = "price_points",
        uniqueConstraints = @UniqueConstraint(columnNames = {"plan_id", "currency", "billingCycle"})
)
public class PricePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyCode currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillingCycle billingCycle;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private int trialDays = 0;

    protected PricePoint() {
        // JPA
    }

    public PricePoint(CurrencyCode currency, BillingCycle billingCycle, BigDecimal amount, int trialDays) {
        this.currency = currency;
        this.billingCycle = billingCycle;
        this.amount = amount;
        this.trialDays = trialDays;
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

    public CurrencyCode getCurrency() {
        return currency;
    }

    public BillingCycle getBillingCycle() {
        return billingCycle;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public int getTrialDays() {
        return trialDays;
    }
}
