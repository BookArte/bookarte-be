package com.library.bookarte.borrow.service;

import com.library.bookarte.book.entity.Book;
import com.library.bookarte.book.repository.BookRepository;
import com.library.bookarte.book.service.BookService;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = CustomException.class)
public class BorrowService {

    private final BorrowRepository borrowRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;

    //도서 대출 등록
    public void borrowBook(long bookId){
        long memberId = Integer.parseInt(SecurityContextHolder.getContext().getAuthentication().getName());
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.MEMBER_NOT_FOUND));

        /**
         * 1. 비관적 릭을 적용하여 도서 조회
         * 2. 해당 도서에 대해서 다른 트랜잭션은 이 도서 정보를 수정할 수 없음
         * 3. 트랜잭션이 종료되면 자물쇠가 해제
         */

        Book book = bookRepository.findByIdWithLock(bookId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.BOOK_NOT_FOUND));

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
        long memberId = Integer.parseInt(SecurityContextHolder.getContext().getAuthentication().getName());
        borrowSearchFilterDto.setMemberId(memberId);

        Page<Borrow> borrows = borrowRepository.findAllBorrowByBorrowSearchFilter(borrowSearchFilterDto, pageable);

        return borrows.map(Borrow::toUserBorrowResDto);

    }

    //유저 대출 현황




}
