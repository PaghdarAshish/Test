package com.activiti.oaf.adapter.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
/**
 * OAF Scheduler.
 * @author Pradip Patel
 */
@Component
public class OAFProcessScheduler {

    private static final Logger logger = LoggerFactory.getLogger(OAFProcessScheduler.class);

    @Autowired
    private OAFSchedulerOperation schedulerOperation;

    /**
     * This method is work as Scheduler, it execute based on specific interval.
     */
    /*@Scheduled(fixedDelayString = "${oaf.scheduler.interval}")
    private void execute() {
        logger.info("OAF Scheduler called");
        this.schedulerOperation.processQuotes();
    }*/
}
