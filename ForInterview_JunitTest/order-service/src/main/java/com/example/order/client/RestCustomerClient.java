package com.example.order.client;

import com.example.order.model.CustomerDto;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestCustomerClient implements CustomerClient {

    private final RestTemplate restTemplate;

    public RestCustomerClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public CustomerDto getCustomerById(Long id) {
        String url = "http://localhost:8081/customers/" + id;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-CLIENT", "order-service");

        HttpEntity<Void> entity = new HttpEntity<>(null, headers);

        ResponseEntity<CustomerDto> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                CustomerDto.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }
        throw new RuntimeException("Failed to fetch customer from customer-service");
    }
}
