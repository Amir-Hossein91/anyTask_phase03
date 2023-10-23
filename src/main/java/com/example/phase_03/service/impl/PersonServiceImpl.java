package com.example.phase_03.service.impl;

import com.example.phase_03.baseService.impl.BaseServiceImpl;
import com.example.phase_03.entity.Customer;
import com.example.phase_03.entity.Manager;
import com.example.phase_03.entity.Person;
import com.example.phase_03.entity.Technician;
import com.example.phase_03.exceptions.NotFoundException;
import com.example.phase_03.repository.PersonRepository;
import com.example.phase_03.service.PersonService;
import com.example.phase_03.utility.Constants;
import jakarta.persistence.PersistenceException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Service
public class PersonServiceImpl extends BaseServiceImpl<Person> implements PersonService {

    private final PersonRepository repository;
    private final ManagerServiceImpl managerService;
    private final CustomerServiceImpl customerService;
    private final TechnicianServiceImpl technicianService;
    private final SubAssistanceServiceImpl subAssistanceService;

    public static boolean isLoggedIn;

    public PersonServiceImpl(PersonRepository repository,
                             ManagerServiceImpl managerService,
                             CustomerServiceImpl customerService,
                             TechnicianServiceImpl technicianService,
                             SubAssistanceServiceImpl subAssistanceService) {
        super();
        this.repository = repository;
        this.managerService = managerService;
        this.customerService = customerService;
        this.technicianService = technicianService;
        this.subAssistanceService = subAssistanceService;
    }

    public void changePassword(String username, String oldPassword, String newPassword){
        Person fetched = findByUsername(username);
        if(fetched != null) {
            try {
                if (!fetched.getPassword().equals(oldPassword))
                    throw new IllegalArgumentException(Constants.INCORRECT_PASSWORD);
                fetched.setPassword(newPassword);
                saveOrUpdate(fetched);
                printer.printMessage("password changed successfully");
            } catch (IllegalArgumentException e) {
                printer.printError(e.getMessage());
            }
        }
    }

    @Override
    public Person saveOrUpdate(Person t) {
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
    public void delete(Person t) {
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
    public Person findById(long id) {
        try{
            return repository.findById(id).orElseThrow(()-> new NotFoundException("\nCould not find " + repository.getClass().getSimpleName()
                    + " with id = " + id));
        } catch (RuntimeException | NotFoundException e){
            printer.printError(e.getMessage());
            return null;
        }
    }

    @Override
    public List<Person> findAll() {
        try{
            return repository.findAll();
        } catch (RuntimeException e){
            printer.printError(e.getMessage());
            return null;
        }
    }

    @Override
    public Person findByUsername(String username) {
        try {
            return repository.findByUsername(username).orElseThrow(()->new NotFoundException(Constants.INVALID_USERNAME));
        } catch (RuntimeException | NotFoundException e) {
            printer.printError(e.getMessage());
            return null;
        }
    }

    public Person registerCustomer(Customer person){
        return customerService.saveOrUpdate(person);
    }

    public Person registerTechnician(Technician technician,Path inputPath,Path outputPath ) throws IOException {

        if(!technicianService.validateImage(inputPath))
            return null;
        byte[] image = Files.readAllBytes(inputPath);
        if(technician == null)
            return null;
        technician.setImage(image);
        Technician savedTechnician = technicianService.saveOrUpdate(technician);
        technicianService.saveImageToDirectory(outputPath, Files.readAllBytes(inputPath));
        return savedTechnician;
    }
    public Person registerManager(Manager manager){
        if(managerService.doesManagerExist()){
            printer.printError("This organization already has a defined manager");
            return null;
        }
        return managerService.saveOrUpdate(manager);
    }

    public void login(String username, String password){
        isLoggedIn = false;
        Person fetched = findByUsername(username);
        if(fetched != null){
            try {
                if (!fetched.getPassword().equals(password))
                    throw new IllegalArgumentException(Constants.INCORRECT_USERNAME_PASSWORD);
                isLoggedIn = true;
                printer.printMessage("Hello " + fetched.getFirstName() + ", you are a " + fetched.getClass().getSimpleName() + " here!");
            } catch (IllegalArgumentException e) {
                printer.printError(e.getMessage());
            }
        }
    }
}
