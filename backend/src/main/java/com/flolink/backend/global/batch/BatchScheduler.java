package com.flolink.backend.global.batch;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BatchScheduler {

	private static final Logger log = LoggerFactory.getLogger(BatchScheduler.class);
	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job combinedJob;

	@Autowired
	private Job calendarJob;

	// 매월 1일 자정에 실행
	@Scheduled(cron = "0 0 0 1 * ?")
	public void runMonthlyPlantUserJob() throws Exception {
		jobLauncher.run(combinedJob, new JobParametersBuilder()
			.addDate("runDate", new Date())
			.toJobParameters());
	}

	// 매일 오전 8시에 실행
	// @Scheduled(cron = "0 0 8 * * ?")
	@Scheduled(cron = "1 * * * * ?")
	public void runDailyCalendarJob() throws Exception {
		jobLauncher.run(calendarJob, new JobParametersBuilder()
			.addDate("runDate", new Date())
			.toJobParameters());
	}
}
