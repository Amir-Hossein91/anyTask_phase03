package com.example.phase_03.controller;

import com.example.phase_03.dto.request.CustomerRequestDTO;
import com.example.phase_03.dto.response.CustomerResponseDTO;
import com.example.phase_03.entity.Customer;
import com.example.phase_03.mapper.CustomerMapper;
import com.example.phase_03.service.impl.CustomerServiceImpl;
import com.example.phase_03.service.impl.PersonServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/customer")
public class CustomerController {


    private final CustomerServiceImpl customerService;
    private final PersonServiceImpl personService;

    public CustomerController(CustomerServiceImpl customerService,
                              PersonServiceImpl personService) {
        this.customerService = customerService;
        this.personService = personService;
    }

    @PostMapping("/register")
    public ResponseEntity<CustomerResponseDTO> saveCustomer (@RequestBody @Valid CustomerRequestDTO requestDTO){
        Customer customer = CustomerMapper.INSTANCE.dtoToModel(requestDTO);
        customer.setRegistrationDate(LocalDateTime.now());

        personService.registerCustomer(customer);
        return new ResponseEntity<>(CustomerMapper.INSTANCE.modelToDto(customerService.findById(customer.getId())), HttpStatus.CREATED);
    }

}
