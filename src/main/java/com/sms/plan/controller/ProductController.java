package com.sms.plan.controller;

import com.sms.plan.domain.Product;
import com.sms.plan.dto.ProductDtos;
import com.sms.plan.service.CatalogService;
import com.sms.plan.dto.ProductDtos.CreateProductRequest;
import com.sms.plan.dto.ProductDtos.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.websocket.server.PathParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "Top-level sellable offerings")
public class ProductController {

    private final CatalogService catalogService;
    private final CatalogMapper mapper;

    public ProductController(CatalogService catalogService, CatalogMapper mapper) {
        this.catalogService = catalogService;
        this.mapper = mapper;
    }

    @Operation(summary = "Create a product", description = "Registers a new top-level sellable offering. productCode must be unique within the organization.")
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(
            @Parameter(description = "Tenant identifier", required = true, example = "org_acme")
            @RequestHeader("X-Organization-Id") @NotBlank String organizationId,
            @Valid @RequestBody CreateProductRequest request) {
        Product product = catalogService.createProduct(
                organizationId, request.productCode(), request.name(), request.description());
        return mapper.toResponse(product);
    }
    @Operation(summary = "Update a product", description = "Registers a new top-level sellable offering. productCode must be unique within the organization.")
    @PatchMapping("/update/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public void updateProduct(
            @RequestHeader("X-Organization-Id") @NotBlank String organizationId,
            @PathVariable Long id,
            @Valid @RequestBody ProductDtos.CreateProductRequest request) {

        catalogService.updateProduct(
                id,organizationId, request);
        //return mapper.toResponse(product);
    }


    @Operation(summary = "List all products in the organization")
    @GetMapping("/all")
    public List<ProductResponse> listProducts(
            @RequestHeader("X-Organization-Id") @NotBlank String organizationId) {
        return catalogService.listProducts(organizationId).stream().map(mapper::toResponse).toList();
    }

    @Operation(summary = "Get a product by its code")
    @GetMapping("/{productCode}")
    public ProductResponse getProduct(
            @RequestHeader("X-Organization-Id") @NotBlank String organizationId,
            @PathVariable String productCode) {
        return mapper.toResponse(catalogService.getProduct(organizationId, productCode));
    }

    @Operation(summary = "List active plans for a product", description = "Returns only ACTIVE plan versions for this product — what a checkout page for this product should show.")
    @GetMapping("/{productCode}/catalog")
    public ResponseEntity<?> getActivePlansForProduct(
            @RequestHeader("X-Organization-Id") @NotBlank String organizationId,
            @PathVariable String productCode) {
        var plans = catalogService.getPublicCatalogForProduct(organizationId, productCode).stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(plans);
    }
}
