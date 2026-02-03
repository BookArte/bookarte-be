package com.library.bookarte.borrow.scheduler;

import com.library.bookarte.borrow.service.BorrowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class BorrowBatchScheduler {
    private final BorrowService borrowService;

    // 순서: 초(0) 분(0) 시(0) 일(*) 월(*) 요일(*)
    /*@Scheduled(cron = "0 0 0 * * *")*/ // 매일 00:00:00에 실행
    // 서버가 시작되고 5초 뒤에 실행, 그 이후 1분마다 반복 실행 -> 테스트 용
    @Scheduled(initialDelay = 5000, fixedRate = 60000)
    public void runOverdueCheck() {
        log.info("스케줄러 작동 확인 - 시간: {}", LocalDateTime.now());
        borrowService.processOverdue();
    }
}
