package com.adyen.checkout.controllers;

import com.adyen.checkout.ApplicationProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for using the Adyen payments API.
 */
@RestController
public class ApiController {
    private final Logger log = LoggerFactory.getLogger(ApiController.class);

    private final ApplicationProperty applicationProperty;

    @Autowired
    public ApiController(ApplicationProperty applicationProperty) {
        this.applicationProperty = applicationProperty;
    }

    @GetMapping("/hello-world")
    public ResponseEntity<String> helloWorld() throws Exception {
        return ResponseEntity.ok()
                .body("This is the 'Hello World' from the workshop! You've successfully finished step 0!");
    }
}
