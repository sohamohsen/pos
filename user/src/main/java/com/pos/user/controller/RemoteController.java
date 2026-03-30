package com.pos.user.controller;

import com.pos.user.dto.BranchExistResponse;
import com.pos.user.service.BranchService;
import com.pos.user.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/remote")
@RequiredArgsConstructor
public class RemoteController {
    private final BranchService branchService;

    @GetMapping("/branch/exist/{id}")
    public ResponseEntity<ApiResponse<BranchExistResponse>> existsBranch(@PathVariable Integer id) {

        BranchExistResponse branchExistResponse = branchService.existsBranches(id);

        return ResponseEntity.ok(
                ApiResponse.<BranchExistResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("branch existence checked")
                        .data(branchExistResponse)
                        .build());
    }
}
