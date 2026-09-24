package com.sms.plan.controller;

import com.sms.plan.domain.Feature;
import com.sms.plan.domain.Plan;
import com.sms.plan.domain.PlanEntitlement;
import com.sms.plan.domain.PricePoint;
import com.sms.plan.service.CatalogService;
import com.sms.plan.dto.PlanDtos.CreatePlanRequest;
import com.sms.plan.dto.PlanDtos.EntitlementRequest;
import com.sms.plan.dto.PlanDtos.PlanResponse;
import com.sms.plan.dto.PlanDtos.PricePointRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/plans")
@Tag(name = "Plans", description = "Versioned pricing/feature bundles and their DRAFT -> ACTIVE -> DEPRECATED -> RETIRED lifecycle")
public class PlanController {

    private final CatalogService catalogService;
    private final CatalogMapper mapper;

    public PlanController(CatalogService catalogService, CatalogMapper mapper) {
        this.catalogService = catalogService;
        this.mapper = mapper;
    }

    @Operation(
            summary = "Create a draft plan version",
            description = "Creates version 1 of a new plan, or the next version of an existing planCode "
                    + "within this organization, always in DRAFT status. Must include at least one price point.")
    @PostMapping("/create/draft")
    @ResponseStatus(HttpStatus.CREATED)
    public PlanResponse createDraftPlan(
            @RequestHeader("X-Organization-Id") @NotBlank String organizationId,
            //@PathVariable String productCode,
            @Valid @RequestBody CreatePlanRequest request) {
        List<PricePoint> pricePoints = request.pricePoints().stream()
                .map(this::toPricePoint)
                .toList();

        List<PlanEntitlement> entitlements = request.entitlements() == null
                ? List.of()
                : request.entitlements().stream().map(r -> toEntitlement(organizationId, r)).toList();

        Plan plan = catalogService.createDraftPlan(
                organizationId, request.productCode(), request.planCode(), request.name(), request.description(),
                pricePoints, entitlements);

        return mapper.toResponse(plan);
    }

    @Operation(summary = "List every version of a plan", description = "Returns all versions of planCode within this organization regardless of status, newest first.")
    @GetMapping("/{planCode}/versions")
    public List<PlanResponse> getPlanVersions(
            @RequestHeader("X-Organization-Id") @NotBlank String organizationId,
            @Parameter(example = "PRO") @PathVariable String planCode) {
        return catalogService.getPlanVersions(organizationId, planCode).stream().map(mapper::toResponse).toList();
    }

    @Operation(summary = "Get one plan version")
    @GetMapping("/{planCode}/versions/{version}")
    public PlanResponse getPlanVersion(
            @RequestHeader("X-Organization-Id") @NotBlank String organizationId,
            @Parameter(example = "PRO") @PathVariable String planCode,
            @Parameter(example = "1") @PathVariable int version) {
        return mapper.toResponse(catalogService.getPlanVersion(organizationId, planCode, version));
    }

    @Operation(
            summary = "Publish a draft plan version",
            description = "DRAFT -> ACTIVE. Requires at least one price point. If another version of the "
                    + "same planCode is currently ACTIVE, it is automatically moved to DEPRECATED so "
                    + "existing subscribers keep their original terms.")
    @PostMapping("/{planCode}/versions/{version}/publish")
    public PlanResponse publish(
            @RequestHeader("X-Organization-Id") @NotBlank String organizationId,
            @Parameter(example = "PRO") @PathVariable String planCode,
            @Parameter(example = "1") @PathVariable int version) {
        return mapper.toResponse(catalogService.publishPlan(organizationId, planCode, version));
    }

    @Operation(summary = "Deprecate an active plan version", description = "ACTIVE -> DEPRECATED. Stops new sign-ups; existing subscribers are unaffected by this service.")
    @PostMapping("/{planCode}/versions/{version}/deprecate")
    public PlanResponse deprecate(
            @RequestHeader("X-Organization-Id") @NotBlank String organizationId,
            @Parameter(example = "PRO") @PathVariable String planCode,
            @Parameter(example = "1") @PathVariable int version) {
        return mapper.toResponse(catalogService.deprecatePlan(organizationId, planCode, version));
    }

    @Operation(summary = "Retire a deprecated plan version", description = "DEPRECATED -> RETIRED. Use only once no subscribers remain on this version.")
    @PostMapping("/{planCode}/versions/{version}/retire")
    public PlanResponse retire(
            @RequestHeader("X-Organization-Id") @NotBlank String organizationId,
            @Parameter(example = "PRO") @PathVariable String planCode,
            @Parameter(example = "1") @PathVariable int version) {
        return mapper.toResponse(catalogService.retirePlan(organizationId, planCode, version));
    }

    /**
     * GET /api/v1/plans?productCode=ACME_CLOUD
     * Requires X-Organization-Id header (set by the gateway based on the
     * authenticated caller's org).
     */
    @GetMapping("/all")
    public ResponseEntity<List<PlanResponse>> getPlans(
            @RequestHeader("X-Organization-Id") String organizationId){
        List<PlanResponse> plans = catalogService.getPlans(organizationId);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<PlanResponse> getPlans(
            @Parameter(example = "1") @PathVariable String id,
            @RequestHeader("X-Organization-Id") String organizationId){
        PlanResponse plans = catalogService.getPlansByPlanId(id, organizationId);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{productCode}")
    public List<PlanResponse> getPlansByProduct(
            @RequestHeader("X-Organization-Id") String organizationId,
            @Parameter(example = "PROD") @PathVariable String productCode){
        return catalogService.getPublicCatalogForProduct(organizationId, productCode).stream().map(mapper::toResponse).toList();
     }

    private PricePoint toPricePoint(PricePointRequest r) {
        return new PricePoint(r.currency(), r.billingCycle(), r.amount(), r.trialDays());
    }

    private PlanEntitlement toEntitlement(String organizationId, EntitlementRequest r) {
        Feature feature = catalogService.getFeature(organizationId, r.featureCode());
        return new PlanEntitlement(feature, r.value());
    }
}
