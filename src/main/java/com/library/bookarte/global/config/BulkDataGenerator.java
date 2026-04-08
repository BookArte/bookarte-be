package com.library.bookarte.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class BulkDataGenerator {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void bulkInsertBooks(int totalCount) {
        // 1. 현재 마지막 ID 조회
        Long startMaxId = jdbcTemplate.queryForObject("SELECT IFNULL(MAX(book_id), 0) FROM book", Long.class);

        String bookSql = "INSERT INTO book (book_title, publisher_name, publication_date, book_isbn, " +
                "book_contents, can_borrow, book_call_number, book_thumbnail, category_id, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String participantSql = "INSERT INTO book_participant (book_id, name, type) VALUES (?, ?, ?)";

        int batchSize = 1000;
        Random random = new Random();

        // [Step A] Book 테이블만 먼저 전부 적재
        System.out.println("Book 데이터 적재 시작...");
        for (int i = 0; i < totalCount; i += batchSize) {
            final int startIdx = i;
            final int currentBatchSize = Math.min(batchSize, totalCount - startIdx);

            jdbcTemplate.batchUpdate(bookSql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int j) throws SQLException {
                    int idx = startIdx + j;
                    ps.setString(1, "테스트 도서 제목 " + idx);
                    ps.setString(2, "출판사 " + (idx % 50));
                    ps.setObject(3, LocalDate.now().minusDays(random.nextInt(3650)));
                    ps.setString(4, "ISBN-" + String.format("%08d", idx));
                    ps.setString(5, "상세 내용 " + idx);
                    ps.setBoolean(6, true);
                    ps.setString(7, "CALL-" + idx);
                    ps.setString(8, "https://example.com/" + idx + ".jpg");
                    ps.setLong(9, 1L); // 에러 방지를 위해 카테고리 ID를 1로 고정 (실제 ID가 1번인지 확인 필수)
                    ps.setObject(10, LocalDateTime.now());
                    ps.setObject(11, LocalDateTime.now());
                }
                @Override
                public int getBatchSize() { return currentBatchSize; }
            });
        }

        // [Step B] Book 적재 완료 후 Participant 데이터 적재
        // Step A에서 생성된 정확한 ID 범위를 사용합니다.
        System.out.println("Participant 데이터 적재 시작...");
        for (int i = 0; i < totalCount; i += batchSize) {
            final int startIdx = i;
            final int currentBatchSize = Math.min(batchSize, totalCount - startIdx);
            final long baseIdForThisBatch = startMaxId + startIdx + 1;

            jdbcTemplate.batchUpdate(participantSql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int j) throws SQLException {
                    ps.setLong(1, baseIdForThisBatch + j);
                    ps.setString(2, "저자 " + ((startIdx + j) % 100));
                    ps.setString(3, "AUTHOR");
                }
                @Override
                public int getBatchSize() { return currentBatchSize; }
            });
        }
    }
}
