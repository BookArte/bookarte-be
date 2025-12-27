package com.library.bookarte.category.controller;


import com.library.bookarte.category.dto.CategoryReqDto;
import com.library.bookarte.category.dto.CategoryResDto;
import com.library.bookarte.category.service.CategoryService;
import com.library.bookarte.global.response.GlobalResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<GlobalResponseDto<Long>> generateCategory(@RequestBody CategoryReqDto categoryReqDto) {
        Long result = categoryService.generateCategory(categoryReqDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponseDto.success(HttpStatus.CREATED, result));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<GlobalResponseDto<CategoryResDto>> findByCategoryId(@PathVariable Long categoryId) {
        CategoryResDto result = categoryService.findByCategoryId(categoryId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<GlobalResponseDto<Long>> updateCategory(@PathVariable Long categoryId,
                                                                  @RequestBody CategoryReqDto categoryReqDto){

        Long result = categoryService.updateCategory(categoryId, categoryReqDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, result));
    }


}
