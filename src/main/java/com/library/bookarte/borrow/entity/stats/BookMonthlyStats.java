package com.library.bookarte.borrow.entity.stats;

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
@Table(name = "book_monthly_stats", indexes = {
        @Index(name = "idx_book_id_period", columnList = "bookId, statYear, statMonth")
})
public class BookMonthlyStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statsId;

    private Long bookId;
    private int statYear;
    private int statMonth;
    private long borrowCount;

    public void addCount(long count){
        this.borrowCount += count;
    }
}
