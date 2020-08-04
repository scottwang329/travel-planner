package com.travelplanner.travelplanner_server.quartz;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {
    private static final String VOTE_SYNC_JOB_IDENTITY = "VoteSyncJob";

    @Bean
    public JobDetail voteSyncJobDetail() {
        return JobBuilder.newJob(VoteSyncJob.class).withIdentity(VOTE_SYNC_JOB_IDENTITY).storeDurably().build();
    }

    @Bean
    public Trigger voteSyncJobTrigger(JobDetail voteSyncJobDetail) {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMinutes(1)
                .repeatForever();
        return TriggerBuilder.newTrigger().forJob(voteSyncJobDetail)
                .withIdentity(VOTE_SYNC_JOB_IDENTITY)
                .withSchedule(scheduleBuilder)
                .build();
    }
}
