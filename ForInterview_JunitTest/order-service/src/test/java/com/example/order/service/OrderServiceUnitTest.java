package com.example.order.service;

import com.example.order.client.CustomerClient;
import com.example.order.model.CustomerDto;
import com.example.order.model.OrderDetailsDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceUnitTest {

    @Mock
    private CustomerClient customerClient;

    @InjectMocks
    private OrderService service;

    @Test
    void getOrderDetails_happyPath() {
        when(customerClient.getCustomerById(1L))
                .thenReturn(new CustomerDto(1L, "alice", "Alice Anderson"));

        OrderDetailsDto dto = service.getOrderDetails(10L);

        assertEquals(10L, dto.orderId());
        assertEquals("MacBook Pro", dto.item());
        assertEquals("alice", dto.customer().username());
    }

    @Test
    void getOrderDetails_orderNotFound_throwsException() {
        assertThrows(RuntimeException.class, () -> service.getOrderDetails(999L));
        verifyNoInteractions(customerClient);
    }
}
