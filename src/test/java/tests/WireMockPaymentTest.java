package tests;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.response.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import service.PaymentClient;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class WireMockPaymentTest {

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
    public void mockPaymentService() {
        wireMockServer.resetAll();
        stubFor(
                post(urlEqualTo("/payments"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader(
                                                "Content-Type",
                                                "application/json"
                                        )
                                        .withBody("""
                                            {
                                              "paymentId": "PAY12345",
                                              "status": "SUCCESS",
                                              "amount": 1999
                                            }
                                            """)
                        )
        );

        PaymentClient paymentClient = new PaymentClient();

        Response response =
                paymentClient.makePayment(wireMockServer.baseUrl());

        response.then()
                .statusCode(200)
                .body("paymentId", equalTo("PAY12345"))
                .body("status", equalTo("SUCCESS"))
                .body("amount", equalTo(1999))
                .body(matchesJsonSchemaInClasspath("payment-schema.json"));
    }
    @Test
    public void paymentServiceReturns500() {

        stubFor(
                post(urlEqualTo("/payments"))
                        .willReturn(
                                aResponse()
                                        .withStatus(500)
                                        .withHeader(
                                                "Content-Type",
                                                "application/json"
                                        )
                                        .withBody("""
                                            {
                                              "error": "Payment Service Unavailable"
                                            }
                                            """)
                        )
        );

        PaymentClient paymentClient = new PaymentClient();

        Response response =
                paymentClient.makePayment(
                        wireMockServer.baseUrl()
                );

        response.then()
                .statusCode(500)
                .body(
                        "error",
                        equalTo("Payment Service Unavailable")
                );
    }

    @AfterClass
    public void stopWireMock() {
        System.out.println("WireMock URL = " + wireMockServer.baseUrl());
        wireMockServer.stop();
    }
}