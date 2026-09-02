package com.sms.plan.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger / OpenAPI setup. With springdoc-openapi on the classpath, the raw
 * spec is auto-generated from the controllers and DTOs and served at
 * /v3/api-docs, with an interactive UI at /swagger-ui.html. This class only
 * adds descriptive metadata on top of that.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI catalogServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Plan & Catalog Service API")
                        .description("""
                                Owns the product / plan / pricing / entitlement model for a subscription
                                management system: what can be sold, at what price, and with what feature
                                limits. Does not handle payments, invoicing, or active subscriptions —
                                those belong to downstream billing/subscription services that treat this
                                service as their source of truth.

                                Plans are versioned (planCode + version) with a DRAFT -> ACTIVE ->
                                DEPRECATED -> RETIRED lifecycle. Publishing a new version automatically
                                deprecates the previously active one so existing subscribers keep the
                                terms they signed up under.
                                """)
                        .version("v1")
                        .contact(new Contact().name("Catalog Team").email("catalog-team@example.com"))
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")))
                .tags(List.of(
                        new Tag().name("Products").description("Top-level sellable offerings"),
                        new Tag().name("Features").description("Reusable entitlement definitions"),
                        new Tag().name("Plans").description("Versioned pricing/feature bundles and their lifecycle"),
                        new Tag().name("Catalog").description("Public, read-only view of active plans")
                ));
    }
}
