package com.pos.user.service;

import com.pos.user.dto.BranchRequest;
import com.pos.user.dto.BranchResponse;
import com.pos.user.entity.Branch;
import com.pos.user.repository.BranchRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;

    public void createBranch(BranchRequest request) {

        if (branchRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Branch code already exists");
        }

        Branch branch = Branch.builder()
                .name(request.getName())
                .code(request.getCode())
                .location(request.getLocation())
                .type(request.getType())
                .isActive(true)
                .build();

        branchRepository.save(branch);
    }

    public List<BranchResponse> getAllBranches() {
        return branchRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public BranchResponse getBranchById(Integer id) {

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found"));

        return mapToResponse(branch);
    }

    @Transactional
    public void updateBranch(Integer id, BranchRequest request) {

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found"));

        if (request.getName() != null)
            branch.setName(request.getName());

        if (request.getLocation() != null)
            branch.setLocation(request.getLocation());

        if (request.getType() != null)
            branch.setType(request.getType());
    }

    @Transactional
    public void deleteBranch(Integer id) {

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found"));

        branch.setIsActive(false); // Soft delete
    }

    private BranchResponse mapToResponse(Branch branch) {
        return BranchResponse.builder()
                .id(branch.getId())
                .name(branch.getName())
                .code(branch.getCode())
                .location(branch.getLocation())
                .type(branch.getType())
                .isActive(branch.getIsActive())
                .createdAt(branch.getCreatedAt())
                .build();
    }
}