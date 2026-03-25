package com.library.bookarte.wish.service;

import com.library.bookarte.book.entity.Book;
import com.library.bookarte.book.repository.BookRepository;
import com.library.bookarte.global.exception.CustomErrorCode;
import com.library.bookarte.global.exception.CustomException;
import com.library.bookarte.member.entity.Member;
import com.library.bookarte.member.repository.MemberRepository;
import com.library.bookarte.wish.dto.response.WishResDto;
import com.library.bookarte.wish.entity.Wish;
import com.library.bookarte.wish.repository.WishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = CustomException.class)
public class WishService {
    private final WishRepository wishRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public void addWishBook(Long bookId,Long memberId){
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.BOOK_NOT_FOUND));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.MEMBER_NOT_FOUND));

        Wish wish = Wish.builder()
                .book(book)
                .member(member)
                .build();

        wishRepository.save(wish);
    }

    public Page<WishResDto> getWishList(Long memberId, Pageable pageable){
        Page<Wish> wishes = wishRepository.findByMember_MemberId(memberId, pageable);
        return wishes.map(Wish::toWishResDto);
    }

    public void deleteWish(Long memeberId ,Long bookId){
        wishRepository.deleteByMember_MemberIdAndBook_BookId(memeberId, bookId);
    }

}
