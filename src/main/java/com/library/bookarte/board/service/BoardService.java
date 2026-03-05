package com.library.bookarte.board.service;

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
import com.library.bookarte.member.entity.Member;
import com.library.bookarte.member.entity.type.MemberType;
import com.library.bookarte.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        validateTypeAndModify(board, boardType, request, member);

        return BoardUpdateResponse.builder()
                .id(board.getBoardId())
                .build();
    }

    public BoardResponse getBoard(Long boardId) {
        Board board = getBoardData(boardId);

        return BoardResponse.builder()
                .id(board.getBoardId())
                .category(board.getCategory())
                .title(board.getTitle())
                .contents(board.getContents())
                .noticeYn(board.getNoticeYn())
                .orderNum(board.getOrderNum())
                .regMemberId(board.getRegMember().getMemberId())
                .modMemberId((board.getModMember() != null) ? board.getModMember().getMemberId() : null)
                .createDate(board.getCreatedAt())
                .build();
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

    private void validateTypeAndModify(Board board, BoardType boardType, BoardUpdateRequest request, Member member) {
        if (boardType == BoardType.NOTICE && board instanceof Notice notice) {
            notice.modify(request, member);
        } else if (boardType == BoardType.NEWS && board instanceof News news) {
            news.modify(request, member);
        } else {
            throw new CustomException(CustomErrorCode.INVALID_BOARD_TYPE);
        }
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
