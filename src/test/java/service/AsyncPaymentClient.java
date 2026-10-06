package service;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AsyncPaymentClient {

    public Response initiatePayment(String baseUrl) {

        return given()
                .baseUri(baseUrl)
                .contentType("application/json")
                .body("""
                        {
                          "orderId": "ORD123",
                          "amount": 1999
                        }
                        """)
                .when()
                .post("/payments");
    }

    public Response getPaymentStatus(String baseUrl, String paymentId) {

        return given()
                .baseUri(baseUrl)

                .when()
                .get("/payments/" + paymentId + "/status");
    }
}