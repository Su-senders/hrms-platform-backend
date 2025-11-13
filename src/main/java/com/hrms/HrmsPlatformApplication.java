package com.hrms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for HRMS Platform.
 *
 * This is a multi-tenant Human Resources Management System designed for large organizations.
 *
 * @author HRMS Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableAsync
@EnableScheduling
public class HrmsPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(HrmsPlatformApplication.class, args);
    }
}
