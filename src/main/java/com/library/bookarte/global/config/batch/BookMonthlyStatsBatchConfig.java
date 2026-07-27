package com.library.bookarte.global.config.batch;


import com.library.bookarte.borrow.dto.BookIdCountDto;
import com.library.bookarte.borrow.entity.stats.BookMonthlyStats;
import com.library.bookarte.borrow.repository.BookMonthlyStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.data.RepositoryItemWriter;
import org.springframework.batch.infrastructure.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BookMonthlyStatsBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    private final BookMonthlyStatsRepository bookMonthlyStatsRepository;

    private static final int CHUNK_SIZE = 100;

    @Bean
    public Job bookMonthlyStatJob(Step bookMonthlyStatStep){
        return new JobBuilder("bookMonthlyStatJob",jobRepository)
                .start(bookMonthlyStatStep)
                .build();
    }

    @Bean
    public Step bookMonthlyStatStep(){
        return new StepBuilder("bookMonthlyStatStep", jobRepository)
                .<BookIdCountDto, BookMonthlyStats> chunk(CHUNK_SIZE)
                .transactionManager(transactionManager)
                .reader(lastMonthBorrowReader())
                .processor(statsProcessor())
                .writer(statsWriter())
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<BookIdCountDto> lastMonthBorrowReader(){
        return new JdbcCursorItemReaderBuilder<BookIdCountDto>()
                .name("lastMonthBorrowReader")
                .dataSource(dataSource)
                .sql("""
                    SELECT book_id, COUNT(*) as borrow_count
                    FROM borrow
                    WHERE created_at >= DATE_FORMAT(CURRENT_DATE - INTERVAL 1 MONTH, '%Y-%m-01')
                      AND created_at < DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')
                    GROUP BY book_id
                """)
                .rowMapper((rs, rowNum) -> new BookIdCountDto(
                        rs.getLong("book_id"),
                        rs.getLong("borrow_count")
                ))
                .fetchSize(CHUNK_SIZE)
                .build();
    }

    @Bean
    public ItemProcessor<BookIdCountDto, BookMonthlyStats> statsProcessor() {
        return dto -> {
            LocalDate lastMonth = LocalDate.now().minusMonths(1);
            int year = lastMonth.getYear();
            int month = lastMonth.getMonthValue();

            return bookMonthlyStatsRepository.findByBookIdAndStatYearAndStatMonth(dto.getBookId(), year, month)
                    .map(existing -> {
                        existing.updateCount(dto.getBorrowCount());
                        return existing;
                    })
                    .orElse(BookMonthlyStats.builder()
                            .bookId(dto.getBookId())
                            .statYear(year)
                            .statMonth(month)
                            .borrowCount(dto.getBorrowCount())
                            .build());
        };
    }

    @Bean
    public RepositoryItemWriter<BookMonthlyStats> statsWriter() {
        return new RepositoryItemWriterBuilder<BookMonthlyStats>()
                .repository(bookMonthlyStatsRepository)
                .methodName("save")
                .build();
    }
}
