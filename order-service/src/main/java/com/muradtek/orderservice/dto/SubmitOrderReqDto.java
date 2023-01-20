package com.muradtek.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record SubmitOrderReqDto(
        @NotBlank
        String symbol,

        @NotBlank @Pattern(regexp = "BUY|SELL", message = "Type must be either BUY or SELL")
        String type,

        @NotNull @Positive
        double price,

        @NotNull @Positive
        int quantity
) {
}
