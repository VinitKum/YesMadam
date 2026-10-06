package service;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class PaymentClient {

    public Response makePayment(String baseUrl) {
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
}