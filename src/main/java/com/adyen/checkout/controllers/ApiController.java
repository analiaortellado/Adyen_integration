package com.adyen.checkout.controllers;

import com.adyen.Client;
import com.adyen.checkout.ApplicationProperty;
import com.adyen.checkout.models.CartItemModel;
import com.adyen.checkout.services.CartService;
import com.adyen.enums.Environment;
import com.adyen.model.checkout.Amount;
import com.adyen.model.RequestOptions;
import com.adyen.model.checkout.*;
import com.adyen.service.checkout.PaymentsApi;
import com.adyen.service.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * REST controller for using the Adyen payments API.
 */
@RestController
public class ApiController {
    private final Logger log = LoggerFactory.getLogger(ApiController.class);

    private final ApplicationProperty applicationProperty;

    private final PaymentsApi paymentsApi;

    @Autowired
    private CartService cartService;

    @Autowired
    public ApiController(ApplicationProperty applicationProperty) {

        this.applicationProperty = applicationProperty;

        if (applicationProperty.getApiKey() == null) {
            log.warn("ADYEN_API_KEY is UNDEFINED");
            throw new RuntimeException("ADYEN_API_KEY is UNDEFINED");
        }

        var client = new Client(applicationProperty.getApiKey(), Environment.TEST);
        this.paymentsApi = new PaymentsApi(client);
    }

    @PostMapping("/api/paymentMethods")
    public ResponseEntity<PaymentMethodsResponse> paymentMethods() throws IOException, ApiException {
        // *Step 2
        var paymentMethodsRequest = new PaymentMethodsRequest();

        var response = paymentsApi.paymentMethods(paymentMethodsRequest);
        return ResponseEntity.ok()
                .body(response);
    }

    @PostMapping("/api/payments")
    public ResponseEntity<PaymentResponse> payments(@RequestHeader String host, @RequestBody PaymentRequest body, HttpServletRequest request) throws IOException, ApiException {
        // *Step 4
        var paymentRequest = new PaymentRequest();

        // Amount
        var amount = new Amount()
                .currency("EUR")
                .value(cartService.getTotalAmount());
        paymentRequest.setAmount(amount);

        // *Step 5 - For 3DS2, we need to add additional parameters to the paymentRequest


        // *Klarna step, we need to add additional parameters to the paymentRequest


        log.info("PaymentsRequest {}", paymentRequest);
        var response = paymentsApi.payments(paymentRequest); // *Idempotency step, we need to add additional parameters to the payments
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/api/payments/details")
    public ResponseEntity<PaymentDetailsResponse> paymentsDetails(@RequestBody PaymentDetailsRequest detailsRequest) throws IOException, ApiException {
        log.info("PaymentDetailsRequest {}", detailsRequest);
        var response = paymentsApi.paymentsDetails(detailsRequest);
        return ResponseEntity.ok()
                .body(response);
    }

    // Handle redirect during payment.
    @GetMapping("/api/handleShopperRedirect")
    public RedirectView redirect(@RequestParam(required = false) String payload, @RequestParam(required = false) String redirectResult) throws IOException, ApiException {
        // *Step 6
        var paymentDetailsRequest = new PaymentDetailsRequest();

        var paymentsDetailsResponse = paymentsApi.paymentsDetails(paymentDetailsRequest);
        log.info("PaymentsDetailsResponse {}", paymentsDetailsResponse);

        // Handle response
        return getRedirectView(paymentsDetailsResponse);
    }

    private RedirectView getRedirectView(final PaymentDetailsResponse paymentsDetailsResponse) {
        // *Step 7 - We're just showing a successful or unsuccessful redirect for now. No need to change anything here!
        var redirectURL = "/result/";
        switch (paymentsDetailsResponse.getResultCode()) {
            case AUTHORISED:
                redirectURL += "success";
                break;
            case PENDING:
            case RECEIVED:
                redirectURL += "pending";
                break;
            case REFUSED:
                redirectURL += "failed";
                break;
            default:
                redirectURL += "error";
                break;
        }
        return new RedirectView(redirectURL + "?reason=" + paymentsDetailsResponse.getResultCode());
    }
}
