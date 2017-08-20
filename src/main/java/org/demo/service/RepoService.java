package org.demo.service;

import lombok.extern.log4j.Log4j2;
import org.demo.dto.Input;
import org.demo.dto.Output;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

//provides service to access repository
//to persist data and compute statistics
//each call is spawned in a separate thread
//of work. However they all work on the same
//respository object, which is thread-safe

@Service
@Log4j2
@EnableScheduling
public class RepoService {

    @Autowired
    private Repository repo;

    //separate thread for persisting transaction information
    //in memory
    @Async
    public Future<Boolean> add(Input input) {
        if(input == null || input.getTsp() == null || input.getTsp() < 0 || input.getAmount() == null) {
            log.info("Discarding input due to bad data: " + input);
            return new AsyncResult<>(false);
        } else if(repo.insert(input)) {
            log.info("Saved: " + input);
            return new AsyncResult<>(true);
        } else {
            log.info("Discarding input from future or older than 60 sec: " + input);
            return new AsyncResult<>(false);
        }
    }

    //separate thread using spring Async to compute and get
    //stats for the request
    @Async
    public Future<Output> get() {
        Output output = repo.compute();
        log.info("Stats: " + output);
        return new AsyncResult<>(output);
    }

    //use spring scheduler to update the repo array by
    //discarding oldest element and creating newest empty element
    //once every second
    @Scheduled(cron="* * * ? * *")
    public void update() {
        log.debug("Right Shifting repo data");
        repo.shift();
    }
}
