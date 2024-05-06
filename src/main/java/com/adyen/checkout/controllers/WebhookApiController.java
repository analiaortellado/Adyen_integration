package com.adyen.checkout.controllers;

import com.adyen.checkout.ApplicationProperty;
import com.adyen.model.notification.NotificationRequest;
import com.adyen.util.HMACValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.SignatureException;

/**
 * REST controller for receiving Adyen webhook notifications.
 */
@RestController
public class WebhookApiController {
    private final Logger log = LoggerFactory.getLogger(WebhookApiController.class);

    private final ApplicationProperty applicationProperty;

    @Autowired
    public WebhookApiController(ApplicationProperty applicationProperty) {
        this.applicationProperty = applicationProperty;

        if (this.applicationProperty.getHmacKey() == null) {
            log.warn("ADYEN_HMAC_KEY is UNDEFINED");
        }
    }

    @PostMapping("/api/webhooks/notifications")
    public ResponseEntity<String> webhooks(@RequestBody String json) throws IOException {
        // from JSON string to object
        var notificationRequest = NotificationRequest.fromJson(json);

        // fetch first (and only) NotificationRequestItem
        var notificationRequestItem = notificationRequest.getNotificationItems().stream().findFirst();

        if (notificationRequestItem.isPresent()) {

            var item = notificationRequestItem.get();

            try {
                if (getHmacValidator().validateHMAC(item, this.applicationProperty.getHmacKey())) {
                    log.info("""
                                    Received webhook with event {} :\s
                                    Merchant Reference: {}
                                    Alias : {}
                                    PSP reference : {}"""
                            , item.getEventCode(), item.getMerchantReference(), item.getAdditionalData().get("alias"), item.getPspReference());
                } else {
                    log.warn("Could not validate HMAC signature for incoming webhook message: {}", item);
                    throw new RuntimeException("Invalid HMAC signature");
                }
            } catch (SignatureException e) {
                log.error("Error while validating HMAC Key", e);
            }
        } else {
            log.warn("Empty NotificationItem");
        }
        return ResponseEntity.ok().body("[accepted]");
    }

    @Bean
    public HMACValidator getHmacValidator() {
        return new HMACValidator();
    }
}
