package com.sms.plan.controller;

import com.sms.plan.domain.Feature;
import com.sms.plan.service.CatalogService;
import com.sms.plan.dto.FeatureDtos.CreateFeatureRequest;
import com.sms.plan.dto.FeatureDtos.FeatureResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/features")
@Tag(name = "Features", description = "Reusable entitlement definitions that plans can grant")
public class FeatureController {

    private final CatalogService catalogService;
    private final CatalogMapper mapper;

    public FeatureController(CatalogService catalogService, CatalogMapper mapper) {
        this.catalogService = catalogService;
        this.mapper = mapper;
    }

    @Operation(summary = "Define a feature", description = "Creates a reusable BOOLEAN, NUMERIC, or TIERED capability that plans can grant via entitlements. code must be unique within the organization.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FeatureResponse createFeature(
            @RequestHeader("X-Organization-Id") @NotBlank String organizationId,
            @Valid @RequestBody CreateFeatureRequest request) {
        Feature feature = catalogService.createFeature(
                organizationId, request.code(), request.name(), request.description(), request.type());
        return mapper.toResponse(feature);
    }

    @Operation(summary = "List all features in the organization")
    @GetMapping
    public List<FeatureResponse> listFeatures(
            @RequestHeader("X-Organization-Id") @NotBlank String organizationId) {
        return catalogService.listFeatures(organizationId).stream().map(mapper::toResponse).toList();
    }
}
