package com.library.bookarte.book.service;

import com.library.bookarte.book.dto.request.BookDelReqDto;
import com.library.bookarte.book.dto.request.BookReqDto;
import com.library.bookarte.book.dto.response.BookResDto;
import com.library.bookarte.book.dto.SearchFilterDto;
import com.library.bookarte.book.dto.response.BulkDeleteResponse;
import com.library.bookarte.book.entity.Book;
import com.library.bookarte.book.entity.type.ParticipantType;
import com.library.bookarte.book.external.aladin.AladinClient;
import com.library.bookarte.book.external.dto.AladinBestSellerResDto;
import com.library.bookarte.book.external.dto.BookSearchResult;
import com.library.bookarte.book.external.kakao.KakaoBookSearchClient;
import com.library.bookarte.book.external.national.NationalLibrarySearchClient;
import com.library.bookarte.book.repository.BookRepository;
import com.library.bookarte.category.entity.Category;
import com.library.bookarte.category.service.CategoryService;
import com.library.bookarte.global.entity.type.FileType;
import com.library.bookarte.global.exception.CustomErrorCode;
import com.library.bookarte.global.exception.CustomException;
import com.library.bookarte.global.util.S3Service;
import com.library.bookarte.global.util.XssUtils;
import com.library.bookarte.recommendation.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = CustomException.class)
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryService categoryService;
    private final RecommendationRepository recommendationRepository;

    private final KakaoBookSearchClient kakaoBookSearchClient;
    private final NationalLibrarySearchClient nationalLibrarySearchClient;
    private final AladinClient aladinClient;

    private final S3Service s3Service;
    private final XssUtils xssUtils;

    /*도서 등록 api*/
    public void registerBook(BookReqDto bookReqDto){

        Category category = categoryService.findByCategoryName(bookReqDto.getBookCategory());

        String  refType = "BOOK";

        sanitizeRequest(bookReqDto);

        Book book = Book.builder()
                .bookTitle(bookReqDto.getBookTitle())
                .publisherName(bookReqDto.getPublisherName())
                .publicationDate(bookReqDto.getPublicationDate())
                .bookIsbn(bookReqDto.getBookIsbn())
                .bookContents(bookReqDto.getBookContents())
                .canBorrow(true)
                .bookCallNumber(bookReqDto.getBookCallNumber())
                .bookThumbnail(bookReqDto.getBookThumbnail())
                .category(category)
                .build();

        addParticipants(book, bookReqDto);

        Book savedBook = bookRepository.save(book);

        MultipartFile bookThumbnailFile = bookReqDto.getBookThumbnailFile();
        if(bookThumbnailFile != null && !bookThumbnailFile.isEmpty()) {
            String uploadUrl = s3Service.uploadFile(bookThumbnailFile);
            s3Service.uploadAndSave(savedBook.getBookId(), refType,bookThumbnailFile, FileType.THUMBNAIL);
            savedBook.updateThumbnail(uploadUrl);
        }
    }

    /*도서 상세 조회 api*/
    @Transactional(readOnly = true)
    public BookResDto findBookWithWish(Long bookId,Long memberId) {
        return bookRepository.findBookDetailWithWish(bookId, memberId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.BOOK_NOT_FOUND));
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

        sanitizeRequest(bookReqDto);

        String updateThumbnailUrl = updateTargetBook.getBookThumbnail();

        Long refId = updateTargetBook.getBookId();
        String refType = "BOOK";

        MultipartFile newThumbnailFile = bookReqDto.getBookThumbnailFile();
        if(newThumbnailFile != null && !newThumbnailFile.isEmpty()){
            s3Service.deleteOldThumbnail(refId,refType);
            updateThumbnailUrl = s3Service.uploadFile(bookReqDto.getBookThumbnailFile());
            s3Service.uploadAndSave(refId, refType, newThumbnailFile,FileType.THUMBNAIL);
        } else if (bookReqDto.getBookThumbnail() != null) {
            updateThumbnailUrl = bookReqDto.getBookThumbnail();
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
                updateThumbnailUrl,
                category,
                updateParticipants
        );

        return bookId;
    }

    /*도서 삭제 api*/
    public BulkDeleteResponse bulkDeleteBooks(BookDelReqDto bookDelReqDto){
        List<Long> delTargetBookIds = bookDelReqDto.getBookIds();

        List<String> skippedTitles = bookRepository.skippedTitles(delTargetBookIds);
        List<Long> deletableIds = bookRepository.deletableBookIds(delTargetBookIds);

        int deletedCount = 0;
        if (!deletableIds.isEmpty()) {
            recommendationRepository.deleteRecommendationsByBookIds(deletableIds);
            deletedCount = (int) bookRepository.deleteBooksByIds(deletableIds);
        }

        return BulkDeleteResponse.builder()
                .totalRequestCount(delTargetBookIds.size())
                .skippedCount(skippedTitles.size())
                .deletedCount(deletedCount)
                .skippedTitles(skippedTitles)
                .build();
    }

    /*도서 조건부 및 전체 조회 api*/
    @Transactional(readOnly = true)
    public Page<BookResDto> findBooksWithFilter(SearchFilterDto searchFilterDto,Pageable pageable){
        return bookRepository.findBooks(searchFilterDto, pageable);
    }

    /*도서 조건부 및 전체 조회 api*/
    @Transactional(readOnly = true)
    public Page<BookResDto> findBooksWithFilterAndFTS(SearchFilterDto searchFilterDto,Pageable pageable){
        return bookRepository.findBooksWithFTS(searchFilterDto, pageable);
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

    /*알라딘 api를 이용하여 베스트셀러 도서 목록 조회*/
    public List<AladinBestSellerResDto> getBestsellersWithAladin(){
        String type = "Bestseller";
        return aladinClient.getBestSellers(type);
    }

    /*연관 도서 목록 조회*/
    public List<BookResDto> getRelatedBooks(Long bookId){
        int limit;

        Book mainBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.BOOK_NOT_FOUND));

        Set<Long> excludeIds = new HashSet<>();
        excludeIds.add(bookId);

        List<Book> finalRelatedBooks = new ArrayList<>();

        //메인 도서를 대출한 사람이 대출한 다른 도서
        List<Book> togetherBooks = bookRepository.findBooksAlsoBorrowed(bookId, excludeIds);
        addBooksToList(finalRelatedBooks, togetherBooks, excludeIds);

        //같은 저자
        if(finalRelatedBooks.size() < 5){
            String mainAuthor = mainBook.getParticipants().stream()
                    .filter(p -> p.getType() == ParticipantType.AUTHOR)
                    .map(Book.Participant::getName)
                    .findFirst()
                    .orElse(null);
            limit = 5 - finalRelatedBooks.size();
            if (mainAuthor != null) {
                List<Book> sameAuthorBooks = bookRepository.findBooksByAuthorOrderByBorrowCount(mainAuthor, excludeIds, limit);
                addBooksToList(finalRelatedBooks,sameAuthorBooks,excludeIds);
            }
        }

        //같은 카테고리
        if(finalRelatedBooks.size() < 5){
            limit = 5 - finalRelatedBooks.size();
            String category = mainBook.getCategory().getCategoryName();
            List<Book> sameCategoryBooks = bookRepository.findBooksByCategoryOrderByBorrowCount(category, excludeIds, limit);
            addBooksToList(finalRelatedBooks,sameCategoryBooks,excludeIds);
        }

        return finalRelatedBooks.stream()
                .map(Book::toBookResDto)
                .toList();
    }

    public LocalDate getLatestRegistrationDate() {
        return bookRepository.findLatestCreatedAt()
                .map(LocalDateTime::toLocalDate)
                .orElse(LocalDate.now());
    }

    private void addBooksToList(List<Book> targetList, List<Book> sourceList, Set<Long> excludeIds) {
        for (Book book : sourceList) {
            if (excludeIds.add(book.getBookId())) { // Set에 추가 성공 시(중복 아님) 리스트에 추가
                targetList.add(book);
            }
        }
    }

    private void addParticipants(Book book, BookReqDto bookReqDto){

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
    }

    private void sanitizeRequest(BookReqDto bookReqDto){
        bookReqDto.setBookContents(xssUtils.filterEditor(bookReqDto.getBookContents()));
        bookReqDto.setBookTitle(xssUtils.escapeText(bookReqDto.getBookTitle()));
    }

}
