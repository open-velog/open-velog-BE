package com.openvelog.openvelogbe.aws.controller;

import com.openvelog.openvelogbe.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "aws related controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aws")
public class AwsController {
    @GetMapping("/health-check")
    @Operation(summary = "Health check this instance",
            description = "This api is called by AWS Load Balancer to check status of this instance")
    public ApiResponse<String> healthCheck() {
        return ApiResponse.successOf(HttpStatus.OK, "up");
    }
}
