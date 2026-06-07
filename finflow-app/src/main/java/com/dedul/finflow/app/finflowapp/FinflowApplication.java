package com.dedul.finflow.app.finflowapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FinflowApplication {
  public static void main(String[] args) {
    SpringApplication.run(FinflowApplication.class, args);
  }
}
