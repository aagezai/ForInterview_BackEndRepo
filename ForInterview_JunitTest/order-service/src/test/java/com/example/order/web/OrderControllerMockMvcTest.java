package com.example.order.web;

import com.example.order.client.CustomerClient;
import com.example.order.model.CustomerDto;
import com.example.order.model.OrderDetailsDto;
import com.example.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerClient customerClient;

    @MockBean
    private OrderService orderService;

    @Test
    void getOrderDetails_returnsJson() throws Exception {
        CustomerDto customer = new CustomerDto(1L, "alice", "Alice Anderson");
        OrderDetailsDto dto = new OrderDetailsDto(10L, "MacBook Pro", customer);

        given(orderService.getOrderDetails(eq(10L))).willReturn(dto);

        mockMvc.perform(get("/orders/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(10))
                .andExpect(jsonPath("$.customer.username").value("alice"));
    }
}
