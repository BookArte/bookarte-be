package com.library.bookarte.global.config.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class OverdueBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private static final int CHUNK_SIZE = 1000;

    //Job
    @Bean
    public Job overdueCheckJob(Step overdueCheckStep){
        return new JobBuilder("overdueCheckJob", jobRepository)
                .start(overdueCheckStep)
                .build();
    }

    //Step
    @Bean
    public Step overdueCheckStep(
            JdbcCursorItemReader<Long> overdueItemReader,
            ItemWriter<Long> overdueItemWriter) {
        return new StepBuilder("overdueCheckStep", jobRepository)
                .<Long, Long>chunk(CHUNK_SIZE)
                .transactionManager(transactionManager)
                .reader(overdueItemReader)
                .writer(overdueItemWriter)
                .build();
    }

    //Reader
    @StepScope
    @Bean
    public JdbcCursorItemReader<Long> overdueItemReader(
            DataSource dataSource, // SQL 실행을 위해 DataSource 주입 필요
            @Value("#{jobParameters['today']}") String today
    ) {
        return new JdbcCursorItemReaderBuilder<Long>()
                .name("overdueItemReader")
                .dataSource(dataSource)
                .sql("""
                SELECT borrow_id
                FROM borrow
                WHERE status = 'BORROWED'
                AND return_due_date < ?
                """)
                .queryArguments(LocalDate.parse(today))
                .rowMapper((rs, rowNum) -> rs.getLong("borrow_id"))
                .fetchSize(1000)
                .build();
    }

    //Writer
    @Bean
    public JdbcBatchItemWriter<Long> overdueItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Long>()
                .dataSource(dataSource)
                .sql("""
                UPDATE borrow SET
                status = 'OVERDUE',
                is_overdue = true,
                can_extend = false,
                overdue_days = DATEDIFF(CURDATE(), return_due_date)
                WHERE borrow_id = :item
                """)
                .itemSqlParameterSourceProvider(item -> {
                    Map<String, Object> params = new HashMap<>();
                    params.put("item", item);
                    return new MapSqlParameterSource(params);
                })
                .build();
    }
}
