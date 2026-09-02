package com.sms.plan.config;

import com.sms.plan.domain.*;
import com.sms.plan.service.CatalogService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

/**
 * Populates the in-memory H2 database with a realistic sample catalog for two
 * separate organizations (tenants) on startup, so the API is immediately
 * explorable and tenant isolation is easy to verify. Not intended for
 * production use.
 */
//@Configuration
public class DataSeeder {

    @org.springframework.context.annotation.Bean
    public CommandLineRunner seed(CatalogService catalogService) {
        return args -> {

            seedAcme(catalogService);
            seedGlobex(catalogService);

            System.out.println("Seeded catalogs for 2 organizations: org_acme (ACME_CLOUD: FREE, PRO v1+v2 draft, "
                    + "ENTERPRISE draft) and org_globex (GLOBEX_SUITE: STANDARD, active). "
                    + "Send requests with header X-Organization-Id: org_acme (or org_globex) to see tenant-scoped data.");
        };
    }

    private void seedAcme(CatalogService catalogService) {
        String org = "f1b419af-ea20-4f38-b3fe-5d38bfacdfde";

        catalogService.createProduct(org, "ACME_CLOUD", "Acme Cloud", "Acme's flagship SaaS product");

        Feature apiAccess = catalogService.createFeature(
                org, "api_access", "API Access", "Programmatic access to the REST API", FeatureType.BOOLEAN);
        Feature maxSeats = catalogService.createFeature(
                org, "max_seats", "Max Seats", "Number of user seats included", FeatureType.NUMERIC);
        Feature maxProjects = catalogService.createFeature(
                org, "max_projects", "Max Projects", "Number of projects that can be created", FeatureType.NUMERIC);
        Feature supportLevel = catalogService.createFeature(
                org, "support_level", "Support Level", "Tier of customer support", FeatureType.TIERED);

        // ---- FREE plan (v1), published immediately ----
        Plan free = catalogService.createDraftPlan(
                org, "ACME_CLOUD", "FREE", "Free", "Get started at no cost",
                List.of(new PricePoint(CurrencyCode.USD, BillingCycle.MONTHLY, BigDecimal.ZERO, 0)),
                List.of(
                        new PlanEntitlement(apiAccess, "false"),
                        new PlanEntitlement(maxSeats, "1"),
                        new PlanEntitlement(maxProjects, "2"),
                        new PlanEntitlement(supportLevel, "community")
                ));
        catalogService.publishPlan(org, free.getPlanCode(), free.getVersion());

        // ---- PRO plan (v1), published ----
        Plan proV1 = catalogService.createDraftPlan(
                org, "ACME_CLOUD", "PRO", "Pro", "For growing teams",
                List.of(
                        new PricePoint(CurrencyCode.USD, BillingCycle.MONTHLY, new BigDecimal("29.00"), 14),
                        new PricePoint(CurrencyCode.USD, BillingCycle.ANNUAL, new BigDecimal("290.00"), 14)
                ),
                List.of(
                        new PlanEntitlement(apiAccess, "true"),
                        new PlanEntitlement(maxSeats, "10"),
                        new PlanEntitlement(maxProjects, "25"),
                        new PlanEntitlement(supportLevel, "priority")
                ));
        catalogService.publishPlan(org, proV1.getPlanCode(), proV1.getVersion());

        // ---- PRO plan re-priced (v2) — demonstrates versioning/grandfathering ----
        Plan proV2 = catalogService.createDraftPlan(
                org, "ACME_CLOUD", "PRO", "Pro", "For growing teams (2026 pricing)",
                List.of(
                        new PricePoint(CurrencyCode.USD, BillingCycle.MONTHLY, new BigDecimal("35.00"), 14),
                        new PricePoint(CurrencyCode.USD, BillingCycle.ANNUAL, new BigDecimal("350.00"), 14)
                ),
                List.of(
                        new PlanEntitlement(apiAccess, "true"),
                        new PlanEntitlement(maxSeats, "10"),
                        new PlanEntitlement(maxProjects, "40"),
                        new PlanEntitlement(supportLevel, "priority")
                ));
        // proV2 is left in DRAFT deliberately, to demonstrate the publish endpoint.
        // Publishing it will auto-deprecate proV1 while existing PRO v1 subscribers are unaffected.
        System.out.println("org_acme: draft plan ready to publish -> PRO v" + proV2.getVersion());

        // ---- ENTERPRISE plan (v1), left as DRAFT (not yet ready to sell) ----
        catalogService.createDraftPlan(
                org, "ACME_CLOUD", "ENTERPRISE", "Enterprise", "Custom limits and SLAs",
                List.of(new PricePoint(CurrencyCode.USD, BillingCycle.ANNUAL, new BigDecimal("12000.00"), 30)),
                List.of(
                        new PlanEntitlement(apiAccess, "true"),
                        new PlanEntitlement(maxSeats, "-1"),
                        new PlanEntitlement(maxProjects, "-1"),
                        new PlanEntitlement(supportLevel, "dedicated")
                ));
    }

    private void seedGlobex(CatalogService catalogService) {
        String org = "org_globex";

        // Note: org_globex can freely reuse the same productCode/planCode/featureCode
        // values as org_acme (e.g. it could also define "PRO") because uniqueness on
        // those business keys is scoped per organization, not global.
        catalogService.createProduct(org, "GLOBEX_SUITE", "Globex Suite", "Globex's productivity suite");

        Feature exportFeature = catalogService.createFeature(
                org, "data_export", "Data Export", "Ability to export data to CSV/PDF", FeatureType.BOOLEAN);

        Plan standard = catalogService.createDraftPlan(
                org, "GLOBEX_SUITE", "STANDARD", "Standard", "The default Globex Suite tier",
                List.of(new PricePoint(CurrencyCode.EUR, BillingCycle.ANNUAL, new BigDecimal("240.00"), 0)),
                List.of(new PlanEntitlement(exportFeature, "true")));
        catalogService.publishPlan(org, standard.getPlanCode(), standard.getVersion());
    }
}
