package com.library.bookarte.board.service;

import com.library.bookarte.board.dto.request.BoardSaveRequest;
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

    public void save(String type, BoardSaveRequest request, Long memberId) {
        if (memberId == null) {
            throw new CustomException(CustomErrorCode.MEMBER_NOT_FOUND);
        }

        BoardType boardType;
        try {
            boardType = BoardType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(CustomErrorCode.INVALID_BOARD_TYPE);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.MEMBER_NOT_FOUND));

        if (!MemberType.Constants.ROLE_ADMIN.equals(member.getMemberRole())) {
            throw new CustomException(CustomErrorCode.MEMBER_NOT_ADMIN);
        }

        Board board = switch (boardType) {
            case NOTICE -> Notice.builder()
                    .category("TEST")
                    .title(request.getTitle())
                    .contents(request.getContents())
                    .noticeYn(request.getNoticeYn())
                    .orderNum(request.getOrderNum())
                    .member(member)
                    .build();
            case NEWS -> News.builder()
                    .category("TEST")
                    .title(request.getTitle())
                    .contents(request.getContents())
                    .noticeYn(request.getNoticeYn())
                    .orderNum(request.getOrderNum())
                    .member(member)
                    .build();
        };

        boardRepository.save(board);
    }

}
