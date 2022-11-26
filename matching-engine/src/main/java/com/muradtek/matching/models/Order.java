package com.muradtek.matching.models;

public class Order {
    private final String orderId;
    private final String userId;

    private final String ticker;
    private final OrderType type;
    private final double price;
    private final int quantity;             // integer - fractional shares out of scope
    private final int remainingQuantity;    // if there are no enough matching Orders
    private final OrderStatus status;
    private final long timestamp;

    Order next;
    Order prev;
    PriceLevel parentLevel;

    public Order(
            String orderId,
            String userId,
            String ticker,
            OrderType type,
            double price,
            int quantity,
            long timestamp
    ) {
        this.orderId = orderId;
        this.userId = userId;
        this.ticker = ticker;
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

    public String getTicker() {
        return ticker;
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
}
