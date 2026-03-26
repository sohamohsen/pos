package com.pos.user.controller;

import com.pos.user.dto.BranchRequest;
import com.pos.user.dto.BranchResponse;
import com.pos.user.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> create(@RequestBody BranchRequest request) {
        branchService.createBranch(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<BranchResponse>> getAll() {
        return ResponseEntity.ok(branchService.getAllBranches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BranchResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(branchService.getBranchById(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> update(
            @PathVariable Integer id,
            @RequestBody BranchRequest request) {

        branchService.updateBranch(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        branchService.deleteBranch(id);
        return ResponseEntity.ok().build();
    }
}