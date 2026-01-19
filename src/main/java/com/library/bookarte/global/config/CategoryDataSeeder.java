package com.library.bookarte.global.config;

import com.library.bookarte.category.entity.Category;
import com.library.bookarte.category.reposiotry.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CategoryDataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String @NonNull ... args) {
        // 한국십분류법 대분류 카테고리 테이블에 주입
        Map<String, String> kdcData = new LinkedHashMap<>();
        kdcData.put("000", "총류");
        kdcData.put("100", "철학");
        kdcData.put("200", "종교");
        kdcData.put("300", "사회과학");
        kdcData.put("400", "자연과학");
        kdcData.put("500", "기술과학");
        kdcData.put("600", "예술");
        kdcData.put("700", "언어");
        kdcData.put("800", "문학");
        kdcData.put("900", "역사");

        //서버 가동 시 해당 카테고리가 존재하는지 확인하고 누락 시 주입
        kdcData.forEach((code, name) -> {
            if (!categoryRepository.existsByCategoryCode(code)) {
                categoryRepository.save(new Category(code, name));
            }
        });


    }
}
