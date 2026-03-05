package com.library.bookarte.board.controller;

import com.library.bookarte.board.dto.request.BoardSaveRequest;
import com.library.bookarte.board.dto.request.BoardUpdateRequest;
import com.library.bookarte.board.dto.response.BoardResponse;
import com.library.bookarte.board.dto.response.BoardSaveResponse;
import com.library.bookarte.board.dto.response.BoardUpdateResponse;
import com.library.bookarte.global.response.GlobalResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    ResponseEntity<GlobalResponseDto<BoardSaveResponse>> save(
            @PathVariable("type") String type,
            @RequestBody BoardSaveRequest boardSaveRequest,
            @AuthenticationPrincipal Long memberId
    );

    /* Update: 게시글 수정 */
    @Operation(summary = "게시글 수정", description = "**성공 응답 데이터:** 게시글 수정 성공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글이 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PatchMapping("/{boardId}")
    ResponseEntity<GlobalResponseDto<BoardUpdateResponse>> updateBoard(
            @PathVariable("type") String type,
            @AuthenticationPrincipal Long memberId,
            @PathVariable("boardId") Long boardId,
            @RequestBody BoardUpdateRequest boardUpdateRequest
    );

    /* Read: 게시글 조회 */
    @Operation(summary = "게시글 조회", description = "**성공 응답 데이터:** 게시글 조회 성공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글이 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/view/{boardId}")
    ResponseEntity<GlobalResponseDto<BoardResponse>> getBoard(
            @PathVariable("boardId") Long boardId
    );
}
