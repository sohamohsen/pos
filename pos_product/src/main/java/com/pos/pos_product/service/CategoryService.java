package com.pos.pos_product.service;

import com.pos.pos_product.Repository.AuditLogRepository;
import com.pos.pos_product.Repository.CategoryRepository;
import com.pos.pos_product.Repository.ProductRepository;
import com.pos.pos_product.dto.CategoryResponse;
import com.pos.pos_product.exception.ResourceNotFoundException;
import com.pos.pos_product.model.AuditLog;
import com.pos.pos_product.model.enums.AuditAction;
import com.pos.pos_product.model.Category;
import com.pos.pos_product.model.enums.AuditField;
import com.pos.pos_product.model.enums.AuditTable;
import com.pos.pos_product.s3.S3Service;
import com.pos.pos_product.util.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final AuditLogRepository auditRepository;
    private final ImageService imageService;

    public void createCategory(String name, String description, boolean active){
        if(categoryRepository.existsByName(name)){
            throw new IllegalArgumentException("this category is already exist.");
        }

        Category category = Category.builder()
                .name(name)
                .description(description)
                .active(active)
                .build();
        categoryRepository.save(category);

        saveAudit(
                category.getId(),
                AuditTable.CATEGORY,
                AuditAction.CREATE,
                AuditField.ALL,
                null,
                null
        );
    }

    @Transactional
    public void addCategoryImage(Integer id, MultipartFile file) {

        Category category = getCategory(id);

        String oldKey = category.getImageUrl();

        String newKey = imageService.replaceImage(oldKey, file, "categories");

        category.setImageUrl(newKey);
        categoryRepository.save(category);

        saveAudit(
                category.getId(),
                AuditTable.CATEGORY,
                oldKey == null ? AuditAction.ADD : AuditAction.UPDATE,
                AuditField.IMAGEURL,
                oldKey,
                newKey
        );
    }
    @Transactional
    public void deleteCategoryImage(Integer id) {

        Category category = getCategory(id);

        String oldKey = category.getImageUrl();

        imageService.deleteImage(oldKey);
        category.setImageUrl(null);
        categoryRepository.save(category);
        saveAudit(
                category.getId(),
                AuditTable.CATEGORY,
                AuditAction.DELETE,
                AuditField.IMAGEURL,
                oldKey,
                null
        );
    }
    @Transactional
    public void updateCategory(Integer id,
                               String name,
                               String description,
                               Boolean active) {

        Category category = getCategory(id); // already throws if not found

        // Update name
        if (name != null && !name.equals(category.getName())) {

            String oldValue = category.getName();
            category.setName(name);

            saveAudit(
                    category.getId(),
                    AuditTable.CATEGORY,
                    AuditAction.UPDATE,
                    AuditField.NAME,
                    oldValue,
                    name
            );
        }

        // Update description
        if (description != null && !description.equals(category.getDescription())) {

            String oldValue = category.getDescription();
            category.setDescription(description);

            saveAudit(
                    category.getId(),
                    AuditTable.CATEGORY,
                    AuditAction.UPDATE,
                    AuditField.DESCRIPTION,
                    oldValue,
                    description
            );

        }

        // Update active
        if (active != null && active != category.isActive()) {

            boolean oldValue = category.isActive();
            category.setActive(active);

            saveAudit(
                    category.getId(),
                    AuditTable.CATEGORY,
                    AuditAction.CHANGESTATUS,
                    AuditField.ACTIVE,
                    String.valueOf(oldValue),
                    String.valueOf(active)
            );
        }

    }

    public CategoryResponse getCategoryById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Category id must not be null");
        }

        Category category = categoryRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("this category not found."));
        Long activeProductCount = productRepository.countByCategoryIdAndActive(id, true);
        Long notActiveProductCount = productRepository.countByCategoryIdAndActive(id, false);
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .active(category.isActive())
                .activeProductCount(activeProductCount)
                .notActiveProductCount(notActiveProductCount)
                .createdAt(category.getCreatedAt())
                .build();
    }

    public PageResponse<CategoryResponse> getAllCategories(int page, int size, String name, Boolean active) {
        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be negative");
        }

        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Category> categoryPage = categoryRepository.findCategories(pageable, name, active);

        List<CategoryResponse> content =
                categoryPage.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList());

        return PageResponse.<CategoryResponse>builder()
                .content(content)
                .pageNumber(categoryPage.getNumber())
                .pageSize(categoryPage.getSize())
                .totalElements(categoryPage.getTotalElements())
                .totalPages(categoryPage.getTotalPages())
                .last(categoryPage.isLast())
                .build();
    }

    @Transactional
    public void toggleCategory(Integer id) {

        Category category = getCategory(id);

        boolean oldValue = category.isActive();
        boolean newValue = !oldValue;

        category.setActive(newValue);

        // Audit
        saveAudit(
                category.getId(),
                AuditTable.CATEGORY,
                AuditAction.CHANGESTATUS,
                AuditField.ACTIVE,
                String.valueOf(oldValue),
                String.valueOf(newValue)

        );
    }

    public void deleteCategory(Integer id) {
        Category category = getCategory(id);
        categoryRepository.deleteById(id);

        saveAudit(
                category.getId(),
                AuditTable.CATEGORY,
                AuditAction.DELETE,
                AuditField.ALL,
                null,
                null
        );

    }

    private Category getCategory(Integer id){
        if (id == null) {
            throw new IllegalArgumentException("Category id must not be null");
        }

        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found with id: " + id));
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .active(category.isActive())
                .createdAt(category.getCreatedAt())
                .build();
    }

    private Integer getCurrentUserId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        return (Integer) authentication.getPrincipal();
    }

    private void saveAudit(
            Integer entityId,
            AuditTable tableName,
            AuditAction actionType,
            AuditField fieldName,
            String oldValue,
            String newValue
    ) {

        Integer userId = getCurrentUserId();

        AuditLog audit = AuditLog.builder()
                .entityId(entityId)
                .tableName(tableName)
                .actionType(actionType)
                .fieldName(fieldName)
                .oldValue(oldValue)
                .newValue(newValue)
                .changedBy(userId)
                .changedAt(LocalDateTime.now())
                .build();

        auditRepository.save(audit);
    }
}
