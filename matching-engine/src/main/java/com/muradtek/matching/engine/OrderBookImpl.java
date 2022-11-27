package com.muradtek.matching.engine;

import com.muradtek.matching.models.Order;
import com.muradtek.matching.models.OrderStatus;
import com.muradtek.matching.models.OrderType;
import com.muradtek.matching.models.Trade;

import java.util.*;

public class OrderBookImpl implements OrderBook {
    private final String symbol;

    private final TreeMap<Double, LinkedList<Order>> buyOrders;
    private final TreeMap<Double, LinkedList<Order>> sellOrders;

    private final Map<String, Order> orderMap;

    public OrderBookImpl(String symbol) {
        this.symbol = symbol;
        this.buyOrders = new TreeMap<>(Collections.reverseOrder());
        this.sellOrders = new TreeMap<>();
        this.orderMap = new HashMap<>();
    }

    @Override
    public List<Trade> addOrder(Order order) {
        if (!order.getSymbol().equals(symbol)) {
            throw new IllegalArgumentException("Order symbol does not match order book");
        }

        List<Trade> trades = new ArrayList<>();

        // Try to match the order first
        if (order.getType() == OrderType.BUY) {
            trades = matchBuyOrder(order);
        } else {
            trades = matchSellOrder(order);
        }

        // If order still has quantity, add to book
        if (order.isActive() && order.getQuantity() > 0) {
            addToBook(order);
        }

        return trades;
    }

    /**
     * Matches a buy order against sell orders.
     * Buy order matches with sells at price <= buy price.
     */
    private List<Trade> matchBuyOrder(Order buyOrder) {
        List<Trade> trades = new ArrayList<>();

        while (buyOrder.isActive() && !sellOrders.isEmpty()) {
            double bestAskPrice = sellOrders.firstKey();

            // Check if prices cross (buy price >= sell price)
            if (buyOrder.getPrice() < bestAskPrice)
                break; // No match possible

            LinkedList<Order> ordersAtPrice = sellOrders.get(bestAskPrice);
            Order sellOrder = ordersAtPrice.getFirst();

            Trade trade = executeTrade(buyOrder, sellOrder, bestAskPrice);
            trades.add(trade);

            // Remove filled sell order
            if (sellOrder.isFilled()) {
                ordersAtPrice.removeFirst();
                if (ordersAtPrice.isEmpty()) {
                    sellOrders.remove(bestAskPrice);
                }
            }
        }

        return trades;
    }

    /**
     * Matches a sell order against buy orders.
     * Sell order matches with buys when sell price <= buy price.
     */
    private List<Trade> matchSellOrder(Order sellOrder) {
        List<Trade> trades = new ArrayList<>();

        while (sellOrder.isActive() && !buyOrders.isEmpty()) {
            double bestBidPrice = buyOrders.firstKey();

            // Check if prices cross (sell price <= buy price)
            if (sellOrder.getPrice() > bestBidPrice) {
                break; // No match possible
            }

            LinkedList<Order> ordersAtPrice = buyOrders.get(bestBidPrice);
            Order buyOrder = ordersAtPrice.getFirst();

            Trade trade = executeTrade(buyOrder, sellOrder, bestBidPrice);
            trades.add(trade);

            // Remove filled buy order from book
            if (buyOrder.isFilled()) {
                ordersAtPrice.removeFirst();
                if (ordersAtPrice.isEmpty()) {
                    buyOrders.remove(bestBidPrice);
                }
            }
        }

        return trades;
    }

    /**
     * Executes trade between two orders.
     * Trade price is the passive (resting) order's price.
     */
    private Trade executeTrade(Order buyOrder, Order sellOrder, double price) {
        int tradeQuantity = Math.min(buyOrder.getQuantity(), sellOrder.getQuantity());

        buyOrder.reduceQuantity(tradeQuantity);
        sellOrder.reduceQuantity(tradeQuantity);

        if (buyOrder.getRemainingQuantity() == 0) {
            orderMap.remove(buyOrder.getOrderId());
            buyOrder.setStatus(OrderStatus.FILLED);
        } else
            buyOrder.setStatus(OrderStatus.PARTIALLY_FILLED);

        if (sellOrder.getRemainingQuantity() == 0) {
            orderMap.remove(sellOrder.getOrderId());
            sellOrder.setStatus(OrderStatus.FILLED);
        } else
            sellOrder.setStatus(OrderStatus.PARTIALLY_FILLED);

        return new Trade(buyOrder.getOrderId(), sellOrder.getOrderId(), price, tradeQuantity);
    }

    /**
     * Adds order to appropriate side of the book.
     */
    private void addToBook(Order order) {
        TreeMap<Double, LinkedList<Order>> book =
                order.getType() == OrderType.BUY ? buyOrders : sellOrders;

        book.computeIfAbsent(order.getPrice(), k -> new LinkedList<>())
                .addLast(order);

        orderMap.put(order.getOrderId(), order);
    }

    @Override
    public Order cancelOrder(String orderId) {
        Order order = orderMap.get(orderId);
        TreeMap<Double, LinkedList<Order>> book =
                order.getType() == OrderType.BUY ? buyOrders : sellOrders;

        LinkedList<Order> ordersAtPrice = book.get(order.getPrice());
        if (ordersAtPrice != null) {
            ordersAtPrice.remove(order);
            if (ordersAtPrice.isEmpty()) {
                book.remove(order.getPrice());
            }
        }

        return orderMap.remove(order.getOrderId());
    }

    @Override
    public Order getOrder(String orderId) {
        return orderMap.get(orderId);
    }

    @Override
    public double getBestBid() {
        return buyOrders.isEmpty() ? null : buyOrders.firstKey();
    }

    @Override
    public double getBestAsk() {
        return sellOrders.isEmpty() ? null : sellOrders.firstKey();
    }

    @Override
    public int getTotalOrders() {
        return orderMap.size();
    }
}
