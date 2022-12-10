package com.muradtek.matching.engine;

import com.muradtek.matching.models.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderBookTest {

    private OrderBook orderBook;

    @BeforeEach
    void setUp() {
        orderBook = new OrderBookImpl("AAPL");
    }

    @AfterEach
    void tearDown() {
        orderBook = null;
    }

    @Test
    void shouldCreateNewBuyOrder() {
        Order order = new Order("AAPL", OrderType.BUY, 100, 5, System.currentTimeMillis());

        List<Trade> trades = orderBook.addOrder(order);

        assertTrue(trades.isEmpty());
        assertEquals(1, orderBook.getTotalOrders());
        assertEquals(100, orderBook.getBestBid());
    }

    @Test
    void shouldCreateNewSellOrder() {
        Order order = new Order("AAPL", OrderType.SELL, 155, 10, System.currentTimeMillis());

        List<Trade> trades = orderBook.addOrder(order);

        assertTrue(trades.isEmpty());
        assertEquals(1, orderBook.getTotalOrders());
        assertEquals(155, orderBook.getBestAsk());
    }

    @Test
    void shouldMatchBuyOrderWithExistingSellOrder() {
        Order sellOrder = new Order("AAPL", OrderType.SELL, 150, 10, System.currentTimeMillis());
        Order buyOrder = new Order("AAPL", OrderType.BUY, 150, 10, System.currentTimeMillis());

        orderBook.addOrder(sellOrder);
        List<Trade> trades = orderBook.addOrder(buyOrder);

        assertEquals(1, trades.size());
        Trade trade = trades.get(0);
        assertEquals(sellOrder.getOrderId(), trade.getSellOrderId());
        assertEquals(buyOrder.getOrderId(), trade.getBuyOrderId());
        assertEquals(150, trade.getPrice());
        assertEquals(10, trade.getQuantity());
        assertEquals(0, orderBook.getTotalOrders());
    }

    @Test
    void shouldHaveRemainingQuantityAfterMatch() {
        Order sellOrder = new Order("AAPL", OrderType.SELL, 150, 100, System.currentTimeMillis());
        Order buyOrder = new Order("AAPL", OrderType.BUY, 150, 10, System.currentTimeMillis());

        orderBook.addOrder(sellOrder);
        List<Trade> trades = orderBook.addOrder(buyOrder);

        assertEquals(1, trades.size());
        Trade trade = trades.get(0);
        assertEquals(sellOrder.getOrderId(), trade.getSellOrderId());
        assertEquals(buyOrder.getOrderId(), trade.getBuyOrderId());
        assertEquals(150, trade.getPrice());
        assertEquals(10, trade.getQuantity());
        assertEquals(1, orderBook.getTotalOrders());
        assertEquals(90, sellOrder.getRemainingQuantity());
    }

    @Test
    void shouldNotMatchWhenPricesDoNotCross() {
        Order sellOrder = new Order("AAPL", OrderType.SELL, 151, 10, System.currentTimeMillis());
        Order buyOrder = new Order("AAPL", OrderType.BUY, 150, 10, System.currentTimeMillis());

        orderBook.addOrder(sellOrder);
        List<Trade> trades = orderBook.addOrder(buyOrder);

        assertTrue(trades.isEmpty());
        assertEquals(2, orderBook.getTotalOrders());
    }

    @Test
    void shouldMatchWhenSellPriceLowerThanBuyPrice() {
        Order sellOrder = new Order("AAPL", OrderType.SELL, 150, 10, System.currentTimeMillis());
        Order buyOrder = new Order("AAPL", OrderType.BUY, 155, 10, System.currentTimeMillis());

        orderBook.addOrder(sellOrder);
        List<Trade> trades = orderBook.addOrder(buyOrder);

        assertFalse(trades.isEmpty());
        assertEquals(0, orderBook.getTotalOrders());
        assertEquals(150, trades.get(0).getPrice());
    }
}
