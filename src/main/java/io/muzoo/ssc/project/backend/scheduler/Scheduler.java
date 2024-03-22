package io.muzoo.ssc.project.backend.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.muzoo.ssc.project.backend.shortcuts.recurring.RecurringsPublisher;

@Service
public class Scheduler {

    @Autowired
    private RecurringsPublisher recurringsPublisher;

    @Scheduled(cron = "0 0 0 * * ?")
    public void dailyUpdate(){

        recurringsPublisher.publishRecurrings();

    }

}
