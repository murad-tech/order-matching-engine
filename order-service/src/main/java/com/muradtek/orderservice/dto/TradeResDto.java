package com.muradtek.orderservice.dto;

public record TradeResDto (
    String buyOrderId,
    String sellOrderId,
    String symbol,
    double price,
    int quantity,
    long timestamp
) {
}
