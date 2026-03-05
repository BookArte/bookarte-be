package com.library.bookarte.board.controller;

import com.library.bookarte.board.dto.request.BoardSaveRequest;
import com.library.bookarte.board.dto.request.BoardUpdateRequest;
import com.library.bookarte.board.dto.response.BoardResponse;
import com.library.bookarte.board.dto.response.BoardSaveResponse;
import com.library.bookarte.board.dto.response.BoardUpdateResponse;
import com.library.bookarte.board.service.BoardService;
import com.library.bookarte.global.response.GlobalResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/board/{type}")
@RequiredArgsConstructor
public class BoardController implements BoardControllerDocs {
    private final BoardService boardService;

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
        return ResponseEntity.status(HttpStatus.CREATED).body(GlobalResponseDto.success(HttpStatus.CREATED, result));
    }

    @Override
    public ResponseEntity<GlobalResponseDto<BoardResponse>> getBoard(
            @PathVariable("boardId") Long boardId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(GlobalResponseDto.success(HttpStatus.CREATED, null));
    }


}
