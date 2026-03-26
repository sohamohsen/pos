package com.pos.pos_product.Repository;

import com.pos.pos_product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Integer countByCategoryId(Integer id);

    Long countByCategoryIdAndActive(Integer id, boolean active);

    @Query("""
    SELECT p FROM Product p
    JOIN Category c ON p.categoryId = c.id
    WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
      AND (:barcode IS NULL OR p.barcode = :barcode)
      AND (:categoryId IS NULL OR p.categoryId = :categoryId)
      AND (:categoryName IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :categoryName, '%')))
      AND (:active IS NULL OR p.active = :active)
""")
    Page<Product> findProducts(
            Pageable pageable,
            @Param("name") String name,
            @Param("barcode") String barcode,
            @Param("categoryId") Integer categoryId,
            @Param("categoryName") String categoryName,
            @Param("active") Boolean active
    );
    boolean existsByBarcode(String barcode);

    Optional<Product> findByBarcode(String barcode);
}
