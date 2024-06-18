const clientKey = document.getElementById("clientKey").innerHTML;
const type = document.getElementById("type").innerHTML;

// Starts the (Adyen.Web) AdyenCheckout with your specified configuration by calling the `/paymentMethods` endpoint.
async function startCheckout() {
    try {
        // call `/paymentMethods` endpoint
        let paymentMethodsResponse = await sendPostRequest("/api/paymentMethods");

        // configure drop-in
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
                        value: 9999,
                        currency: "EUR",
                    },
                }
            },
            onSubmit: async (state, component) => {
                if (state.isValid) {
                    const response = await sendPostRequest("/api/payments", state.data);
                    handleResponse(response, component);
                }
            },
            onAdditionalDetails: async (state, component) => {
                const response = await sendPostRequest("/api/payments/details", state.data);
                handleResponse(response, component);
            }
        };

        // Initiate AdyenCheckout and mount the element onto the `payment`-div.
        let adyenCheckout = await new AdyenCheckout(configuration);
        adyenCheckout.create(type).mount(document.getElementById("payment"));
    } catch (error) {
        console.error(error);
        alert("Error occurred. Look at console for details.");
    }
}

// Handles responses sent from your server to the client.
function handleResponse(response, component) {
    // If there's an action, handle it, otherwise redirect the user to the correct page based on the resultCode.
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
