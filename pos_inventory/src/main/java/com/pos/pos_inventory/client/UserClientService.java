package com.pos.pos_inventory.client;

import com.pos.pos_inventory.dto.BranchExistResponse;
import com.pos.pos_inventory.exception.ResourceNotFoundException;
import com.pos.pos_inventory.exception.ServiceUnavailableException;
import com.pos.pos_inventory.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserClientService {
    private final UserClient userClient;

    public BranchExistResponse validateBranchExists(Integer branchId) {
        try {
            ApiResponse<BranchExistResponse> response = userClient.existsBranch(branchId);

            if (response == null || response.getData() == null || !response.getData().isExist()) {
                throw new ResourceNotFoundException(
                        "Branch not found with id: " + branchId
                );
            }
            return BranchExistResponse.builder()
                    .exist(response.getData().isExist())
                    .branchType(response.getData().getBranchType())
                    .build();

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Branch service call failed for branchId={}", branchId, e);
            throw new ServiceUnavailableException(
                    "Branch service is currently unavailable. Please try again later."
            );
        }
    }
}
