package com.muradtek.matching.engine;

import com.muradtek.matching.models.Order;
import com.muradtek.matching.models.Trade;

import java.util.List;

/**
 * Interface for the Matching Engine Service.
 * Manages multiple order books and provides order management operations.
 */
public interface MatchingEngineService {

    /**
     * Submits a new order to the matching engine.
     * @param order Order to submit
     * @return List of trades generated from matching
     */
    List<Trade> submitOrder(Order order);

    /**
     * Cancels an order by orderId.
     * @param orderId Order ID to cancel
     * @return true if cancelled, false if not found or already filled
     */
    boolean cancelOrder(String orderId);

    /**
     * Retrieves an order by orderId.
     * @param orderId Order ID to retrieve
     * @return Order or null if not found
     */
    Order getOrder(String orderId);

    /**
     * Gets best bid price for a symbol.
     * @param symbol Trading symbol
     * @return Best bid price or null if no buy orders
     */
    double getBestBid(String symbol);

    /**
     * Gets best ask price for a symbol.
     * @param symbol Trading symbol
     * @return Best ask price or null if no sell orders
     */
    double getBestAsk(String symbol);

    /**
     * Gets total number of active orders across all symbols.
     * @return Total order count
     */
    int getTotalOrders();

    /**
     * Gets total number of active orders for a specific symbol.
     * @param symbol Trading symbol
     * @return Order count for symbol
     */
    int getTotalOrdersForSymbol(String symbol);

    /**
     * Gets the order book for a specific symbol.
     * @param symbol Trading symbol
     * @return OrderBook or null if symbol does not exist
     */
    OrderBook getOrderBook(String symbol);
}

