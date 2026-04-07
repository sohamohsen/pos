package com.pos.pos_inventory.service;

import com.pos.pos_inventory.client.ProductClientService;
import com.pos.pos_inventory.client.UserClientService;
import com.pos.pos_inventory.dto.BranchExistResponse;
import com.pos.pos_inventory.dto.DeductStockRequest;
import com.pos.pos_inventory.dto.InitRequest;
import com.pos.pos_inventory.dto.InitResponse;
import com.pos.pos_inventory.exception.DuplicateInventoryException;
import com.pos.pos_inventory.exception.InsufficientStockException;
import com.pos.pos_inventory.exception.ResourceNotFoundException;
import com.pos.pos_inventory.model.Inventory;
import com.pos.pos_inventory.model.StockMovement;
import com.pos.pos_inventory.model.enums.BranchType;
import com.pos.pos_inventory.model.enums.InventoryStatus;
import com.pos.pos_inventory.model.enums.MovementReason;
import com.pos.pos_inventory.repository.InventoryRepository;
import com.pos.pos_inventory.repository.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ProductClientService productClientService;
    private final UserClientService userClientService;


    public InitResponse createInit(InitRequest request) {

        if (inventoryRepository.existsByProductIdAndBranchId(
                request.getProductId(), request.getBranchId())) {
            throw new DuplicateInventoryException(
                    "Inventory already initialized for productId=" + request.getProductId()
                            + " at locationId=" + request.getBranchId());
        }

        productClientService.validateProductExists(request.getProductId());
        BranchExistResponse branch = userClientService.validateBranchExists(request.getBranchId());

        validateByLocationType(request, branch.getBranchType());

        Inventory inventory = Inventory.builder()
                .productId(request.getProductId())
                .branchId(request.getBranchId())
                .locationType(branch.getBranchType())
                .availableQuantity(request.getAvailableQuantity())
                .displayQuantity(request.getDisplayQuantity())
                .reservedQuantity(0)
                .damagedQuantity(0)
                .reorderLevel(request.getReorderLevel())
                .reorderQuantity(request.getReorderQuantity())
                .status(resolveStatus(request.getAvailableQuantity(), request.getReorderLevel()))
                .build();

        Inventory saved = inventoryRepository.save(inventory);

        // Record initial stock movement
        StockMovement movement = StockMovement.builder()
                .inventoryId(saved.getId().longValue())
                .delta(request.getAvailableQuantity())
                .quantityAfter(saved.getAvailableQuantity())
                .reason(MovementReason.INITIAL)
                .performedBy(null)
                .build();
        stockMovementRepository.save(movement);

        return mapToInitResponse(saved, branch.getBranchType());
    }

    // ================ DEDUCT STOCK ================

    @Transactional
    public void deductStock(Integer inventoryId, DeductStockRequest request) {

        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found with id: " + inventoryId));

        int requested = request.getQuantity();

        if (inventory.getAvailableQuantity() < requested) {
            throw new InsufficientStockException(
                    "Insufficient stock for inventoryId=" + inventoryId
                            + ". Available=" + inventory.getAvailableQuantity()
                            + ", Requested=" + requested);
        }

        int newQty = inventory.getAvailableQuantity() - requested;
        inventory.setAvailableQuantity(newQty);
        inventory.setStatus(resolveStatus(newQty, inventory.getReorderLevel()));
        inventoryRepository.save(inventory);

        StockMovement movement = StockMovement.builder()
                .inventoryId(inventoryId.longValue())
                .delta(-requested)
                .quantityAfter(newQty)
                .reason(MovementReason.SALE)
                .referenceId(request.getReferenceId())
                .performedBy(request.getPerformedBy())
                .build();
        stockMovementRepository.save(movement);
    }

    private InitResponse mapToInitResponse(Inventory saved, BranchType locationType) {
        return InitResponse.builder()
                .id(saved.getId())
                .productId(saved.getProductId())
                .locationType(locationType)
                .locationId(saved.getBranchId())
                .availableQuantity(saved.getAvailableQuantity())
                .displayQuantity(saved.getDisplayQuantity())
                .reorderLevel(saved.getReorderLevel())
                .reorderQuantity(saved.getReorderQuantity())
                .build();
    }

    private InventoryStatus resolveStatus(int available, int reorderLevel) {
        if (available == 0)                return InventoryStatus.OUT_OF_STOCK;
        if (available <= reorderLevel)     return InventoryStatus.LOW_STOCK;
        return InventoryStatus.IN_STOCK;
    }

    private void validateByLocationType(InitRequest request, BranchType locationType) {
        switch (locationType) {
            case STORE     -> validateStoreInventory(request);
            case WAREHOUSE -> validateWarehouseInventory(request);
            default -> throw new IllegalArgumentException(
                    "Unsupported location type: " + locationType);
        }
    }

    private void validateStoreInventory(InitRequest request) {
        if (request.getAvailableQuantity() < 0)
            throw new IllegalArgumentException("availableQuantity cannot be negative");

        if (request.getDisplayQuantity() == null)
            throw new IllegalArgumentException("displayQuantity is required for STORE");

        if (request.getDisplayQuantity() < 0)
            throw new IllegalArgumentException("displayQuantity cannot be negative");

        if (request.getDisplayQuantity() > request.getAvailableQuantity())
            throw new IllegalArgumentException("displayQuantity cannot exceed availableQuantity");

        if (request.getReorderLevel() < 0 || request.getReorderQuantity() <= 0)
            throw new IllegalArgumentException("Invalid reorder configuration");
    }

    private void validateWarehouseInventory(InitRequest request) {
        if (request.getAvailableQuantity() < 0)
            throw new IllegalArgumentException("availableQuantity cannot be negative");

        if (request.getDisplayQuantity() != null)
            throw new IllegalArgumentException("displayQuantity is not applicable for WAREHOUSE");

        if (request.getReorderLevel() < 0 || request.getReorderQuantity() <= 0)
            throw new IllegalArgumentException("Invalid reorder configuration");
    }
}
