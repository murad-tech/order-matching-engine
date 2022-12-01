package com.muradtek.matching.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Order {
    private final String orderId = UUID.randomUUID().toString();

    private final String userId;
    private final String symbol;
    private final OrderType type;
    private final double price;
    private final int quantity;                 // integer - fractional shares out of scope
    private int remainingQuantity;   // if there are no enough matching Orders
    private OrderStatus status;
    private final long timestamp;

    public Order(String userId, String symbol, OrderType type, double price, int quantity, long timestamp) {
        this.userId = userId;
        this.symbol = symbol;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.remainingQuantity = quantity;
        this.status = OrderStatus.PENDING;
        this.timestamp = timestamp;
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
}
