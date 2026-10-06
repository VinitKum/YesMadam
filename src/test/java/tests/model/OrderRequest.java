package tests.model;

public class OrderRequest {

    private int customerId;
    private int productId;
    private int quantity;
    private double amount;
    private String status;

    public OrderRequest() {
    }

    private OrderRequest(Builder builder) {
        this.customerId = builder.customerId;
        this.productId = builder.productId;
        this.quantity = builder.quantity;
        this.amount = builder.amount;
        this.status = builder.status;
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

    public static class Builder {

        private int customerId;
        private int productId;
        private int quantity;
        private double amount;
        private String status;

        public Builder customerId(int customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder productId(int productId) {
            this.productId = productId;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder amount(double amount) {
            this.amount = amount;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public OrderRequest build() {
            return new OrderRequest(this);
        }
    }
}