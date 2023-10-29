package com.example.phase_03.service.impl;

import com.example.phase_03.entity.Customer;
import com.example.phase_03.entity.Manager;
import com.example.phase_03.entity.Person;
import com.example.phase_03.entity.Technician;
import com.example.phase_03.exceptions.NotFoundException;
import com.example.phase_03.repository.PersonRepository;
import com.example.phase_03.service.PersonService;
import com.example.phase_03.utility.Constants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository repository;
    private final ManagerServiceImpl managerService;
    private final CustomerServiceImpl customerService;
    private final TechnicianServiceImpl technicianService;

    public static boolean isLoggedIn;

    public PersonServiceImpl(PersonRepository repository,
                             ManagerServiceImpl managerService,
                             CustomerServiceImpl customerService,
                             TechnicianServiceImpl technicianService) {
        super();
        this.repository = repository;
        this.managerService = managerService;
        this.customerService = customerService;
        this.technicianService = technicianService;
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        Person fetched = findByUsername(username);
        if (fetched != null) {
            if (!fetched.getPassword().equals(oldPassword))
                throw new IllegalArgumentException(Constants.INCORRECT_PASSWORD);
            fetched.setPassword(newPassword);
            saveOrUpdate(fetched);
        }
    }

    @Override
    @Transactional
    public Person saveOrUpdate(Person t) {
        return repository.save(t);
    }

    @Override
    @Transactional
    public void delete(Person t) {
        repository.delete(t);
    }

    @Override
    public Person findById(long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("\nCould not find person with id = " + id));
    }

    @Override
    public List<Person> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Person findByUsername(String username) {
        return repository.findByUsername(username).orElseThrow(() -> new NotFoundException(Constants.INVALID_USERNAME));
    }

    @Transactional
    public Customer registerCustomer(Customer person) {
        return customerService.saveOrUpdate(person);
    }

    @Transactional
    public Technician registerTechnician(Technician technician) {
        if (technician == null)
            return null;
        return technicianService.saveOrUpdate(technician);
    }

    @Transactional
    public Manager registerManager(Manager manager) {
        if (managerService.doesManagerExist())
            throw new IllegalArgumentException("This organization already has a defined manager");
        return managerService.saveOrUpdate(manager);
    }

    public void login(String username, String password) {
        isLoggedIn = false;
        Person fetched = findByUsername(username);
        if(fetched == null || !fetched.getPassword().equals(password))
            throw new NotFoundException(Constants.INCORRECT_USERNAME_PASSWORD);
        isLoggedIn = true;
//          printer.printMessage("Hello " + fetched.getFirstName() + ", you are a " + fetched.getClass().getSimpleName() + " here!");
    }
}
