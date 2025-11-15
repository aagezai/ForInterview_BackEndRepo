package com.example.address.controller;

import com.example.address.model.Address;
import com.example.address.repository.AddressRepository;
import com.example.address.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
public class AddressController {

    private final AddressRepository addressRepository;

    public AddressController(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @GetMapping("/employee/{employeeId}")
    public List<Address> getByEmployeeId(@PathVariable("employeeId") Long employeeId) {
        return addressRepository.findByEmployeeId(employeeId);
    }

    @GetMapping("/{id}")
    public Address getOne(@RequestParam("id") Long id) {
        return addressRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Address not found"));
    }

    @PostMapping
    @Transactional
    public Address save(@RequestBody Address address) {
        return addressRepository.save(address);
    }

    @PutMapping("/{id}")
    @Transactional
    public Address update(@RequestParam("id") Long id, @RequestBody Address updated) {
        Address a = addressRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        a.setStreet(updated.getStreet());
        a.setCity(updated.getCity());
        a.setState(updated.getState());
        a.setZip(updated.getZip());
        a.setEmployeeId(updated.getEmployeeId());
        return a;
    }

    @DeleteMapping("/{id}")
    @Transactional
    public void delete(@RequestParam("id") Long id) {
        if (!addressRepository.existsById(id)) throw new ResourceNotFoundException("Address not found");
        addressRepository.deleteById(id);
    }
}
