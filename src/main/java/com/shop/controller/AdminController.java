package com.shop.controller;

import com.shop.config.AppSettingsProperties;
import com.shop.dto.response.AdminDashboardResponse;
import com.shop.exception.ErrorResponse;
import com.shop.repository.CategoryRepository;
import com.shop.repository.ProductRepository;
import com.shop.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "ADMIN-only management endpoints")
@SecurityRequirement(name = "basicAuth")
public class AdminController {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AppSettingsProperties appSettings;
    private final MessageSource messageSource;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin dashboard", description = "Returns shop statistics (ADMIN role required)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dashboard data retrieved",
                    content = @Content(schema = @Schema(implementation = AdminDashboardResponse.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Not authorized (ADMIN required)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AdminDashboardResponse> getDashboard(Locale locale) {
        log.info("Admin dashboard accessed for application {}", appSettings.getApplicationTitle());

        String message = messageSource.getMessage(
                "admin.dashboard.welcome",
                new Object[]{appSettings.getApplicationTitle()},
                locale
        );

        AdminDashboardResponse response = AdminDashboardResponse.builder()
                .message(message)
                .totalCategories(categoryRepository.count())
                .totalProducts(productRepository.count())
                .totalUsers(userRepository.count())
                .build();
        return ResponseEntity.ok(response);
    }
}
