package com.library.bookarte.book.service;

import com.library.bookarte.book.dto.BookReqDto;
import com.library.bookarte.book.dto.BookResDto;
import com.library.bookarte.book.dto.SearchFilterDto;
import com.library.bookarte.book.entity.Book;
import com.library.bookarte.book.entity.type.ParticipantType;
import com.library.bookarte.book.external.dto.BookSearchResult;
import com.library.bookarte.book.external.kakao.KakaoBookSearchClient;
import com.library.bookarte.book.external.national.NationalLibrarySearchClient;
import com.library.bookarte.book.repository.BookRepository;
import com.library.bookarte.category.entity.Category;
import com.library.bookarte.category.service.CategoryService;
import com.library.bookarte.global.exception.CustomErrorCode;
import com.library.bookarte.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = CustomException.class)
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryService categoryService;

    private final KakaoBookSearchClient kakaoBookSearchClient;
    private final NationalLibrarySearchClient nationalLibrarySearchClient;


    /*도서 등록 api*/
    public void registerBook(BookReqDto bookReqDto){

        Category category = categoryService.findByCategoryName(bookReqDto.getBookCategory());

        Book book = Book.builder()
                .bookTitle(bookReqDto.getBookTitle())
                .publisherName(bookReqDto.getPublisherName())
                .publicationDate(bookReqDto.getPublicationDate())
                .bookIsbn(bookReqDto.getBookIsbn())
                .bookContents(bookReqDto.getBookContents())
                .bookBorrowYn('Y')
                .bookCallNumber(bookReqDto.getBookCallNumber())
                .bookThumbnail(bookReqDto.getBookThumbnail())
                .category(category)
                .build();

        //저자 정보 저장
        if (bookReqDto.getBookAuthor() != null) {
            String[] authors = bookReqDto.getBookAuthor().split(","); // 구분자에 맞게 설정
            for (String authorName : authors) {
                book.addParticipant(authorName, ParticipantType.AUTHOR);
            }
        }

        //역자 정보 저장
        if (bookReqDto.getBookTranslator() != null) {
            String[] translators = bookReqDto.getBookTranslator().split(",");
            for (String translatorName : translators) {
                book.addParticipant(translatorName, ParticipantType.TRANSLATOR);
            }
        }

        bookRepository.save(book);
    }

    /*도서 상세 조회 api*/
    @Transactional(readOnly = true)
    public BookResDto findBookById(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.BOOK_NOT_FOUND));

        return book.toBookResDto();
    }

    @Transactional(readOnly = true)
    public Book findBook(Long bookId) {

        return bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.BOOK_NOT_FOUND));
    }

    /*도서 수정 api*/
    public Long updateBook(Long bookId,BookReqDto bookReqDto){

        Book updateTargetBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.BOOK_NOT_FOUND));

        Category category = updateTargetBook.getCategory();

        if(bookReqDto.getBookCategory() != null){
            category = categoryService.findByCategoryName(bookReqDto.getBookCategory());
        }

        List<Book.Participant> updateParticipants;

        if(bookReqDto.getBookAuthor() != null || bookReqDto.getBookTranslator() != null) {
            List<Book.Participant> newList = new ArrayList<>(updateTargetBook.getParticipants());

            //저자 리스트 파싱
            if (bookReqDto.getBookAuthor() != null) {
                newList.removeIf(p -> p.getType() == ParticipantType.AUTHOR);
                Arrays.stream(bookReqDto.getBookAuthor().split(","))
                        .map(String::trim)
                        .filter(name -> !name.isEmpty())
                        .forEach(name -> newList.add(new Book.Participant(name, ParticipantType.AUTHOR)));
            }

            //역자 리스트 파싱
            if (bookReqDto.getBookTranslator() != null) {
                newList.removeIf(p -> p.getType() == ParticipantType.TRANSLATOR);
                Arrays.stream(bookReqDto.getBookTranslator().split(","))
                        .map(String::trim)
                        .filter(name -> !name.isEmpty())
                        .forEach(name -> newList.add(new Book.Participant(name, ParticipantType.TRANSLATOR)));
            }
            updateParticipants = newList;
        } else {
            updateParticipants = updateTargetBook.getParticipants();
        }

        updateTargetBook.updateBook(
                bookReqDto.getBookTitle(),
                bookReqDto.getPublisherName(),
                bookReqDto.getPublicationDate(),
                bookReqDto.getBookIsbn(),
                bookReqDto.getBookContents(),
                bookReqDto.getBookCallNumber(),
                bookReqDto.getBookThumbnail(),
                category,
                updateParticipants
        );

        return bookId;
    }

    /*도서 삭제 api*/
    public void deleteBook(Long bookId) {
        Book deleteTargetBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.BOOK_NOT_FOUND));

        bookRepository.delete(deleteTargetBook);
    }

    /*도서 전체 조회 api*/

    /*
    public Page<BookResDto> findAllBooks(Pageable pageable){
        int page = pageable.getPageNumber() - 1;

        Page<Book> books = bookRepository.findAll(PageRequest.of(page, defaultSize, Sort.Direction.DESC, "bookId"));

        List<BookResDto> bookResDtoList = books.getContent().stream()
                .map(Book::toBookResDto)
                .toList();

        return new PageImpl<>(bookResDtoList, pageable, books.getTotalElements());

    }

    /*도서 카테고리 별 조회*/
    /*
    public Page<BookResDto> findBooksWithCategory(String bookCategoryName, Pageable pageable) {
        int page = pageable.getPageNumber() - 1;

        System.out.println(bookCategoryName);
        Category category = categoryService.findByCategoryName(bookCategoryName);
        System.out.println(category.getCategoryId());

        return bookRepository.findBookResDtosByCategoryId(category.getCategoryId(),
                PageRequest.of(page,defaultSize,Sort.Direction.DESC,"bookId"));
    }*/

    /*도서 조건부 및 전체 조회 api*/
    @Transactional(readOnly = true)
    public Page<BookResDto> findBooksWithFilter(SearchFilterDto searchFilterDto,Pageable pageable){
        return bookRepository.findBooks(searchFilterDto, pageable);
    }

    /*카카오, 국립 중앙 도서관 api 호출*/
    public List<BookSearchResult> searchBooksWithApi(String query){

        List<BookSearchResult> kakaoBookList = kakaoBookSearchClient.search(query);
        String category = nationalLibrarySearchClient.fetchCategoryByTitle(query);

        return kakaoBookList.stream()
                .map(book -> BookSearchResult.builder()
                        .bookTitle(book.getBookTitle())
                        .bookAuthor(book.getBookAuthor())
                        .bookTranslator(book.getBookTranslator())
                        .bookContents(book.getBookContents())
                        .publisherName(book.getPublisherName())
                        .publicationDate(book.getPublicationDate())
                        .bookIsbn(book.getBookIsbn())
                        .bookThumbnail(book.getBookThumbnail())
                        .bookCategory(category)
                        .build()
                )
                .toList();
    }

    /* DB 내 이미 존재하는 도서인지 검색*/
    public boolean isDuplicateIsbn(String isbn){
        return bookRepository.existsByBookIsbn(isbn);
    }
}
