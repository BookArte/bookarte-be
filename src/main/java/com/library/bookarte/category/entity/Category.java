package com.library.bookarte.category.entity;

import com.library.bookarte.category.dto.CategoryResDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "category")
public class Category {
    //카테고리일련번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    //카테고리명
    @Column
    private String categoryName;


    public CategoryResDto toCategoryResDto(){
        return CategoryResDto.builder()
                .categoryId(this.categoryId)
                .categoryName(this.categoryName)
                .build();
    }
}
