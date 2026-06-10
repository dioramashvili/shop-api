package com.shop.controller;

import com.shop.config.AppSettingsProperties;
import com.shop.dto.response.AppInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/api/info")
@RequiredArgsConstructor
@Tag(name = "Application Info", description = "Application metadata and configuration")
public class AppInfoController {

    private final AppSettingsProperties appSettings;
    private final MessageSource messageSource;

    @GetMapping
    @Operation(summary = "Get application metadata",
            description = "Returns application title, contact info, and localized description")
    public ResponseEntity<AppInfoResponse> getAppInfo(Locale locale) {
        log.debug("Serving application info for locale {}", locale);

        AppInfoResponse response = AppInfoResponse.builder()
                .title(appSettings.getApplicationTitle())
                .description(messageSource.getMessage("app.info.description", null, locale))
                .paginationLimit(appSettings.getPaginationLimit())
                .contactEmail(appSettings.getContactEmail())
                .externalServiceUrl(appSettings.getExternalServiceUrl())
                .build();

        return ResponseEntity.ok(response);
    }
}
