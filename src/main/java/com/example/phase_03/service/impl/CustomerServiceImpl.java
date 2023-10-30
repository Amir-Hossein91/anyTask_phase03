package com.example.phase_03.service.impl;

import com.example.phase_03.entity.*;
import com.example.phase_03.entity.enums.OrderStatus;
import com.example.phase_03.exceptions.NotEnoughCreditException;
import com.example.phase_03.exceptions.NotFoundException;
import com.example.phase_03.repository.CustomerRepository;
import com.example.phase_03.service.CustomerService;
import com.example.phase_03.utility.Constants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;
    private final ManagerServiceImpl managerService;
    private final OrderServiceImpl orderService;
    private final TechnicianSuggestionServiceImpl technicianSuggestionService;
    private final TechnicianServiceImpl technicianService;

    public CustomerServiceImpl(CustomerRepository repository,
                               OrderServiceImpl orderService,
                               ManagerServiceImpl managerService,
                               TechnicianSuggestionServiceImpl technicianSuggestionService,
                               TechnicianServiceImpl technicianService) {
        super();
        this.repository = repository;
        this.orderService = orderService;
        this.technicianSuggestionService = technicianSuggestionService;
        this.managerService = managerService;
        this.technicianService = technicianService;
    }

    public List<String> showAllCustomers(String managerUsername) {
        Manager manager = managerService.findByUsername(managerUsername);
        if (manager == null)
            throw new IllegalArgumentException("Only manager can see the list of all customers");
        return findAll().stream().map(Object::toString).toList();
    }

    public List<String> seeOrdersOf(String customerUsername) {
        Customer customer = findByUsername(customerUsername);
        if (customer == null)
            throw new IllegalArgumentException("this function is only available for 'customers'");
        return orderService.findByCustomer(customer).stream().map(Object::toString).toList();
    }

    private boolean isSuggestionChoosingPossible(Person person, Order order) {
        if (order == null)
            throw new NotFoundException(Constants.NO_SUCH_ORDER);

        if (!order.getCustomer().equals(person))
            throw new NotFoundException(Constants.ORDER_NOT_BELONG_TO_CUSTOMER);

        if (!(order.getOrderStatus() == OrderStatus.WAITING_FOR_TECHNICIANS_SUGGESTIONS
                || order.getOrderStatus() == OrderStatus.CHOOSING_TECHNICIAN))
            throw new NotFoundException(Constants.SUGGESTION_NOT_AVAILABLE_IN_THIS_STATUS);
        return true;
    }

    public List<TechnicianSuggestion> seeTechnicianSuggestionsOrderedByPrice(String customerUsername, long orderId) {
        Customer customer = findByUsername(customerUsername);
        if (customer == null)
            throw new IllegalArgumentException("Only customers have access to this function");

        Order order = orderService.findById(orderId);
        if (!isSuggestionChoosingPossible(customer, order))
            throw new IllegalStateException("Can not see the technician suggestions");

        List<TechnicianSuggestion> technicianSuggestions = technicianSuggestionService.getSuggestionsOrderedByPrice(order);
        if (technicianSuggestions == null)
            throw new NotFoundException("No technician suggestion available for this order");

        if (order.getOrderStatus() == OrderStatus.WAITING_FOR_TECHNICIANS_SUGGESTIONS) {
            order.setOrderStatus(OrderStatus.CHOOSING_TECHNICIAN);
            orderService.saveOrUpdate(order);
        }
        return technicianSuggestions;

    }

    public List<TechnicianSuggestion> seeTechnicianSuggestionsOrderedByScore(String customerUsername, long orderId) {
        Customer customer = findByUsername(customerUsername);
        if (customer == null)
            throw new IllegalArgumentException("Only customers have access to this function");

        Order order = orderService.findById(orderId);
        if (!isSuggestionChoosingPossible(customer, order))
            throw new IllegalStateException("Can not see the technician suggestions");

        List<TechnicianSuggestion> technicianSuggestions = technicianSuggestionService.getSuggestionsOrderedByScore(order);
        if (technicianSuggestions == null)
            throw new NotFoundException("No technician suggestion available for this order");

        if (order.getOrderStatus() == OrderStatus.WAITING_FOR_TECHNICIANS_SUGGESTIONS) {
            order.setOrderStatus(OrderStatus.CHOOSING_TECHNICIAN);
            orderService.saveOrUpdate(order);
        }
        return technicianSuggestions;
    }

    @Transactional
    public TechnicianSuggestion chooseSuggestion(String customerUsername, long orderId, long suggestionId) {
        Customer customer = findByUsername(customerUsername);
        if (customer == null)
            throw new IllegalArgumentException("Only customers have access to this function");

        Order order = orderService.findById(orderId);
        if (!isSuggestionChoosingPossible(customer, order))
            throw new IllegalStateException("Can not choose a technician suggestion in this state");

        List<TechnicianSuggestion> technicianSuggestions = technicianSuggestionService.getSuggestionsOrderedByPrice(order);

        if (technicianSuggestions == null)
            throw new NotFoundException(Constants.NO_TECHNICIAN_SUGGESTION_FOUND);

        List<Long> suggestionsIds = technicianSuggestions.stream()
                .map(TechnicianSuggestion::getId)
                .toList();

        TechnicianSuggestion suggestion = technicianSuggestionService.findById(suggestionId);

        if (suggestion == null)
            throw new NotFoundException(Constants.TECHNICIAN_SUGGESTION_NOT_EXIST);

        if (!suggestionsIds.contains(suggestion.getId()))
            throw new NotFoundException(Constants.TECHNICIAN_SUGGESTION_NOT_IN_LIST);

        order.setTechnician(suggestion.getTechnician());
        order.setChosenTechnicianSuggestion(suggestion);
        order.setOrderStatus(OrderStatus.TECHNICIAN_IS_ON_THE_WAY);
        orderService.saveOrUpdate(order);
        return suggestion;
    }

    @Transactional
    public void markOrderAsStarted(String customerUsername, long orderId) {
        Customer customer = findByUsername(customerUsername);
        if (customer == null)
            throw new IllegalArgumentException("Only customers have access to this function");
        Order order = orderService.findById(orderId);
        if (order == null)
            throw new NotFoundException(Constants.NO_SUCH_ORDER);

        if (!order.getCustomer().equals(customer))
            throw new NotFoundException(Constants.ORDER_NOT_BELONG_TO_CUSTOMER);

        if (order.getOrderStatus() != OrderStatus.TECHNICIAN_IS_ON_THE_WAY)
            throw new IllegalStateException(Constants.NO_TECHNICIAN_SELECTED);

        List<TechnicianSuggestion> technicianSuggestions = technicianSuggestionService.getSuggestionsOrderedByPrice(order);

        List<Long> suggestionsIds = technicianSuggestions.stream()
                .map(TechnicianSuggestion::getId)
                .toList();

        TechnicianSuggestion suggestion = order.getChosenTechnicianSuggestion();
        if (suggestion == null)
            throw new NotFoundException(Constants.TECHNICIAN_SUGGESTION_NOT_EXIST);

        if (!suggestionsIds.contains(suggestion.getId()))
            throw new NotFoundException(Constants.TECHNICIAN_SUGGESTION_NOT_IN_LIST);

        if (!suggestion.equals(order.getChosenTechnicianSuggestion()))
            throw new NotFoundException(Constants.SUGGESTION_IS_NOT_THE_CHOSEN_ONE);

//        if (LocalDateTime.now().isBefore(suggestion.getTechSuggestedDate()))
//            throw new IllegalStateException(Constants.ORDER_CANT_START_BEFORE_SUGGESTED_TIME);

        order.setOrderStatus(OrderStatus.STARTED);
//        if(LocalDateTime.now().isAfter(suggestion.getTechSuggestedDate()))
//            order.setStartedTime(LocalDateTime.now());
//        else
//            order.setStartedTime(suggestion.getTechSuggestedDate());
        order.setStartedTime(LocalDateTime.now());
        orderService.saveOrUpdate(order);
    }

    @Transactional
    public void markOrderAsFinished(String customerUsername, long orderId, long suggestionId) {
        Customer customer = findByUsername(customerUsername);
        if (customer == null)
            throw new IllegalArgumentException("Only customers have access to this function");
        Order order = orderService.findById(orderId);
        if (order == null)
            throw new NotFoundException(Constants.NO_SUCH_ORDER);

        if (!order.getCustomer().equals(customer))
            throw new NotFoundException(Constants.ORDER_NOT_BELONG_TO_CUSTOMER);

        if (order.getOrderStatus() != OrderStatus.STARTED)
            throw new IllegalStateException(Constants.ORDER_NOT_STARTED);

        List<TechnicianSuggestion> technicianSuggestions = technicianSuggestionService.getSuggestionsOrderedByPrice(order);

        List<Long> suggestionsIds = technicianSuggestions.stream()
                .map(TechnicianSuggestion::getId)
                .toList();

        TechnicianSuggestion suggestion = technicianSuggestionService.findById(suggestionId);
        if (suggestion == null)
            throw new NotFoundException(Constants.TECHNICIAN_SUGGESTION_NOT_EXIST);

        if (!suggestionsIds.contains(suggestion.getId()))
            throw new NotFoundException(Constants.TECHNICIAN_SUGGESTION_NOT_IN_LIST);

        if (!suggestion.getTechnician().equals(order.getTechnician()))
            throw new NotFoundException(Constants.SUGGESTION_IS_NOT_THE_CHOSEN_ONE);

        order.setOrderStatus(OrderStatus.FINISHED);
        order.setFinishedTime(LocalDateTime.now());
        orderService.saveOrUpdate(order);
    }

    @Transactional
    public void payThePrice(String customerUsername, long orderId) {
        Customer customer = findByUsername(customerUsername);
        if (customer == null)
            throw new IllegalArgumentException("Paying the price is an act of 'customer'");
        Order order = orderService.findById(orderId);
        if (order == null)
            throw new NotFoundException(Constants.NO_SUCH_ORDER);

        if (!order.getCustomer().equals(customer))
            throw new NotFoundException(Constants.ORDER_NOT_BELONG_TO_CUSTOMER);

        if (order.getOrderStatus() != OrderStatus.FINISHED)
            throw new IllegalStateException(Constants.PAYING_NOT_POSSIBLE_IN_THIS_STATE);

        TechnicianSuggestion selectSuggestion = new TechnicianSuggestion();
        Technician selectedTechnician = order.getTechnician();
        for (TechnicianSuggestion t : order.getTechnicianSuggestions()) {
            Technician test = t.getTechnician();
            if (test == selectedTechnician) {
                selectSuggestion = t;
                break;
            }
        }
        customer.setCredit(customer.getCredit() - selectSuggestion.getTechSuggestedPrice());
        if (customer.getCredit() < 0)
            throw new NotEnoughCreditException(Constants.NOT_ENOUGH_CREDIT);

        selectedTechnician.setCredit(selectedTechnician.getCredit() + selectSuggestion.getTechSuggestedPrice());
        selectedTechnician.setNumberOfFinishedTasks(selectedTechnician.getNumberOfFinishedTasks() + 1);
        order.setOrderStatus(OrderStatus.FULLY_PAID);
        saveOrUpdate(customer);
        orderService.saveOrUpdate(order);
    }

    @Transactional
    public void scoreTheTechnician(String customerUsername, long orderId, int score, String opinion) {
        Customer customer = findByUsername(customerUsername);
        if (customer == null)
            throw new IllegalArgumentException("Scoring the 'technician' is an act of 'customer'");
        Order order = orderService.findById(orderId);
        if (order == null)
            throw new NotFoundException(Constants.NO_SUCH_ORDER);

        if (!order.getCustomer().equals(customer))
            throw new NotFoundException(Constants.ORDER_NOT_BELONG_TO_CUSTOMER);

        if (order.getOrderStatus() != OrderStatus.FINISHED)
            throw new IllegalStateException(Constants.SCORING_NOT_POSSIBLE_IN_THIS_STATE);

        Technician selectedTechnician = order.getTechnician();

        selectedTechnician.setScore(selectedTechnician.getScore() + score);

        TechnicianSuggestion chosenSuggestion = order.getChosenTechnicianSuggestion();
        LocalDateTime suggestedFinishTime = chosenSuggestion.getTechSuggestedDate().plusHours(chosenSuggestion.getTaskEstimatedDuration());
        if(order.getFinishedTime().isAfter(suggestedFinishTime)){
            int negativeScore = (int) suggestedFinishTime.until(order.getFinishedTime(),ChronoUnit.HOURS);
            selectedTechnician.setScore(selectedTechnician.getScore() - negativeScore);
            if(selectedTechnician.getScore() < 0){
                selectedTechnician.setActive(false);
            }
        }

        technicianService.saveOrUpdate(selectedTechnician);
        order.setTechnicianScore(score);
        order.setTechEvaluation(opinion);
        orderService.saveOrUpdate(order);
    }

    @Override
    @Transactional
    public Customer saveOrUpdate(Customer t) {
        return repository.save(t);
    }

    @Override
    @Transactional
    public void delete(Customer t) {
        repository.delete(t);
    }

    @Override
    public Customer findById(long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("\nCould not find customer with id = " + id));
    }

    @Override
    public List<Customer> findAll() {
        return repository.findAll();
    }

    @Override
    public Customer findByUsername(String customerUsername) {
        return repository.findByUsername(customerUsername).orElse(null);
    }
}
