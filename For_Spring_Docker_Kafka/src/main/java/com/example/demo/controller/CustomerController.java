package com.example.demo.controller;

import com.example.demo.dto.CustomerRequest;
import com.example.demo.model.Customer;
import com.example.demo.service.CustomerService;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

     @Autowired
    private final CustomerService service;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "customers-topic";

    public CustomerController(CustomerService service,
                              KafkaTemplate<String, String> kafkaTemplate) {
        this.service = service;
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping
    public List<Customer> findAll() {
        return service.findAll();
    }

    @PostMapping
    public ResponseEntity<Customer> create(@RequestBody CustomerRequest request) {
        Customer customer = new Customer(request.getName(), request.getEmail());
        Customer saved = service.save(customer);

        String message = "New customer created: id=" + saved.getId() + ", name=" + saved.getName();
        kafkaTemplate.send(new ProducerRecord<>(TOPIC, saved.getId().toString(), message));

        return ResponseEntity.ok(saved);
    }


}
