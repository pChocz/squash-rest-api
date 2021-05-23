package com.pj.squashrestapp;

import static com.pj.squashrestapp.util.GeneralUtil.UTC_ZONE;

import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Entry Point for the entire application. */
@EnableScheduling
@SpringBootApplication
@SuppressWarnings({"JavaDoc", "resource"})
public class Application {

  public static void main(final String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @PostConstruct
  void setUtcTimezone() {
    TimeZone.setDefault(UTC_ZONE);
  }
}
