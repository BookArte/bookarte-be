package com.library.bookarte.book.controller;

import com.library.bookarte.book.dto.request.BookDelReqDto;
import com.library.bookarte.book.dto.request.BookReqDto;
import com.library.bookarte.book.dto.response.BestsellerResponse;
import com.library.bookarte.book.dto.response.BookResDto;
import com.library.bookarte.book.dto.SearchFilterDto;
import com.library.bookarte.book.dto.response.BulkDeleteResponse;
import com.library.bookarte.book.external.dto.BookSearchResult;
import com.library.bookarte.global.response.GlobalResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Book")
public interface BookControllerDocs {

    /*Create: 도서 등록*/
    @Operation(summary = "도서 등록 요청", description = "**성공 응답 데이터:** 도서 정보 저장 성공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "도서 등록 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "카테고리 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @PostMapping("/register")
    ResponseEntity<GlobalResponseDto<String>> registerBook(@Valid @ModelAttribute BookReqDto bookReqDto);

    /*Read: 단일 도서 정보 조회*/
    @Operation(summary = "단일 도서 조회 요청", description = "**성공 응답 데이터:** 단일 도서 정보")
    @Parameter(name = "bookId", description = "상세 조회할 도서 id", example = "1")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단일 도서 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 도서가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @GetMapping("/view/{bookId}")
    ResponseEntity<GlobalResponseDto<BookResDto>> findBookWithWish(@PathVariable("bookId") Long bookId, @AuthenticationPrincipal Long memberId);

    /*Read: 도서 목록 조회*/
    @Operation(summary = "도서 목록 조회 요청", description = "**성공 응답 데이터:** 도서 목록 정보")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "도서 목록 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @GetMapping("/list")
    ResponseEntity<GlobalResponseDto<Page<BookResDto>>> listBook(@ParameterObject  @ModelAttribute SearchFilterDto searchFilterDto,
                                                                 @ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable);

    /*Update: 도서 정보 수정*/
    @Operation(summary = "도서 정보 수정 요청", description = "**성공 응답 데이터:** 도서의 `bookId`")
    @Parameter(name = "bookId", description = "수정할 도서 id", example = "1")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "도서 정보 수정 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "해당 도서가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @PatchMapping("/{bookId}")
    ResponseEntity<GlobalResponseDto<Long>> updateBook(@PathVariable("bookId") Long bookId,
                                                       @Valid @ModelAttribute BookReqDto bookReqDto);
    /*Delete: 도서 정보 삭제*/
    @Operation(summary = "도서 삭제 요청", description = "**성공 응답 데이터:** 도서 삭제 성공")
    @Parameter(name = "bookId", description = "삭제할 도서 id", example = "1")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "도서 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "해당 도서가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @DeleteMapping
    ResponseEntity<GlobalResponseDto<BulkDeleteResponse>> deleteBooks(@RequestBody BookDelReqDto bookDelReqDto);

    /*Read: 외부 api에서 도서 정보 검색*/
    @Operation(summary = "외부 api 도서 정보 검색", description = "**성공 응답 데이터:** 해당되는 도서 목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "외부 api 요청 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @GetMapping("/library/search")
    @Parameter(name = "query", description = "검색할 도서 이름", example = "이방인")
    ResponseEntity<GlobalResponseDto<List<BookSearchResult>>> searchBookWithLibraryApi(@RequestParam(name = "query") String query);

    @Operation(summary = "외부 api를 활용한 도서 상세 조회", description = "**성공 응답 데이터:** 도서 상세 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "도서 상세 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @GetMapping("/library/search/{isbn}")
    ResponseEntity<GlobalResponseDto<BookSearchResult>> findBookByISBNWithLibraryApi(@RequestParam(name = "isbn") String isbn);


    @Operation(summary = "DB 내 도서 존재 확인", description = "**성공 응답 데이터:** 존재 유무에 대한 boolean")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "db 유무에 대한 응답 성공"),
            @ApiResponse(responseCode = "400", description = "이미 반납 중이거나 반납 완료된 도서에 대한 잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "해당 도서를 빌린 사용자가 요청한 타 사용자가 요청"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @GetMapping("/is-duplicate-isbn")
    ResponseEntity<GlobalResponseDto<Boolean>> isDuplicateIsbn(@RequestParam(name = "isbn") String isbn);

    @Operation(summary = "알라딘 api를 활용한 베스트셀러 목록 조회", description = "**성공 응답 데이터:** 베스트셀러 도서 목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "도서 목록 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @GetMapping("/bestseller")
    ResponseEntity<GlobalResponseDto<BestsellerResponse>> getBestseller(@RequestParam(defaultValue = "1") int page,
                                                                       @RequestParam(defaultValue = "10") int size );

    /*Read: 연관 도서 목록 조회*/
    @Operation(summary = "연관 도서 목록 조회 요청", description = "**성공 응답 데이터:** 도서 목록 정보")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "도서 목록 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @GetMapping("/{bookId}/related")
    ResponseEntity<GlobalResponseDto<List<BookResDto>>> listRelatedBook(@PathVariable Long bookId);

    @Operation(summary = "최근 등록된 도서 등록일 조회 요청", description = "**성공 응답 데이터:** 최근 등록된 도서 등록일")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "도서 목록 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 에러"),
    })
    @GetMapping("/latest-registration-date")
    ResponseEntity<GlobalResponseDto<LocalDate>> getLatestRegistrationDate();
}
