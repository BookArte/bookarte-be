package com.library.bookarte.borrow.service;

import com.library.bookarte.book.entity.Book;
import com.library.bookarte.book.service.BookService;
import com.library.bookarte.borrow.entity.Borrow;
import com.library.bookarte.borrow.entity.type.Status;
import com.library.bookarte.borrow.repository.BorrowRepository;
import com.library.bookarte.global.exception.CustomErrorCode;
import com.library.bookarte.global.exception.CustomException;
import com.library.bookarte.member.entity.Member;
import com.library.bookarte.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
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
    private final BookService bookService;

    public void borrowBook(long bookId){
        long memberId = Integer.parseInt(SecurityContextHolder.getContext().getAuthentication().getName());
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.MEMBER_NOT_FOUND));
        Book book = bookService.findBook(bookId);

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

}
