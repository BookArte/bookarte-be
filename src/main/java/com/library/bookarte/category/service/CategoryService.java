package com.library.bookarte.category.service;

import com.library.bookarte.category.dto.CategoryReqDto;
import com.library.bookarte.category.dto.CategoryResDto;
import com.library.bookarte.category.entity.Category;
import com.library.bookarte.category.reposiotry.CategoryRepository;
import com.library.bookarte.global.exception.CustomErrorCode;
import com.library.bookarte.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = CustomException.class)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Long generateCategory(CategoryReqDto categoryReqDto) {
        Category category = Category.builder()
                .categoryName(categoryReqDto.getCategoryName())
                .build();

        categoryRepository.save(category);

        return category.getCategoryId();
    }

    public CategoryResDto findByCategoryId(Long categoryId){
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.CATEGORY_NOT_FOUND));

        return category.toCategoryResDto();
    }

    public Long updateCategory(Long categoryId, CategoryReqDto categoryReqDto) {
        Category updateTargetCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.CATEGORY_NOT_FOUND));

        updateTargetCategory.updateCategory(categoryReqDto.getCategoryName());

        return updateTargetCategory.getCategoryId();
    }

    public void deleteCategory(Long categoryId) {
        Category deleteTargetCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.CATEGORY_NOT_FOUND));

        categoryRepository.delete(deleteTargetCategory);
    }

    public List<CategoryResDto> findAllCategory(){
      List<Category> categoryList = categoryRepository.findAll();

        return categoryList.stream()
                .map(Category::toCategoryResDto)
                .toList();
    }
}
