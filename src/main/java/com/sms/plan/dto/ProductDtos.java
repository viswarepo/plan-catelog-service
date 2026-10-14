package com.sms.plan.dto;

import jakarta.validation.constraints.NotBlank;

public final class ProductDtos {

    private ProductDtos() {
    }

    public record CreateProductRequest(
            @NotBlank String organizationId,
            @NotBlank String productCode,
            @NotBlank String name,
            @NotBlank String holder,
            String description,
            boolean active
    ) {
    }

    public record UpdateProductRequest(
            @NotBlank String id,
            String name,
            String holder,
            String description,
            boolean active
    ) {
    }

    public record ProductResponse(
            String productId,
            String organizationId,
            String productCode,
            String holder,
            String name,
            String description,
            boolean active
    ) {
    }
}
