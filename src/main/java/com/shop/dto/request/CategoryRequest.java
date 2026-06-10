package com.shop.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {

    @NotBlank(message = "{validation.category.name.notblank}")
    @Size(min = 2, max = 100, message = "{validation.category.name.size}")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
}