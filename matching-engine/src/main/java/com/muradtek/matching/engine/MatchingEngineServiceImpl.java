package com.muradtek.matching.engine;

import com.muradtek.matching.models.Order;
import com.muradtek.matching.models.OrderStatus;
import com.muradtek.matching.models.Trade;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MatchingEngineServiceImpl implements MatchingEngineService {

    // Map of symbol -> OrderBook
    private final Map<String, OrderBook> orderBooks;

    // Map of orderId -> symbol for fast lookups
    private final Map<String, String> orderToSymbolMap;

    public MatchingEngineServiceImpl() {
        this.orderBooks = new ConcurrentHashMap<>();
        this.orderToSymbolMap = new ConcurrentHashMap<>();
    }

    @Override
    public List<Trade> submitOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }

        String symbol = order.getSymbol();

        // Get or create order book for symbol
        OrderBook orderBook = orderBooks.computeIfAbsent(
                symbol,
                k -> new OrderBookImpl(symbol)
        );

        // Track order-to-symbol mapping
        orderToSymbolMap.put(order.getOrderId(), symbol);

        // Submit order to order book
        return orderBook.addOrder(order);
    }

    @Override
    public boolean cancelOrder(String orderId) {
        if (orderId == null)
            return false;

        // Find which symbol this order belongs to
        String symbol = orderToSymbolMap.get(orderId);
        if (symbol == null)
            return false;

        OrderBook orderBook = orderBooks.get(symbol);
        if (orderBook == null)
            return false;

        boolean cancelled = orderBook.cancelOrder(orderId).getStatus() == OrderStatus.CANCELED;

        // Remove from tracking map if cancelled
        if (cancelled)
            orderToSymbolMap.remove(orderId);

        return cancelled;
    }

    @Override
    public Order getOrder(String orderId) {
        if (orderId == null) {
            return null;
        }

        String symbol = orderToSymbolMap.get(orderId);
        if (symbol == null) {
            return null;
        }

        OrderBook orderBook = orderBooks.get(symbol);
        return orderBook != null ? orderBook.getOrder(orderId) : null;
    }

    @Override
    public double getBestBid(String symbol) {
        OrderBook orderBook = orderBooks.get(symbol);
        return orderBook != null ? orderBook.getBestBid() : 0;
    }

    @Override
    public double getBestAsk(String symbol) {
        OrderBook orderBook = orderBooks.get(symbol);
        return orderBook != null ? orderBook.getBestAsk() : 0.0;
    }

    @Override
    public int getTotalOrders() {
        Collection<OrderBook> orders = orderBooks.values();
        int sum = 0;

        for (OrderBook orderBook : orders)
            sum += orderBook.getTotalOrders();

        return sum;
    }

    @Override
    public int getTotalOrdersForSymbol(String symbol) {
        OrderBook orderBook = orderBooks.get(symbol);
        return orderBook != null ? orderBook.getTotalOrders() : 0;
    }

    /**
     * Gets the order book for a specific symbol (for testing/monitoring).
     */
    protected OrderBook getOrderBook(String symbol) {
        return orderBooks.get(symbol);
    }
}
