package com.muradtek.matching.models;

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

    public String getBuyOrderId() {
        return buyOrderId;
    }
    public String getSellOrderId() {
        return sellOrderId;
    }
    public double getPrice() {
        return price;
    }
    public int getQuantity() {
        return quantity;
    }
    public long getTimestamp() {
        return timestamp;
    }

    public String getSymbol() {
        return symbol;
    }
}
