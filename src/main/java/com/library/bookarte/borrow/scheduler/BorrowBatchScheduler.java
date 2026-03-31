package com.library.bookarte.borrow.scheduler;

import com.library.bookarte.borrow.service.BorrowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Slf4j
@RequiredArgsConstructor
public class BorrowBatchScheduler {
    private final BorrowService borrowService;
    private final JobOperator jobOperator;
    private final Job overdueCheckJob;

    @Scheduled(cron = "0 0 0 * * *")
    public void runOverdueJob() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("run.id", System.currentTimeMillis())
                    .addString("today", LocalDate.now().toString())
                    .toJobParameters();

            JobExecution executionId = jobOperator.start(overdueCheckJob, params);

            log.info("배치 실행 성공, executionId={}", executionId);

        } catch (Exception e) {
            log.error("배치 실행 실패", e);
        }
    }

/*    @Scheduled(cron = "0 0 0 * * *")
    // 서버가 시작되고 5초 뒤에 실행, 그 이후 1분마다 반복 실행 -> 테스트 용
    @Scheduled(initialDelay = 5000, fixedRate = 60000)
    public void runOverdueCheck() {
        log.info("스케줄러 작동 확인 - 시간: {}", LocalDateTime.now());
        borrowService.processOverdue();
    }*/
}

