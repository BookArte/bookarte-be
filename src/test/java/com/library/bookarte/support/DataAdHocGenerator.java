package com.library.bookarte.support;

import com.library.bookarte.global.config.BulkDataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class DataAdHocGenerator {

    @Autowired private BulkDataGenerator bulkDataGenerator;

    @Test
    @Commit // 테스트가 성공하면 롤백하지 않고 DB에 반영
    @DisplayName("로컬 DB 성능 테스트를 위한 10만 건 데이터 적재")
    void generateLargeScaleData() {
        int totalBooks = 100_000;

        long startTime = System.currentTimeMillis();
        bulkDataGenerator.bulkInsertBooks(totalBooks);
        long endTime = System.currentTimeMillis();

        System.out.println("------------------------------------------");
        System.out.println("성공적으로 " + totalBooks + "건의 데이터를 적재했습니다.");
        System.out.println("소요 시간: " + (endTime - startTime) + "ms");
        System.out.println("------------------------------------------");
    }
}
