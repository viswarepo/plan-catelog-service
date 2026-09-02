package com.sms.plan.dto;

import jakarta.validation.constraints.NotBlank;

public final class ProductDtos {

    private ProductDtos() {
    }

    public record CreateProductRequest(
            @NotBlank String organizationId,
            @NotBlank String productCode,
            @NotBlank String name,
            String description,
            boolean active
    ) {
    }

    public record UpdateProductRequest(
            @NotBlank Long id,
            String name,
            String description,
            boolean active
    ) {
    }

    public record ProductResponse(
            Long id,
            String organizationId,
            String productCode,
            String name,
            String description,
            boolean active
    ) {
    }
}
