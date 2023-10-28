package com.example.phase_03.service;

import com.example.phase_03.baseService.BaseService;
import com.example.phase_03.entity.Customer;
import com.example.phase_03.entity.Order;
import com.example.phase_03.entity.Technician;
import com.example.phase_03.entity.TechnicianSuggestion;

import java.util.List;

public interface OrderService extends BaseService<Order> {

    List<Order> findRelatedOrders(Technician technician);
    void sendTechnicianSuggestion(Technician technician, Order order, TechnicianSuggestion technicianSuggestion);
    List<Order> findByCustomer(Customer customer);
}
