package com.example.phase_03.service.impl;

import com.example.phase_03.entity.*;
import com.example.phase_03.exceptions.DeactivatedTechnicianException;
import com.example.phase_03.exceptions.DuplicateSubAssistanceException;
import com.example.phase_03.exceptions.NoSuchAsssistanceCategoryException;
import com.example.phase_03.exceptions.NotFoundException;
import com.example.phase_03.repository.SubAssistanceRepository;
import com.example.phase_03.service.SubAssistanceService;
import com.example.phase_03.utility.Constants;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SubAssistanceServiceImpl implements SubAssistanceService {

    private final SubAssistanceRepository repository;
    private final PersonServiceImpl personService;
    private final AssistanceServiceImpl assistanceService;

    public SubAssistanceServiceImpl(SubAssistanceRepository repository,
                                    @Lazy PersonServiceImpl personService,
                                    AssistanceServiceImpl assistanceService) {
        super();
        this.repository = repository;
        this.personService = personService;
        this.assistanceService = assistanceService;
    }

    @Override
    @Transactional
    public SubAssistance saveOrUpdate(SubAssistance t) {
        return repository.save(t);
    }

    @Override
    @Transactional
    public void delete(SubAssistance t) {
        repository.delete(t);
    }

    @Override
    public SubAssistance findById(long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("\nCould not find sub-assistance with id = " + id));
    }

    @Override
    public List<SubAssistance> findAll() {
        return repository.findAll();
    }

    @Override
    public SubAssistance findSubAssistance(String title, Assistance assistance) {
        return repository.findByTitleAndAssistance(title, assistance).orElse(null);
    }

    @Override
    public List<SubAssistance> findByTechniciansContaining(Technician technician) {
        return repository.findByTechniciansContaining(technician).orElse(List.of());
    }

    @Transactional
    public SubAssistance addSubAssistance(SubAssistance subAssistance, String assistanceTitle) {
        Assistance assistance = assistanceService.findAssistance(assistanceTitle);
        if (assistance == null)
            throw new NoSuchAsssistanceCategoryException(Constants.NO_SUCH_ASSISTANCE_CATEGORY);
        if (findSubAssistance(subAssistance.getTitle(), assistance) != null)
            throw new DuplicateSubAssistanceException(Constants.SUBASSISTANCE_ALREADY_EXISTS);
        subAssistance.setAssistance(assistance);
        return saveOrUpdate(subAssistance);
    }


    @Transactional
    public List<SubAssistance> showSubAssistancesToManager(String userName) {
        Person person = personService.findByUsername(userName);
        if (person instanceof Manager) {
            return findAll();
        } else
            throw new IllegalArgumentException("This operation is only valid for manager");
    }

    @Transactional
    public List<SubAssistance> showSubAssistancesToOthers(String userName) {
        Person person = personService.findByUsername(userName);

        if (person instanceof Technician && !((Technician) person).isActive())
            throw new DeactivatedTechnicianException(Constants.DEACTIVATED_TECHNICIAN);
        return findAll();
    }

    @Transactional
    public void changeDescription(String subAssistanceTitle, String assistanceTitle, String newDescription) {

        Assistance assistance = assistanceService.findAssistance(assistanceTitle);
        if (assistance == null)
            throw new NotFoundException(Constants.NO_SUCH_ASSISTANCE_CATEGORY);
        SubAssistance subAssistance = findSubAssistance(subAssistanceTitle, assistance);
        if (subAssistance == null)
            throw new NotFoundException(Constants.NO_SUCH_SUBASSISTANCE);
        subAssistance.setAbout(newDescription);
        saveOrUpdate(subAssistance);
    }

    @Transactional
    public void changeBasePrice(String subAssistanceTitle, String assistanceTitle, long basePrice) {

        Assistance assistance = assistanceService.findAssistance(assistanceTitle);
        if (assistance == null)
            throw new NotFoundException(Constants.NO_SUCH_ASSISTANCE_CATEGORY);
        SubAssistance subAssistance = findSubAssistance(subAssistanceTitle, assistance);
        if (subAssistance == null)
            throw new NotFoundException(Constants.NO_SUCH_SUBASSISTANCE);
        subAssistance.setBasePrice(basePrice);
        saveOrUpdate(subAssistance);
    }
}
