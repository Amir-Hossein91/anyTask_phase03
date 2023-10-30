package com.example.phase_03.controller;

import com.example.phase_03.controller.requestObjects.ChooseSuggestion;
import com.example.phase_03.controller.requestObjects.MarkAsStartedOrFinished;
import com.example.phase_03.controller.requestObjects.SeeSuggestions;
import com.example.phase_03.dto.request.CustomerRequestDTO;
import com.example.phase_03.dto.request.OrderRequestDTO;
import com.example.phase_03.dto.response.CustomerResponseDTO;
import com.example.phase_03.dto.response.OrderResponseDTO;
import com.example.phase_03.dto.response.SubAssistanceResponseDTO;
import com.example.phase_03.dto.response.TechnicianSuggestionResponseDTO;
import com.example.phase_03.entity.Customer;
import com.example.phase_03.entity.Order;
import com.example.phase_03.entity.SubAssistance;
import com.example.phase_03.entity.TechnicianSuggestion;
import com.example.phase_03.entity.enums.OrderStatus;
import com.example.phase_03.mapper.CustomerMapper;
import com.example.phase_03.mapper.OrderMapper;
import com.example.phase_03.mapper.SubAssistanceMapper;
import com.example.phase_03.mapper.TechnicianSuggestionMapper;
import com.example.phase_03.service.impl.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {


    private final CustomerServiceImpl customerService;
    private final PersonServiceImpl personService;
    private final SubAssistanceServiceImpl subAssistanceService;
    private final AssistanceServiceImpl assistanceService;
    private final OrderServiceImpl orderService;

    public CustomerController(CustomerServiceImpl customerService,
                              PersonServiceImpl personService,
                              SubAssistanceServiceImpl subAssistanceService,
                              AssistanceServiceImpl assistanceService,
                              OrderServiceImpl orderService) {
        this.customerService = customerService;
        this.personService = personService;
        this.subAssistanceService = subAssistanceService;
        this.assistanceService = assistanceService;
        this.orderService = orderService;
    }

    @PostMapping("/register")
    public ResponseEntity<CustomerResponseDTO> saveCustomer (@RequestBody @Valid CustomerRequestDTO requestDTO){
        Customer customer = CustomerMapper.INSTANCE.dtoToModel(requestDTO);
        customer.setRegistrationDate(LocalDateTime.now());

        personService.registerCustomer(customer);
        return new ResponseEntity<>(CustomerMapper.INSTANCE.modelToDto(customerService.findById(customer.getId())), HttpStatus.CREATED);
    }

    @GetMapping("/seeSubAssistance/{username}")
    public ResponseEntity<List<SubAssistanceResponseDTO>> seeSubAssistances(@PathVariable String username){
        List<SubAssistance> subAssistances = subAssistanceService.showSubAssistancesToOthers(username);
        List<SubAssistanceResponseDTO> responseDTOS = new ArrayList<>();

        for(SubAssistance s: subAssistances)
            responseDTOS.add(SubAssistanceMapper.INSTANCE.modelToDto(s));

        return new ResponseEntity<>(responseDTOS,HttpStatus.OK);
    }

    @PostMapping("/makeOrder")
    public ResponseEntity<OrderResponseDTO> makeOrder (@RequestBody @Valid OrderRequestDTO requestDTO) {

        Order order = OrderMapper.INSTANCE.dtoToModel(requestDTO);
        order.setTechnicianScore(1);
        order.setSubAssistance(subAssistanceService.findSubAssistance(requestDTO.subAssistanceTitle(), assistanceService.findAssistance(requestDTO.assistanceTitle())));
        order.setCustomer(customerService.findByUsername(requestDTO.customerUsername()));
        order.setOrderRegistrationDateAndTime(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.WAITING_FOR_TECHNICIANS_SUGGESTIONS);

        order = orderService.makeOrder(order.getCustomer().getUsername(),order.getSubAssistance().getTitle()
                    ,order.getSubAssistance().getAssistance().getTitle(),order.getOrderDescription());

        return new ResponseEntity<>(OrderMapper.INSTANCE.modelToDto(order),HttpStatus.CREATED);
    }

    @PostMapping("/seeSuggestions")
    public ResponseEntity<List<TechnicianSuggestionResponseDTO>> seeSuggestions (@RequestBody SeeSuggestions request){
        String username = request.getCustomerUsername();
        long orderId = request.getOrderId();
        String orderingBy = request.getOrderingBy();

        List<TechnicianSuggestion> suggestions;
        switch (orderingBy){
            case "price" -> suggestions = customerService.seeTechnicianSuggestionsOrderedByPrice(username,orderId);
            case "score" -> suggestions = customerService.seeTechnicianSuggestionsOrderedByScore(username,orderId);
            default -> throw new IllegalArgumentException("The 'orderingBy' field can either be 'price' or 'score'");
        }
        List<TechnicianSuggestionResponseDTO> responseDTOS = new ArrayList<>();

        for(TechnicianSuggestion t : suggestions)
            responseDTOS.add(TechnicianSuggestionMapper.INSTANCE.modelToDto(t));

        return new ResponseEntity<>(responseDTOS,HttpStatus.OK);
    }

    @PostMapping("/chooseSuggestion")
    public ResponseEntity<TechnicianSuggestionResponseDTO> chooseSuggestion(@RequestBody ChooseSuggestion request){
        String customerUsername = request.getCustomerUsername();
        long orderId = request.getOrderId();
        long suggestionId = request.getSuggestionId();

        return new ResponseEntity<>(TechnicianSuggestionMapper.INSTANCE
                .modelToDto(customerService.chooseSuggestion(customerUsername,orderId,suggestionId)),
                HttpStatus.CREATED);
    }

    @PostMapping("/markAsStarted")
    public ResponseEntity<String> markAsStarted (@RequestBody MarkAsStartedOrFinished request){

        customerService.markOrderAsStarted(request.getCustomerUsername(), request.getOrderId());
        Order order = orderService.findById(request.getOrderId());
        return new ResponseEntity<>("Technician started its job at " + order.getStartedTime(),HttpStatus.CREATED);
    }

    @PostMapping("/markAsFinished")
    public ResponseEntity<String> markAsFinished (@RequestBody MarkAsStartedOrFinished request){

        customerService.markOrderAsFinished(request.getCustomerUsername(), request.getOrderId());
        Order order = orderService.findById(request.getOrderId());
        return new ResponseEntity<>("Technician finished its job at " + order.getFinishedTime(),HttpStatus.CREATED);
    }
}
