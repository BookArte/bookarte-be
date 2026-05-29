package com.library.bookarte.category.reposiotry;


import com.library.bookarte.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCategoryName(String categoryName);

    boolean existsByCategoryCode(String code);

}
