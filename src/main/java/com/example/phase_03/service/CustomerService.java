package com.example.phase_03.service;

import com.example.phase_03.baseService.BaseService;
import com.example.phase_03.entity.Customer;

public interface CustomerService extends BaseService<Customer> {
    Customer findByUsername (String customerUsername);
}
