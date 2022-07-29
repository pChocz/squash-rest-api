package com.pj.squashrestapp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

import static com.pj.squashrestapp.util.GeneralUtil.UTC_ZONE;

/** Entry Point for the entire application. */
@EnableScheduling
@SpringBootApplication
@SuppressWarnings({"JavaDoc", "resource"})
@EnableJpaRepositories(basePackages = "com.pj.squashrestapp.repository")
@EnableMongoRepositories(basePackages = "com.pj.squashrestapp.mongologs")
@MapperScan(basePackages = "com.pj.squashrestapp.mybatis")
public class Application {

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    void setUtcTimezone() {
        TimeZone.setDefault(UTC_ZONE);
    }
}
