package tests;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import com.fasterxml.jackson.databind.ObjectMapper;
import tests.model.OrderRequest;
import tests.model.OrderResponse;

public class OrderTest {

    @Test
    public void getOrder() {

        given()
                .baseUri("http://localhost:3001")

                .when()
                .get("/orders/1001")

                .then()
                .statusCode(200)
                .body("id", equalTo("1001"))
                .body("customerId", equalTo(101))
                .body("productId", equalTo(5001))
                .body("quantity", equalTo(2))
                .body("amount", equalTo(1499))
                .body("status", equalTo("CREATED"));
    }


    @Test
    public void createOrderAndVerify() throws Exception {
        String token = "dummy-jwt-token";
        OrderRequest orderRequest =
                new OrderRequest.Builder()
                        .customerId(101)
                        .productId(5002)
                        .quantity(2)
                        .amount(1999)
                        .status("CREATED")
                        .build();

        ObjectMapper objectMapper = new ObjectMapper();

        String requestBody = objectMapper.writeValueAsString(orderRequest);

        System.out.println("JSON Request = " + requestBody);

        Response response =
                given()
                        .baseUri("http://localhost:3001")
                        .contentType("application/json")
                        .body(requestBody)
                        .auth().oauth2(token)

                        .when()
                        .post("/orders");

        response.then()
                .statusCode(201);

        String orderId = response.jsonPath().getString("id");

        System.out.println("Created Order ID = " + orderId);

        OrderResponse orderResponse =
                objectMapper.readValue(response.asString(), OrderResponse.class);

        System.out.println("Order ID = " + orderResponse.getId());
        System.out.println("Order Status = " + orderResponse.getStatus());
        System.out.println("Order Amount = " + orderResponse.getAmount());
    }
}