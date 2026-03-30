package com.pos.pos_product.service;

import com.pos.pos_product.Repository.AuditLogRepository;
import com.pos.pos_product.Repository.CategoryRepository;
import com.pos.pos_product.Repository.ProductRepository;
import com.pos.pos_product.dto.ProductRequest;
import com.pos.pos_product.dto.ProductResponse;
import com.pos.pos_product.dto.UpdateProductRequest;
import com.pos.pos_product.exception.ResourceNotFoundException;
import com.pos.pos_product.model.AuditLog;
import com.pos.pos_product.model.Category;
import com.pos.pos_product.model.Product;
import com.pos.pos_product.model.enums.AuditAction;
import com.pos.pos_product.model.enums.AuditField;
import com.pos.pos_product.model.enums.AuditTable;
import com.pos.pos_product.util.PageResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final AuditLogRepository auditRepository;
    private final ImageService imageService;


    @Transactional
    public void createProduct(ProductRequest request) {

        if (productRepository.existsByBarcode(request.getBarcode())) {
            throw new IllegalArgumentException("Barcode already exists");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .barcode(request.getBarcode())
                .categoryId(category.getId())
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        productRepository.save(product);

        saveAudit(
                product.getId(),
                AuditTable.PRODUCT,
                AuditAction.CREATE,
                AuditField.ALL,
                null,
                null
        );
    }

    @Transactional
    public void bulkUploadProducts(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null) continue;

                String name = getCellValue(row.getCell(0));
                String description = getCellValue(row.getCell(1));
                BigDecimal price = parseBigDecimal(getCellValue(row.getCell(2)));
                String barcode = getCellValue(row.getCell(3));
                String categoryName = getCellValue(row.getCell(4));

                processProductRow(name, description, price, barcode, categoryName);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to process Excel file", e);
        }
    }

    // ================= IMAGE UPDATE =================

    @Transactional
    public void addProductImg(Integer id, MultipartFile file) {

        Product product = getProduct(id);

        String oldKey = product.getImageUrl();

        String newKey = imageService.replaceImage(oldKey, file, "products");

        product.setImageUrl(newKey);
        productRepository.save(product);

        saveAudit(
                id,
                AuditTable.PRODUCT,
                oldKey == null ? AuditAction.ADD : AuditAction.UPDATE,
                AuditField.IMAGEURL,
                oldKey,
                newKey
        );
    }

    @Transactional
    public void updateProduct(Integer id, UpdateProductRequest request) {

        Product product = getProduct(id);

        if (request.getName() != null &&
                !request.getName().equals(product.getName())) {

            String oldValue = product.getName();
            product.setName(request.getName());

            saveAudit(id, AuditTable.PRODUCT,
                    AuditAction.UPDATE,
                    AuditField.NAME,
                    oldValue,
                    request.getName());
        }

        if (request.getPrice() != null &&
                !request.getPrice().equals(product.getPrice())) {

            String oldValue = String.valueOf(product.getPrice());
            String newValue = String.valueOf(request.getPrice());

            product.setPrice(request.getPrice());

            saveAudit(id, AuditTable.PRODUCT,
                    AuditAction.UPDATE,
                    AuditField.PRICE,
                    oldValue,
                    newValue);
        }

        if (request.getActive() != null &&
                request.getActive() != product.isActive()) {

            boolean oldValue = product.isActive();
            product.setActive(request.getActive());

            saveAudit(id, AuditTable.PRODUCT,
                    AuditAction.CHANGESTATUS,
                    AuditField.ACTIVE,
                    String.valueOf(oldValue),
                    String.valueOf(request.getActive()));
        }

        if (request.getCategoryId() != null &&
                !request.getCategoryId().equals(product.getCategoryId())) {

            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Category not found"));

            Integer oldValue = product.getCategoryId();
            product.setCategoryId(category.getId());

            saveAudit(id, AuditTable.PRODUCT,
                    AuditAction.UPDATE,
                    AuditField.CATEGORY,
                    String.valueOf(oldValue),
                    String.valueOf(category.getId()));
        }
    }

    // ================= DELETE PRODUCT =================

    @Transactional
    public void deleteProduct(Integer id) {

        Product product = getProduct(id);

        productRepository.deleteById(id);

        saveAudit(
                id,
                AuditTable.PRODUCT,
                AuditAction.DELETE,
                AuditField.ALL,
                null,
                null
        );
    }


    @Transactional
    public void deleteProductImage(Integer id) {

        Product product = getProduct(id);

        String oldKey = product.getImageUrl();

        if (oldKey == null) return;

        imageService.deleteImage(oldKey);

        product.setImageUrl(null);
        productRepository.save(product);

        saveAudit(
                id,
                AuditTable.PRODUCT,
                AuditAction.DELETE,
                AuditField.IMAGEURL,
                oldKey,
                null
        );
    }


    public ProductResponse getProductById(Integer id) {

        Product product = getProduct(id);

        return mapToResponse(product);
    }


    public PageResponse<ProductResponse> getAllProducts(
            int page,
            int size,
            String name,
            String barcode,
            Integer categoryId,
            String category,
            Boolean active
    ) {

        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be negative");
        }

        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }

        Pageable pageable =
                PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Product> productPage =
                productRepository.findProducts(
                        pageable,
                        normalize(name),
                        normalize(barcode),
                        categoryId,
                        normalize(category),
                        active
                );

        List<ProductResponse> content =
                productPage.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return PageResponse.<ProductResponse>builder()
                .content(content)
                .pageNumber(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .last(productPage.isLast())
                .build();
    }

    // ================= HELPERS =================

    private Product getProduct(Integer id) {

        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found with id: " + id));
    }

    private ProductResponse mapToResponse(Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .barcode(product.getBarcode())
                .categoryId(product.getCategoryId())
                .active(product.isActive())
                .imgUrl(product.getImageUrl())
                .createdAt(product.getCreatedAt())
                .build();
    }

    private void saveAudit(
            Integer entityId,
            AuditTable tableName,
            AuditAction actionType,
            AuditField fieldName,
            String oldValue,
            String newValue
    ) {

        AuditLog audit = AuditLog.builder()
                .entityId(entityId)
                .tableName(tableName)
                .actionType(actionType)
                .fieldName(fieldName)
                .oldValue(oldValue)
                .newValue(newValue)
                .changedBy(getCurrentUserId())
                .build();

        auditRepository.save(audit);
    }

    private Integer getCurrentUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return (Integer) authentication.getPrincipal();
    }

    private String normalize(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    private String getCellValue(Cell cell) {

        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> null;
        };
    }

    private void processProductRow(
            String name,
            String description,
            BigDecimal price,
            String barcode,
            String categoryName
    ) {

        Category category = categoryRepository
                .findByNameIgnoreCase(categoryName)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found: " + categoryName));

        Optional<Product> existing =
                productRepository.findByBarcode(barcode);

        if (existing.isPresent()) {

            Product product = existing.get();

            String oldPrice = String.valueOf(product.getPrice());

            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setCategoryId(category.getId());

            saveAudit(product.getId(),
                    AuditTable.PRODUCT,
                    AuditAction.UPDATE,
                    AuditField.ALL,
                    oldPrice,
                    String.valueOf(price));

        } else {

            Product product = Product.builder()
                    .name(name)
                    .description(description)
                    .price(price)
                    .barcode(barcode)
                    .categoryId(category.getId())
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();

            productRepository.save(product);

            saveAudit(product.getId(),
                    AuditTable.PRODUCT,
                    AuditAction.CREATE,
                    AuditField.ALL,
                    null,
                    null);
        }
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return new BigDecimal(value.trim());
    }

    public boolean existsProduct(Integer id) {
        return productRepository.existsById(id);
    }
}