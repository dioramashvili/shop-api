package com.shop.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppInfoResponse {

    private String title;
    private String description;
    private int paginationLimit;
    private String contactEmail;
    private String externalServiceUrl;
}
