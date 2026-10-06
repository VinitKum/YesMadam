package tests;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.response.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import service.AsyncPaymentClient;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.hamcrest.Matchers.equalTo;
import com.github.tomakehurst.wiremock.stubbing.Scenario;

public class AsyncPaymentTest {
    private String waitForPaymentSuccess(
            AsyncPaymentClient client,
            String baseUrl,
            String paymentId,
            int timeoutSeconds,
            int pollingIntervalSeconds) throws InterruptedException {

        long startTime = System.currentTimeMillis();

        while (true) {

            Response response =
                    client.getPaymentStatus(baseUrl, paymentId);

            response.then()
                    .statusCode(200);

            String status =
                    response.jsonPath().getString("status");

            System.out.println(
                    "Payment Status = " + status
            );

            if ("SUCCESS".equalsIgnoreCase(status)) {
                return status;
            }

            if ("FAILED".equalsIgnoreCase(status)) {
                throw new AssertionError(
                        "Payment processing failed"
                );
            }

            long elapsedSeconds =
                    (System.currentTimeMillis() - startTime) / 1000;

            if (elapsedSeconds >= timeoutSeconds) {
                throw new AssertionError(
                        "Payment did not reach SUCCESS within "
                                + timeoutSeconds + " seconds"
                );
            }

            Thread.sleep(
                    pollingIntervalSeconds * 1000L
            );
        }
    }

    private WireMockServer wireMockServer;

    @BeforeClass
    public void startWireMock() {

        wireMockServer = new WireMockServer(
                options().dynamicPort()
        );

        wireMockServer.start();

        configureFor(
                "localhost",
                wireMockServer.port()
        );
    }

    @Test
    public void verifyAsyncPayment() throws InterruptedException {

        wireMockServer.resetAll();

        // Step 1: POST /payments -> 202 Accepted
        stubFor(
                post(urlEqualTo("/payments"))
                        .inScenario("Payment Processing")
                        .whenScenarioStateIs(Scenario.STARTED)
                        .willReturn(
                                aResponse()
                                        .withStatus(202)
                                        .withHeader(
                                                "Content-Type",
                                                "application/json"
                                        )
                                        .withBody("""
                                                {
                                                  "paymentId": "PAY123"
                                                }
                                                """)
                        )
                        .willSetStateTo("PROCESSING")
        );

        // Step 2: First status check -> PENDING
        stubFor(
                get(urlEqualTo("/payments/PAY123/status"))
                        .inScenario("Payment Processing")
                        .whenScenarioStateIs("PROCESSING")
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader(
                                                "Content-Type",
                                                "application/json"
                                        )
                                        .withBody("""
                                                {
                                                  "paymentId": "PAY123",
                                                  "status": "PENDING"
                                                }
                                                """)
                        )
                        .willSetStateTo("COMPLETED")
        );

        // Step 3: Second status check -> SUCCESS
        stubFor(
                get(urlEqualTo("/payments/PAY123/status"))
                        .inScenario("Payment Processing")
                        .whenScenarioStateIs("COMPLETED")
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader(
                                                "Content-Type",
                                                "application/json"
                                        )
                                        .withBody("""
                                                {
                                                  "paymentId": "PAY123",
                                                  "status": "SUCCESS"
                                                }
                                                """)
                        )
        );

        AsyncPaymentClient client = new AsyncPaymentClient();

        // Initiate payment
        Response initiateResponse =
                client.initiatePayment(
                        wireMockServer.baseUrl()
                );

        initiateResponse.then()
                .statusCode(202)
                .body("paymentId", equalTo("PAY123"));

        String paymentId =
                initiateResponse.jsonPath().getString("paymentId");

        System.out.println(
                "Payment ID = " + paymentId
        );

        String finalStatus =
                waitForPaymentSuccess(
                        client,
                        wireMockServer.baseUrl(),
                        paymentId,
                        10,
                        1
                );

        System.out.println("Final Payment Status = " + finalStatus);
    }

    @AfterClass
    public void stopWireMock()  {

        wireMockServer.stop();
    }
}