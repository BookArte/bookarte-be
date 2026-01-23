package com.library.bookarte.book.controller;

import com.library.bookarte.book.dto.BookReqDto;
import com.library.bookarte.book.dto.BookResDto;
import com.library.bookarte.book.dto.SearchFilterDto;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book")
public class BookController implements BookControllerDocs {
    private final BookService bookService;

    //도서 등록
    @Override
    public ResponseEntity<GlobalResponseDto<String>> registerBook(@Valid @RequestBody BookReqDto bookReqDto){
        bookService.registerBook(bookReqDto);

        String result = "도서 정보 저장 성공";

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponseDto.success(HttpStatus.CREATED,result));
    }

    //도서 상제 조회
    @Override
    public ResponseEntity<GlobalResponseDto<BookResDto>> findBookById(@PathVariable("bookId") Long bookId){
        BookResDto result = bookService.findBookById(bookId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    //도서 정보 수정
    @Override
    public ResponseEntity<GlobalResponseDto<Long>> updateBook(@PathVariable("bookId") Long bookId,
                                                              @Valid @RequestBody BookReqDto bookReqDto) {
        Long result = bookService.updateBook(bookId, bookReqDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    //도서 삭제
    @Override
    public ResponseEntity<GlobalResponseDto<?>> deleteBook(@PathVariable("bookId") Long bookId){

        bookService.deleteBook(bookId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK,null));

    }

/*
    //도서 리스트 조회
    @GetMapping("/list")
    public ResponseEntity<GlobalResponseDto<Page<BookResDto>>> listBook(@PageableDefault(page = 1) Pageable pageable){
        Page<BookResDto> result = bookService.findAllBooks(pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK,result));
    }
 */
/*
    //도서 카테고리 조회
    @GetMapping("/list/category")
    public ResponseEntity<GlobalResponseDto<Page<BookResDto>>> listBookWithCategory(@RequestParam(required = false) String categoryName,
                                                                                    @PageableDefault(page = 1) Pageable pageable) {

        Page<BookResDto> result = bookService.findBooksWithCategory(categoryName, pageable);


        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK,result));
    }
*/
    //도서 리스트 조회
    @Override
    public ResponseEntity<GlobalResponseDto<Page<BookResDto>>> listBook(@ModelAttribute SearchFilterDto searchFilterDto,
                                                                        @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        Page<BookResDto> result = bookService.findBooksWithFilter(searchFilterDto, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK,result));
    }


    //카카오 api + 국립 중앙 도서관 api 호출
    @Override
    public  ResponseEntity<GlobalResponseDto<List<BookSearchResult>>> searchBookWithLibraryApi(@RequestParam String query){
        List<BookSearchResult> result = bookService.searchBooksWithApi(query);

        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK,result));
    }

    //중복된 ISBN인지 확인
    @Override
    public ResponseEntity<GlobalResponseDto<Boolean>> isDuplicateIsbn(@RequestParam String isbn){
        boolean result = bookService.isDuplicateIsbn(isbn);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));
    }
}
