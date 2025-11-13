package com.example.order.client;

import com.example.order.model.CustomerDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RestCustomerClientUnitTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RestCustomerClient client;

    @Test
    void getCustomerById_successfulExchange_returnsBody() {
        CustomerDto dto = new CustomerDto(1L, "alice", "Alice Anderson");
        ResponseEntity<CustomerDto> response = new ResponseEntity<>(dto, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(CustomerDto.class))
        ).thenReturn(response);

        CustomerDto actual = client.getCustomerById(1L);

        assertEquals("alice", actual.username());
        verify(restTemplate).exchange(
                contains("/customers/1"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(CustomerDto.class)
        );
    }

    @Test
    void getCustomerById_non2xx_throwsException() {
        ResponseEntity<CustomerDto> response = new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(CustomerDto.class))
        ).thenReturn(response);

        assertThrows(RuntimeException.class, () -> client.getCustomerById(1L));
    }
}
