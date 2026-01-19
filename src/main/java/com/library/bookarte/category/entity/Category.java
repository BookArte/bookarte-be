package com.library.bookarte.category.entity;

import com.library.bookarte.book.entity.Book;
import com.library.bookarte.category.dto.CategoryResDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    //카테고리 코드
    @Column(unique = true, nullable = false )
    private String categoryCode;

    //카테고리명
    @Column
    private String categoryName;

    @OneToMany(mappedBy = "category")
    private List<Book> books = new ArrayList<>();


    public CategoryResDto toCategoryResDto(){
        return CategoryResDto.builder()
                .categoryId(this.categoryId)
                .categoryCode(this.categoryCode)
                .categoryName(this.categoryName)
                .build();
    }

    public void updateCategory(String categoryName){
        this.categoryName = categoryName;
    }
}
