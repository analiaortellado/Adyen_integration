package com.adyen.workshop.configurations;

import com.adyen.Client;
import com.adyen.Config;
import com.adyen.enums.Environment;
import com.adyen.service.checkout.PaymentsApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DependencyInjectionConfiguration {
    private final ApplicationConfiguration applicationProperties;

    public DependencyInjectionConfiguration(ApplicationConfiguration applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean
    Client client() {
        var config = new Config();
        config.setApiKey(applicationProperties.getAdyenApiKey());
        config.setEnvironment(Environment.TEST);

        return new Client(config);
    }

    @Bean
    PaymentsApi paymentsApi(){
        return new PaymentsApi(client());
    }
}
