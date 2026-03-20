package com.library.bookarte.board.service;

import com.library.bookarte.board.dto.request.BoardDelsRequest;
import com.library.bookarte.board.dto.request.BoardSaveRequest;
import com.library.bookarte.board.dto.request.BoardUpdateRequest;
import com.library.bookarte.board.dto.response.BoardResponse;
import com.library.bookarte.board.dto.response.BoardSaveResponse;
import com.library.bookarte.board.dto.response.BoardUpdateResponse;
import com.library.bookarte.board.entity.Board;
import com.library.bookarte.board.entity.News;
import com.library.bookarte.board.entity.Notice;
import com.library.bookarte.board.entity.type.BoardType;
import com.library.bookarte.board.repository.BoardRepository;
import com.library.bookarte.global.exception.CustomErrorCode;
import com.library.bookarte.global.exception.CustomException;
import com.library.bookarte.global.response.PageResponse;
import com.library.bookarte.member.entity.Member;
import com.library.bookarte.member.entity.type.MemberType;
import com.library.bookarte.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = CustomException.class)
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    public BoardSaveResponse save(String type, BoardSaveRequest request, Long memberId) {
        Member member = validateAndGetMember(memberId);
        BoardType boardType = getBoardType(type);

        Board board = createBoard(boardType, request, member);

        Board resultBoard = boardRepository.save(board);

        return BoardSaveResponse.builder()
                .id(resultBoard.getBoardId())
                .build();
    }

    public BoardUpdateResponse updateBoard(String type, Long memberId, Long boardId, BoardUpdateRequest request) {
        Member member = validateAndGetMember(memberId);
        BoardType boardType = getBoardType(type);
        Board board = getBoardData(boardId);

        validateBoardType(board, boardType);

        if (board instanceof Notice notice) {
            notice.modify(request, member);
        } else if (board instanceof News news) {
            news.modify(request, member);
        }

        return BoardUpdateResponse.builder()
                .id(board.getBoardId())
                .build();
    }

    public void deleteBoard(String type, Long memberId, BoardDelsRequest boardDelsRequest) {
        Member member = validateAndGetMember(memberId);
        BoardType boardType = getBoardType(type);
        List<Long> boardIds = boardDelsRequest.getBoardIds();

        List<Board> targets = boardRepository.findAllById(boardIds);

        if (targets.size() != boardIds.size()) {
            throw new CustomException(CustomErrorCode.BOARD_NOT_FOUND);
        }

        for (Board board : targets) {
            validateBoardType(board, boardType);
        }

        boardRepository.deleteAllInBatch(targets);
    }

    @Transactional(readOnly = true)
    public BoardResponse getBoard(Long boardId) {
        Board board = getBoardData(boardId);

        return BoardResponse.builder()
                .id(board.getBoardId())
                .category(board.getCategory())
                .title(board.getTitle())
                .contents(board.getContents())
                .noticeYn(board.getNoticeYn())
                .orderNum(board.getOrderNum())
                .regMemberUserId(board.getRegMember().getMemberUserId())
                .modMemberUserId((board.getModMember() != null) ? board.getModMember().getMemberUserId() : null)
                .createdAt(board.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<BoardResponse> getBoardList(String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Order.desc("noticeYn"),
                        Sort.Order.desc("orderNum"),
                        Sort.Order.desc("boardId")));

        BoardType boardType = getBoardType(type);

        Page<Board> boardPage = boardRepository.findAllByType(boardType.getEntityClass(), pageable);

        Page<BoardResponse> responsePage = boardPage.map(BoardResponse::from);

        return PageResponse.from(responsePage);

    }

    private Member validateAndGetMember(Long memberId) {
        if (memberId == null) throw new CustomException(CustomErrorCode.MEMBER_NOT_FOUND);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.MEMBER_NOT_FOUND));

        if (!MemberType.Constants.ROLE_ADMIN.equals(member.getMemberRole())) {
            throw new CustomException(CustomErrorCode.MEMBER_NOT_ADMIN);
        }

        return member;
    }

    private void validateBoardType(Board board, BoardType boardType) {
        boolean isValid = switch (boardType) {
            case NOTICE -> board instanceof Notice;
            case NEWS -> board instanceof News;
        };

        if (!isValid) {
            throw new CustomException(CustomErrorCode.INVALID_BOARD_TYPE);
        }
    }

    private BoardType getBoardType(String type) {
        try {
            return BoardType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new CustomException(CustomErrorCode.INVALID_BOARD_TYPE);
        }
    }

    private Board getBoardData(Long boardId) {
        if (boardId == null) throw new CustomException(CustomErrorCode.BOARD_NOT_FOUND);

        return boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.BOARD_NOT_FOUND));
    }

    private Board createBoard(BoardType boardType, BoardSaveRequest request, Member member) {

        return switch (boardType) {
            case NOTICE -> Notice.builder()
                    .category(request.getCategory())
                    .title(request.getTitle())
                    .contents(request.getContents())
                    .noticeYn(request.getNoticeYn())
                    .orderNum(request.getOrderNum())
                    .regMember(member)
                    .build();
            case NEWS -> News.builder()
                    .category(request.getCategory())
                    .title(request.getTitle())
                    .contents(request.getContents())
                    .noticeYn(request.getNoticeYn())
                    .orderNum(request.getOrderNum())
                    .regMember(member)
                    .build();
        };

    }

}
