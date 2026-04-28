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
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BulkDataGenerator {

    private final JdbcTemplate jdbcTemplate;
    private static final String[] SUBJECTS = {"스프링", "자바", "객체지향", "알고리즘", "데이터베이스", "클라우드", "파이썬", "리액트", "도커", "MSA"};
    private static final String[] ACTIONS = {"정석", "가이드", "입문", "실전 프로젝트", "고급 프로그래밍", "깊이 있게", "이해하기", "성능 최적화"};
    private static final String[] SUFFIXES = {"1권", "2권", "개정판", "완성", "비결", "핵심 원리"};

    @Transactional
    public void bulkInsertBooks(int totalCount) {
        Long startMaxId = jdbcTemplate.queryForObject("SELECT IFNULL(MAX(book_id), 0) FROM book", Long.class);

        String bookSql = "INSERT INTO book (book_title, publisher_name, publication_date, book_isbn, " +
                "book_contents, can_borrow, book_call_number, book_thumbnail, category_id, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String participantSql = "INSERT INTO book_participant (book_id, name, type) VALUES (?, ?, ?)";

        int batchSize = 1000;
        Random random = new Random();

        System.out.println("100만 건 Book 데이터 적재 시작...");
        for (int i = 0; i < totalCount; i += batchSize) {
            final int startIdx = i;
            final int currentBatchSize = Math.min(batchSize, totalCount - startIdx);

            jdbcTemplate.batchUpdate(bookSql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int j) throws SQLException {
                    int idx = startIdx + j;

                    // 랜덤한 제목 생성: 예) "자바 성능 최적화 핵심 원리"
                    String randomTitle = SUBJECTS[random.nextInt(SUBJECTS.length)] + " " +
                            ACTIONS[random.nextInt(ACTIONS.length)] + " " +
                            SUFFIXES[random.nextInt(SUFFIXES.length)] + " " + idx;

                    ps.setString(1, randomTitle);
                    ps.setString(2, "출판사 " + (random.nextInt(100))); // 출판사도 무작위
                    ps.setObject(3, LocalDate.now().minusDays(random.nextInt(3650)));
                    ps.setString(4, "ISBN-" + UUID.randomUUID().toString().substring(0, 8) + idx);
                    ps.setString(5, randomTitle + "에 대한 상세 상세 상세 내용입니다.");
                    ps.setBoolean(6, true);
                    ps.setString(7, "CALL-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase());
                    ps.setString(8, "https://example.com/" + idx + ".jpg");
                    ps.setLong(9, 1L);
                    ps.setObject(10, LocalDateTime.now());
                    ps.setObject(11, LocalDateTime.now());
                }
                @Override
                public int getBatchSize() { return currentBatchSize; }
            });
        }

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
