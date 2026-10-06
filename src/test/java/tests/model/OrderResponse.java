package tests.model;

public class OrderResponse {

    private String id;
    private int customerId;
    private int productId;
    private int quantity;
    private double amount;
    private String status;

    public OrderResponse() {
    }

    public String getId() {
        return id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }
}