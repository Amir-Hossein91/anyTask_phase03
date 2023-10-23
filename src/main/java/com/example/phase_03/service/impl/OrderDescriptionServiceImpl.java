package com.example.phase_03.service.impl;

import com.example.phase_03.baseService.impl.BaseServiceImpl;
import com.example.phase_03.entity.OrderDescription;
import com.example.phase_03.exceptions.NotFoundException;
import com.example.phase_03.repository.OrderDescriptionRepository;
import com.example.phase_03.service.OrderDescriptionService;
import jakarta.persistence.PersistenceException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class OrderDescriptionServiceImpl extends BaseServiceImpl<OrderDescription> implements OrderDescriptionService {

    private final OrderDescriptionRepository repository;

    public OrderDescriptionServiceImpl(OrderDescriptionRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    public OrderDescription saveOrUpdate(OrderDescription t) {
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
    public void delete(OrderDescription t) {
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
    public OrderDescription findById(long id) {
        try{
            return repository.findById(id).orElseThrow(()-> new NotFoundException("\nCould not find " + repository.getClass().getSimpleName()
                    + " with id = " + id));
        } catch (RuntimeException | NotFoundException e){
            printer.printError(e.getMessage());
            return null;
        }
    }

    @Override
    public List<OrderDescription> findAll() {
        try{
            return repository.findAll();
        } catch (RuntimeException e){
            printer.printError(e.getMessage());
            return null;
        }
    }
}
