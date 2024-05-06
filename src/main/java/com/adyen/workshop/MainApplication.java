package com.adyen.workshop;

import com.adyen.workshop.configurations.ApplicationConfiguration;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainApplication {
    private final ApplicationConfiguration applicationProperties;

    public MainApplication(ApplicationConfiguration applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    private static final Logger log = LoggerFactory.getLogger(MainApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @PostConstruct
    public void init() {
        log.info("\n----------------------------------------------------------\n\t" +
                "Application is running on http://localhost:" + applicationProperties.getServerPort() +
                "\n----------------------------------------------------------");
    }
}