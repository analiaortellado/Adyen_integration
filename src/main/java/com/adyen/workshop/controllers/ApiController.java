package com.adyen.workshop.controllers;

import com.adyen.workshop.configurations.ApplicationConfiguration;
import com.adyen.model.checkout.PaymentMethodsRequest;
import com.adyen.model.checkout.PaymentMethodsResponse;
import com.adyen.service.checkout.PaymentsApi;
import com.adyen.service.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * REST controller for using the Adyen payments API.
 */
@RestController
public class ApiController {
    private final Logger log = LoggerFactory.getLogger(ApiController.class);

    private final ApplicationConfiguration applicationProperties;
    private final PaymentsApi paymentsApi;

    public ApiController(ApplicationConfiguration applicationProperties, PaymentsApi paymentsApi) {
        this.applicationProperties = applicationProperties;
        this.paymentsApi = paymentsApi;
    }

    @GetMapping("/hello-world")
    public ResponseEntity<String> helloWorld() throws Exception {
        return ResponseEntity.ok()
                .body("This is the 'Hello World' from the workshop - You've successfully finished step 0!");
    }
    
    @PostMapping("/api/paymentMethods")
    public ResponseEntity<PaymentMethodsResponse> paymentMethods() throws IOException, ApiException {
        var paymentMethodsRequest = new PaymentMethodsRequest();

        paymentMethodsRequest.setMerchantAccount(applicationProperties.getAdyenMerchantAccount());

        var response = paymentsApi.paymentMethods(paymentMethodsRequest);
        return ResponseEntity.ok()
                .body(response);
    }
}