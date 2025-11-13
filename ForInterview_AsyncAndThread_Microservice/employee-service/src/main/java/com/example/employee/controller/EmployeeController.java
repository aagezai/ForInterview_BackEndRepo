package com.example.employee.controller;

import com.example.employee.model.Employee;
import com.example.employee.service.EmployeeService;
import com.example.employee.web.dto.EmployeeResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public Employee save(@RequestBody Employee employee) {
        return employeeService.saveEmployee(employee);
    }

  /*  @GetMapping
    public EmployeeResponse get(@RequestParam("id") Long id) throws Exception {
        return employeeService.getEmployeeDetails(id);
    }*/
    // GET: /employees/1
    @GetMapping("/{id}")
    public EmployeeResponse getByPath(@PathVariable("id") Long id) throws Exception {
        return employeeService.getEmployeeDetails(id);
    }

    // GET: /employees?id=1
    @GetMapping
    public EmployeeResponse getByQuery(@RequestParam("id") Long id) throws Exception {
        return employeeService.getEmployeeDetails(id);
    }
}
