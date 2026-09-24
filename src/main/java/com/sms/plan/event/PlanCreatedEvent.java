package com.sms.plan.event;

import com.sms.plan.domain.PlanStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Plan Creation Event
 */
public record PlanCreatedEvent(
        UUID eventId,
        String planId,
        String organizationId,
        String productId,
        String name,
        String currency,
        String billingCycle,
        BigDecimal unitAmount,
        Instant occurredAt
) {
    public static PlanCreatedEvent of(
            String planId,
            String organizationId,
            String productId,
            String name,
            String currency,
            String billingCycle,
            BigDecimal unitAmount) {
        return new PlanCreatedEvent(
                UUID.randomUUID(),
                planId,
                organizationId,
                productId,
                name,
                currency,
                billingCycle,
                unitAmount,
                Instant.now());
    }
}
