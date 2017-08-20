package org.demo;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

//initializes as a web application
//HTTP call listening on
//inbuilt tomcat port 8080

@SpringBootApplication
@Configuration
@EnableAsync
@Log4j2
public class Main {

    @Bean
    @Primary
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(75);
        return executor;
    }

    @Bean
    @Primary
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler();
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args).registerShutdownHook();
        log.info("--------------------- started application --------------------------");
    }
}
