package com.library.bookarte.book.controller;

import com.library.bookarte.book.dto.request.BookDelReqDto;
import com.library.bookarte.book.dto.request.BookReqDto;
import com.library.bookarte.book.dto.response.BookResDto;
import com.library.bookarte.book.dto.SearchFilterDto;
import com.library.bookarte.book.dto.response.BulkDeleteResponse;
import com.library.bookarte.book.external.dto.AladinBestSellerResDto;
import com.library.bookarte.book.external.dto.BookSearchResult;
import com.library.bookarte.book.service.BookService;
import com.library.bookarte.global.response.GlobalResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book")
public class BookController implements BookControllerDocs {
    private final BookService bookService;

    //도서 등록
    @Override
    public ResponseEntity<GlobalResponseDto<String>> registerBook(@Valid @ModelAttribute BookReqDto bookReqDto){
        bookService.registerBook(bookReqDto);

        String result = "도서 정보 저장 성공";

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponseDto.success(HttpStatus.CREATED,result));
    }

    //도서 상제 조회
    @Override
    public ResponseEntity<GlobalResponseDto<BookResDto>> findBookWithWish(@PathVariable("bookId") Long bookId, @AuthenticationPrincipal Long memberId){
        BookResDto result = bookService.findBookWithWish(bookId,memberId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    //도서 정보 수정
    @Override
    public ResponseEntity<GlobalResponseDto<Long>> updateBook(@PathVariable("bookId") Long bookId,
                                                              @Valid @ModelAttribute BookReqDto bookReqDto) {
        Long result = bookService.updateBook(bookId, bookReqDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    //도서 삭제
    @Override
    public ResponseEntity<GlobalResponseDto<BulkDeleteResponse>> deleteBooks(BookDelReqDto bookDelReqDto){

        BulkDeleteResponse result = bookService.bulkDeleteBooks(bookDelReqDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK,result));

    }

    //도서 리스트 조회
    @Override
    public ResponseEntity<GlobalResponseDto<Page<BookResDto>>> listBook(@ModelAttribute SearchFilterDto searchFilterDto,
                                                                        @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        Page<BookResDto> result = bookService.findBooksWithFilterAndFTS(searchFilterDto, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK,result));
    }


    //카카오 api + 국립 중앙 도서관 api 호출
    @Override
    public  ResponseEntity<GlobalResponseDto<List<BookSearchResult>>> searchBookWithLibraryApi(@RequestParam(name = "query") String query){
        List<BookSearchResult> result = bookService.searchBooksWithApi(query);

        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK,result));
    }

    //중복된 ISBN인지 확인
    @Override
    public ResponseEntity<GlobalResponseDto<Boolean>> isDuplicateIsbn(@RequestParam(name = "isbn") String isbn){
        boolean result = bookService.isDuplicateIsbn(isbn);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    @Override
    public ResponseEntity<GlobalResponseDto<List<AladinBestSellerResDto>>> getBestseller(){
        List<AladinBestSellerResDto> result = bookService.getBestsellersWithAladin();
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    @Override
    public ResponseEntity<GlobalResponseDto<List<BookResDto>>> listRelatedBook(@PathVariable Long bookId){
        List<BookResDto> result = bookService.getRelatedBooks(bookId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    @Override
    public ResponseEntity<GlobalResponseDto<LocalDate>> getLatestRegistrationDate() {
        LocalDate result = bookService.getLatestRegistrationDate();
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));
    }
}
