package service;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class PaymentService {

    public static void main(String[] args) throws Exception {

        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(8089),
                        0
                );

        server.createContext(
                "/payments",
                PaymentService::handlePayment
        );

        server.start();

        System.out.println(
                "Payment Service started at http://localhost:8089"
        );
    }

    private static void handlePayment(HttpExchange exchange)
            throws IOException {

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {

            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }

        String response = """
                {
                  "paymentId": "PAY12345",
                  "status": "SUCCESS",
                  "amount": 1999
                }
                """;

        exchange.getResponseHeaders()
                .set("Content-Type", "application/json");

        byte[] responseBytes =
                response.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(
                200,
                responseBytes.length
        );

        exchange.getResponseBody()
                .write(responseBytes);

        exchange.close();
    }
}