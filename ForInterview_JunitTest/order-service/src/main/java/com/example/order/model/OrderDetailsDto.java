package com.example.order.model;

public record OrderDetailsDto(Long orderId, String item, CustomerDto customer) {
}
