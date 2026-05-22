package com.library.bookarte.board.controller;

import com.library.bookarte.board.dto.request.*;
import com.library.bookarte.board.dto.response.BoardResponse;
import com.library.bookarte.board.dto.response.BoardSaveResponse;
import com.library.bookarte.board.dto.response.BoardUpdateResponse;
import com.library.bookarte.board.service.BoardService;
import com.library.bookarte.global.response.GlobalResponseDto;
import com.library.bookarte.global.response.PageResponse;
import com.library.bookarte.global.util.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/board/{type}")
@RequiredArgsConstructor
public class BoardController implements BoardControllerDocs {
    private final BoardService boardService;

    private final S3Service s3Service;

    @Override
    public ResponseEntity<GlobalResponseDto<BoardSaveResponse>> save(
            @PathVariable("type") String type,
            @ModelAttribute BoardSaveRequest boardSaveRequest,
            @AuthenticationPrincipal Long memberId
    ) {
        BoardSaveResponse result = boardService.save(type, boardSaveRequest, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(GlobalResponseDto.success(HttpStatus.CREATED, result));
    }

    @Override
    public ResponseEntity<GlobalResponseDto<BoardUpdateResponse>> updateBoard(
            @PathVariable("type") String type,
            @AuthenticationPrincipal Long memberId,
            @PathVariable("boardId") Long boardId,
            @ModelAttribute BoardUpdateRequest boardUpdateRequest
    ) {
        BoardUpdateResponse result = boardService.updateBoard(type, memberId, boardId, boardUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    @Override
    public ResponseEntity<GlobalResponseDto<BoardResponse>> getBoard(
            @PathVariable("type") String type,
            @PathVariable("boardId") Long boardId
    ) {
        BoardResponse result = boardService.getBoard(boardId, type);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    @Override
    public ResponseEntity<GlobalResponseDto<Void>> deleteBoard(
            @PathVariable("type") String type,
            @AuthenticationPrincipal Long memberId,
            @RequestBody BoardDelsRequest boardDelsRequest

    ) {
        boardService.deleteBoard(type, memberId, boardDelsRequest);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponseDto.success(HttpStatus.OK, null));
    }

    @Override
    public ResponseEntity<GlobalResponseDto<PageResponse<BoardResponse>>> getBoardList(
            @PathVariable("type") String type,
            @ModelAttribute BoardListRequest boardListRequest
    ) {
        PageResponse<BoardResponse> result = boardService.getBoardList(type, boardListRequest);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    @Override
    public ResponseEntity<GlobalResponseDto<PageResponse<BoardResponse>>> getMyBoardList(
            @PathVariable("type") String type,
            @ModelAttribute BoardListRequest boardListRequest,
            @AuthenticationPrincipal Long memberId
    ) {
        PageResponse<BoardResponse> result = boardService.getMyBoardList(type, boardListRequest, memberId);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    @Override
    public ResponseEntity<Resource> downloadFile(@PathVariable("fileId") Long fileId) {

        return s3Service.downloadFile(fileId);
    }
}
