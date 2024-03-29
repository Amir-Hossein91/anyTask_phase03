package com.example.phase_03.service.impl;

import com.example.phase_03.entity.*;
import com.example.phase_03.exceptions.NotFoundException;
import com.example.phase_03.repository.PersonRepository;
import com.example.phase_03.service.PersonService;
import com.example.phase_03.utility.Constants;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository repository;
    private final ManagerServiceImpl managerService;
    private final CustomerServiceImpl customerService;
    private final TechnicianServiceImpl technicianService;
    private final SubAssistanceServiceImpl subAssistanceService;


    @PersistenceContext
    private EntityManager em;

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
    public void registerCustomer(Customer person) {
        customerService.saveOrUpdate(person);
    }

    @Transactional
    public void registerTechnician(Technician technician) {
        if (technician == null)
            return;
        technicianService.saveOrUpdate(technician);
    }

    @Transactional
    public Manager registerManager(Manager manager) {
        if (managerService.doesManagerExist())
            throw new IllegalArgumentException("This organization already has a defined manager");
        return managerService.saveOrUpdate(manager);
    }

    public Person login(String username, String password) {

        Person fetched = findByUsername(username);
        if (fetched == null || !fetched.getPassword().equals(password))
            throw new NotFoundException(Constants.INCORRECT_USERNAME_PASSWORD);
        return fetched;
    }

    public List<Person> filter(Optional<String> roll,
                               Optional<String> firstName,
                               Optional<String> lastname,
                               Optional<String> email,
                               long subAssistanceId,
                               Optional<String> maxMin) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Person> cq = cb.createQuery(Person.class);
        Root<Person> personRoot = cq.from(Person.class);


        List<Predicate> finalPredicates = new ArrayList<>();

        List<Predicate> subAssistancePredicateList = new ArrayList<>();

        firstName.map(fn -> finalPredicates.add(cb.like(personRoot.get("firstName"), "%" + fn + "%")));
        lastname.map(ln -> finalPredicates.add(cb.like(personRoot.get("lastName"), "%" + ln + "%")));
        email.map(e -> finalPredicates.add(cb.like(personRoot.get("email"), "%" + e + "%")));
        if (subAssistanceId != 0) {
            SubAssistance subAssistance = subAssistanceService.findById(subAssistanceId);
            List<Technician> technicians = subAssistance.getTechnicians();
            for (Technician t : technicians) {
                subAssistancePredicateList.add(cb.equal(personRoot.get("id"), t.getId()));
            }
            Predicate subAssistancePredicate = cb.or(subAssistancePredicateList.toArray(new Predicate[0]));
            finalPredicates.add(subAssistancePredicate);
        }

        if (maxMin.isPresent()) {
            String m = maxMin.get();
            if (m.equalsIgnoreCase("max")) {
                Subquery<Integer> subquery = cq.subquery(Integer.class);
                Root<Person> subqueryRoot = subquery.from(Person.class);
                subquery.select(cb.max(subqueryRoot.get("score")));
                finalPredicates.add(cb.equal(personRoot.get("score"), subquery));
            } else if (m.equalsIgnoreCase("min")) {
                Subquery<Integer> subquery = cq.subquery(Integer.class);
                Root<Person> subqueryRoot = subquery.from(Person.class);
                subquery.select(cb.min(subqueryRoot.get("score")));
                finalPredicates.add(cb.equal(personRoot.get("score"), subquery));
            }
        }

        cq.select(personRoot).where(finalPredicates.toArray(new Predicate[0]));
        Query typedQuery = em.createQuery(cq);
        List<Person> result = typedQuery.getResultList();

        if (roll.isPresent()) {
            String r = roll.get();
            if (r.equals("customer")) {
                for (int i = 0; i < result.size(); i++) {
                    Person person = result.get(i);
                    if (!(person instanceof Customer)) {
                        result.remove(person);
                        i--;
                    }
                }
            } else if (r.equals("technician")) {
                for (int i = 0; i < result.size(); i++) {
                    Person person = result.get(i);
                    if (!(person instanceof Technician)) {
                        result.remove(person);
                        i--;
                    }
                }
            }
        }

        return result;
    }
}
