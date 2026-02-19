package com.library.bookarte.ai.controller;

import com.library.bookarte.ai.dto.request.GeminiRequest;
import com.library.bookarte.ai.dto.response.GeminiResponse;
import com.library.bookarte.ai.service.GeminiService;
import com.library.bookarte.global.response.GlobalResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class GeminiController implements GeminiControllerDocs {
    private final GeminiService geminiService;

    @Override
    public ResponseEntity<GlobalResponseDto<GeminiResponse>> chat(
            @AuthenticationPrincipal Long memberId,
            @RequestBody GeminiRequest geminiRequest,
            HttpServletRequest request
    ) {
        GeminiResponse result = geminiService.getChateResponse(memberId, geminiRequest, request);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

}
