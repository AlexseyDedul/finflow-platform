package com.dedul.finflow.app.finflowapp.shared.config;

import java.util.concurrent.Executor;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

  @Bean(name = "applicationTaskExecutor")
  public Executor applicationTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    executor.setCorePoolSize(4);
    executor.setMaxPoolSize(12);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("finflow-async-");
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(20);
    executor.setTaskDecorator(mdcTaskDecorator());

    executor.initialize();
    return executor;
  }

  @Bean(name = "sqsMessageProcessingExecutor")
  public Executor sqsMessageProcessingExecutor(TaskDecorator mdcTaskDecorator) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(4);
    executor.setMaxPoolSize(8);
    executor.setQueueCapacity(50);
    executor.setThreadNamePrefix("finflow-sqs-");
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(30);
    executor.setTaskDecorator(mdcTaskDecorator);

    executor.initialize();

    return executor;
  }

  @Bean
  public TaskDecorator mdcTaskDecorator() {
    return runnable -> {
      var contextMap = MDC.getCopyOfContextMap();

      return () -> {
        var previousContextMap = org.slf4j.MDC.getCopyOfContextMap();

        try {
          if (contextMap != null) {
            MDC.setContextMap(contextMap);
          } else {
            MDC.clear();
          }
          runnable.run();
        } finally {
          if (previousContextMap != null) {
            MDC.setContextMap(previousContextMap);
          } else {
            MDC.clear();
          }
        }
      };
    };
  }
}
