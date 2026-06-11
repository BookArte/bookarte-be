package com.library.bookarte.ai.controller;

import com.library.bookarte.ai.dto.request.GeminiRequest;
import com.library.bookarte.ai.dto.response.GeminiResponse;
import com.library.bookarte.global.response.GlobalResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Tag(name = "gemini")
public interface GeminiControllerDocs {
    /* Create: 채팅 */
    @Operation(summary = "채팅", description = "**성공 응답 데이터:** 응답 성공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "응답 성공"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/chat")
    ResponseEntity<GlobalResponseDto<GeminiResponse>> chat(@AuthenticationPrincipal Long memberId, GeminiRequest geminiRequest, HttpServletRequest request);

    /* Read: 채팅 리스트 */
    @Operation(summary = "채팅 리스트", description = "**성공 응답 데이터:** 응답 성공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "응답 성공"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/history")
    ResponseEntity<GlobalResponseDto<List<GeminiResponse>>> getHistory(@AuthenticationPrincipal Long memberId, HttpServletRequest request);
}
