package com.example.demo.model;


import jakarta.persistence.*;

@Entity
@Table(name = "customers")  // optional but good practice
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;     // or Integer, etc.

    private String name;
    private String email;

    // ✅ JPA needs a no-args constructor
    protected Customer() {
    }

    // convenience constructor
    public Customer(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // getters & setters

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}