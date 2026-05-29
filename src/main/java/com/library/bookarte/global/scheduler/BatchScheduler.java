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

import java.util.UUID;


@Slf4j
@Component
@RequiredArgsConstructor
public class BatchScheduler {

    private final JobOperator jobOperator;

    private final Job libraryMasterJob;
    private final Job dailyLibraryJob;
    private final Job monthlyStatJob;

    //서버 구동 시
    @EventListener(ApplicationReadyEvent.class)
    public  void onApplicationReady(){
        log.info(">>>> [System Startup] 서버 구동 완료. 초기 배치 실행 및 캐시 워밍을 시작합니다.");
        runLibraryMasterJob();
    }

    //매일 자정 00시 실행 (일일 배치)
    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleDailyJob() {
        log.info(">>>> [Scheduled] 정기 자정 배치를 실행합니다.");
        runJob(dailyLibraryJob);
    }

    // 매월 1일 00시 05분 실행 (월간 통계 확정 로직)
    @Scheduled(cron = "0 5 0 1 * *")
    public void scheduleMonthlyJob() {
        log.info(">>>> [Scheduled] 월간 통계 확정 배치 실행");
        runJob(monthlyStatJob);
    }

    public void runLibraryMasterJob() {
        try {
            JobExecution execution = jobOperator.start(libraryMasterJob, buildJobParameters());
            log.info(">>>> [Scheduler] libraryMasterJob 시작, executionId={}", execution.getId());
        } catch (Exception e) {
            log.error("libraryMasterJob 실행 실패", e);
        }
    }

    private void runJob(Job job) {
        try {
            JobExecution execution = jobOperator.start(job, buildJobParameters());
            log.info(">>>> [Scheduler] {} 실행 성공, executionId={}", job.getName(), execution.getId());
        } catch (Exception e) {
            log.error("{} 실행 실패", job.getName(), e);
        }
    }
    private JobParameters buildJobParameters() {
        return new JobParametersBuilder()
                .addString("run.id", UUID.randomUUID().toString(), true)
                .toJobParameters();
    }
}
