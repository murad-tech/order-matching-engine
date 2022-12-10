package com.muradtek.orderservice.mappers;

import com.muradtek.matching.models.Order;
import com.muradtek.matching.models.OrderType;
import com.muradtek.matching.models.Trade;
import com.muradtek.orderservice.dto.SubmitOrderReqDto;
import com.muradtek.orderservice.dto.SubmitOrderResDto;
import com.muradtek.orderservice.dto.TradeResDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {
    public Order mapToOrder(SubmitOrderReqDto request) {
        return new Order(
                request.symbol(),
                OrderType.valueOf(request.type()),
                request.price(),
                request.quantity(),
                System.currentTimeMillis());
    }

    public SubmitOrderResDto mapToOrderResDto(Order order, List<Trade> trades) {
        return new SubmitOrderResDto(
                order.getOrderId(),
                order.getSymbol(),
                order.getType().name(),
                order.getPrice(),
                order.getQuantity(),
                order.getRemainingQuantity(),
                order.getStatus().name(),
                order.getTimestamp(),
                trades.stream()
                        .map(this::mapToTradeResDto)
                        .toList());
    }

    private TradeResDto mapToTradeResDto(Trade trade) {
        return new TradeResDto(
                trade.getBuyOrderId(),
                trade.getSellOrderId(),
                trade.getSymbol(),
                trade.getPrice(),
                trade.getQuantity(),
                trade.getTimestamp());
    }
}
