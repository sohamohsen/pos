package com.pos.pos_customer.service;

import com.pos.pos_customer.dto.CustomerDto;
import com.pos.pos_customer.model.Customer;
import com.pos.pos_customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    public CustomerDto createCustomer(CustomerDto request) {
        if (customerRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Customer with phone " + request.getPhone() + " already exists.");
        }

        Customer customer = Customer.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .build();

        Customer saved = customerRepository.save(customer);
        return mapToDto(saved);
    }

    public CustomerDto getCustomerById(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        return mapToDto(customer);
    }

    public CustomerDto getCustomerByPhone(String phone) {
        Customer customer = customerRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("Customer not found with phone: " + phone));
        return mapToDto(customer);
    }

    @Transactional
    public CustomerDto addPoints(Integer id, BigDecimal points) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        customer.setLoyaltyPoints(customer.getLoyaltyPoints().add(points));
        return mapToDto(customerRepository.save(customer));
    }

    private CustomerDto mapToDto(Customer c) {
        return CustomerDto.builder()
                .id(c.getId())
                .name(c.getName())
                .phone(c.getPhone())
                .email(c.getEmail())
                .loyaltyPoints(c.getLoyaltyPoints())
                .build();
    }
}
