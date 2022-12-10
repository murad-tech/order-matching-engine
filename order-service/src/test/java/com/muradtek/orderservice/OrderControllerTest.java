package com.muradtek.orderservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muradtek.matching.engine.MatchingEngineService;
import com.muradtek.matching.models.Order;
import com.muradtek.matching.models.OrderType;
import com.muradtek.orderservice.controllers.OrderController;
import com.muradtek.orderservice.dto.SubmitOrderReqDto;
import com.muradtek.orderservice.mappers.OrderMapper;
import com.muradtek.orderservice.config.SecurityConfig;

@WebMvcTest(OrderController.class)
@Import({ OrderMapper.class, SecurityConfig.class })
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MatchingEngineService matchingEngineService;

    @Test
    void shouldSubmitOrderSuccessfully() throws Exception {
        SubmitOrderReqDto orderRequest = new SubmitOrderReqDto(
                "AAPL", "BUY", 150.0, 10);

        Order mockOrder = new Order(
                "AAPL", OrderType.BUY, 150.0, 10, System.currentTimeMillis());

        when(matchingEngineService.submitOrder(mockOrder))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.symbol").value("AAPL"))
                .andExpect(jsonPath("$.type").value("BUY"))
                .andExpect(jsonPath("$.price").value(150.0))
                .andExpect(jsonPath("$.quantity").value(10))
                .andExpect(jsonPath("$.trades").isArray())
                .andExpect(jsonPath("$.trades").isEmpty());

        verify(matchingEngineService).submitOrder(any(Order.class));
    }

    @Test
    void shouldReturnBadRequestForInvalidOrder() throws Exception {
        SubmitOrderReqDto invalidOrderRequest = new SubmitOrderReqDto(
                "", "BUY", -150.0, 0);

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidOrderRequest)))
                .andExpect(status().isBadRequest());

        verify(matchingEngineService, org.mockito.Mockito.never()).submitOrder(any(Order.class));
    }

    @Test
    void shouldCancelOrderSuccessfully() throws Exception {
        when(matchingEngineService.cancelOrder("order-123")).thenReturn(true);

        mockMvc.perform(delete("/api/v1/orders/{orderId}", "order-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(matchingEngineService).cancelOrder("order-123");
    }

    @Test
    void shouldReturnNotFoundWhenCancellingNonExistentOrder() throws Exception {
        when(matchingEngineService.cancelOrder("non-existent")).thenReturn(false);

        mockMvc.perform(delete("/api/v1/orders/non-existent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetOrder() throws Exception {
        Order mockOrder = new Order(
                "AAPL", OrderType.BUY, 150.0, 10, System.currentTimeMillis());

        when(matchingEngineService.getOrder(mockOrder.getOrderId())).thenReturn(mockOrder);

        mockMvc.perform(get("/api/v1/orders/{orderId}", mockOrder.getOrderId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(mockOrder.getOrderId()))
                .andExpect(jsonPath("$.symbol").value("AAPL"))
                .andExpect(jsonPath("$.type").value("BUY"));
    }

    @Test
    void shouldReturnNotFoundForNonExistentOrder() throws Exception {
        when(matchingEngineService.getOrder("non-existent")).thenReturn(null);

        mockMvc.perform(get("/api/v1/orders/non-existent"))
                .andExpect(status().isNotFound());
    }
}
