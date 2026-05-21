package com.shop.repository;

import com.shop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Find all products by category ID
    List<Product> findByCategoryId(Long categoryId);

    // Find products by name (partial match)
    List<Product> findByNameContainingIgnoreCase(String name);
}