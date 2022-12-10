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
        Order order = new Order("AAPL", OrderType.BUY, 150, 5, System.currentTimeMillis());

        List<Trade> trades = matchingEngine.submitOrder(order);

        assertTrue(trades.isEmpty());
        assertEquals(1, matchingEngine.getTotalOrders());
        assertEquals(1, matchingEngine.getTotalOrdersForSymbol("AAPL"));
        assertEquals(150, matchingEngine.getBestBid("AAPL"));
    }

    @Test
    void shouldSubmitSellOrder() {
        Order order = new Order("AAPL", OrderType.SELL, 155, 5, System.currentTimeMillis());

        List<Trade> trades = matchingEngine.submitOrder(order);

        assertTrue(trades.isEmpty());
        assertEquals(155.0, matchingEngine.getBestAsk("AAPL"));
    }

    @Test
    void shouldMatchPartialBuyAndUpdateRemainingQuantity() {
        Order sellOrder = new Order("AAPL", OrderType.SELL, 155, 5, System.currentTimeMillis());
        Order buyOrder = new Order("AAPL", OrderType.BUY, 155, 10, System.currentTimeMillis());

        matchingEngine.submitOrder(sellOrder);
        List<Trade> trades = matchingEngine.submitOrder(buyOrder);

        assertEquals(1, trades.size());
        Trade trade = trades.get(0);
        assertEquals("AAPL", trade.getSymbol());
        assertEquals(155, trade.getPrice());
        assertEquals(5, trade.getQuantity());
        assertEquals(1, matchingEngine.getTotalOrders()); // Buy order partially filled
        assertEquals(OrderStatus.PARTIALLY_FILLED, buyOrder.getStatus());
        assertEquals(OrderStatus.FILLED, sellOrder.getStatus());
        assertEquals(0, sellOrder.getRemainingQuantity());
        assertEquals(5, buyOrder.getRemainingQuantity());
    }

    @Test
    void shouldMatchPartialSellAndUpdateRemainingQuantity() {
        Order buyOrder = new Order("AAPL", OrderType.BUY, 155, 5, System.currentTimeMillis());
        Order sellOrder = new Order("AAPL", OrderType.SELL, 155, 10, System.currentTimeMillis());

        matchingEngine.submitOrder(buyOrder);
        List<Trade> trades = matchingEngine.submitOrder(sellOrder);

        assertEquals(1, trades.size());
        Trade trade = trades.get(0);
        assertEquals("AAPL", trade.getSymbol());
        assertEquals(155, trade.getPrice());
        assertEquals(5, trade.getQuantity());
        assertEquals(1, matchingEngine.getTotalOrders()); // Buy order partially filled
        assertEquals(OrderStatus.PARTIALLY_FILLED, sellOrder.getStatus());
        assertEquals(OrderStatus.FILLED, buyOrder.getStatus());
        assertEquals(0, buyOrder.getRemainingQuantity());
        assertEquals(5, sellOrder.getRemainingQuantity());
    }

    @Test
    void shouldProcessMultipleBuyOrders() {
        Order sellOrder = new Order("AAPL", OrderType.SELL, 155, 100, System.currentTimeMillis());
        Order buyOrder1 = new Order("AAPL", OrderType.BUY, 155, 30, System.currentTimeMillis());
        Order buyOrder2 = new Order("AAPL", OrderType.BUY, 155, 100, System.currentTimeMillis());

        matchingEngine.submitOrder(sellOrder);
        List<Trade> trades = matchingEngine.submitOrder(buyOrder1);
        assertEquals(1, trades.size());
        assertEquals(30, trades.get(0).getQuantity());
        assertEquals(70, sellOrder.getRemainingQuantity());

        trades = matchingEngine.submitOrder(buyOrder2);
        assertEquals(1, trades.size());
        assertEquals(70, trades.get(0).getQuantity());
        assertEquals(0, sellOrder.getRemainingQuantity());
        assertEquals(30, buyOrder2.getRemainingQuantity());
    }

    @Test
    void shouldProcessMultipleSellOrders() {
        Order buyOrder = new Order("AAPL", OrderType.BUY, 155, 100, System.currentTimeMillis());
        Order sellOrder1 = new Order("AAPL", OrderType.SELL, 155, 30, System.currentTimeMillis());
        Order sellOrder2 = new Order("AAPL", OrderType.SELL, 155, 100, System.currentTimeMillis());

        matchingEngine.submitOrder(buyOrder);
        List<Trade> trades = matchingEngine.submitOrder(sellOrder1);
        assertEquals(1, trades.size());
        assertEquals(30, trades.get(0).getQuantity());
        assertEquals(70, buyOrder.getRemainingQuantity());

        trades = matchingEngine.submitOrder(sellOrder2);
        assertEquals(1, trades.size());
        assertEquals(70, trades.get(0).getQuantity());
        assertEquals(0, buyOrder.getRemainingQuantity());
        assertEquals(30, sellOrder2.getRemainingQuantity());
    }

    @Test
    void shouldMatchTwoBuyOrdersWithOneSell() {
        Order buyOrder1 = new Order("AAPL", OrderType.BUY, 155, 30, System.currentTimeMillis());
        Order buyOrder2 = new Order("AAPL", OrderType.BUY, 155, 70, System.currentTimeMillis());
        Order sellOrder = new Order("AAPL", OrderType.SELL, 155, 200, System.currentTimeMillis());

        matchingEngine.submitOrder(buyOrder1);
        matchingEngine.submitOrder(buyOrder2);
        List<Trade> trades = matchingEngine.submitOrder(sellOrder);
        assertEquals(2, trades.size());
        assertEquals(30, trades.get(0).getQuantity());
        assertEquals(70, trades.get(1).getQuantity());
        assertEquals(100, sellOrder.getRemainingQuantity());
        assertEquals(0, buyOrder1.getRemainingQuantity());
        assertEquals(0, buyOrder2.getRemainingQuantity());
    }

    @Test
    void shouldMatchTwoSellOrdersWithOneBuy() {
        Order sellOrder1 = new Order("AAPL", OrderType.BUY, 155, 30, System.currentTimeMillis());
        Order sellOrder2 = new Order("AAPL", OrderType.BUY, 155, 70, System.currentTimeMillis());
        Order buyOrder = new Order("AAPL", OrderType.SELL, 155, 200, System.currentTimeMillis());

        matchingEngine.submitOrder(sellOrder1);
        matchingEngine.submitOrder(sellOrder2);
        List<Trade> trades = matchingEngine.submitOrder(buyOrder);
        assertEquals(2, trades.size());
        assertEquals(30, trades.get(0).getQuantity());
        assertEquals(70, trades.get(1).getQuantity());
        assertEquals(100, buyOrder.getRemainingQuantity());
        assertEquals(0, sellOrder1.getRemainingQuantity());
        assertEquals(0, sellOrder2.getRemainingQuantity());
    }

    @Test
    void shouldMatchOrdersForSameSymbol() {
        Order sellOrder = new Order("AAPL", OrderType.SELL, 155, 5, System.currentTimeMillis());
        Order buyOrder = new Order("AAPL", OrderType.BUY, 155, 5, System.currentTimeMillis());

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
        Order appleOrder = new Order("AAPL", OrderType.BUY, 155, 5, System.currentTimeMillis());
        Order googleOrder = new Order("GOOGL", OrderType.BUY, 155, 5, System.currentTimeMillis());

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
        Order sellOrder = new Order("AAPL", OrderType.SELL, 155, 5, System.currentTimeMillis());
        Order buyOrder = new Order("GOOGL", OrderType.BUY, 155, 5, System.currentTimeMillis());

        matchingEngine.submitOrder(sellOrder);
        List<Trade> trades = matchingEngine.submitOrder(buyOrder);

        assertTrue(trades.isEmpty());
        assertEquals(2, matchingEngine.getTotalOrders());
    }

    @Test
    void shouldCancelOrder() {
        Order order = new Order("AAPL", OrderType.BUY, 155, 5, System.currentTimeMillis());
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
        Order order = new Order("AAPL", OrderType.BUY, 155, 5, System.currentTimeMillis());
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
        Order sellOrder = new Order("AAPL", OrderType.SELL, 155, 50, System.currentTimeMillis());
        Order buyOrder = new Order("AAPL", OrderType.BUY, 155, 5, System.currentTimeMillis());

        matchingEngine.submitOrder(sellOrder);
        List<Trade> trades = matchingEngine.submitOrder(buyOrder);

        assertEquals(1, trades.size());
        assertEquals(5, trades.get(0).getQuantity());
        assertEquals(1, matchingEngine.getTotalOrders()); // Sell order partially filled
        assertEquals(OrderStatus.PARTIALLY_FILLED, sellOrder.getStatus());
        assertEquals(OrderStatus.FILLED, buyOrder.getStatus());
    }
}
