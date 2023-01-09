package com.muradtek.orderservice.dto;

public record OrderSlimResDto(
        String orderId,
        String symbol,
        String type,
        double price,
        int remainingQuantity,
        String status
) {
}
