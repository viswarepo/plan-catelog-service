package com.sms.plan.controller;

import com.sms.plan.service.CatalogService;
import com.sms.plan.dto.PlanDtos.PlanResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Read-only, public-facing view of one organization's catalog: only ACTIVE
 * plan versions, across all of that organization's products. This is the
 * endpoint a storefront/checkout UI would call.
 */
@RestController
@Tag(name = "Catalog", description = "Public, read-only view of active plans")
public class CatalogController {

    private final CatalogService catalogService;
    private final CatalogMapper mapper;

    public CatalogController(CatalogService catalogService, CatalogMapper mapper) {
        this.catalogService = catalogService;
        this.mapper = mapper;
    }

    @Operation(summary = "Get the public catalog", description = "Returns every ACTIVE plan version for this organization across all of its products — the sellable price list.")
    @GetMapping("/api/v1/catalog")
    public List<PlanResponse> getCatalog(
            @RequestHeader("X-Organization-Id") @NotBlank String organizationId) {
        return catalogService.getPublicCatalog(organizationId).stream().map(mapper::toResponse).toList();
    }
}
