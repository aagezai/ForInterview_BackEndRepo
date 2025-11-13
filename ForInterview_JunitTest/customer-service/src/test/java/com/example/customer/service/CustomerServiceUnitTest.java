package com.example.customer.service;

import com.example.customer.model.Customer;
import com.example.customer.model.CustomerDto;
import com.example.customer.repo.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceUnitTest {

    @Spy
    private CustomerRepository repo = new CustomerRepository();

    @InjectMocks
    private CustomerService service;

    @Test
    void getCustomer_existingId_returnsDto() {
        CustomerDto dto = service.getCustomer(1L);
        assertEquals(1L, dto.id());
        assertEquals("alice", dto.username());
    }

    @Test
    void getCustomer_unknownId_throwsException() {
        // ensure repo returns empty
        assertThrows(RuntimeException.class, () -> service.getCustomer(999L));
    }
}
