package com.muradtek.orderservice.controllers;

import com.muradtek.matching.engine.MatchingEngineService;
import com.muradtek.matching.models.Order;
import com.muradtek.matching.models.Trade;
import com.muradtek.orderservice.dto.SubmitOrderReqDto;
import com.muradtek.orderservice.dto.SubmitOrderResDto;
import com.muradtek.orderservice.mappers.OrderMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final MatchingEngineService matchingEngineService;
    private final OrderMapper orderMapper;

    public OrderController(MatchingEngineService matchingEngineService,
                           OrderMapper orderMapper) {
        this.matchingEngineService = matchingEngineService;
        this.orderMapper = orderMapper;
    }

    @PostMapping
    public ResponseEntity<SubmitOrderResDto> submitOrder(
            @RequestBody @Valid SubmitOrderReqDto request) throws URISyntaxException {

        Order order = orderMapper.mapToOrder(request);
        List<Trade> trades = matchingEngineService.submitOrder(order);

        return ResponseEntity.created(new URI("/orders/" + order.getOrderId()))
                .body(orderMapper.mapToOrderResDto(order, trades));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(matchingEngineService.getOrder(orderId));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable String orderId) {
        matchingEngineService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
