package com.muradtek.matching.models;

public class Order {
    private final String orderId;
    private final String userId;

    private final String symbol;
    private final OrderType type;
    private final double price;
    private final int quantity;             // integer - fractional shares out of scope
    private int remainingQuantity;          // if there are no enough matching Orders
    private OrderStatus status;
    private final long timestamp;

    public Order(
            String orderId,
            String userId,
            String symbol,
            OrderType type,
            double price,
            int quantity,
            long timestamp
    ) {
        this.orderId = orderId;
        this.userId = userId;
        this.symbol = symbol;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.remainingQuantity = quantity;
        this.status = OrderStatus.PENDING;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }

    public String getSymbol() {
        return symbol;
    }

    public OrderType getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getRemainingQuantity() {
        return remainingQuantity;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isActive() {
        return status == OrderStatus.PENDING || status == OrderStatus.PARTIALLY_FILLED;
    }

    public boolean isFilled() {
        return status == OrderStatus.FILLED;
    }

    public void reduceQuantity(int tradeQuantity) {
        // Implementation to reduce remaining quantity and update status
        this.remainingQuantity -= tradeQuantity;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
