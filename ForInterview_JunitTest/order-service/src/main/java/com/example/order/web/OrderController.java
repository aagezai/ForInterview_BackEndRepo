package com.example.order.web;

import com.example.order.model.OrderDetailsDto;
import com.example.order.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public OrderDetailsDto getById(@PathVariable Long id) {
        return service.getOrderDetails(id);
    }
}
