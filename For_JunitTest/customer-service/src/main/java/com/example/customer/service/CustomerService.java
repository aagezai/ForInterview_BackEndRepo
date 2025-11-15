package com.example.customer.service;

import com.example.customer.model.Customer;
import com.example.customer.model.CustomerDto;
import com.example.customer.repo.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository repo;

    public CustomerService(CustomerRepository repo) {
        this.repo = repo;
    }

    public CustomerDto getCustomer(Long id) {
        Customer customer = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return new CustomerDto(customer.getId(), customer.getUsername(), customer.getFullName());
    }
}
