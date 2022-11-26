package com.muradtek.matching.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PriceLevelTest {

    @Test
    void testAddOrder() {
        // given
        PriceLevel priceLevel = new PriceLevel(100.0);

        Order order1 = new Order("o1", "u1", "SPY", OrderType.BUY, 100.0, 5, LocalDateTime.now().getNano());
        Order order2 = new Order("o2", "u2", "SPY", OrderType.SELL, 100.0, 5, LocalDateTime.now().getNano());

        // when
        priceLevel.addOrder(order1);
        priceLevel.addOrder(order2);

        // then
        assertEquals(2, priceLevel.getOrderCount());
        assertSame(order1, priceLevel.getHead());
        assertSame(order2, priceLevel.getTail());
        assertSame(priceLevel, order1.parentLevel);
        assertSame(priceLevel, order2.parentLevel);
        assertSame(order2, order1.next);
        assertSame(order1, order2.prev);
        assertEquals(10, priceLevel.getTotalQuantity());
    }

    @Test
    void testRemoveOrder() {
        // given
        PriceLevel priceLevel = new PriceLevel(100.0);

        Order order1 = new Order("o1", "u1", "SPY", OrderType.BUY, 100.0, 5, LocalDateTime.now().getNano());
        Order order2 = new Order("o2", "u2", "SPY", OrderType.BUY, 100.0, 5, LocalDateTime.now().getNano());

        priceLevel.addOrder(order1);
        priceLevel.addOrder(order2);

        // when
        priceLevel.removeOrder(order1);

        // then
        assertEquals(1, priceLevel.getOrderCount());
        assertSame(order2, priceLevel.getHead());
        assertSame(order2, priceLevel.getTail());
        assertSame(priceLevel, order2.parentLevel);
        assertNull(order2.prev);
        assertNull(order2.next);
        assertEquals(5, priceLevel.getTotalQuantity());
    }

    @Test
    void testRemoveLastOrder() {
        // given
        PriceLevel priceLevel = new PriceLevel(100.0);

        Order order1 = new Order("o1", "u1", "SPY", OrderType.BUY, 100.0, 5, LocalDateTime.now().getNano());

        priceLevel.addOrder(order1);

        // when
        priceLevel.removeOrder(order1);

        // then
        assertEquals(0, priceLevel.getOrderCount());
        assertNull(priceLevel.getHead());
        assertNull(priceLevel.getTail());
        assertNull(order1.parentLevel);
        assertNull(order1.prev);
        assertNull(order1.next);
        assertEquals(0, priceLevel.getTotalQuantity());
    }
}
