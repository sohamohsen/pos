package com.pos.pos_inventory.client;

import com.pos.pos_inventory.dto.BranchExistResponse;
import com.pos.pos_inventory.util.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "pos-user",
        url = "${services.user.base-url}",
        configuration = FeignClientConfig.class
)
public interface UserClient {
    @GetMapping("/branch/exist/{id}")
    ApiResponse<BranchExistResponse> existsBranch(@PathVariable("id") Integer id);


}
