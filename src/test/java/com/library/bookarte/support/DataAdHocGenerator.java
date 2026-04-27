package com.library.bookarte.support;

import com.library.bookarte.global.config.BulkDataGenerator;
import com.library.bookarte.member.entity.Member;
import com.library.bookarte.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
public class DataAdHocGenerator {

    @Autowired private BulkDataGenerator bulkDataGenerator;
    @Autowired private MemberRepository memberRepository;

    @Test
    @Commit // 테스트가 성공하면 롤백하지 않고 DB에 반영
    @DisplayName("로컬 DB 성능 테스트를 위한 100만 건 데이터 적재")
    void generateLargeScaleData() {
        int totalBooks = 1000000;

        long startTime = System.currentTimeMillis();
        bulkDataGenerator.bulkInsertBooks(totalBooks);
        long endTime = System.currentTimeMillis();

        System.out.println("------------------------------------------");
        System.out.println("성공적으로 " + totalBooks + "건의 데이터를 적재했습니다.");
        System.out.println("소요 시간: " + (endTime - startTime) + "ms");
        System.out.println("------------------------------------------");
    }

    @Test
    @Commit
    @DisplayName("로컬 DB 테스트 유저 100건 데이터 적재")
    void generateUserDate() {
        int totalUsers =  100;

        for (int i = 1; i <= totalUsers; i++){
            Member member = FixtureFactory.createMember("test" + i);
            memberRepository.save(member);
        }
    }
}
