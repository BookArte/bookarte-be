package com.library.bookarte.global.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchScheduler {

    private final JobOperator jobOperator;
    private final Job libraryMasterJob;

/*    @EventListener(ApplicationReadyEvent.class)
    public  void onApplicationReady(){
        log.info(">>>> [System Startup] 서버 구동 완료. 초기 배치 실행 및 캐시 워밍을 시작합니다.");
        runLibraryMasterJob();
    }*/

    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleLibraryMasterJob() {
        log.info(">>>> [Scheduled] 정기 자정 배치를 실행합니다.");
        runLibraryMasterJob();
    }

    public void runLibraryMasterJob() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("run.id", System.currentTimeMillis())
                    .addString("today", LocalDate.now().toString())
                    .toJobParameters();

            JobExecution executionId = jobOperator.start(libraryMasterJob, params);
            log.info(">>>> [Scheduler] 도서관 관리 마스터 배치 시작...");
            log.info("배치 실행 성공, executionId={}", executionId);
            log.info(">>>> [Scheduler] 도서관 관리 마스터 배치 종료.");
        } catch (Exception e){
            log.error("배치 실행 실패", e);
        }
    }
}
