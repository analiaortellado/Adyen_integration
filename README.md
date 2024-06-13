# Build Your Own Payment Integration with Adyen - A Step-by-Step Guide

### Prerequisites

_Note: For this workshop, we're asking you to start with a clean Adyen Merchant Account._

You will need a few things to get started:

* an IDE (like IntelliJ or VsCode)
* Java SDK v17+ - You can use any but the project was tested with Java 17.
* Access to an [Adyen Test Account](https://www.adyen.com/signup).

Alternatively, if you do not want to run this locally, you can spin-up this repository in a VM such as [Gitpod](https://gitpod.io/#https://github.com/adyen-examples/adyen-step-by-step-integration-workshop).

## Introduction

This repository is a step-by-step guide to building your own payment integration with Adyen.
We'll guide you through the steps needed to build an Adyen integration and make your first payment on TEST.
This includes the credentials, configuration, API requests (using the Java Adyen library, `/paymentMethods`, `/payments`, `/payments/details` and 3DS2), error handling and webhooks.


### Context of the code repository.

In this workshop, we are using Java and Spring Boot, together with a static (javascript) frontend with a `thymeleaf` template (a server-side Java template engine that can process HTML).

_Note: In case the static frontend environment is not to your liking, feel free to implement your own frontend solution (node) using the framework of your choice._

In this workshop we are not asking you to build a complete integration from scratch, but rather to fill in the voids based on resources you can find in on the [Adyen Documentation](https://docs.adyen.com)
or other online resources ([GitHub](https://github.com/adyen), [GitHub Examples](https://github.com/adyen-examples), [Adyen Help](https://help.adyen.com) etc).
We use an empty Spring Boot template at the start, which you'll extend into a fully working application that can accept payments.

### Project Structure
The project structure follows a Model-View-Controller (MVC) structure.

* The java code is to be found in `src/main/java/com/adyen/workshop`
  * `/controllers`-folder contains your endpoints.
  * `/views`-folder contains the view controllers that show the pages (views).
  * The code you need to update is in the `/controllers` folder. `ApiController.java`.
  * You can add your environment variables (`ADYEN_API_KEY`, `ADYEN_MERCHANT_ACCOUNT`, `ADYEN_CLIENT_KEY`, `ADYEN_HMAC_KEY`) in the `ApplicationConfiguration.java` class.
* The frontend templates are to be found in `src/main/resources/templates` and the static resources in `src/main/resources/static`
  * The code you need to update is in the `src/main/resources/static/adyenWebImplementation.js` and `src/main/resources/templates/layout.html`
* Some additional information:
  * The `clientKey` in `adyenWebImplementation.js` is automatically passed from the backend to the client side and can be accessed using: `clientKey`.
  * In order to play around with multiple payment methods, a `type` value is passed from the client to the server, which contains the name of an adyen payment method and that the adyen web components will recognize.
* To run the project
  * `./gradlew bootJar` will build the project.
  * `./gradlew bootRun` will start the server on port 8080.
  * To run the project from your IDE (e.g. IntelliJ), go to `src/main/java/com/adyen/workshop/MainApplication.java` -> Right-click and click `Debug` or `Run`.



# Workshop: Accepting Online payments using the Advanced flow
Learn how to integrate with Adyen using the `/paymentMethods`, `/payments` and `/payments/details` endpoints

### Briefing

You're working as a full-stack developer for an E-Commerce company that sells headphones and sunglasses in the Netherlands.
In fact, they sell the best headphones and sunglasses at 49.99 each and you're extremely excited to take on this challenge.
You're not afraid of Java, and you can code JavaScript, you're ready for this. You've been tasked to implement credit card, iDeal and Klarna payments using Adyen.


### Learn

In this workshop, you'll learn how to:
1. Retrieve a list of available payment methods using `/paymentMethods`
2. Make a payment using `/payments` followed by `/payments/details`
3. Handle the 3D Secure challenge
4. Present the payment results to the user
5. Receive updates on the payments through webhooks


### Start - Step-by-Step Guide:

Step 0. Build the project and run it to see if it works. If you can visit `http://localhost:8080/hello-world`, `https://8080-adyenexampl-adyenstepby-xxxxxx21.ws-eu114.gitpod.io/hello-world` (gitpod) or `https://xxxx.github.dev/hello-world`, this means it works! In the next steps, we'll guide you through getting the paymentMethods and making your first payment!
     * `./gradlew bootRun` will build and start the application on port 8080.
     * To run the project from your IDE (e.g. IntelliJ), go to `src/main/java/com/adyen/workshop/MainApplication.java` -> Right-click and click `Debug` or `Run`.

**Step 1.** [Get your Adyen Merchant Account](https://docs.adyen.com/account/manage-account-structure/#request-merchant-account).

**Step 2.** [Get your Adyen API Key](https://docs.adyen.com/development-resources/api-credentials/#generate-api-key).
   - Pro-tip #1: Make sure you copy your key correctly.
   - Pro-tip #2: Make 101% sure you copy your key correctly :)


**Step 3.** [Get your Adyen Client Key](https://docs.adyen.com/development-resources/client-side-authentication/#get-your-client-key).
   - Do **not** forget to add the correct URL to my allowed origins (e.g. `http://localhost:8080`, `https://*.gitpod.io` or `https://*.github.dev`
   This allows Adyen.Web Dropin/Components to load on your page. The `*`-symbol indicates to accept any subdomain.

**Step 4.** Add your keys to `ApplicationConfiguration.java` in `/main/java/com/adyen/workshop/configurations`:
   - Best practice: export the vars as follows so that the Spring.Boot framework can inject your variables on startup.
   - If you're using gitpod/codespaces, you can export your variables as follows in your terminal:
   - If you've used gitpod before, the program will inject previously used environment variables as configured in [https://gitpod.io/variables](https://gitpod.io/variables).
```
ADYEN_API_KEY="Aq42....xx"
ADYEN_CLIENT_KEY="test_yourclientkey"
ADYEN_MERCHANT_ACCOUNT="YourMerchantAccountName"
```

You can now access your keys in your application anywhere:
- `applicationConfiguration.getAdyenApiKey()`
- `applicationConfiguration.getAdyenClientKey()`
- `applicationConfiguration.getAdyenMerchantAccount()`.

**Additional context:**

In `/com/adyen/workshop/configurations/`, you'll find a `DependencyInjectionConfiguration`. This is where we create our Adyen instances and **re-use** them using Spring's Constructor Dependency Injection (CDI) - A `@Bean` is an object that is instantiated, assembled, and managed by a Spring IoC container.

**Exercise:** Create your Adyen-`Client` by creating a `new Config()`-object, pass your `ADYEN_API_KEY` and specify `Environment.TEST`, which we'll use later on.
We've created `PaymentsApi`-service (communicates with the Adyen endpoints) and `hmacValidator` instances already for you.

<details>
<summary>Show me the answer</summary>

```java

@Configuration
public class DependencyInjectionConfiguration {
    private final ApplicationConfiguration applicationConfiguration;

    public DependencyInjectionConfiguration(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    @Bean
    Client client() {
        var config = new Config();
        config.setApiKey(applicationConfiguration.getAdyenApiKey());
        config.setEnvironment(Environment.TEST);
        return new Client(config);
    }

    @Bean
    PaymentsApi paymentsApi(){
        return new PaymentsApi(client());
    }

    @Bean
    HMACValidator hmacValidator() { return new HMACValidator(); }
}

```

</details>


**Step 5.** **Skip this step**: Install the [Java library](https://github.com/Adyen/adyen-java-api-library) by adding the following line to the `build.gradle` file, build the project to pull-in the Adyen Java API Library.
For your convenience, we've already included this in the project.

```
	implementation 'com.adyen:adyen-java-api-library:25.1.0'
```


**Step 6.** Install the latest [Adyen.Web Dropin/Components](https://docs.adyen.com/online-payments/release-notes/) by adding embed script(`.js`) and stylesheet(`.css`) to `/resources/templates/layout.html`.
  - Including this allows you to access the AdyenCheckout instance in JavaScript. In this example, we use `Web Components/Drop-in v5.63.0`.




**Step 7.** Let's prepare our backend (`com/adyen/workshop/controllers`) to [retrieve a list of available payment methods](https://docs.adyen.com/online-payments/build-your-integration/advanced-flow/?platform=Web&integration=Drop-in&version=5.63.0&programming_language=java#web-advanced-flow-post-payment-methods-request). Go to `ApiController.java` and use the `paymentsApi` to make `/paymentMethods`-request to Adyen.

<details>
<summary>Show me the answer</summary>

```java
    @PostMapping("/api/paymentMethods")
    public ResponseEntity<PaymentMethodsResponse> paymentMethods() throws IOException, ApiException {
        var paymentMethodsRequest = new PaymentMethodsRequest();

        // Fill in the parameters below
        //paymentMethodsRequest.setMerchantAccount(...);

        log.info("Retrieving available Payment Methods from Adyen {}", paymentMethodsRequest);
        var response = paymentsApi.paymentMethods(paymentMethodsRequest);
        return ResponseEntity.ok()
                .body(response);
    }
```

</details>

**Step 8.** On your frontend (`adyenWebImplementation.js`), let's call this new endpoint and display the payment methods to the shopper.
We automatically pass on your public `ADYEN_CLIENT_KEY` to your frontend, you can access this variable using `clientKey`.
Create the configuration for the `AdyenCheckout`-instance, call the `/api/paymentMethods/`-endpoint, create the `AdyenCheckOut()`-instance and mount it to `"payment"-div` container.

**Note:** We've added a `sendPostRequest(...)` function that can communicate with your backend.


```js
// Starts the (Adyen.Web) AdyenCheckout with your specified configuration by calling the `/paymentMethods` endpoint.
async function startCheckout() {
    try {
        // Step 8 - Retrieve payment methods and instantiate it
        let paymentMethodsResponse = await sendPostRequest("/api/paymentMethods");

        const configuration = {
            paymentMethodsResponse: paymentMethodsResponse,
            clientKey,
            locale: "en_US",
            environment: "test",
            showPayButton: true,
            paymentMethodsConfiguration: {
                card: {
                    hasHolderName: true,
                    holderNameRequired: true,
                    name: "Credit or debit card",
                    amount: {
                        value: 9998,
                        currency: "EUR",
                    },
                }
            },
        };

        // Start the AdyenCheckout and mount the element onto the `payment`-div.
        let adyenCheckout = await new AdyenCheckout(configuration);
        adyenCheckout.create(type).mount(document.getElementById("payment"));
    } catch (error) {
        console.error(error);
        alert("Error occurred. Look at console for details.");
    }
}

// This function sends a POST request to your specified URL,
// the `data`-parameters will be serialized as JSON in the body parameters.
async function sendPostRequest(url, data) {
    const res = await fetch(url, {
        method: "POST",
        body: data ? JSON.stringify(data) : "",
        headers: {
            "Content-Type": "application/json",
        },
    });

    return await res.json();
}

startCheckout();

```


Run your application to see whether the Dropin is showing a list of payment methods. You'll notice that the application will crash when you try to continue as we haven't implemented the payment yet.

**Step 9.** Let's create the `/payments`-request ([see docs](https://docs.adyen.com/online-payments/build-your-integration/advanced-flow/?platform=Web&integration=Drop-in&version=5.63.0&programming_language=java#post-payments-request-web)) on the backend.
We start by defining a new endpoint `/api/payments` that our frontend will send a request to.

<details>
<summary>Show me the answer</summary>

```java
@PostMapping("/api/payments")
public ResponseEntity<PaymentResponse> payments(@RequestHeader String host, @RequestBody PaymentRequest body, HttpServletRequest request) throws IOException, ApiException {
    var paymentRequest = new PaymentRequest();

    var amount = new Amount()
            .currency("EUR")
            .value(9998L);
    paymentRequest.setAmount(amount);
    paymentRequest.setMerchantAccount(...);
    paymentRequest.setChannel(...);

    // Tip: You can get the paymentMethod from the frontend e.g. see: body.getPaymentMethod()
    paymentRequest.setPaymentMethod(...);

    var orderRef = UUID.randomUUID().toString();
    paymentRequest.setReference(orderRef);
    // Once done with the payment, where shall we redirect you?
    paymentRequest.setReturnUrl(request.getScheme() + "://" + host + "/api/handleShopperRedirect?orderRef=" + orderRef); // Example: Turns into http://localhost:8080/api/handleShopperRedirect?orderRef=354fa90e-0858-4d2f-92b9-717cb8e18173


    log.info("PaymentsRequest {}", paymentRequest);
    var response = paymentsApi.payments(paymentRequest);
    return ResponseEntity.ok().body(response);
}
```

</details>

**Step 10.** Best practices: Add the Idempotency key, see [documentation](https://docs.adyen.com/development-resources/api-idempotency/) to your payment request.
```java
var requestOptions = new RequestOptions();
requestOptions.setIdempotencyKey(UUID.randomUUID().toString());

log.info("PaymentsRequest {}", paymentRequest);
var response = paymentsApi.payments(paymentRequest, requestOptions);
return ResponseEntity.ok().body(response);
```

**Step 11.** Let's finalize the payment by calling the `/payments/details`-endpoint. We need to create another endpoint `/api/payments/details` - We take the details passed from the frontend and finalize the payment.
```java
@PostMapping("/api/payments/details")
public ResponseEntity<PaymentDetailsResponse> paymentsDetails(@RequestBody PaymentDetailsRequest detailsRequest) throws IOException, ApiException {
    log.info("PaymentDetailsRequest {}", detailsRequest);
    var response = paymentsApi.paymentsDetails(detailsRequest);
    return ResponseEntity.ok()
            .body(response);
}
```


**Step 12.** Let's now send a request to our backend from our frontend, modify the `adyenWebImplementation.js` to override the `onSubmit(...)` function to call `/api/payments` and `onAdditionaDetails(...)` to call `/api/payments/details`.
We've added a helper function `handleResponse(...)` to do a simple redirect.

<details>
<summary>Show me the answer</summary>

```js
async function startCheckout() {
    try {
        let paymentMethodsResponse = await sendPostRequest("/api/paymentMethods");

        const configuration = {
            paymentMethodsResponse: paymentMethodsResponse,
            clientKey,
            locale: "en_US",
            environment: "test",
            showPayButton: true,
            paymentMethodsConfiguration: {
                card: {
                    hasHolderName: true,
                    holderNameRequired: true,
                    name: "Credit or debit card",
                    amount: {
                        value: 9998,
                        currency: "EUR",
                    },
                }
            },
            // Step 12 onSubmit(...)
            onSubmit: async (state, component) => {
                if (state.isValid) {
                    const response = await sendPostRequest("/api/payments", state.data);
                    handleResponse(response, component);
                }
            },
            // Step 12 onAdditionalDetails(...)
            onAdditionalDetails: async (state, component) => {
                const response = await sendPostRequest("/api/payments/details", state.data);
                handleResponse(response, component);
            }
        };

        // Start the AdyenCheckout and mount the element onto the `payment`-div.
        let adyenCheckout = await new AdyenCheckout(configuration);
        adyenCheckout.create(type).mount(document.getElementById("payment"));
    } catch (error) {
        console.error(error);
        alert("Error occurred. Look at console for details.");
    }
}

// Step 12 - Handles responses, do a simple redirect based on the result.
function handleResponse(response, component) {
    switch (response.resultCode) {
        case "Authorised":
            window.location.href = "/result/success";
            break;
        case "Pending":
        case "Received":
            window.location.href = "/result/pending";
            break;
        case "Refused":
            window.location.href = "/result/failed";
            break;
        default:
            window.location.href = "/result/error";
            break;
    }
}

// ...
```

</details>

You should now be able to make a payment, **however** it will fail when a challenge is presented to the shopper. Let's handle this by adding 3D Secure 2 Authentication support.

3D Secure 2 is an authentication protocol (3DS2) that provides an additional layer of verification for card-not-present (CNP) transactions.
Pick one of these two options:
   * [Native](https://docs.adyen.com/online-payments/3d-secure/native-3ds2/web/): The card issuer performs the authentication within your website or mobile app using passive, biometric, and two-factor authentication approaches. For more information, refer to 3D Secure 2 authentication flows.
   * [Redirect](https://docs.adyen.com/online-payments/3d-secure/redirect-3ds2/web/): Shoppers are redirected to the card issuer's site to provide additional authentication data, for example a password or an SMS verification code. The redirection might lead to lower conversion rates due to technical errors during the redirection, or shoppers dropping out of the authentication process.


Let's add 3DS2 to our `/payments`-request. Note: New to 3DS2? You can read our [docs](https://docs.adyen.com/online-payments/3d-secure/) or go to this [technical blogpost](https://www.adyen.com/knowledge-hub/a-guide-to-integrating-with-adyen-web-for-3d-secure-2-payments) that will guide you through why/what.
Go back to the `ApiController`, we'll need to [add additional properties](https://docs.adyen.com/online-payments/3d-secure/redirect-3ds2/web-drop-in/#payments-request-3ds-redirect-web) to the `PaymentRequest` in `ApiController.java` (in the function `/api/payments/`).

**Note:** In this example, we use the redirect flow. You can also opt-in for the [Native flow](https://docs.adyen.com/online-payments/3d-secure/native-3ds2/web-drop-in/#make-a-payment).


**Step 13.** Let's handle the 3DS2 in our `/payments/details`-request by simply passing the `redirectResult` or `payload` in the `/payments/details`-call.

<details>
<summary>Show me the answer</summary>

```java
// Handle redirect during payment.
@GetMapping("/api/handleShopperRedirect")
public RedirectView redirect(@RequestParam(required = false) String payload, @RequestParam(required = false) String redirectResult) throws IOException, ApiException {
    var paymentDetailsRequest = new PaymentDetailsRequest();

    PaymentCompletionDetails paymentCompletionDetails = new PaymentCompletionDetails();

    // Handle redirect result or payload
    if (redirectResult != null && !redirectResult.isEmpty()) {
        // For redirect, you are redirected to an Adyen domain to complete the 3DS2 challenge
        // After completing the 3DS2 challenge, you get the redirect result from Adyen in the returnUrl
        // We then pass on the redirectResult
        paymentCompletionDetails.redirectResult(redirectResult);
    } else if (payload != null && !payload.isEmpty()) {
        paymentCompletionDetails.payload(payload);
    }

    paymentDetailsRequest.setDetails(paymentCompletionDetails);

    var paymentsDetailsResponse = paymentsApi.paymentsDetails(paymentDetailsRequest);
    log.info("PaymentsDetailsResponse {}", paymentsDetailsResponse);

    // Handle response and redirect user accordingly
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

```

</details>

**Step 14.** We'll have to update our frontend accordingly if there's an action to handle, go to `adyenWebImplementation.js` and modify the `handleResponse(...)`-function:

```js
// Handles responses sent from your server to the client.
function handleResponse(response, component) {
    // Step 14 - If there's an action, handle it, otherwise redirect the user to the correct page based on the resultCode.
    if (response.action) {
        component.handleAction(response.action);
    } else {
        switch (response.resultCode) {
            case "Authorised":
                window.location.href = "/result/success";
                break;
            case "Pending":
            case "Received":
                window.location.href = "/result/pending";
                break;
            case "Refused":
                window.location.href = "/result/failed";
                break;
            default:
                window.location.href = "/result/error";
                break;
        }
    }
}
```


**Step 15.** Handle the response from the API. There are multiple cases, for the workshop, we kept it to the minimal cases needed. See [result codes on docs](https://docs.adyen.com/development-resources/overview-response-handling/#result-codes).

```js
switch (response.resultCode) {
        case "Authorised":
            window.location.href = "/result/success";
            break;
        case "Pending":
        case "Received":
            window.location.href = "/result/pending";
            break;
        case "Refused":
            window.location.href = "/result/failed";
            break;
        default:
            window.location.href = "/result/error";
            break;
    }
```


**Step 16.** Let's test this flow by making a payment using a regular flow and a 3DS flow.

**Note:** For Cards, use the following Visa Test Card number, to trigger a 3DS2 flow. You can also download the official [Adyen Test Card Extension](https://chromewebstore.google.com/detail/adyen-test-cards/icllkfleeahmemjgoibajcmeoehkeoag) to prefill your card numbers, or visit the Adyen [docs](https://docs.adyen.com/development-resources/testing/test-card-numbers/).

```
4871 0499 9999 9910
03/30
737
```


**Step 17.** Receive webhooks by enabling webhooks in the Customer Area and creating your `/webhooks`-endpoint in `Controllers/WebhookController.java`.
   - [Read the docs: Enable and verify HMAC signatures](https://docs.adyen.com/development-resources/webhooks/verify-hmac-signatures/)
   - Create a standard webhook in your Customer Area.
   - Don't forget to inject your `ADYEN_HMAC_KEY` in your `ApplicationConfiguration.java`, which you can then use to verify the HMAC signature.
   - Create a new `WebhookController.java` in `/java/com/adyen/workshop/controllers/WebhookController.java`

<details>
  <summary>Show me the answer</summary>

```java
@PostMapping("/webhooks")
public ResponseEntity<String> webhooks(@RequestBody String json) throws Exception {
    var notificationRequest = NotificationRequest.fromJson(json);
    var notificationRequestItem = notificationRequest.getNotificationItems().stream().findFirst();

    try {
        NotificationRequestItem item = notificationRequestItem.get();

        if (!hmacValidator.validateHMAC(item, this.applicationConfiguration.getAdyenHmacKey())) {
            log.warn("Could not validate HMAC signature for incoming webhook message: {}", item);
            return ResponseEntity.unprocessableEntity().build();
        }

        // Success, log it for now
        log.info("""
                        Received webhook with event {} :\s
                        Merchant Reference: {}
                        Alias : {}
                        PSP reference : {}""",
                item.getEventCode(),
                item.getMerchantReference(),
                item.getAdditionalData().get("alias"),
                item.getPspReference());

        return ResponseEntity.accepted().build();
    } catch (SignatureException e) {
        // Handle invalid signature
        return ResponseEntity.unprocessableEntity().build();
    } catch (Exception e) {
        // Handle all other errors
        return ResponseEntity.status(500).build();
    }
}
```
</details>



**Step 18.** Enable [iDeal](https://docs.adyen.com/payment-methods/ideal/web-drop-in/).
   - Do not forget to enable the payment method in your [Customer Area](https://ca-test.adyen.com/)
   - On the frontend, modify your configuration so that it pre-selects an iDeal issuer + shows the images of the bank logos


**Step 19.** [Enable Klarna](https://docs.adyen.com/payment-methods/klarna/web-drop-in/?tab=_code_payments_code__2), we'll need to add an additional parameter in the payment request.
   - Do not forget to enable the payment method in your [Customer Area](https://ca-test.adyen.com/)
   - Do not forget to add lineItems to your payment-request


## Contacting us

If you have any questions, feel free to contact us at devrel@adyen.com.