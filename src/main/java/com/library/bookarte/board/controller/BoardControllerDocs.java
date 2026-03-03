package com.library.bookarte.board.controller;

import com.library.bookarte.board.dto.request.BoardSaveRequest;
import com.library.bookarte.global.response.GlobalResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Board")
public interface BoardControllerDocs {
    /* Create: 게시글 등록 */
    @Operation(summary = "게시글 등록 요청", description = "**성공 응답 데이터:** 게시글 등록 성공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "게시글 등록 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping
    ResponseEntity<GlobalResponseDto<Void>> save(
            @PathVariable("type") String type,
            @RequestBody BoardSaveRequest boardSaveRequest,
            @AuthenticationPrincipal Long memberId
    );
}
