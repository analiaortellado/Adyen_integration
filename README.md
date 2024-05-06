# Build Your Own Payment Integration with Adyen

### Prerequisites

_Note: For this workshop, we're asking you to start from a clean merchant account._

You will need a few things to get started:

* an IDE (like IntelliJ or VsCode)
* Java SDK v17+ - You can use any but the project was tested with Java 17.
* Access to an [Adyen Test Account](https://www.adyen.com/signup).

## Introduction

This repository is a step-by-step guide to building your own payment integration with Adyen. We will start with an Adyen.Web Drop-in/Components integration.


### Context of the code repository.

In this workshop, we are using Java and Spring Boot, together with a static (javascript) frontend based on a `thymeleaf` template.
We use those because we want to reduce the amount of prerequisite knowledge (like a frontend framework) and use a Java library.

_Note: In case the static frontend environment is not to your liking, feel free to implement your own frontend solution using the framework of your choice._

In this workshop we are not asking you to build a complete integration, but rather to fill in the voids based on resources you can find in our docs and other online resources.

### Project Structure
The project structure follows a Model-View-Controller (MVC) structure.

* The java code is to be found in `src/main/java/com/adyen/checkout`
  * `/controllers`-folder contains your endpoints.
  * `/views`-folder contains the view controllers that show the pages (views).
  * The code you need to update is in the `/controllers` folder. `ApiController.java`.
  * You can add your environment variables (`ADYEN_API_KEY`, `ADYEN_MERCHANT_ACCOUNT`, `ADYEN_CLIENT_KEY`, `ADYEN_HMAC_KEY`) via the `ApplicationProperty.java` class.
* The frontend templates are to be found in `src/main/resources/templates` and the static resources in `src/main/resources/static`
  * The code you need to update is in the `src/main/resources/static/adyenWebImplementation.js` and `src/main/resources/templates/layout.html`
* Some additional information:
  * The `clientKey` in `adyenWebImplementation.js` is automatically passed from the backend to the client side and can be accessed using: `clientKey`.
  * In order to play around with multiple payment methods, a `type` value is passed from the client to the server, which contains the name of an adyen payment method and that the adyen web components will recognize.
* To run the project
  * `./gradlew build` will build the project (you can use this to test the code compiles).
  * `./gradlew bootRun` will start the server on port 8080.
  * To run the project from your IDE (e.g. IntelliJ), go to `src/main/java/com/adyen/checkout/OnlinePaymentsApplication.java` -> Right-click and click `Debug` or `Run`.




# Workshop: Accepting Online payments using the `/paymentMethods`, `/payments` and `/payments/details` endpoints

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


### Start - Step-by-step Guide:

0. Build the project and run it to see if it works. If you can visit `http://localhost:8080/hello-world`, this means it works! You won't be able to make a payment yet though and the application will throw an error if you try to proceed.
     * `./gradlew build` will build the project.
     * `./gradlew bootRun` will start the server on port 8080.
     * To run the project from your IDE (e.g. IntelliJ), go to `src/main/java/com/adyen/checkout/OnlinePaymentsApplication.java` -> Right-click and click `Debug` or `Run`.

1. Install the [Java library](https://github.com/Adyen/adyen-java-api-library) by adding the following line to the `build.gradle` file, build the project to pull-in the Adyen Java API Library.

```
	implementation 'com.adyen:adyen-java-api-library:25.1.0'
```

**Note:** For simplicity' sake, we've already included the library for you. You'll find the dependency in the `build.gradle` file already.

2. Send a paymentMethod request to `/api/paymentMethods` get to get the available payment methods in `/controllers/ApiController`

3. Add `Adyen.Web` using the embed script and stylesheet option in `/resources/templates/layout.html`
   * In `adyenWebImplementation.js`, enter the configuration from the documentation - we've already coded the `onSubmit` and `onAdditionalDetails` for you. You don't need to overwrite this.

4. Send a payment request to `/api/payments` to initialize a payment in `/controllers/ApiController`
    * Use `cartService.getTotalAmount()` to pass the `totalAmount`, you can leave the currency as `EUR` for now
    * You can set the `returnUrl` to `request.getScheme() + "://" + host + "/api/handleShopperRedirect?orderRef=YOUR_ORDER_REF")`
      * Instead `request.getScheme()`, you can also hard-code it to `http://"
      * This url handles the shopper redirect. For it to work, you'll just need to pass the correct `orderRef`
    * You can add an idempotency key for each request here as well.

5. Add 3D Secure 2 - Redirect

6. Send a `/payment/details` to finalize the payment.

7. Handle the response from the API after the redirect.


From here, you can add the following two payment methods [iDeal](
https://docs.adyen.com/payment-methods/ideal/web-drop-in/) and [Klarna](
https://docs.adyen.com/payment-methods/klarna/web-drop-in/?tab=_code_payments_code__2).
  * If you're using Components, in `/resources/templates/index.html`, you can uncomment the lines to show the iDEAL and Klarna components.


You've successfully completed this workshop if you can make a payment with Cards, iDEAL and Klarna.

For Cards, use the following Visa Test Card number, to trigger a 3DS2 flow.

```
4871 0499 9999 9910
03/30
737
```

## Contacting us

If you have any questions, feel free to contact us at devrel@adyen.com.

* [Kwok He Chu](https://github.com/Kwok-he-Chu)