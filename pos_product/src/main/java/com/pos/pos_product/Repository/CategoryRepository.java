package com.pos.pos_product.Repository;

import com.pos.pos_product.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("""
       SELECT c
       FROM Category c
       WHERE (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
         AND (:active IS NULL OR c.active = :active)
       """)
    Page<Category> findCategories(
            Pageable pageable,
            @Param("name") String name,
            @Param("active") Boolean active
    );

    boolean existsByName(String name);

    Optional<Category> findByNameIgnoreCase(String categoryName);
}
