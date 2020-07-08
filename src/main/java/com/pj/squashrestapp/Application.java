package com.pj.squashrestapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * Entry Point for the entire application.
 */
@SpringBootApplication
@SuppressWarnings({"JavaDoc", "resource"})
public class Application {

  public static void main(final String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @PostConstruct
  void setUtcTimezone() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }

}
