package com.muradtek.orderservice.websocket;

import com.muradtek.matching.engine.MatchingEngineService;
import com.muradtek.matching.engine.OrderBook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderBookBroadcaster {

    private final OrderBookWebSocketHandler webSocketHandler;
    @Autowired
    private final MatchingEngineService matchingEngineService;

    public OrderBookBroadcaster(OrderBookWebSocketHandler webSocketHandler,
                                MatchingEngineService matchingEngineService) {
        this.webSocketHandler = webSocketHandler;
        this.matchingEngineService = matchingEngineService;
    }

    /**
     * Broadcasts current order book for a symbol to all connected clients
     */
    public void broadcastOrderBook(String ticker) throws Exception {
        OrderBook orderBook = matchingEngineService.getOrderBook(ticker);
        if (orderBook != null)
            webSocketHandler.broadcastOrderBook(orderBook);
    }
}
