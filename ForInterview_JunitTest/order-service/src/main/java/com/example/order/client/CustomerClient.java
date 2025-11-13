package com.example.order.client;

import com.example.order.model.CustomerDto;

public interface CustomerClient {
    CustomerDto getCustomerById(Long id);
}
