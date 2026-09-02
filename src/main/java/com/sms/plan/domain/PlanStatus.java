package com.sms.plan.domain;

/**
 * Lifecycle states for a single plan version.
 *
 * DRAFT       -> being authored, freely editable, never visible in the public catalog
 * ACTIVE      -> published, sellable, visible in the public catalog
 * DEPRECATED  -> no longer sellable to new subscribers, but existing subscribers
 *                on this version continue to be honoured
 * RETIRED     -> fully sunset, kept only for historical/audit purposes
 */
public enum PlanStatus {
    DRAFT,
    ACTIVE,
    DEPRECATED,
    RETIRED
}
