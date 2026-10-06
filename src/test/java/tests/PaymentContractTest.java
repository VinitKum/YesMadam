package tests;
import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import service.PaymentClient;
import java.util.Map;
import static org.hamcrest.Matchers.equalTo;

@PactConsumerTest
public class PaymentContractTest {

    /*
     * This defines the contract between:
     *
     * Consumer = OrderService
     * Provider = PaymentService
     */
    @Pact(
            provider = "PaymentService",
            consumer = "OrderService"
    )
    public V4Pact paymentContract(PactBuilder builder) {

        return builder
                .usingLegacyDsl()

                .given("payment service is available")

                .uponReceiving("create payment request")
                .path("/payments")
                .method("POST")
                .body("""
                {
                  "orderId": "ORD123",
                  "amount": 1999
                }
                """)

                .willRespondWith()
                .status(200)
                .headers(
                        Map.of(
                                "Content-Type",
                                "application/json"
                        )
                )
                .body("""
                {
                  "paymentId": "PAY12345",
                  "status": "SUCCESS",
                  "amount": 1999
                }
                """)

                .toPact(V4Pact.class);
    }


    /*
     * Actual consumer test.
     *
     * Pact starts a mock Payment Service.
     * PaymentClient sends a real HTTP request to that mock server.
     */
    @Test
    @PactTestFor(
            providerName = "PaymentService",
            pactMethod = "paymentContract"
    )
    public void verifyPaymentContract(MockServer mockServer) {

        System.out.println(
                "Pact Mock Server = " + mockServer.getUrl()
        );

        PaymentClient paymentClient = new PaymentClient();

        Response response =
                paymentClient.makePayment(
                        mockServer.getUrl()
                );

        response.then()
                .statusCode(200)
                .body("paymentId", equalTo("PAY12345"))
                .body("status", equalTo("SUCCESS"))
                .body("amount", equalTo(1999));
    }
}