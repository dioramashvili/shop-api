package com.shop.service.impl;

import com.shop.dto.request.CategoryRequest;
import com.shop.dto.response.CategoryResponse;
import com.shop.dto.response.ProductSummaryResponse;
import com.shop.entity.Category;
import com.shop.entity.Product;
import com.shop.exception.DuplicateResourceException;
import com.shop.exception.ResourceNotFoundException;
import com.shop.repository.CategoryRepository;
import com.shop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse createCategory(CategoryRequest request) {
        validateUniqueName(request.getName(), null);

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        Category saved = categoryRepository.save(category);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAllWithProducts()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findByIdWithProducts(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        return mapToResponse(category);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        validateUniqueName(request.getName(), id);

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category updated = categoryRepository.save(category);
        return mapToResponse(categoryRepository.findByIdWithProducts(updated.getId()).orElse(updated));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        categoryRepository.delete(category);
    }

    private void validateUniqueName(String name, Long excludeId) {
        boolean duplicate = excludeId == null
                ? categoryRepository.existsByNameIgnoreCase(name)
                : categoryRepository.existsByNameIgnoreCaseAndIdNot(name, excludeId);

        if (duplicate) {
            throw new DuplicateResourceException("Category with name '" + name + "' already exists");
        }
    }

    private CategoryResponse mapToResponse(Category category) {
        List<ProductSummaryResponse> products = category.getProducts().stream()
                .map(this::mapProductSummary)
                .collect(Collectors.toList());

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .products(products)
                .build();
    }

    private ProductSummaryResponse mapProductSummary(Product product) {
        return ProductSummaryResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }
}
