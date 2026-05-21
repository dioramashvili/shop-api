package com.shop.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardResponse {

    private String message;
    private long totalCategories;
    private long totalProducts;
    private long totalUsers;
}
