package com.muradtek.matching.models;

import lombok.Getter;

@Getter
public class Trade {
    private final String buyOrderId;
    private final String sellOrderId;
    private final String symbol;
    private final double price;
    private final int quantity;
    private final long timestamp;

    public Trade(String buyOrderId, String sellOrderId, String symbol, double price, int quantity) {
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.symbol = symbol;
        this.price = price;
        this.quantity = quantity;
        this.timestamp = System.currentTimeMillis();
    }
}
