package com.shop.controller;

import com.shop.dto.response.ProfileResponse;
import com.shop.entity.User;
import com.shop.entity.UserRole;
import com.shop.exception.ErrorResponse;
import com.shop.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/profile")
@Tag(name = "Profile", description = "Authenticated user profile endpoints")
@SecurityRequirement(name = "basicAuth")
public class ProfileController {

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Returns the logged-in user's profile (requires authentication)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile retrieved",
                    content = @Content(schema = @Schema(implementation = ProfileResponse.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProfileResponse> getCurrentUser(@AuthenticationPrincipal CustomUserDetails principal) {
        User user = principal.getUser();
        ProfileResponse response = ProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles().stream()
                        .map(UserRole::name)
                        .collect(Collectors.toSet()))
                .build();
        return ResponseEntity.ok(response);
    }
}
