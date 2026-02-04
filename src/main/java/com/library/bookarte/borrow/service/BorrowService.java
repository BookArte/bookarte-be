package com.library.bookarte.borrow.service;

import com.library.bookarte.book.entity.Book;
import com.library.bookarte.book.repository.BookRepository;
import com.library.bookarte.borrow.dto.BorrowSearchFilterDto;
import com.library.bookarte.borrow.dto.response.TotalBorrowResDto;
import com.library.bookarte.borrow.dto.response.UserBorrowResDto;
import com.library.bookarte.borrow.entity.Borrow;
import com.library.bookarte.borrow.entity.type.Status;
import com.library.bookarte.borrow.repository.BorrowRepository;
import com.library.bookarte.global.exception.CustomErrorCode;
import com.library.bookarte.global.exception.CustomException;
import com.library.bookarte.member.entity.Member;
import com.library.bookarte.member.repository.MemberRepository;
import com.library.bookarte.penalty.repository.PenaltyRepository;
import com.library.bookarte.penalty.service.PenaltyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(rollbackFor = CustomException.class)
public class BorrowService {

    private final BorrowRepository borrowRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final PenaltyRepository penaltyRepository;
    private final PenaltyService penaltyService;

    //도서 대출 등록
    public void borrowBook(Long bookId){
        Long memberId = Long.parseLong(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName());

        //패널티 여부 존재 시 대출 불가
        if (penaltyRepository.existsByMember_MemberIdAndEndDateAfterAndIsReleasedFalse(memberId, LocalDate.now())) {
            throw new CustomException(CustomErrorCode.USER_BORROW_RESTRICTED);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.MEMBER_NOT_FOUND));

        /**
         * 1. 비관적 릭을 적용하여 도서 조회
         * 2. 해당 도서에 대해서 다른 트랜잭션은 이 도서 정보를 수정할 수 없음
         * 3. 트랜잭션이 종료되면 자물쇠가 해제
         */

        Book book = bookRepository.findByIdWithLock(bookId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.BOOK_NOT_FOUND));

        //도서 대출 가능 상태 확인
        if(!book.isCanBorrow()){
            throw new CustomException(CustomErrorCode.BOOK_BORROW_FORBIDDEN);
        }

        Borrow borrow = Borrow.builder()
                .returnDate(null)
                .returnDueDate(LocalDate.now().plusDays(14))
                .canExtend(true)
                .isOverdue(false)
                .member(member)
                .book(book)
                .status(Status.BORROWED)
                .build();

        borrowRepository.save(borrow);

        book.updateCanBorrow(false);
    }
    //전체 대출 이력 조회
    public Page<TotalBorrowResDto> getTotalBorrows(BorrowSearchFilterDto borrowSearchFilterDto,
                                                   Pageable pageable){
        Page<Borrow> borrows = borrowRepository.findAllBorrowByBorrowSearchFilter(borrowSearchFilterDto,
                pageable);

        return borrows.map(Borrow::toTotalBorrowResDto);
    }

    //유저 대출 이력
    public Page<UserBorrowResDto> getUserBorrows(BorrowSearchFilterDto borrowSearchFilterDto,
                                                 Pageable pageable){
        Long memberId = Long.parseLong(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName());
        borrowSearchFilterDto.setMemberId(memberId);

        Page<Borrow> borrows = borrowRepository.findAllBorrowByBorrowSearchFilter(borrowSearchFilterDto, pageable);

        return borrows.map(Borrow::toUserBorrowResDto);
    }

    //도서 반납 신청
    public void requestReturnBook(Long borrowId){
        Long memberId = Long.parseLong(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName());

        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.BORROW_NOT_FOUND));

        Long borrowMemberId = borrow.getMember().getMemberId();
        Status borrowStatus = borrow.getStatus();

        if(!borrowMemberId.equals(memberId)){
            throw new CustomException(CustomErrorCode.NOT_YOUR_BORROW_RECORD);
        }

        if (borrowStatus != Status.BORROWED && borrowStatus != Status.OVERDUE) {
            throw new CustomException(CustomErrorCode.INVALID_RETURN_REQUEST);
        }

        Status status = Status.RETURN_REQUESTED;
        borrow.updateStatus(status);
    }

    //도서 반납 승인
    public void approveReturnBook(Long borrowId){
        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.BORROW_NOT_FOUND));

        Book book = bookRepository.findById(borrow.getBook().getBookId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.BOOK_NOT_FOUND));
        Status borrowStatus = borrow.getStatus();

        if(!borrowStatus.equals(Status.RETURN_REQUESTED)){
            throw new CustomException(CustomErrorCode.NOT_RETURN_REQUEST);
        }

        //도서 연체에 의한 패널티 부여
        int overdueDays = borrow.calculateOverdueDays();
        log.info(String.valueOf(overdueDays));
        if (overdueDays > 0) {
            penaltyService.createPenalty(borrow.getMember(),borrow, overdueDays);
        }

        Status status = Status.RETURNED;
        borrow.updateStatus(status);
        borrow.updateReturnDate(LocalDate.now());
        book.updateCanBorrow(true);
    }

    //도서 대출 연장
    public void extendReturnDate(Long borrowId){
        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.BORROW_NOT_FOUND));
        if(!borrow.isCanExtend()) throw new CustomException(CustomErrorCode.CAN_NOT_EXTEND);

        Status borrowStatus = borrow.getStatus();
        if(!borrowStatus.equals(Status.BORROWED)) throw new CustomException(CustomErrorCode.NOT_STATUS_BORROW);

        borrow.updateCanExtend(false);
        borrow.extendReturnDate(borrow.getReturnDueDate().plusDays(7));
    }

    //도서 연체 처리
    public void processOverdue(){
        LocalDate today = LocalDate.now();

        List<Borrow> overdueBorrows = borrowRepository
                .findAllByStatusInAndReturnDueDateBefore(List.of(Status.BORROWED, Status.RETURN_REQUESTED), today);

        overdueBorrows.forEach(borrow -> {
            borrow.updateOverdueStatus();
            borrow.updateOverdueDays(borrow.calculateOverdueDays());
        });
    }

}
