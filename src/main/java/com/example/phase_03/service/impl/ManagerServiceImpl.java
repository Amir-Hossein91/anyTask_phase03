package com.example.phase_03.service.impl;

import com.example.phase_03.baseService.impl.BaseServiceImpl;
import com.example.phase_03.entity.Manager;
import com.example.phase_03.exceptions.NotFoundException;
import com.example.phase_03.repository.ManagerRepository;
import com.example.phase_03.service.ManagerService;
import jakarta.persistence.PersistenceException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ManagerServiceImpl extends BaseServiceImpl<Manager> implements ManagerService {

    private final ManagerRepository repository;
    public ManagerServiceImpl(ManagerRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    public Manager saveOrUpdate(Manager t) {
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
    public void delete(Manager t) {
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
    public Manager findById(long id) {
        try{
            return repository.findById(id).orElseThrow(()-> new NotFoundException("\nCould not find " + repository.getClass().getSimpleName()
                    + " with id = " + id));
        } catch (RuntimeException | NotFoundException e){
            printer.printError(e.getMessage());
            return null;
        }
    }

    @Override
    public List<Manager> findAll() {
        try{
            return repository.findAll();
        } catch (RuntimeException e){
            printer.printError(e.getMessage());
            return null;
        }
    }

    public boolean doesManagerExist(){
        return !repository.findAll().isEmpty();
    }

    @Override
    public Manager findByUsername(String managerUsername) {
        return repository.findByUsername(managerUsername).orElse(null);
    }
}
