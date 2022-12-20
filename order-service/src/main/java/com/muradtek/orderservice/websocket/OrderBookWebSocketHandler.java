package com.muradtek.orderservice.websocket;

import com.muradtek.matching.engine.OrderBook;
import com.muradtek.matching.models.Order;
import com.muradtek.orderservice.dto.OrderSlimResDto;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

@Component
public class OrderBookWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("Client connected: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("Client disconnected: " + session.getId());
    }

    /**
     * Broadcasts OrderBook object to all connected clients
     */
    public void broadcastOrderBook(OrderBook orderBook) throws Exception {
        List<OrderSlimResDto> buyOrders = extractOrdersAsList(orderBook.getBuyOrders());
        List<OrderSlimResDto> sellOrders = extractOrdersAsList(orderBook.getSellOrders());

        String message = objectMapper.writeValueAsString(Map.of(
                "bestBid", orderBook.getBestBid(),
                "bestAsk", orderBook.getBestAsk(),
                "buyOrders", buyOrders,
                "sellOrders", sellOrders
        ));
        TextMessage textMessage = new TextMessage(message);

        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(textMessage);
                } catch (Exception e) {
                    System.err.println("Error sending message: " + e.getMessage());
                }
            }
        }
    }

    private List<OrderSlimResDto> extractOrdersAsList(TreeMap<Double, LinkedList<Order>> ordersMap) {
        return ordersMap.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .map(order -> new OrderSlimResDto(
                        order.getSymbol(),
                        order.getType().toString(),
                        order.getPrice(),
                        order.getRemainingQuantity(),
                        order.getStatus().toString()
                ))
                .toList();
    }
}
