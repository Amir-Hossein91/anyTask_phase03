package com.example.phase_03.service.impl;

import com.example.phase_03.entity.*;
import com.example.phase_03.entity.dto.OrderDTO;
import com.example.phase_03.entity.enums.TechnicianStatus;
import com.example.phase_03.exceptions.DeactivatedTechnicianException;
import com.example.phase_03.exceptions.DuplicateTechnicianException;
import com.example.phase_03.exceptions.InvalidImageException;
import com.example.phase_03.exceptions.NotFoundException;
import com.example.phase_03.repository.TechnicianRepository;
import com.example.phase_03.service.TechnicianService;
import com.example.phase_03.utility.Constants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class TechnicianServiceImpl implements TechnicianService {

    private final TechnicianRepository repository;
    private final ManagerServiceImpl managerService;
    private final SubAssistanceServiceImpl subAssistanceService;
    private final AssistanceServiceImpl assistanceService;
    private final OrderServiceImpl orderService;

    public TechnicianServiceImpl(TechnicianRepository repository,
                                 ManagerServiceImpl managerService,
                                 SubAssistanceServiceImpl subAssistanceService,
                                 AssistanceServiceImpl assistanceService,
                                 OrderServiceImpl orderService) {
        super();
        this.repository = repository;
        this.managerService = managerService;
        this.subAssistanceService = subAssistanceService;
        this.assistanceService = assistanceService;
        this.orderService = orderService;
    }

    public boolean validateImage(Path path) {
        try {
            String pathString = path.toString();
            if (!pathString.endsWith(".jpg"))
                throw new InvalidImageException(Constants.INVALID_IMAGE_FORMAT);
            byte[] image = Files.readAllBytes(path);
            if (image.length > 307200)
                throw new InvalidImageException(Constants.INVALID_IMAGE_SIZE);
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void saveImageToDirectory(Path path, byte[] image) {
        try {
            Files.write(path, image);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public void addTechnicianToSubAssistance(String managerName, String technicianName,
                                             String subAssistanceTitle, String assistanceTitle) {

        Manager manager = managerService.findByUsername(managerName);
        if (manager == null)
            throw new IllegalArgumentException("Only manager can add technicians to a sub-assistance");
        Technician technician = findByUsername(technicianName);
        Assistance assistance = assistanceService.findAssistance(assistanceTitle);

        if (assistance == null)
            throw new NotFoundException(Constants.ASSISTANCE_NOT_FOUND);

        SubAssistance subAssistance = subAssistanceService.findSubAssistance(subAssistanceTitle, assistance);

        if (technician == null || subAssistance == null)
            throw new NotFoundException(Constants.TECHNICIAN_OR_SUBASSISTANCE_NOT_FOUND);

        if (!technician.isActive() && technician.getTechnicianStatus() == TechnicianStatus.APPROVED)
            throw new DeactivatedTechnicianException(Constants.DEACTIVATED_TECHNICIAN);

        List<Technician> technicians = subAssistance.getTechnicians();
        if (technicians.contains(technician))
            throw new DuplicateTechnicianException(Constants.DUPLICATE_TECHNICIAN_SUBASSISTANCE);

        technicians.add(technician);
        technician.setTechnicianStatus(TechnicianStatus.APPROVED);
        technician.setActive(true);
        subAssistanceService.saveOrUpdate(subAssistance);
    }

    @Transactional
    public void removeTechnicianFromSubAssistance(String managerName, String technicianName,
                                                  String subAssistanceTitle, String assistanceTitle) {

        Manager manager = managerService.findByUsername(managerName);
        if (manager == null)
            throw new IllegalArgumentException("Only manager can remove technicians from a sub-assistance");

        Technician technician = findByUsername(technicianName);
        Assistance assistance = assistanceService.findAssistance(assistanceTitle);

        if (assistance == null)
            throw new NotFoundException(Constants.ASSISTANCE_NOT_FOUND);

        SubAssistance subAssistance = subAssistanceService.findSubAssistance(subAssistanceTitle, assistance);

        if (technician == null || subAssistance == null)
            throw new NotFoundException(Constants.TECHNICIAN_OR_SUBASSISTANCE_NOT_FOUND);

        List<Technician> technicians = subAssistance.getTechnicians();
        if (!technicians.contains(technician))
            throw new NotFoundException(Constants.TECHNICIAN_NOT_IN_LIST);

        technicians.remove(technician);
        subAssistanceService.saveOrUpdate(subAssistance);
    }

    @Override
    @Transactional
    public Technician saveOrUpdate(Technician t) {
        return repository.save(t);
    }

    @Override
    @Transactional
    public void delete(Technician t) {
        repository.delete(t);
    }

    @Override
    public Technician findById(long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("\nCould not find technician with id = " + id));
    }

    @Override
    public List<Technician> findAll() {
        return repository.findAll();
    }

    @Override
    public Technician findByUsername(String technicianUsername) {
        return repository.findByUsername(technicianUsername).orElse(null);
    }

    @Override
    @Transactional
    public List<Technician> saveOrUpdate(List<Technician> technicians) {
        technicians = repository.saveAll(technicians);
        return technicians;
    }

    public List<Technician> showAllTechnicians(String managerUsername) {
        Manager manager = managerService.findByUsername(managerUsername);
        if (manager == null)
            throw new IllegalArgumentException("Only manager can see the list of all technicians");
        return findAll();
    }

    public List<Technician> seeUnapprovedTechnicians(String managerUsername) {

        Manager manager = managerService.findByUsername(managerUsername);
        if (manager == null)
            throw new IllegalArgumentException("Only manager can see unapproved technicians");

        List<Technician> technicians = repository.findUnapproved().orElse(null);
        if (technicians == null || technicians.isEmpty())
            throw new NotFoundException(Constants.NO_UNAPPROVED_TECHNICIANS);
        boolean isListChanged = false;
        for (Technician t : technicians) {
            if (t.getTechnicianStatus() == TechnicianStatus.NEW) {
                t.setTechnicianStatus(TechnicianStatus.PENDING);
                isListChanged = true;
            }
        }
        if (isListChanged)
            saveOrUpdate(technicians);
        return technicians;
    }

    public List<Technician> seeDeactivatedTechnicians(String managerUsername) {

        Manager manager = managerService.findByUsername(managerUsername);
        if (manager == null)
            throw new IllegalArgumentException("Only manager can see deactivated technicians");

        List<Technician> technicians = repository.findDeactivated().orElse(null);
        if (technicians == null || technicians.isEmpty())
            throw new NotFoundException(Constants.NO_DEACTIVATED_TECHNICIANS);
        return technicians;
    }

    public List<OrderDTO> findRelatedOrders(String technicianUsername) {
        Technician technician = findByUsername(technicianUsername);
        if (technician == null)
            throw new IllegalArgumentException("Only technicians can see their relative orders");

        if (!technician.isActive())
            throw new DeactivatedTechnicianException(Constants.DEACTIVATED_TECHNICIAN);

        return orderService.findRelatedOrders(technician);
    }

    @Transactional
    public void sendTechnicianSuggestion(String technicianUsername, long orderId, TechnicianSuggestion technicianSuggestion) {
        Technician technician = findByUsername(technicianUsername);
        if (technician == null)
            throw new IllegalArgumentException("Only technicians can send suggestions to an order");

        if (!technician.isActive())
            throw new DeactivatedTechnicianException(Constants.DEACTIVATED_TECHNICIAN);

        Order order = orderService.findById(orderId);
        if (order == null)
            throw new NotFoundException(Constants.NO_SUCH_ORDER);

        orderService.sendTechnicianSuggestion(technician, order, technicianSuggestion);
    }
}
