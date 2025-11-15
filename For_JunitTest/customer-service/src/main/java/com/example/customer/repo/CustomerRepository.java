package com.example.customer.repo;

import com.example.customer.model.Customer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class CustomerRepository {

    private final Map<Long, Customer> store = new HashMap<>();

    public CustomerRepository() {
        store.put(1L, new Customer(1L, "alice", "Alice Anderson"));
        store.put(2L, new Customer(2L, "bob", "Bob Brown"));
    }

    public Optional<Customer> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }
}
