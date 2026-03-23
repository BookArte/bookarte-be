package com.library.bookarte.support;

import com.library.bookarte.book.entity.Book;
import com.library.bookarte.category.entity.Category;
import com.library.bookarte.member.entity.Member;

import java.time.LocalDate;

public class FixtureFactory {

    public static Member createMember(String userId){
        return Member.builder()
                .memberUserId(userId)
                .memberName("테스트유저")
                .memberTel("010-1234-5678")
                .memberEmail(userId + "@test.com")
                .memberSocialType("SOCIAL01")
                .memberRole("ROLE01")
                .useServiceYn("Y")
                .usePrivacyYn("Y")
                .memberStatus("STATUS01")
                .memberPoint(0L)
                .build();
    }

    public static Book createBook(String title, Category category){
        return Book.builder()
                .bookTitle(title)
                .publisherName("테스트출판사")
                .publicationDate(LocalDate.now())
                .bookIsbn("1234567890")
                .bookContents("테스트 내용입니다")
                .canBorrow(true)
                .bookThumbnail("https://test.com/img.jpg")
                .category(category)
                .build();
    }
}
