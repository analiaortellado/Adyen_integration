# Build Your Own Payment Integration with Adyen - A Step-by-Step Guide

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
  * `./gradlew build` will build the project (you can use this to test the code compiles).
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

0. Build the project and run it to see if it works. If you can visit `http://localhost:8080/hello-world`, this means it works! You won't be able to make a payment yet though and the application will throw an error if you try to proceed.
     * `./gradlew build` will build the project.
     * `./gradlew bootRun` will start the server on port 8080.
     * To run the project from your IDE (e.g. IntelliJ), go to `src/main/java/com/adyen/workshop/OnlinePaymentsApplication.java` -> Right-click and click `Debug` or `Run`.

1. Install the [Java library](https://github.com/Adyen/adyen-java-api-library) by adding the following line to the `build.gradle` file, build the project to pull-in the Adyen Java API Library.

```
	implementation 'com.adyen:adyen-java-api-library:25.1.0'
```

2. [Get your Adyen API Key](https://docs.adyen.com/development-resources/api-credentials/#generate-api-key)

3. [Get your MerchantAccount](https://docs.adyen.com/account/manage-account-structure/#request-merchant-account)

3. [Get your Adyen Client Key](https://docs.adyen.com/development-resources/client-side-authentication/#get-your-client-key)

4. Build the endpoint `/api/paymentMethods`  in `/controllers/ApiController` which retrieves the available payment methods from Adyen
[Payment Methods Request](https://docs.adyen.com/online-payments/build-your-integration/advanced-flow/?platform=Web&integration=API+only&version=71#advanced-flow-post-payment-methods-request)

5. Add `Adyen.Web` using the embed script and stylesheet option in `/resources/templates/layout.html`
   * In `adyenWebImplementation.js`, enter the configuration from the documentation
   * You can use `Web Components/Drop-in v5.63.0` for now.
[Link Adyen.Web release notes](https://docs.adyen.com/online-payments/release-notes/?title%5B0%5D=Web+Components%2FDrop-in)

6. Send a payment request to `/api/payments` in `/controllers/ApiController` to initialize a payment
    * After the payment completes, the Drop-in/Components will need to know where to redirect the user. You can set the `returnUrl` to `http://localhost:8080` for now. Ideally, this would go to a page that shows the result of the payment.
[Payments Request](https://docs.adyen.com/online-payments/build-your-integration/advanced-flow/?platform=Web&integration=API+only&version=71#payments-request-encrypted-card-web)

7. 3D Secure 2 is an authentication protocol that provides an additional layer of verification for card-not-present (CNP) transactions.
Pick one of these two options:
   * [Native](https://docs.adyen.com/online-payments/3d-secure/native-3ds2/web/): The card issuer performs the authentication within your website or mobile app using passive, biometric, and two-factor authentication approaches. For more information, refer to 3D Secure 2 authentication flows.
   * [Redirect](https://docs.adyen.com/online-payments/3d-secure/redirect-3ds2/web/): Shoppers are redirected to the card issuer's site to provide additional authentication data, for example a password or an SMS verification code. The redirection might lead to lower conversion rates due to technical errors during the redirection, or shoppers dropping out of the authentication process.

8. Send a `/payment/details` to finalize the payment.
[Payments Details Request](https://docs.adyen.com/online-payments/build-your-integration/advanced-flow/?tab=3d-secure-redirect-1_2#payments-details-request-6360345697)

9. Handle the response from the API.
[Result codes](https://docs.adyen.com/development-resources/overview-response-handling/#result-codes)

You've successfully completed this workshop if you can make a payment with Cards.

_Note:_ For Cards, use the following Visa Test Card number, to trigger a 3DS2 flow.

```
4871 0499 9999 9910
03/30
737
```

## Contacting us

If you have any questions, feel free to contact us at devrel@adyen.com.

* [Kwok He Chu](https://github.com/Kwok-he-Chu)