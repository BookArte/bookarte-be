package com.library.bookarte.board.controller;

import com.library.bookarte.board.dto.request.BoardSaveRequest;
import com.library.bookarte.board.dto.request.BoardUpdateRequest;
import com.library.bookarte.board.dto.request.FileUploadTest;
import com.library.bookarte.board.dto.response.BoardResponse;
import com.library.bookarte.board.dto.response.BoardSaveResponse;
import com.library.bookarte.board.dto.response.BoardUpdateResponse;
import com.library.bookarte.board.service.BoardService;
import com.library.bookarte.global.response.GlobalResponseDto;
import com.library.bookarte.global.response.PageResponse;
import com.library.bookarte.global.util.S3Service;
import lombok.RequiredArgsConstructor;
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
            @RequestBody BoardSaveRequest boardSaveRequest,
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
            @RequestBody BoardUpdateRequest boardUpdateRequest
    ) {
        BoardUpdateResponse result = boardService.updateBoard(type, memberId, boardId, boardUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    @Override
    public ResponseEntity<GlobalResponseDto<BoardResponse>> getBoard(
            @PathVariable("boardId") Long boardId
    ) {
        BoardResponse result = boardService.getBoard(boardId);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    @Override
    public ResponseEntity<GlobalResponseDto<Void>> deleteBoard(
            @PathVariable("type") String type,
            @AuthenticationPrincipal Long memberId,
            @PathVariable("boardId") Long boardId

    ) {
        boardService.deleteBoard(type, memberId, boardId);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponseDto.success(HttpStatus.OK, null));
    }

    @Override
    public ResponseEntity<GlobalResponseDto<PageResponse<BoardResponse>>> getBoardList(
            @PathVariable("type") String type,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        PageResponse<BoardResponse> result = boardService.getBoardList(type, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    @Override
    public ResponseEntity<GlobalResponseDto<String>> fileUpload(
            @ModelAttribute FileUploadTest fileUploadTest
    ) {
        MultipartFile file = fileUploadTest.getFile();

        if (file == null || file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(GlobalResponseDto.fail(HttpStatus.BAD_REQUEST, "파일이 없습니다."));
        }

        String result = s3Service.uploadFile(file);

        return ResponseEntity.status(HttpStatus.OK).body(GlobalResponseDto.success(HttpStatus.OK, result));
    }
}
