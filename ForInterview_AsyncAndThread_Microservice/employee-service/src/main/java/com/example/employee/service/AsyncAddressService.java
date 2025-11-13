package com.example.employee.service;

import com.example.employee.web.dto.AddressDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class AsyncAddressService {

    private final RestTemplate restTemplate;

    @Value("${address.service.base-url}")
    private String addressServiceBaseUrl;

    public AsyncAddressService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async("taskExecutor")//pool in config
    public CompletableFuture<List<AddressDTO>> getAddresses(Long employeeId) {
        String url = addressServiceBaseUrl + "/addresses/employee/" + employeeId;
        ResponseEntity<List<AddressDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<AddressDTO>>() {}
        );
        return CompletableFuture.completedFuture(response.getBody());
    }
    // for test purpose
    public List<AddressDTO> getAddressesInThread(Long employeeId) throws InterruptedException {
        List<AddressDTO> result = new ArrayList<>();

        Thread thread = new Thread(() -> {
            try {
                String url = "http://localhost:8082/addresses/employee/" + employeeId;
                ResponseEntity<List<AddressDTO>> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<List<AddressDTO>>() {}
                );
                result.addAll(response.getBody());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
        thread.join(); // wait for thread to finish
        return result;
    }
    //For non-blocking concurrent calls
    public List<AddressDTO> fetchAddresses(Long employeeId) {
        try {
            CompletableFuture<List<AddressDTO>> future =
                    CompletableFuture.supplyAsync(() -> {
                        String url = "http://localhost:8082/addresses/employee/" + employeeId;
                        ResponseEntity<List<AddressDTO>> response = restTemplate.exchange(
                                url,
                                HttpMethod.GET,
                                HttpEntity.EMPTY,
                                new ParameterizedTypeReference<List<AddressDTO>>() {}
                        );
                        return response.getBody();
                    });

            // wait max 5 seconds for the async thread to finish
            return future.get(5, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while fetching addresses", e);
        } catch (TimeoutException e) {
            throw new RuntimeException("Timeout while fetching addresses", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Error fetching addresses", e.getCause());
        }
    }
}


