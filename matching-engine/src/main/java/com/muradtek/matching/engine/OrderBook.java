package com.muradtek.matching.engine;

import com.muradtek.matching.models.Order;
import com.muradtek.matching.models.Trade;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

public interface OrderBook {

    /**
     * Adds order to the book and attempts to match it.
     *
     * @return List of trades generated from matching
     */
    List<Trade> addOrder(Order order);

    /**
     * Cancels an order by orderId.
     *
     * @return true if order was cancelled, false if not found or already filled
     */
    Order cancelOrder(String orderId);

    /**
     * Gets order by orderId.
     *
     * @return Order or null if not found
     */
    Order getOrder(String orderId);

    /**
     * @return Best bid price or null if no buy orders
     */
    double getBestBid();

    /**
     * @return Best ask price or null if no sell orders
     */
    double getBestAsk();

    /**
     * @return Total number of active orders in the book
     */
    int getTotalOrders();

    /**
     * @return Buy orders map
     */
    TreeMap<Double, LinkedList<Order>> getBuyOrders();

    /**
     * @return Sell orders map
     */
    TreeMap<Double, LinkedList<Order>> getSellOrders();

    /**
     * @return The symbol for this order book
     */
    String getSymbol();
}
