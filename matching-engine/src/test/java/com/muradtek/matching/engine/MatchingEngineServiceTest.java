package com.muradtek.matching.engine;

import com.muradtek.matching.models.Order;
import com.muradtek.matching.models.OrderStatus;
import com.muradtek.matching.models.OrderType;
import com.muradtek.matching.models.Trade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchingEngineServiceTest {

    private MatchingEngineService matchingEngine;

    @BeforeEach
    void setUp() {
        matchingEngine = new MatchingEngineServiceImpl();
    }

    @Test
    void shouldSubmitBuyOrder() {
        Order order = new Order("o1", "u1",
                "AAPL", OrderType.BUY, 150, 5, System.currentTimeMillis());

        List<Trade> trades = matchingEngine.submitOrder(order);

        assertTrue(trades.isEmpty());
        assertEquals(1, matchingEngine.getTotalOrders());
        assertEquals(1, matchingEngine.getTotalOrdersForSymbol("AAPL"));
        assertEquals(150, matchingEngine.getBestBid("AAPL"));
    }

    @Test
    void shouldSubmitSellOrder() {
        Order order = new Order("o1", "u1",
                "AAPL", OrderType.SELL, 155, 5, System.currentTimeMillis());

        List<Trade> trades = matchingEngine.submitOrder(order);

        assertTrue(trades.isEmpty());
        assertEquals(155.0, matchingEngine.getBestAsk("AAPL"));
    }

    @Test
    void shouldMatchOrdersForSameSymbol() {
        Order sellOrder = new Order("o1", "u1",
                "AAPL", OrderType.SELL, 155, 5, System.currentTimeMillis());
        Order buyOrder = new Order("o2", "u1",
                "AAPL", OrderType.BUY, 155, 5, System.currentTimeMillis());

        matchingEngine.submitOrder(sellOrder);
        List<Trade> trades = matchingEngine.submitOrder(buyOrder);

        assertEquals(1, trades.size());
        Trade trade = trades.get(0);
        assertEquals("AAPL", trade.getSymbol());
        assertEquals(155, trade.getPrice());
        assertEquals(5, trade.getQuantity());
        assertEquals(0, matchingEngine.getTotalOrders());
    }

    @Test
    void shouldHandleMultipleSymbols() {
        Order appleOrder = new Order("o1", "u1",
                "AAPL", OrderType.BUY, 155, 5, System.currentTimeMillis());
        Order googleOrder = new Order("o2", "u1",
                "GOOGL", OrderType.BUY, 155, 5, System.currentTimeMillis());

        matchingEngine.submitOrder(appleOrder);
        matchingEngine.submitOrder(googleOrder);

        assertEquals(2, matchingEngine.getTotalOrders());
        assertEquals(1, matchingEngine.getTotalOrdersForSymbol("AAPL"));
        assertEquals(1, matchingEngine.getTotalOrdersForSymbol("GOOGL"));
        assertEquals(155, matchingEngine.getBestBid("AAPL"));
        assertEquals(155, matchingEngine.getBestBid("GOOGL"));
    }

    @Test
    void shouldNotMatchOrdersForDifferentSymbols() {
        Order sellOrder = new Order("o1", "u1",
                "AAPL", OrderType.SELL, 155, 5, System.currentTimeMillis());
        Order buyOrder = new Order("o2", "u1",
                "GOOGL", OrderType.BUY, 155, 5, System.currentTimeMillis());

        matchingEngine.submitOrder(sellOrder);
        List<Trade> trades = matchingEngine.submitOrder(buyOrder);

        assertTrue(trades.isEmpty());
        assertEquals(2, matchingEngine.getTotalOrders());
    }

    @Test
    void shouldCancelOrder() {
        Order order = new Order("o1", "u1",
                "AAPL", OrderType.BUY, 155, 5, System.currentTimeMillis());
        matchingEngine.submitOrder(order);

        boolean cancelled = matchingEngine.cancelOrder(order.getOrderId());

        assertTrue(cancelled);
        assertEquals(0, matchingEngine.getTotalOrders());
        assertNull(matchingEngine.getOrder(order.getOrderId()));
    }

    @Test
    void shouldReturnFalseWhenCancellingNonExistentOrder() {
        boolean cancelled = matchingEngine.cancelOrder("non-existent-id");
        assertFalse(cancelled);
    }

    @Test
    void shouldRetrieveOrderById() {
        Order order = new Order("o1", "u1",
                "AAPL", OrderType.BUY, 155, 5, System.currentTimeMillis());
        matchingEngine.submitOrder(order);

        Order retrieved = matchingEngine.getOrder(order.getOrderId());

        assertNotNull(retrieved);
        assertEquals(order.getOrderId(), retrieved.getOrderId());
        assertEquals("AAPL", retrieved.getSymbol());
        assertEquals(OrderType.BUY, retrieved.getType());
    }

    @Test
    void shouldReturnNullForNonExistentOrder() {
        Order retrieved = matchingEngine.getOrder("non-existent-id");
        assertNull(retrieved);
    }

    @Test
    void shouldReturnNullBestBidForNonExistentSymbol() {
        assertEquals(0, matchingEngine.getBestBid("NONEXISTENT"));
    }

    @Test
    void shouldThrowExceptionForNullOrder() {
        assertThrows(IllegalArgumentException.class, () -> {
            matchingEngine.submitOrder(null);
        });
    }

    @Test
    void shouldHandlePartialFills() {
        Order sellOrder = new Order("o1", "u1",
                "AAPL", OrderType.SELL, 155, 50, System.currentTimeMillis());
        Order buyOrder = new Order("o2", "u1",
                "AAPL", OrderType.BUY, 155, 5, System.currentTimeMillis());

        matchingEngine.submitOrder(sellOrder);
        List<Trade> trades = matchingEngine.submitOrder(buyOrder);

        assertEquals(1, trades.size());
        assertEquals(5, trades.get(0).getQuantity());
        assertEquals(1, matchingEngine.getTotalOrders()); // Sell order partially filled
        assertEquals(OrderStatus.PARTIALLY_FILLED, sellOrder.getStatus());
        assertEquals(OrderStatus.FILLED, buyOrder.getStatus());
    }
}
