package com.sms.plan.service;

import com.sms.plan.domain.*;
import com.sms.plan.dto.PlanDtos;
import com.sms.plan.dto.ProductDtos;
import com.sms.plan.exception.ConflictException;
import com.sms.plan.exception.InvalidPlanStateException;
import com.sms.plan.exception.ResourceNotFoundException;
import com.sms.plan.repository.FeatureRepository;
import com.sms.plan.repository.PlanRepository;
import com.sms.plan.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class CatalogService {

    private final ProductRepository productRepository;
    private final PlanRepository planRepository;
    private final FeatureRepository featureRepository;

    public CatalogService(ProductRepository productRepository,
                           PlanRepository planRepository,
                           FeatureRepository featureRepository) {
        this.productRepository = productRepository;
        this.planRepository = planRepository;
        this.featureRepository = featureRepository;
    }

    // ---------------------------------------------------------------- Products

    public Product createProduct(String organizationId, String productCode, String name, String description) {
        if (productRepository.existsByOrganizationIdAndProductCode(organizationId, productCode)) {
            throw new ConflictException("Product already exists in this organization: " + productCode);
        }
        Product product = new Product(organizationId, productCode, name, description);
        return productRepository.save(product);
    }
    public void  updateProduct(Long id, String orgId, ProductDtos.CreateProductRequest request) {
        Product product = findProductOrThrow(id);
        product.setOrganizationId(orgId);
        product.setId(id);
        if (StringUtils.hasText(request.name())) {
            product.setName(request.name());
        }
        if (StringUtils.hasText(request.description())) {
            product.setDescription(request.description());
        }
        product.setActive(request.active());

        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Product getProduct(String organizationId, String productCode) {
        return productRepository.findByOrganizationIdAndProductCode(organizationId, productCode)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productCode));
    }

    @Transactional(readOnly = true)
    public List<Product> listProducts(String organizationId) {
        return productRepository.findByOrganizationId(organizationId);
    }

    // ---------------------------------------------------------------- Features

    public Feature createFeature(String organizationId, String code, String name, String description, FeatureType type) {
        if (featureRepository.existsByOrganizationIdAndCode(organizationId, code)) {
            throw new ConflictException("Feature already exists in this organization: " + code);
        }
        return featureRepository.save(new Feature(organizationId, code, name, description, type));
    }

    @Transactional(readOnly = true)
    public List<Feature> listFeatures(String organizationId) {
        return featureRepository.findByOrganizationId(organizationId);
    }

    // ---------------------------------------------------------------- Plans

    /**
     * Creates a new DRAFT plan version. If planCode already has prior versions
     * in this organization, the new version number is the latest + 1; otherwise
     * it starts at 1.
     */
    public Plan createDraftPlan(String organizationId, String productCode, String planCode, String name, String description,
                                 List<PricePoint> pricePoints, List<PlanEntitlement> entitlements) {
        Product product = getProduct(organizationId, productCode);

        int nextVersion = planRepository.findByOrganizationIdAndPlanCodeOrderByVersionDesc(organizationId, planCode).stream()
                .findFirst()
                .map(p -> p.getVersion() + 1)
                .orElse(1);

        Plan plan = new Plan(organizationId, planCode, nextVersion, name, description);
        product.addPlan(plan);

        for (PricePoint pp : pricePoints) {
            plan.addPricePoint(pp);
        }
        for (PlanEntitlement ent : entitlements) {
            plan.addEntitlement(ent);
        }

        return planRepository.save(plan);
    }

    @Transactional(readOnly = true)
    public Plan getPlanVersion(String organizationId, String planCode, int version) {
        return planRepository.findByOrganizationIdAndPlanCodeAndVersion(organizationId, planCode, version)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Plan version not found: " + planCode + " v" + version));
    }

    @Transactional(readOnly = true)
    public List<Plan> getPlanVersions(String organizationId, String planCode) {
        List<Plan> versions = planRepository.findByOrganizationIdAndPlanCodeOrderByVersionDesc(organizationId, planCode);
        if (versions.isEmpty()) {
            throw new ResourceNotFoundException("No such plan code: " + planCode);
        }
        return versions;
    }

    @Transactional(readOnly = true)
    public List<Plan> getPublicCatalog(String organizationId) {
        return planRepository.findByOrganizationIdAndStatus(organizationId, PlanStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<Plan> getPublicCatalogForProduct(String organizationId, String productCode) {
        return planRepository.findByOrganizationIdAndProduct_ProductCodeAndStatus(organizationId, productCode, PlanStatus.ACTIVE);
    }

    /**
     * Publishes a DRAFT plan version, making it ACTIVE and sellable.
     * If another version of the same planCode is currently ACTIVE, it is
     * automatically transitioned to DEPRECATED (grandfathered) so existing
     * subscribers keep their original terms while new sign-ups get the new version.
     */
    public Plan publishPlan(String organizationId, String planCode, int version) {
        Plan plan = getPlanVersion(organizationId, planCode, version);
        validateTransition(plan.getStatus(), PlanStatus.ACTIVE);

        if (plan.getPricePoints().isEmpty()) {
            throw new InvalidPlanStateException(
                    "Cannot publish plan " + planCode + " v" + version + " with no price points");
        }

        planRepository.findByOrganizationIdAndPlanCodeAndStatus(organizationId, planCode, PlanStatus.ACTIVE)
                .ifPresent(currentlyActive -> {
                    currentlyActive.setStatus(PlanStatus.DEPRECATED);
                    currentlyActive.setEffectiveTo(Instant.now());
                });

        plan.setStatus(PlanStatus.ACTIVE);
        plan.setEffectiveFrom(Instant.now());
        return plan;
    }

    /** Stops new sign-ups for this version; existing subscribers are unaffected by this service. */
    public Plan deprecatePlan(String organizationId, String planCode, int version) {
        Plan plan = getPlanVersion(organizationId, planCode, version);
        validateTransition(plan.getStatus(), PlanStatus.DEPRECATED);
        plan.setStatus(PlanStatus.DEPRECATED);
        plan.setEffectiveTo(Instant.now());
        return plan;
    }

    /** Fully sunsets a plan version. Should only be used once no subscribers remain on it. */
    public Plan retirePlan(String organizationId, String planCode, int version) {
        Plan plan = getPlanVersion(organizationId, planCode, version);
        validateTransition(plan.getStatus(), PlanStatus.RETIRED);
        plan.setStatus(PlanStatus.RETIRED);
        return plan;
    }

    /**
     * Enforces the allowed lifecycle transitions:
     * DRAFT -> ACTIVE -> DEPRECATED -> RETIRED (strictly forward, no skipping, no going back).
     */
    private void validateTransition(PlanStatus current, PlanStatus target) {
        boolean valid = switch (current) {
            case DRAFT -> target == PlanStatus.ACTIVE;
            case ACTIVE -> target == PlanStatus.DEPRECATED;
            case DEPRECATED -> target == PlanStatus.RETIRED;
            case RETIRED -> false;
        };
        if (!valid) {
            throw new InvalidPlanStateException(
                    "Illegal plan status transition: " + current + " -> " + target);
        }
    }

    public Feature getFeature(String organizationId, String code) {
        return featureRepository.findByOrganizationIdAndCode(organizationId, code)
                .orElseThrow(() -> new ResourceNotFoundException("Feature not found: " + code));
    }


    public List<PlanDtos.PlanResponse> getPlans(String organizationId) {
        List<Plan> plans = planRepository.findPlanByOrganizationId(organizationId);

        /*if (plans.isEmpty()) {
            throw new PlanNotFoundException(organizationId, productCode);
        }
*/
        return plans.stream()
                .map(this::toResponse)
                .toList();
    }

    private PlanDtos.PlanResponse toResponse(Plan plan) {
        return new PlanDtos.PlanResponse(
                plan.getOrganizationId(),
                plan.getProduct().getProductCode(),
                plan.getPlanCode(),
                plan.getVersion(),
                plan.getName(),
                plan.getDescription(),
                plan.getStatus()

        );
    }

    private Product findProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }
}
