package com.example.phase_03.service.impl;

import com.example.phase_03.baseService.impl.BaseServiceImpl;
import com.example.phase_03.entity.Assistance;
import com.example.phase_03.entity.Manager;
import com.example.phase_03.entity.Person;
import com.example.phase_03.entity.Technician;
import com.example.phase_03.exceptions.DeactivatedTechnicianException;
import com.example.phase_03.exceptions.DuplicateAssistanceException;
import com.example.phase_03.exceptions.NotFoundException;
import com.example.phase_03.repository.AssistanceRepository;
import com.example.phase_03.service.AssistanceService;
import com.example.phase_03.utility.Constants;
import jakarta.persistence.PersistenceException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AssistanceServiceImpl extends BaseServiceImpl<Assistance> implements AssistanceService {
    private final AssistanceRepository repository;

    private final ManagerServiceImpl managerService;
    private final PersonServiceImpl personService;

    public AssistanceServiceImpl(AssistanceRepository repository,
                                 ManagerServiceImpl managerService,
                                 @Lazy PersonServiceImpl personService) {
        super();
        this.repository = repository;
        this.managerService = managerService;
        this.personService = personService;
    }

    @Override
    public Assistance saveOrUpdate(Assistance t) {
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
    public void delete(Assistance t) {
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
    public Assistance findById(long id) {
        try{
            return repository.findById(id).orElseThrow(()-> new NotFoundException("\nCould not find " + repository.getClass().getSimpleName()
                    + " with id = " + id));
        } catch (RuntimeException | NotFoundException e){
            printer.printError(e.getMessage());
            return null;
        }
    }

    @Override
    public List<Assistance> findAll() {
        try{
            return repository.findAll();
        } catch (RuntimeException e){
            printer.printError(e.getMessage());
            return null;
        }
    }

    @Override
    public Assistance findAssistance(String assistanceName) {
        return repository.findByTitle(assistanceName).orElse(null);
    }

    public void addAssistance(String username, Assistance assistance){
        Manager manager = managerService.findByUsername(username);
        if(manager != null){
            try {
                if (findAssistance(assistance.getTitle()) != null)
                    throw new DuplicateAssistanceException(Constants.ASSISTANCE_ALREADY_EXISTS);
                saveOrUpdate(assistance);
            } catch (DuplicateAssistanceException e ){
                printer.printError(e.getMessage());
            }
        }
        else
            printer.printError("Only manager can add assistance categories");
    }

    public List<String> seeAssistances(String personUsername){
        Person person = personService.findByUsername(personUsername);
        try {
            if(person == null)
                throw new NotFoundException(Constants.INVALID_USERNAME);
            if(person instanceof Technician && !((Technician) person).isActive())
                throw new DeactivatedTechnicianException(Constants.DEACTIVATED_TECHNICIAN);
            return findAll().stream().map(Object::toString).toList();
        } catch (NotFoundException | DeactivatedTechnicianException e) {
            printer.printError(e.getMessage());
            return List.of();
        }
    }
}
