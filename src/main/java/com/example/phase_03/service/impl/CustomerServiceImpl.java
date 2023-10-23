package com.example.phase_03.service.impl;

import com.example.phase_03.baseService.impl.BaseServiceImpl;
import com.example.phase_03.entity.*;
import com.example.phase_03.entity.dto.TechnicianSuggestionDTO;
import com.example.phase_03.entity.enums.OrderStatus;
import com.example.phase_03.exceptions.NotEnoughCreditException;
import com.example.phase_03.exceptions.NotFoundException;
import com.example.phase_03.repository.CustomerRepository;
import com.example.phase_03.service.CustomerService;
import com.example.phase_03.utility.Constants;
import jakarta.persistence.PersistenceException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class CustomerServiceImpl extends BaseServiceImpl<Customer> implements CustomerService {

    private final CustomerRepository repository;
    private final ManagerServiceImpl managerService;
    private final OrderServiceImpl orderService;
    private final TechnicianSuggestionServiceImpl technicianSuggestionService;

    public CustomerServiceImpl(CustomerRepository repository,
                               OrderServiceImpl orderService,
                               ManagerServiceImpl managerService,
                               TechnicianSuggestionServiceImpl technicianSuggestionService) {
        super();
        this.repository = repository;
        this.orderService = orderService;
        this.technicianSuggestionService = technicianSuggestionService;
        this.managerService = managerService;
    }

    public List<String> showAllCustomers(String managerUsername){
        Manager manager = managerService.findByUsername(managerUsername);
        if(manager != null){
            return findAll().stream().map(Object::toString).toList();
        }
        else{
            printer.printError("Only manager can see the list of all customers");
            return List.of();
        }
    }

    public List<String> seeOrdersOf (String customerUsername){
        Customer customer = findByUsername(customerUsername);
        if(customer != null){
            return orderService.findByCustomer(customer).stream().map(Object::toString).toList();
        }
        else {
            printer.printError("this function is only available for 'customers'");
            return List.of();
        }
    }

    private boolean isSuggestionChoosingPossible(Person person, Order order){
        try{
            if(order == null)
                throw new NotFoundException(Constants.NO_SUCH_ORDER);

            if(!order.getCustomer().equals(person))
                throw new NotFoundException(Constants.ORDER_NOT_BELONG_TO_CUSTOMER);

            if(!(order.getOrderStatus() == OrderStatus.WAITING_FOR_TECHNICIANS_SUGGESTIONS
                    || order.getOrderStatus() == OrderStatus.CHOOSING_TECHNICIAN))
                throw new NotFoundException(Constants.SUGGESTION_NOT_AVAILABLE_IN_THIS_STATUS);

        } catch (NotFoundException e) {
            printer.printError(e.getMessage());
            return false;
        }
        return true;
    }

    public List<TechnicianSuggestionDTO> seeTechnicianSuggestionsOrderedByPrice(String customerUsername, long orderId){
        Customer customer = findByUsername(customerUsername);
        if(customer != null){
            Order order = orderService.findById(orderId);
            if(!isSuggestionChoosingPossible(customer,order))
                return List.of();

            List<TechnicianSuggestionDTO> technicianSuggestions = technicianSuggestionService.getSuggestionsOrderedByPrice(order);
            if(technicianSuggestions == null)
                return List.of();

            if(order.getOrderStatus() == OrderStatus.WAITING_FOR_TECHNICIANS_SUGGESTIONS) {
                order.setOrderStatus(OrderStatus.CHOOSING_TECHNICIAN);
                orderService.saveOrUpdate(order);
            }
            return technicianSuggestions;
        }
        else {
            printer.printError("Only customers have access to this function");
            return List.of();
        }
    }

    public List<TechnicianSuggestionDTO> seeTechnicianSuggestionsOrderedByScore(String customerUsername, long orderId){
        Customer customer = findByUsername(customerUsername);
        if(customer != null){
            Order order = orderService.findById(orderId);
            if(!isSuggestionChoosingPossible(customer,order))
                return List.of();

            List<TechnicianSuggestionDTO> technicianSuggestions = technicianSuggestionService.getSuggestionsOrderedByScore(order);
            if(technicianSuggestions == null)
                return List.of();

            if(order.getOrderStatus() == OrderStatus.WAITING_FOR_TECHNICIANS_SUGGESTIONS) {
                order.setOrderStatus(OrderStatus.CHOOSING_TECHNICIAN);
                orderService.saveOrUpdate(order);
            }
            return technicianSuggestions;
        }
        else {
            printer.printError("Only customers have access to this function");
            return List.of();
        }
    }

    public void chooseSuggestion(String customerUsername, long orderId, long suggestionId){
        Customer customer = findByUsername(customerUsername);
        if(customer != null){
            Order order = orderService.findById(orderId);
            if(!isSuggestionChoosingPossible(customer,order))
                return;

            List<TechnicianSuggestionDTO> technicianSuggestions = technicianSuggestionService.getSuggestionsOrderedByPrice(order);
            try{
                if(technicianSuggestions == null)
                    throw new NotFoundException(Constants.NO_TECHNICIAN_SUGGESTION_FOUND);

                List<Long> suggestionsIds = technicianSuggestions.stream()
                        .map(TechnicianSuggestionDTO::getSuggestionId)
                        .toList();

                TechnicianSuggestion suggestion = technicianSuggestionService.findById(suggestionId);
                if(suggestion == null)
                    throw new NotFoundException(Constants.TECHNICIAN_SUGGESTION_NOT_EXIST);

                if(!suggestionsIds.contains(suggestion.getId()))
                    throw new NotFoundException(Constants.TECHNICIAN_SUGGESTION_NOT_IN_LIST);

                order.setTechnician(suggestion.getTechnician());
                order.setOrderStatus(OrderStatus.TECHNICIAN_IS_ON_THE_WAY);
                order = orderService.saveOrUpdate(order);

                if(order != null)
                    printer.printMessage("Technician successfully assigned to the order");
            } catch (NotFoundException e) {
                printer.printError(e.getMessage());
            }
        }
        else
            printer.printError("Only customers have access to this function");
    }

    public void markOrderAsStarted (String customerUsername, long orderId, long suggestionId){
        Customer customer = findByUsername(customerUsername);
        if(customer != null){
            Order order = orderService.findById(orderId);
            try{
                if(order == null)
                    throw new NotFoundException(Constants.NO_SUCH_ORDER);

                if(!order.getCustomer().equals(customer))
                    throw new NotFoundException(Constants.ORDER_NOT_BELONG_TO_CUSTOMER);

                if(order.getOrderStatus()!= OrderStatus.TECHNICIAN_IS_ON_THE_WAY)
                    throw new IllegalStateException(Constants.NO_TECHNICIAN_SELECTED);

                List<TechnicianSuggestionDTO> technicianSuggestions = technicianSuggestionService.getSuggestionsOrderedByPrice(order);

                List<Long> suggestionsIds = technicianSuggestions.stream()
                        .map(TechnicianSuggestionDTO::getSuggestionId)
                        .toList();

                TechnicianSuggestion suggestion = technicianSuggestionService.findById(suggestionId);
                if(suggestion == null)
                    throw new NotFoundException(Constants.TECHNICIAN_SUGGESTION_NOT_EXIST);

                if(!suggestionsIds.contains(suggestion.getId()))
                    throw new NotFoundException(Constants.TECHNICIAN_SUGGESTION_NOT_IN_LIST);

                if(!suggestion.getTechnician().equals(order.getTechnician()))
                    throw new NotFoundException(Constants.SUGGESTION_IS_NOT_THE_CHOSEN_ONE);

                if(LocalDateTime.now().isBefore(suggestion.getTechSuggestedDate()))
                    throw new IllegalStateException(Constants.ORDER_CANT_START_BEFORE_SUGGESTED_TIME);

                order.setOrderStatus(OrderStatus.STARTED);
                order = orderService.saveOrUpdate(order);

                if(order != null)
                    printer.printMessage("The technician arrived and started working");
            } catch (NotFoundException | IllegalStateException e) {
                printer.printError(e.getMessage());
            }
        }
        else
            printer.printError("Only customers have access to this function");
    }

    public void markOrderAsFinished (String customerUsername, long orderId, long suggestionId){
        Customer customer = findByUsername(customerUsername);
        if(customer != null){
            Order order = orderService.findById(orderId);
            try{
                if(order == null)
                    throw new NotFoundException(Constants.NO_SUCH_ORDER);

                if(!order.getCustomer().equals(customer))
                    throw new NotFoundException(Constants.ORDER_NOT_BELONG_TO_CUSTOMER);

                if(order.getOrderStatus()!= OrderStatus.STARTED)
                    throw new IllegalStateException(Constants.ORDER_NOT_STARTED);

                List<TechnicianSuggestionDTO> technicianSuggestions = technicianSuggestionService.getSuggestionsOrderedByPrice(order);

                List<Long> suggestionsIds = technicianSuggestions.stream()
                        .map(TechnicianSuggestionDTO::getSuggestionId)
                        .toList();

                TechnicianSuggestion suggestion = technicianSuggestionService.findById(suggestionId);
                if(suggestion == null)
                    throw new NotFoundException(Constants.TECHNICIAN_SUGGESTION_NOT_EXIST);

                if(!suggestionsIds.contains(suggestion.getId()))
                    throw new NotFoundException(Constants.TECHNICIAN_SUGGESTION_NOT_IN_LIST);

                if(!suggestion.getTechnician().equals(order.getTechnician()))
                    throw new NotFoundException(Constants.SUGGESTION_IS_NOT_THE_CHOSEN_ONE);

                order.setOrderStatus(OrderStatus.FINISHED);
                order = orderService.saveOrUpdate(order);

                if(order != null)
                    printer.printMessage("Technician has finished the job");
            } catch (NotFoundException | IllegalStateException e) {
                printer.printError(e.getMessage());
            }
        }
        else
            printer.printError("Only customers have access to this function");
    }

    public void payThePrice(String customerUsername, long orderId){
        Customer customer = findByUsername(customerUsername);
        if(customer != null){
            try{
                Order order = orderService.findById(orderId);
                if(order == null)
                    throw new NotFoundException(Constants.NO_SUCH_ORDER);

                if(!order.getCustomer().equals(customer))
                    throw new NotFoundException(Constants.ORDER_NOT_BELONG_TO_CUSTOMER);

                if(order.getOrderStatus() != OrderStatus.FINISHED)
                    throw new IllegalStateException(Constants.PAYING_NOT_POSSIBLE_IN_THIS_STATE);

                TechnicianSuggestion selecteSuggestion = new TechnicianSuggestion();
                Technician selectedTechnician = order.getTechnician();
                for(TechnicianSuggestion t : order.getTechnicianSuggestions()){
                    Technician test = t.getTechnician();
                    if(test == selectedTechnician){
                        selecteSuggestion = t;
                        break;
                    }
                }
                customer.setCredit(customer.getCredit() - selecteSuggestion.getTechSuggestedPrice());
                if(customer.getCredit() < 0)
                    throw new NotEnoughCreditException(Constants.NOT_ENOUGH_CREDIT);

                selectedTechnician.setCredit(selectedTechnician.getCredit() + selecteSuggestion.getTechSuggestedPrice());
                selectedTechnician.setNumberOfFinishedTasks(selectedTechnician.getNumberOfFinishedTasks() + 1);
                order.setOrderStatus(OrderStatus.FULLY_PAID);
                customer = saveOrUpdate(customer);
                order = orderService.saveOrUpdate(order);

                if(customer != null && order != null)
                    printer.printMessage("Payment successful");

            } catch (IllegalStateException | NotFoundException | NotEnoughCreditException e) {
                printer.printError(e.getMessage());
            }
        }
        else
            printer.printError("Paying the price is an act of 'customer'");
    }

    public void scoreTheTechnician(String customerUsername, long orderId, int score, String opinion){
        Customer customer = findByUsername(customerUsername);
        if(customer != null){
            try{
                Order order = orderService.findById(orderId);
                if(order == null)
                    throw new NotFoundException(Constants.NO_SUCH_ORDER);

                if(!order.getCustomer().equals(customer))
                    throw new NotFoundException(Constants.ORDER_NOT_BELONG_TO_CUSTOMER);

                if(order.getOrderStatus() != OrderStatus.FINISHED)
                    throw new IllegalStateException(Constants.SCORING_NOT_POSSIBLE_IN_THIS_STATE);

                Technician selectedTechnician = order.getTechnician();

                selectedTechnician.setScore(selectedTechnician.getScore() + score);
                order.setTechnicianScore(score);
                order.setTechEvaluation(opinion);
                order = orderService.saveOrUpdate(order);

                if(order != null)
                    printer.printMessage("Scoring successful");

            } catch (IllegalStateException | NotFoundException e) {
                printer.printError(e.getMessage());
            }
        }
        else
            printer.printError("Scoring the 'technician' is an act of 'customer'");
    }

    @Override
    public Customer saveOrUpdate(Customer t) {
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
    public void delete(Customer t) {
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
    public Customer findById(long id) {
        try{
            return repository.findById(id).orElseThrow(()-> new NotFoundException("\nCould not find " + repository.getClass().getSimpleName()
                    + " with id = " + id));
        } catch (RuntimeException | NotFoundException e){
            printer.printError(e.getMessage());
            return null;
        }
    }

    @Override
    public List<Customer> findAll() {
        try{
            return repository.findAll();
        } catch (RuntimeException e){
            printer.printError(e.getMessage());
            return null;
        }
    }

    @Override
    public Customer findByUsername(String customerUsername) {
        return repository.findByUsername(customerUsername).orElse(null);
    }
}
