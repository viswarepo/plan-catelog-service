package com.sms.plan.dto;

import com.sms.plan.domain.FeatureType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class FeatureDtos {

    private FeatureDtos() {
    }

    public record CreateFeatureRequest(
            @NotBlank String code,
            @NotBlank String name,
            String description,
            @NotNull FeatureType type
            //@NotNull FeatureStatus status
    ) {
    }

    public record FeatureResponse(
            String featureId,
            String organizationId,
            String code,
            String name,
            String description,
            FeatureType type
            //FeatureStatus status
    ) {
    }
}
