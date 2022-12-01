package com.muradtek.orderservice.dto;

import jakarta.validation.constraints.*;

public record SubmitOrderReqDto(
    @NotBlank
    String userId,

    @NotBlank
    String symbol,

    @NotBlank
    @Pattern(regexp = "BUY|SELL", message = "Type must be either BUY or SELL")
    String type,

    @NotNull
    @Positive
    double price,

    @NotNull
    @Positive
    int quantity
) {
}
