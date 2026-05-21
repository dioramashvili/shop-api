package com.shop.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSummaryResponse {

    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;
}
