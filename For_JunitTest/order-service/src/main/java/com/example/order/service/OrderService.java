package com.example.order.service;

import com.example.order.client.CustomerClient;
import com.example.order.model.CustomerDto;
import com.example.order.model.Order;
import com.example.order.model.OrderDetailsDto;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderService {

    private final Map<Long, Order> orders = new HashMap<>();
    private final CustomerClient customerClient;

    public OrderService(CustomerClient customerClient) {
        this.customerClient = customerClient;
        orders.put(10L, new Order(10L, 1L, "MacBook Pro"));
        orders.put(11L, new Order(11L, 2L, "iPhone 16"));
    }

    public OrderDetailsDto getOrderDetails(Long orderId) {
        Order order = Optional.ofNullable(orders.get(orderId))
                .orElseThrow(() -> new RuntimeException("Order not found"));

        CustomerDto customer = customerClient.getCustomerById(order.getCustomerId());
        return new OrderDetailsDto(order.getId(), order.getItem(), customer);
    }
}
