package com.adyen.workshop;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OnlinePaymentsApplicationTests {

    @BeforeAll
    public static void onceExecutedBeforeAll() {
        System.setProperty("ADYEN_API_KEY", "testKey");
        System.setProperty("ADYEN_MERCHANT_ACCOUNT", "testAccount");
        System.setProperty("ADYEN_CLIENT_KEY", "testKey");
        System.setProperty("ADYEN_HMAC_KEY", "testHmac");
    }

    @AfterAll
    public static void onceExecutedAfterAll() {
        System.clearProperty("ADYEN_API_KEY");
        System.clearProperty("ADYEN_MERCHANT_ACCOUNT");
        System.clearProperty("ADYEN_CLIENT_KEY");
        System.clearProperty("ADYEN_HMAC_KEY");
    }

}
