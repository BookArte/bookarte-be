package com.library.bookarte.borrow.repository;

import com.library.bookarte.borrow.entity.Borrow;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.library.bookarte.book.entity.QBook.book;
import static com.library.bookarte.borrow.entity.QBorrow.borrow;
import static com.library.bookarte.member.entity.QMember.member;

@RequiredArgsConstructor
@Repository
public class BorrowRepositoryCustomImpl implements BorrowRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Borrow> findAllBorrowByBorrowSearchFilter(Pageable pageable){

        List<Borrow> content = jpaQueryFactory
                .selectFrom(borrow)
                .join(borrow.member, member).fetchJoin()
                .join(borrow.book, book).fetchJoin()
                .orderBy(borrow.borrowId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory
                .select(borrow.count())
                .from(borrow)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

}
