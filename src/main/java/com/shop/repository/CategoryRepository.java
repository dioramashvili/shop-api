package com.shop.repository;

import com.shop.entity.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    @EntityGraph(attributePaths = "products")
    @Query("SELECT c FROM Category c")
    List<Category> findAllWithProducts();

    @EntityGraph(attributePaths = "products")
    @Query("SELECT c FROM Category c WHERE c.id = :id")
    Optional<Category> findByIdWithProducts(@Param("id") Long id);
}
