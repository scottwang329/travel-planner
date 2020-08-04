package com.travelplanner.travelplanner_server.quartz;

import com.travelplanner.travelplanner_server.services.VoteService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class VoteSyncJob extends QuartzJobBean {

    @Autowired
    private VoteService voteService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("VoteSyncJob------------{}", sdf.format(new Date()));

        voteService.syncRedisToDB();
    }
}
