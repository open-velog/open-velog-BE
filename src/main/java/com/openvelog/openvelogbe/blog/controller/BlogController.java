package com.openvelog.openvelogbe.blog.controller;

import com.openvelog.openvelogbe.blog.dto.BlogRequestDto;
import com.openvelog.openvelogbe.blog.dto.BlogResponseDto;
import com.openvelog.openvelogbe.blog.service.BlogService;
import com.openvelog.openvelogbe.common.dto.ApiResponse;
import com.openvelog.openvelogbe.common.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "blog")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/blogs")
public class BlogController {
    private final BlogService blogService;

    @PostMapping
    @Operation(summary = "블로그 등록", description = "블로그 등록")
    public ApiResponse<BlogResponseDto> createBlog(
            @RequestBody @Valid BlogRequestDto.BlogAdd dto,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ApiResponse.successOf(HttpStatus.CREATED, blogService.createBlog(dto, userDetails.getUser()));
    }

    @DeleteMapping("/{blogId}")
    @Operation(summary = "블로그 삭제", description = "블로그 삭제")
    public ApiResponse<BlogResponseDto> deleteBlog(
            @PathVariable Long blogId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        blogService.deleteBlog(blogId, userDetails.getUser());
        return ApiResponse.successOf(HttpStatus.NO_CONTENT, null);
    }

    @PutMapping("/{blogId}")
    @Operation(summary = "블로그 수정", description = "블로그 수정")
    public ApiResponse<BlogResponseDto> updateBlog(
            @PathVariable Long blogId,
            @RequestBody @Valid BlogRequestDto.BlogUpdate dto,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ApiResponse.successOf(HttpStatus.OK, blogService.updateBlog(blogId, dto, userDetails.getUser()));
    }



    @GetMapping("/{userId}")
    @SecurityRequirements()
    @Operation(summary = "블로그 조회", description = "블로그 조회")
    public ApiResponse<BlogResponseDto> getBlog(
            @PathVariable String userId) {
        return ApiResponse.successOf(HttpStatus.OK, blogService.getBlog(userId));
    }

}
