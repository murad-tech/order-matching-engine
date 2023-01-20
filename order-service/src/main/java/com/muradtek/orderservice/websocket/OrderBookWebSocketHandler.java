package com.muradtek.orderservice.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muradtek.matching.engine.OrderBook;
import com.muradtek.matching.models.Order;
import com.muradtek.orderservice.dto.OrderSlimResDto;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrderBookWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
    private final Map<String, String> sessionSymbols = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("Client connected: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        sessionSymbols.remove(session.getId());
        System.out.println("Client disconnected: " + session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            JsonNode node = objectMapper.readTree(message.getPayload());

            // Check if this is a symbol selection message
            if (node.has("action") && "setSymbol".equals(node.get("action").asText())) {
                String symbol = node.get("symbol").asText();
                sessionSymbols.put(session.getId(), symbol);
                System.out.println("Client " + session.getId() + " selected symbol: " + symbol);
            }
        } catch (Exception e) {
            System.err.println("Error handling WebSocket message: " + e.getMessage());
        }
    }

    /**
     * Broadcasts OrderBook object only to clients watching that symbol
     */
    public void broadcastOrderBook(OrderBook orderBook) throws Exception {
        String symbol = orderBook.getSymbol();

        List<OrderSlimResDto> buyOrders = extractOrdersAsList(orderBook.getBuyOrders());
        List<OrderSlimResDto> sellOrders = extractOrdersAsList(orderBook.getSellOrders());

        String message = objectMapper.writeValueAsString(Map.of(
                "symbol", symbol,
                "bestBid", orderBook.getBestBid(),
                "bestAsk", orderBook.getBestAsk(),
                "buyOrders", buyOrders,
                "sellOrders", sellOrders));
        TextMessage textMessage = new TextMessage(message);

        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                String clientSymbol = sessionSymbols.get(session.getId());
                if (clientSymbol != null && clientSymbol.equalsIgnoreCase(symbol)) {
                    try {
                        session.sendMessage(textMessage);
                    } catch (Exception e) {
                        System.err.println("Error sending message to " + session.getId() + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    private List<OrderSlimResDto> extractOrdersAsList(TreeMap<Double, LinkedList<Order>> ordersMap) {
        return ordersMap.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .map(order -> new OrderSlimResDto(
                        order.getOrderId(),
                        order.getSymbol(),
                        order.getType().toString(),
                        order.getPrice(),
                        order.getRemainingQuantity(),
                        order.getStatus().toString()))
                .toList();
    }
}
