package com.library.bookarte.board.controller;

import com.library.bookarte.board.dto.request.*;
import com.library.bookarte.board.dto.response.BoardResponse;
import com.library.bookarte.board.dto.response.BoardSaveResponse;
import com.library.bookarte.board.dto.response.BoardUpdateResponse;
import com.library.bookarte.global.response.GlobalResponseDto;
import com.library.bookarte.global.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            @ModelAttribute BoardSaveRequest boardSaveRequest,
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
            @ModelAttribute BoardUpdateRequest boardUpdateRequest
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
            @PathVariable("type") String type,
            @PathVariable("boardId") Long boardId
    );

    /* Delete: 게시글 삭제 */
    @Operation(summary = "게시글 삭제", description = "**성공 응답 데이터:** 게시글 삭제 성공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글이 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @DeleteMapping
    ResponseEntity<GlobalResponseDto<Void>> deleteBoard(
            @PathVariable("type") String type,
            @AuthenticationPrincipal Long memberId,
            @RequestBody BoardDelsRequest boardDelsRequest
    );

    /* Read: 게시글 리스트 조회 */
    @Operation(summary = "게시글 리스트 조회", description = "**성공 응답 데이터:** 게시글 리스트 조회 성공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 리스트 조회 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/list")
    ResponseEntity<GlobalResponseDto<PageResponse<BoardResponse>>> getBoardList(
            @PathVariable("type") String type,
            @ModelAttribute BoardListRequest boardListRequest
    );

    /* Read: 본인 게시글 리스트 조회 */
    @Operation(summary = "본인 게시글 리스트 조회", description = "**성공 응답 데이터:** 게시글 리스트 조회 성공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 리스트 조회 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/my_list")
    ResponseEntity<GlobalResponseDto<PageResponse<BoardResponse>>> getMyBoardList(
            @PathVariable("type") String type,
            @ModelAttribute BoardListRequest boardListRequest,
            @AuthenticationPrincipal Long memberId
    );

    /* Read: 본인 게시글 조회 */
    @Operation(summary = "본인 게시글 조회", description = "**성공 응답 데이터:** 게시글 조회 성공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글이 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/my_view/{boardId}")
    ResponseEntity<GlobalResponseDto<BoardResponse>> getMyBoard(
            @PathVariable("type") String type,
            @PathVariable("boardId") Long boardId,
            @AuthenticationPrincipal Long memberId
    );

    /* 파일 다운로드 */
    @Operation(summary = "파일 다운로드", description = "**성공 응답 데이터:** 파일 다운로드 성공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "파일 다운로드 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping(value = "/download/{fileId}")
    ResponseEntity<Resource> downloadFile(@PathVariable("fileId") Long fileId);
}
