package com.library.bookarte.wish.controller;

import com.library.bookarte.global.response.GlobalResponseDto;
import com.library.bookarte.wish.dto.response.WishResDto;
import com.library.bookarte.wish.service.WishService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wish")
public class WishController implements WishControllerDocs {
    private final WishService wishService;

    @Override
    public ResponseEntity<GlobalResponseDto<String>> addWishBook(@PathVariable Long bookId,
                                                                 @AuthenticationPrincipal Long memberId) {
        wishService.addWishBook(bookId,memberId);
        String result = "관심 도서 추가";
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponseDto.success(HttpStatus.CREATED, result));
    }

    @Override
    public ResponseEntity<GlobalResponseDto<Page<WishResDto>>> getWishList(@AuthenticationPrincipal Long memberId, Pageable pageable) {
        Page<WishResDto> results = wishService.getWishList(memberId,pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, results));
}
}
