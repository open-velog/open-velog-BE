package com.openvelog.openvelogbe.member.controller;

import com.openvelog.openvelogbe.common.dto.ApiResponse;
import com.openvelog.openvelogbe.member.dto.LoginRequestDto;
import com.openvelog.openvelogbe.member.dto.MemberResponseDto;
import com.openvelog.openvelogbe.member.dto.SignupRequestDto;
import com.openvelog.openvelogbe.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Tag(name = "member")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    @SecurityRequirements()
    @Operation(summary = "회원 가입", description = "userId은 영문숫자 조합 4자 이상, 10자 이하\n password은 영문숫자 조합 8자 이상, 15자 이하\n, username은 아무 문자 4자 이상 20자 이하")
    public ApiResponse<MemberResponseDto> signup(@RequestBody @Valid SignupRequestDto signupRequestDto) {
        return ApiResponse.successOf(HttpStatus.CREATED, memberService.signup(signupRequestDto));
    }

    @PostMapping("/login")
    @SecurityRequirements()
    @Operation(summary = "로그인")
    public ApiResponse<MemberResponseDto> login(
            @RequestBody LoginRequestDto loginRequestDto,
            @Parameter(hidden = true) HttpServletResponse response
    ) {
        return ApiResponse.successOf(HttpStatus.OK, memberService.login(loginRequestDto, response));
    }

}
