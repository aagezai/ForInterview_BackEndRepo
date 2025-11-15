package com.example.order.model;

public class Order {
    private Long id;
    private Long customerId;
    private String item;

    public Order() {
    }

    public Order(Long id, Long customerId, String item) {
        this.id = id;
        this.customerId = customerId;
        this.item = item;
    }

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getItem() {
        return item;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setItem(String item) {
        this.item = item;
    }
}
