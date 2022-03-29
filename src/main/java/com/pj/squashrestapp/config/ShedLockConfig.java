package com.pj.squashrestapp.config;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration for the lock of Scheduled actions (CRON).
 * It assures that no action will be invoked more than once,
 * what is required in case there are multiple instances
 * of the backend running in parallel.
 */
@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtLeastFor = "PT15M", defaultLockAtMostFor = "PT30M")
public class ShedLockConfig {

    @Bean
    public LockProvider lockProvider(RedisConnectionFactory connectionFactory) {
        return new RedisLockProvider(connectionFactory);
    }
}
