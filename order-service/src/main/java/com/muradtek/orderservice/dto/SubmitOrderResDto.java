package com.muradtek.orderservice.dto;

import java.util.List;

public record SubmitOrderResDto(
    String orderId,
    String userId,
    String symbol,
    String type,
    double price,
    int quantity,
    int remainingQuantity,
    String status,
    long timestamp,
    List<TradeResDto> trades
) {
}
