package com.example.employee.service;

import com.example.employee.exception.ResourceNotFoundException;
import com.example.employee.model.Employee;
import com.example.employee.repository.EmployeeRepository;
import com.example.employee.web.dto.AddressDTO;
import com.example.employee.web.dto.EmployeeResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AsyncAddressService asyncAddressService;

    public EmployeeService(EmployeeRepository employeeRepository, AsyncAddressService asyncAddressService) {
        this.employeeRepository = employeeRepository;
        this.asyncAddressService = asyncAddressService;
    }

    @Transactional
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeDetails(Long id) throws Exception {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        CompletableFuture<List<AddressDTO>> addressesFuture = asyncAddressService.getAddresses(id);
        List<AddressDTO> addresses = addressesFuture.get(5, TimeUnit.SECONDS);

        return new EmployeeResponse(employee.getId(), employee.getName(), employee.getDepartment(), addresses);
    }

}
