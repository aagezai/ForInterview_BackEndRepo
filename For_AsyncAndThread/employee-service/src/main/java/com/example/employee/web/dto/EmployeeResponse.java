package com.example.employee.web.dto;

import java.util.List;

public class EmployeeResponse {
    private Long id;
    private String name;
    private String department;
    private List<AddressDTO> addresses;

    public EmployeeResponse() {}

    public EmployeeResponse(Long id, String name, String department, List<AddressDTO> addresses) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.addresses = addresses;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public List<AddressDTO> getAddresses() { return addresses; }
    public void setAddresses(List<AddressDTO> addresses) { this.addresses = addresses; }
}
