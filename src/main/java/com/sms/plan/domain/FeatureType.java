package com.sms.plan.domain;

/**
 * Determines how a Feature's value should be interpreted when attached
 * to a plan as an entitlement.
 *
 * BOOLEAN -> feature is simply on/off (value is "true"/"false")
 * NUMERIC -> feature carries a limit, e.g. "seats" = "25", "-1" conventionally means unlimited
 * TIERED  -> feature carries a named tier, e.g. "support_level" = "priority"
 */
public enum FeatureType {
    BOOLEAN,
    NUMERIC,
    TIERED
}
