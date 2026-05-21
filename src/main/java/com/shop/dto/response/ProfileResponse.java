package com.shop.dto.response;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponse {

    private Long id;
    private String username;
    private String email;
    private Set<String> roles;
}
