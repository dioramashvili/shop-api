package com.shop.config;

import com.shop.entity.Category;
import com.shop.entity.Product;
import com.shop.repository.CategoryRepository;
import com.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevDataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            return;
        }

        log.info("Seeding development catalog data");

        Category electronics = saveCategory("Electronics", "Gadgets and devices");
        Category clothing = saveCategory("Clothing", "Apparel and accessories");

        saveProduct("Wireless Mouse", "Ergonomic wireless mouse", new BigDecimal("29.99"), 100, electronics);
        saveProduct("USB-C Hub", "7-in-1 USB-C adapter", new BigDecimal("49.99"), 50, electronics);
        saveProduct("Cotton T-Shirt", "Comfortable everyday t-shirt", new BigDecimal("19.99"), 200, clothing);

        log.info("Development catalog seeded with {} categories and {} products",
                categoryRepository.count(), productRepository.count());
    }

    private Category saveCategory(String name, String description) {
        Category category = Category.builder()
                .name(name)
                .description(description)
                .build();
        return categoryRepository.save(category);
    }

    private void saveProduct(String name, String description, BigDecimal price, int stock, Category category) {
        Product product = Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .stock(stock)
                .category(category)
                .build();
        category.getProducts().add(product);
        productRepository.save(product);
    }
}
