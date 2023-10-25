package com.example.phase_03.service.impl;

import com.example.phase_03.baseService.impl.BaseServiceImpl;
import com.example.phase_03.entity.*;
import com.example.phase_03.entity.dto.OrderDTO;
import com.example.phase_03.entity.enums.OrderStatus;
import com.example.phase_03.exceptions.NotFoundException;
import com.example.phase_03.repository.OrderRepository;
import com.example.phase_03.service.OrderService;
import com.example.phase_03.utility.Constants;
import jakarta.persistence.PersistenceException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class OrderServiceImpl extends BaseServiceImpl<Order> implements OrderService {

    private final OrderRepository repository;
    private final ManagerServiceImpl managerService;
    private final CustomerServiceImpl customerService;
    private final AssistanceServiceImpl assistanceService;
    private final SubAssistanceServiceImpl subAssistanceService;
    private final OrderDescriptionServiceImpl orderDescriptionService;

    public OrderServiceImpl(OrderRepository repository,
                            ManagerServiceImpl managerService,
                            @Lazy CustomerServiceImpl customerService,
                            @Lazy AssistanceServiceImpl assistanceService,
                            @Lazy SubAssistanceServiceImpl subAssistanceService, OrderDescriptionServiceImpl orderDescriptionService) {
        super();
        this.repository = repository;
        this.managerService = managerService;
        this.customerService = customerService;
        this.assistanceService = assistanceService;
        this.subAssistanceService = subAssistanceService;
        this.orderDescriptionService = orderDescriptionService;
    }

    public List<String> showAllOrders(String managerUsername){
        Manager manager = managerService.findByUsername(managerUsername);
        if(manager != null){
            return findAll().stream().map(Object::toString).toList();
        }
        else{
            printer.printError("Only manager can see the list of all orders");
            return List.of();
        }
    }

    public Order makeOrder(String customerUsername, String assistanceTitle, String subAssistanceTitle, OrderDescription orderDescription){
        Customer customer = customerService.findByUsername(customerUsername);
        if( customer != null){
            try{
                Assistance assistance = assistanceService.findAssistance(assistanceTitle);
                if(assistance == null)
                    throw new NotFoundException(Constants.ASSISTANCE_NOT_FOUND);

                SubAssistance subAssistance = subAssistanceService.findSubAssistance(subAssistanceTitle,assistance);
                if(subAssistance == null)
                    throw new NotFoundException(Constants.NO_SUCH_SUBASSISTANCE);

                if(orderDescription.getCustomerSuggestedPrice()<subAssistance.getBasePrice())
                    throw new IllegalArgumentException(Constants.INVALID_SUGGESTED_PRICE);

                if(orderDescription.getCustomerDesiredDateAndTime().isBefore(LocalDateTime.now()))
                    throw new IllegalArgumentException(Constants.DATE_BEFORE_NOW);

                Order order = Order.builder().subAssistance(subAssistance).customer(customer)
                        .orderRegistrationDateAndTime(LocalDateTime.now()).orderDescription(orderDescription)
                        .orderStatus(OrderStatus.WAITING_FOR_TECHNICIANS_SUGGESTIONS)
                        .technicianScore(1).build();

                order = saveOrUpdate(order);

                if(order != null)
                    printer.printMessage("Order saved successfully with id of: " + order.getId());
                return order;
            } catch (NotFoundException | DateTimeException | IllegalArgumentException e) {
                printer.printError(e.getMessage());
                return null;
            }
        }
        else {
            printer.printError("Only a customer can make an order");
            return null;
        }
    }

    @Override
    public Order saveOrUpdate(Order t) {
        if(!isValid(t))
            return null;
        try{
            return repository.save(t);
        } catch (RuntimeException e){
            printer.printError(e.getMessage());
            printer.printError(Arrays.toString(e.getStackTrace()));
            input.nextLine();
            return null;
        }
    }

    @Override
    public void delete(Order t) {
        if(!isValid(t))
            return;
        try{
            repository.delete(t);
        } catch (RuntimeException e){
            if(e instanceof PersistenceException)
                printer.printError("Could not delete " + repository.getClass().getSimpleName());
            else
                printer.printError("Could not complete deletion. Specified " + repository.getClass().getSimpleName() + " not found!");
            printer.printError(Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public Order findById(long id) {
        try{
            return repository.findById(id).orElseThrow(()-> new NotFoundException("\nCould not find " + repository.getClass().getSimpleName()
                    + " with id = " + id));
        } catch (RuntimeException | NotFoundException e){
            printer.printError(e.getMessage());
            return null;
        }
    }

    @Override
    public List<Order> findAll() {
        try{
            return repository.findAll();
        } catch (RuntimeException e){
            printer.printError(e.getMessage());
            return null;
        }
    }

    public List<OrderDTO> findRelatedOrders(Technician technician){
        try{
            List<Order> fetchedOrders = repository.findRelatedOrders(technician).orElseThrow(
                    () -> new NotFoundException(Constants.NO_RELATED_ORDERS)
            );
            List<OrderDTO> orderDTOs = new ArrayList<>();
            for(Order o : fetchedOrders){
                OrderDTO orderDTO = OrderDTO.builder()
                        .orderId(o.getId())
                        .subAssistanceTitle(o.getSubAssistance().getTitle())
                        .assistanceTitle(o.getSubAssistance().getAssistance().getTitle())
                        .basePrice(o.getSubAssistance().getBasePrice())
                        .customerFirstname(o.getCustomer().getFirstName())
                        .customerLastname(o.getCustomer().getLastName())
                        .customerId(o.getCustomer().getId())
                        .orderDate(o.getOrderRegistrationDateAndTime())
                        .orderDescription(o.getOrderDescription()).build();
                orderDTOs.add(orderDTO);
            }
            return orderDTOs;

        } catch (NotFoundException e){
            printer.printError(e.getMessage());
            return List.of();
        }

    }

    @Override
    public void sendTechnicianSuggestion(Technician technician, Order order, TechnicianSuggestion technicianSuggestion) {
        try{
            List<Order> orders = repository.findRelatedOrders(technician).orElseThrow(
                    () -> new NotFoundException(Constants.NO_RELATED_ORDERS)
            );
            boolean isFound = false;
            for(Order o: orders){
                if(o.getId()==order.getId()){
                    isFound = true;
                    break;
                }
            }
            if(!isFound)
                throw new NotFoundException(Constants.ORDER_IS_NOT_RELATED);
            if(technicianSuggestion != null){

                if(technicianSuggestion.getTechSuggestedPrice()<order.getSubAssistance().getBasePrice())
                    throw new IllegalArgumentException(Constants.INVALID_SUGGESTED_PRICE);

                if(technicianSuggestion.getTechSuggestedDate().isBefore(order.getOrderDescription().getCustomerDesiredDateAndTime()))
                    throw new IllegalArgumentException(Constants.DATE_BEFORE_CUSTOMER_DESIRED);

                order.getTechnicianSuggestions().add(technicianSuggestion);
                saveOrUpdate(order);
            }
        } catch (NotFoundException | IllegalArgumentException e){
            printer.printError(e.getMessage());
        }
    }

    @Override
    public List<Order> findByCustomer(Customer customer) {
        try{
            List<Order> fetchedOrders = repository.findByCustomer(customer).orElseThrow(
                    () -> new NotFoundException(Constants.NO_ORDERS_FOR_CUSTOMER)
            );
            return fetchedOrders;

        } catch (NotFoundException e){
            printer.printError(e.getMessage());
            return List.of();
        }
    }

}
