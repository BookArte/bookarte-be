package com.library.bookarte.board.service;

import com.library.bookarte.board.dto.request.BoardDelsRequest;
import com.library.bookarte.board.dto.request.BoardListRequest;
import com.library.bookarte.board.dto.request.BoardSaveRequest;
import com.library.bookarte.board.dto.request.BoardUpdateRequest;
import com.library.bookarte.board.dto.response.BoardResponse;
import com.library.bookarte.board.dto.response.BoardSaveResponse;
import com.library.bookarte.board.dto.response.BoardUpdateResponse;
import com.library.bookarte.board.entity.*;
import com.library.bookarte.board.entity.type.BoardType;
import com.library.bookarte.board.repository.BoardRepository;
import com.library.bookarte.global.entity.UploadFile;
import com.library.bookarte.global.entity.type.FileType;
import com.library.bookarte.global.exception.CustomErrorCode;
import com.library.bookarte.global.exception.CustomException;
import com.library.bookarte.global.response.PageResponse;
import com.library.bookarte.global.util.S3Service;
import com.library.bookarte.global.util.XssUtils;
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
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = CustomException.class)
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final S3Service s3Service;
    private final XssUtils xssUtils;

    public BoardSaveResponse save(String type, BoardSaveRequest request, Long memberId) {
        Member member = validateAndGetMember(memberId);
        BoardType boardType = getBoardType(type);

        sanitizeRequest(request);

        Board board = createBoard(boardType, request, member);
        Board resultBoard = boardRepository.save(board);

        Long refId = resultBoard.getBoardId();

        handleFileUpload(refId, boardType.getValue(), request);

        return BoardSaveResponse.builder()
                .id(refId)
                .build();
    }

    public BoardUpdateResponse updateBoard(String type, Long memberId, Long boardId, BoardUpdateRequest request) {
        Member member = validateAndGetMember(memberId);
        BoardType boardType = getBoardType(type);
        Board board = getBoardData(boardId);

        validateBoardType(board, boardType);

        sanitizeRequest(request);

        updateBoardContent(board, request, member);

        handleFileUpdate(boardId, boardType.getValue(), request);

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

            s3Service.deleteAllFilesByRef(board.getBoardId(), boardType.getValue());
        }

        boardRepository.deleteAllInBatch(targets);
    }

    @Transactional(readOnly = true)
    public BoardResponse getBoard(Long boardId, String type) {
        Board board = getBoardData(boardId);

        List<UploadFile> files = s3Service.getAllFileList(boardId, type);

        return BoardResponse.from(board, files);
    }

    @Transactional(readOnly = true)
    public PageResponse<BoardResponse> getBoardList(String type, BoardListRequest boardListRequest) {
        Pageable pageable = PageRequest.of(boardListRequest.getPage(), boardListRequest.getSize(),
                Sort.by(Sort.Order.desc("noticeYn"),
                        Sort.Order.desc("orderNum"),
                        Sort.Order.desc("boardId")));

        BoardType boardType = getBoardType(type);

        String searchText = (boardListRequest.getSearchText() != null && !boardListRequest.getSearchText().isEmpty())
                ? boardListRequest.getSearchText() : null;

        LocalDateTime startDateTime = (boardListRequest.getSearchStartDate() != null)
                ? boardListRequest.getSearchStartDate().atStartOfDay() : null;
        LocalDateTime endDateTime = (boardListRequest.getSearchEndDate() != null)
                ? boardListRequest.getSearchEndDate().atTime(23, 59, 59) : null;

        Page<Board> boardPage = boardRepository.findAllByTypeAndSearch(
                boardType.getEntityClass(),
                searchText,
                startDateTime,
                endDateTime,
                pageable
        );

        List<Long> boardIds = boardPage.getContent().stream()
                .map(Board::getBoardId)
                .collect(Collectors.toList());

        final Map<Long, UploadFile> thumbnailMap = new HashMap<>();
        if (!boardIds.isEmpty()) {
            List<UploadFile> thumbnails = s3Service.getThumbnailList(boardIds, type);
            thumbnails.forEach(file -> thumbnailMap.put(file.getRefId(), file));
        }

        return PageResponse.from(boardPage.map(board -> {
            UploadFile thumbnail = thumbnailMap.get(board.getBoardId());
            return BoardResponse.from(board, thumbnail);
        }));

    }

    @Transactional(readOnly = true)
    public PageResponse<BoardResponse> getMyBoardList(String type, BoardListRequest boardListRequest, Long memberId) {
        Pageable pageable = PageRequest.of(boardListRequest.getPage(), boardListRequest.getSize(),
                Sort.by(Sort.Order.desc("noticeYn"),
                        Sort.Order.desc("orderNum"),
                        Sort.Order.desc("boardId")));

        BoardType boardType = getBoardType(type);

        Page<Board> boardPage = boardRepository.findAllByTypeAndMember(
                boardType.getEntityClass(),
                memberId,
                pageable
        );

        List<Long> boardIds = boardPage.getContent().stream()
                .map(Board::getBoardId)
                .collect(Collectors.toList());

        final Map<Long, UploadFile> thumbnailMap = new HashMap<>();
        if (!boardIds.isEmpty()) {
            List<UploadFile> thumbnails = s3Service.getThumbnailList(boardIds, type);
            thumbnails.forEach(file -> thumbnailMap.put(file.getRefId(), file));
        }

        return PageResponse.from(boardPage.map(board -> {
            UploadFile thumbnail = thumbnailMap.get(board.getBoardId());
            return BoardResponse.from(board, thumbnail);
        }));

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
            case FAQ -> board instanceof Faq;
            case QNA -> board instanceof Qna;
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
                    .contents(request.getEditor())
                    .noticeYn(request.getNoticeYn())
                    .orderNum(request.getOrderNum())
                    .regMember(member)
                    .build();
            case NEWS -> News.builder()
                    .category(request.getCategory())
                    .title(request.getTitle())
                    .contents(request.getEditor())
                    .noticeYn(request.getNoticeYn())
                    .orderNum(request.getOrderNum())
                    .regMember(member)
                    .build();
            case FAQ -> Faq.builder()
                    .category(request.getCategory())
                    .title(request.getTitle())
                    .contents(request.getEditor())
                    .noticeYn(request.getNoticeYn())
                    .orderNum(request.getOrderNum())
                    .regMember(member)
                    .build();
            case QNA -> Qna.builder()
                    .category(request.getCategory())
                    .title(request.getTitle())
                    .contents(request.getContents())
                    .regMember(member)
                    .build();
        };

    }

    private void updateBoardContent(Board board, BoardUpdateRequest request, Member member) {
        if (board instanceof Notice notice) {
            notice.modify(request, member);
        } else if (board instanceof News news) {
            news.modify(request, member);
        } else if (board instanceof Faq faq) {
            faq.modify(request, member);
        } else if (board instanceof Qna qna) {
            qna.modify(request, member);
        }
    }

    private void sanitizeRequest(BoardSaveRequest request) {
        request.setEditor(xssUtils.filterEditor(request.getEditor()));
        request.setTitle(xssUtils.escapeText(request.getTitle()));
    }

    private void sanitizeRequest(BoardUpdateRequest request) {
        request.setEditor(xssUtils.filterEditor(request.getEditor()));
        request.setTitle(xssUtils.escapeText(request.getTitle()));
    }

    private void handleFileUpload(Long refId, String refType, BoardSaveRequest request) {
        uploadThumbnail(refId, refType, request.getThumbnailFile());
        uploadFiles(refId, refType, request.getFiles());
    }

    private void handleFileUpdate(Long refId, String refType, BoardUpdateRequest request) {
        if (request.getDeletedFileIds() != null && !request.getDeletedFileIds().isEmpty()) {
            request.getDeletedFileIds().forEach(s3Service::deleteFile);
        }

        if (request.getThumbnailFile() != null && !request.getThumbnailFile().isEmpty()) {
            s3Service.deleteOldThumbnail(refId, refType);
            uploadThumbnail(refId, refType, request.getThumbnailFile());
        }

        uploadFiles(refId, refType, request.getFiles());
    }

    private void uploadThumbnail(Long refId, String refType, MultipartFile thumbnailFile) {
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            s3Service.uploadAndSave(refId, refType, thumbnailFile, FileType.THUMBNAIL);
        }
    }

    private void uploadFiles(Long refId, String refType, List<MultipartFile> files) {
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                s3Service.uploadAndSave(refId, refType, file, FileType.FILE);
            }
        }
    }

}
