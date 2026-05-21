package com.shop.service;

import com.shop.dto.request.ProductRequest;
import com.shop.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    List<ProductResponse> getAllProducts();

    ProductResponse getProductById(Long id);

    List<ProductResponse> getProductsByCategory(Long categoryId);

    ProductResponse updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);
}
