package com.library.bookarte.book.repository;

import com.library.bookarte.book.dto.response.BookResDto;
import com.library.bookarte.book.dto.SearchFilterDto;
import com.library.bookarte.book.entity.Book;
import com.library.bookarte.book.entity.type.ParticipantType;
import com.library.bookarte.book.service.SearchCacheService;
import com.library.bookarte.book.utils.BookParticipantUtils;
import com.library.bookarte.borrow.entity.type.Status;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.library.bookarte.book.entity.QBook.book;
import static com.library.bookarte.book.entity.QBook_Participant.participant;
import static com.library.bookarte.category.entity.QCategory.category;
import static com.library.bookarte.borrow.entity.QBorrow.borrow;
import static com.library.bookarte.wish.entity.QWish.wish;

@RequiredArgsConstructor
@Repository
@Slf4j
public class BookRepositoryCustomImpl implements BookRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final SearchCacheService searchCacheService;

    @Override
    public Page<BookResDto> findBooks(SearchFilterDto searchFilterDto, Pageable pageable){

/*        long startFetch = System.currentTimeMillis();*/

        //파라미터
        String categoryName = searchFilterDto.getCategory();
        String bookTitle = searchFilterDto.getBookTitle();
        String bookIsbn = searchFilterDto.getBookIsbn();
        String publisherName = searchFilterDto.getPublisherName();
        String author = searchFilterDto.getBookAuthor();
        LocalDate start = searchFilterDto.getPublicationDateStart();
        LocalDate end = searchFilterDto.getPublicationDateEnd();
        LocalDate createAtStart = searchFilterDto.getCreatedAtStart();
        LocalDate createAtEnd = searchFilterDto.getCreatedAtEnd();

        //조건 메서드들 분리
        BooleanExpression[] predicates = {
                categoryNameEq(categoryName),
                titleContains(bookTitle),
                isbnContains(bookIsbn),
                publisherContains(publisherName),
                authorContains(author),
                publicationDateBetween(start,end),
                createAtBetween(createAtStart,createAtEnd),
                notDeletedBook()
        };

        //도서 id만 선 조회
        List<Long> ids = jpaQueryFactory
                .select(book.bookId)
                .from(book)
                .join(book.category, category)
                .where(predicates)
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

/*        long afterIds = System.currentTimeMillis();
        System.out.println("Step 1 (ID 조회) 소요 시간: " + (afterIds - startFetch) + "ms");*/

        if (ids.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        // 조회된 id들에 해당하는 데이터만 Fetch join으로 조회
        List<Book> books = jpaQueryFactory
                .selectFrom(book)
                .join(book.category, category).fetchJoin()
                .leftJoin(book.participants).fetchJoin()
                .where(book.bookId.in(ids).and(notDeletedBook()))
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .fetch();

        List<BookResDto> content = books.stream()
                .map(Book::toBookResDto)
                .collect(Collectors.toList());

/*        long afterFetch = System.currentTimeMillis();
        System.out.println("Step 2 (Fetch Join) 소요 시간: " + (afterFetch - afterIds) + "ms");*/


        // 전체 카운트 조회
        long total = jpaQueryFactory
                .select(book.count())
                .from(book)
                .where(predicates)
                .fetchOne();

/*        System.out.println("Step 3 (Count 조회) 소요 시간: " + (System.currentTimeMillis() - afterFetch) + "ms");*/

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<BookResDto> findBooksWithFTS(SearchFilterDto searchFilterDto, Pageable pageable){

/*        long startFetch = System.currentTimeMillis();*/

        //파라미터
        String categoryName = searchFilterDto.getCategory();
        String bookTitle = searchFilterDto.getBookTitle();
        String bookIsbn = searchFilterDto.getBookIsbn();
        String publisherName = searchFilterDto.getPublisherName();
        String author = searchFilterDto.getBookAuthor();
        LocalDate start = searchFilterDto.getPublicationDateStart();
        LocalDate end = searchFilterDto.getPublicationDateEnd();
        LocalDate createAtStart = searchFilterDto.getCreatedAtStart();
        LocalDate createAtEnd = searchFilterDto.getCreatedAtEnd();

        //조건 메서드들 분리
        BooleanExpression[] predicates = {
                categoryNameEq(categoryName),
                titleFullText(bookTitle),
                isbnContains(bookIsbn),
                publisherContains(publisherName),
                authorFullText(author),
                publicationDateBetween(start,end),
                createAtBetween(createAtStart,createAtEnd),
                notDeletedBook()
        };

        //도서 id만 선 조회
        List<Long> ids = jpaQueryFactory
                .select(book.bookId)
                .from(book)
                .join(book.category, category)
                .where(predicates)
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

/*        long afterIds = System.currentTimeMillis();
        System.out.println("Step 1 (ID 조회) 소요 시간: " + (afterIds - startFetch) + "ms");*/

        if (ids.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }


        // 조회된 id들에 해당하는 데이터만 Fetch join으로 조회
        List<Book> books = jpaQueryFactory
                .selectFrom(book)
                .join(book.category, category).fetchJoin()
                .leftJoin(book.participants).fetchJoin()
                .where(book.bookId.in(ids).and(notDeletedBook()))
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .fetch();

        List<BookResDto> content = books.stream()
                .map(Book::toBookResDto)
                .collect(Collectors.toList());

/*        long afterFetch = System.currentTimeMillis();
        System.out.println("Step 2 (Fetch Join) 소요 시간: " + (afterFetch - afterIds) + "ms");*/

        String filterHash = generateFilterHash(searchFilterDto);

        long total = searchCacheService.getCachedTotalCount(
                filterHash,
                5,
                () -> {
                    return (long) jpaQueryFactory
                            .select(book.bookId)
                            .from(book)
                            .where(predicates)
                            .limit(10000)
                            .fetch().size();
                }
        );

/*        System.out.println("Step 3 (Count 조회) 소요 시간: " + (System.currentTimeMillis() - afterFetch) + "ms");*/


        // 아래 상황일 때 카운트 쿼리 x
        // - 첫 페이지이면서 콘텐츠가 pageSize보다 작을 때 (전체 개수를 안 세어도 됨)
        // - 마지막 페이지일 때 (offset + content size로 계산 가능)
        return PageableExecutionUtils.getPage(content, pageable, () -> total);
    }


    @Override
    public  List<Book> findBooksAlsoBorrowed(Long bookId, Set<Long> excludeIds){
        List<Long> userIds = jpaQueryFactory
                .select(borrow.member.memberId)
                .from(borrow)
                .where(borrow.book.bookId.eq(bookId))
                .fetch();

        if(userIds.isEmpty()) return new ArrayList<>();

        return jpaQueryFactory
                .select(borrow.book)
                .from(borrow)
                .where(
                        borrow.member.memberId.in(userIds),
                        borrow.book.bookId.notIn(excludeIds),
                        borrow.book.deletedAt.isNull()
                )
                .groupBy(borrow.book.bookId)
                .orderBy(borrow.count().desc())
                .limit(5)
                .fetch();
    }

    //같은 저자 대출수 순 조회
    @Override
    public List<Book> findBooksByAuthorOrderByBorrowCount(String authorName, Set<Long> excludeIds, int limit) {
        return jpaQueryFactory
                .select(book)
                .from(book)
                .leftJoin(borrow).on(borrow.book.eq(book))
                .join(book.participants, participant)
                .where(
                        authorContains(authorName),
                        book.bookId.notIn(excludeIds),
                        notDeletedBook()
                )
                .groupBy(book.bookId)
                .orderBy(borrow.count().desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<String> skippedTitles(List<Long> bookIds){
        return jpaQueryFactory
                .select(book.bookTitle)
                .from(book)
                .where(book.bookId.in(bookIds).and(notDeletedBook()).and(notDeletableBook()))
                .fetch();
    }

    @Override
    public List<Long> deletableBookIds(List<Long> bookIds){
        return jpaQueryFactory
                .select(book.bookId)
                .from(book)
                .where(book.bookId.in(bookIds).and(notDeletedBook()).and(notDeletableBook().not()))
                .fetch();
    }

    @Override
    public long softDeleteBooksByIds(List<Long> bookIds){
        return jpaQueryFactory
                .update(book)
                .set(book.deletedAt, LocalDateTime.now())
                .where(book.bookId.in(bookIds).and(notDeletedBook()))
                .execute();
    }

    private BooleanExpression notDeletableBook() {
        return book.canBorrow.isFalse().or(activeBorrowExists());
    }

    private BooleanExpression activeBorrowExists() {
        return JPAExpressions
                .selectOne()
                .from(borrow)
                .where(
                        borrow.book.eq(book),
                        borrow.status.in(Status.BORROWED, Status.OVERDUE, Status.RETURN_REQUESTED)
                )
                .exists();
    }

    //같은 카테고리 대출수 순 조회
    @Override
    public List<Book> findBooksByCategoryOrderByBorrowCount(String category, Set<Long> excludeIds, int limit) {
        return jpaQueryFactory
                .select(book)
                .from(book)
                .leftJoin(borrow).on(borrow.book.eq(book))
                .where(
                        categoryNameEq(category),
                        book.bookId.notIn(excludeIds),
                        notDeletedBook()
                )
                .groupBy(book.bookId)
                .orderBy(borrow.count().desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public Optional<BookResDto> findBookDetailWithWish(Long bookId, Long memberId){
        Book result = jpaQueryFactory
                .selectFrom(book)
                .leftJoin(book.participants).fetchJoin()
                .leftJoin(book.category).fetchJoin()
                .where(book.bookId.eq(bookId).and(notDeletedBook()))
                .fetchOne();

        if (result == null) {
            return Optional.empty();
        }

        boolean isWish = checkWishStatus(bookId, memberId);

        String authors = BookParticipantUtils.extractAuthors(result.getParticipants());
        String translators = BookParticipantUtils.extractTranslators(result.getParticipants());

        return Optional.of(
                BookResDto.builder()
                        .bookId(result.getBookId())
                        .bookTitle(result.getBookTitle())
                        .bookAuthor(authors)
                        .bookTranslator(translators)
                        .publisherName(result.getPublisherName())
                        .publicationDate(result.getPublicationDate())
                        .bookIsbn(result.getBookIsbn())
                        .bookContents(result.getBookContents())
                        .bookThumbnail(result.getBookThumbnail())
                        .bookCallNumber(result.getBookCallNumber())
                        .bookCategory(result.getCategory().getCategoryName())
                        .canBorrow(result.isCanBorrow())
                        .isWish(isWish)
                .build()
        );
    }

    // ===== 조건 메서드 =====

    //카테고리 조건 메서드
    private BooleanExpression categoryNameEq(String categoryName) {
        return StringUtils.hasText(categoryName)
                ? book.category.categoryName.eq(categoryName)
                : null;
    }
    //도서 제목 조건 메서드
    private BooleanExpression titleContains(String bookTitle) {
        return StringUtils.hasText(bookTitle)
                ? book.bookTitle.contains(bookTitle)
                : null;
    }

    //isbn 조건 메서드
    private BooleanExpression isbnContains(String isbn) {
        return isbn != null
                ? book.bookIsbn.contains(isbn)
                : null;
    }

    //출판사 조건 메서드
    private BooleanExpression publisherContains(String publisherName) {
        return StringUtils.hasText(publisherName)
                ? book.publisherName.contains(publisherName)
                : null;
    }

    //저자 조건 메서드
    private BooleanExpression authorContains(String author){
        if(!StringUtils.hasText(author)) {
            return null;
        }
        return book.participants.any().name.contains(author)
                .and(book.participants.any().type.eq(ParticipantType.AUTHOR));
    }

    //특정 날짜 이후 출판일 조건 메서드 (publicationDate >= start)
    private BooleanExpression publicationDateGoe(LocalDate start) {
        return start != null ? book.publicationDate.goe(start) : null;
    }

    //특정 날짜 이전 출판일 조건 메서드 (publicationDate <= start)
    private BooleanExpression publicationDateLoe(LocalDate end) {
        return end != null ? book.publicationDate.loe(end) : null;
    }

    private BooleanExpression publicationDateBetween(LocalDate start, LocalDate end ){
        BooleanExpression isGoe = publicationDateGoe(start);
        BooleanExpression isLoe = publicationDateLoe(end);

        if (isGoe != null && isLoe != null) return isGoe.and(isLoe);
        if (isGoe != null) return isGoe;
        if (isLoe != null) return isLoe;

        return null;
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        sort.forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            String property = order.getProperty();

            switch (property) {
                case "bookTitle":
                    orders.add(new OrderSpecifier<>(direction, book.bookTitle));
                    break;
                case "bookAuthor":
                    // 저자가 다수가 있을 경우 첫번째 저자를 대표 저자로 하여 정렬
                    orders.add(new OrderSpecifier<>(direction, book.participants.any().name));
                    break;
                case "publicationDate":
                    orders.add(new OrderSpecifier<>(direction, book.publicationDate));
                    break;
                case "createdAt":
                    orders.add(new OrderSpecifier<>(direction, book.createdAt));
                    break;
                default:
                    // 기본 정렬값 (최신 등록순)
                    orders.add(new OrderSpecifier<>(Order.DESC, book.createdAt));
                    break;
            }
        });

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression createAtBetween(LocalDate start, LocalDate end){
        if(start == null || end == null) return null;
        return book.createdAt.between(start.atStartOfDay(), end.atTime(LocalTime.MAX));
    }

    private BooleanExpression notDeletedBook() {
        return book.deletedAt.isNull();
    }

    private boolean checkWishStatus(Long bookId, Long memberId) {
        if (memberId == null) return false;

        Integer fetchWish = jpaQueryFactory
                .selectOne()
                .from(wish)
                .where(wish.book.bookId.eq(bookId)
                        .and(wish.member.memberId.eq(memberId)))
                .fetchFirst();

        return fetchWish != null;
    }

    //조건 메서드 고도화 (Full-Text Search 적용)

    //제목 조건 메서드
    private BooleanExpression titleFullText(String bookTitle){
        if(!StringUtils.hasText(bookTitle)) return null;

        String formattedTitle = Arrays.stream(bookTitle.split(" "))
                .filter(word -> word.length() > 1)
                .map(word -> "+" + word)
                .collect(Collectors.joining(" "));

        return Expressions.numberTemplate(Double.class,
                "function('match_against', {0}, {1})",
                book.bookTitle, formattedTitle).gt(0);
    }

    //저자 조건 메서드
    private BooleanExpression authorFullText(String author) {
        if (!StringUtils.hasText(author)) return null;

        return book.participants.any().type.eq(ParticipantType.AUTHOR)
                .and(book.participants.any().name.contains(author));
    }

    private String generateFilterHash(SearchFilterDto searchFilterDto){

        String bookTitle =  searchFilterDto.getBookTitle();
        String category = searchFilterDto.getCategory();
        String bookIsbn = searchFilterDto.getBookIsbn();
        String pulisherName = searchFilterDto.getPublisherName();
        String bookAuthor = searchFilterDto.getBookAuthor();

        LocalDate publicationDateStart = searchFilterDto.getPublicationDateStart();
        LocalDate publicationDateEnd = searchFilterDto.getPublicationDateEnd();
        LocalDate createdAtStart = searchFilterDto.getCreatedAtStart();
        LocalDate createdAtEnd = searchFilterDto.getCreatedAtEnd();

        return DigestUtils.md5DigestAsHex((bookTitle +
                        category +
                        bookIsbn +
                        pulisherName +
                        bookAuthor +
                        publicationDateStart +
                        publicationDateEnd +
                        createdAtStart +
                        createdAtEnd).getBytes());
    }
}
