package com.example.phase_03.service.impl;

import com.example.phase_03.entity.Manager;
import com.example.phase_03.entity.Order;
import com.example.phase_03.entity.TechnicianSuggestion;
import com.example.phase_03.entity.dto.TechnicianSuggestionDTO;
import com.example.phase_03.exceptions.NotFoundException;
import com.example.phase_03.repository.TechnicianSuggestionRepository;
import com.example.phase_03.service.TechnicianSuggestionService;
import com.example.phase_03.utility.Constants;
import jakarta.persistence.PersistenceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TechnicianSuggestionServiceImpl implements TechnicianSuggestionService {

    private final TechnicianSuggestionRepository repository;
    private final ManagerServiceImpl managerService;

    public TechnicianSuggestionServiceImpl(TechnicianSuggestionRepository repository, ManagerServiceImpl managerService) {
        super();
        this.repository = repository;
        this.managerService = managerService;
    }


    public List<String> showAllSuggestions(String managerUsername) {
        Manager manager = managerService.findByUsername(managerUsername);
        if (manager == null)
            throw new IllegalArgumentException("Only manager can see the list of all technician suggestion");
        return findAll().stream().map(Object::toString).toList();

    }

    @Override
    @Transactional
    public TechnicianSuggestion saveOrUpdate(TechnicianSuggestion t) {
        try {
            return repository.save(t);
        } catch (RuntimeException e) {
//            printer.printError(e.getMessage());
//            printer.printError(Arrays.toString(e.getStackTrace()));
//            input.nextLine();
            return null;
        }
    }

    @Override
    @Transactional
    public void delete(TechnicianSuggestion t) {
        try {
            repository.delete(t);
        } catch (RuntimeException e) {
//            if (e instanceof PersistenceException)
//                printer.printError("Could not delete " + repository.getClass().getSimpleName());
//            else
//                printer.printError("Could not complete deletion. Specified " + repository.getClass().getSimpleName() + " not found!");
//            printer.printError(Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public TechnicianSuggestion findById(long id) {
        try {
            return repository.findById(id).orElseThrow(() -> new NotFoundException("\nCould not find technician suggestion with id = " + id));
        } catch (RuntimeException | NotFoundException e) {
//            printer.printError(e.getMessage());
            return null;
        }
    }

    @Override
    public List<TechnicianSuggestion> findAll() {
        try {
            return repository.findAll();
        } catch (RuntimeException e) {
//            printer.printError(e.getMessage());
            return null;
        }
    }

    @Override
    public List<TechnicianSuggestionDTO> getSuggestionsOrderedByPrice(Order order) {
        try {
            List<TechnicianSuggestion> suggestions = repository.findByOrderOrderByTechSuggestedPriceAsc(order).orElseThrow(
                    () -> new NotFoundException(Constants.NO_TECHNICIAN_SUGGESTION_FOUND)
            );
            List<TechnicianSuggestionDTO> result = new ArrayList<>();
            for (TechnicianSuggestion t : suggestions) {
                TechnicianSuggestionDTO suggestionDTO = TechnicianSuggestionDTO.builder()
                        .suggestionId(t.getId())
                        .suggestionRegistrationDate(LocalDateTime.now())
                        .technicianFirstname(t.getTechnician().getFirstName())
                        .technicianLastname(t.getTechnician().getLastName())
                        .technicianId(t.getTechnician().getId())
                        .technicianScore(t.getTechnician().getScore())
                        .numberOfFinishedTasks(t.getTechnician().getNumberOfFinishedTasks())
                        .suggestedPrice(t.getTechSuggestedPrice())
                        .suggestedDate(t.getTechSuggestedDate())
                        .taskEstimatedDuration(t.getTaskEstimatedDuration()).build();
                result.add(suggestionDTO);
            }
            return result;
        } catch (NotFoundException e) {
//            printer.printError(e.getMessage());
            return null;
        }
    }

    @Override
    public List<TechnicianSuggestionDTO> getSuggestionsOrderedByScore(Order order) {
        try {
            List<TechnicianSuggestion> suggestions = repository.findByOrderOrderByTechnicianScore(order).orElseThrow(
                    () -> new NotFoundException(Constants.NO_TECHNICIAN_SUGGESTION_FOUND)
            );
            List<TechnicianSuggestionDTO> result = new ArrayList<>();
            for (TechnicianSuggestion t : suggestions) {
                TechnicianSuggestionDTO suggestionDTO = TechnicianSuggestionDTO.builder()
                        .suggestionId(t.getId())
                        .suggestionRegistrationDate(LocalDateTime.now())
                        .technicianFirstname(t.getTechnician().getFirstName())
                        .technicianLastname(t.getTechnician().getLastName())
                        .technicianId(t.getTechnician().getId())
                        .technicianScore(t.getTechnician().getScore())
                        .numberOfFinishedTasks(t.getTechnician().getNumberOfFinishedTasks())
                        .suggestedPrice(t.getTechSuggestedPrice())
                        .suggestedDate(t.getTechSuggestedDate())
                        .taskEstimatedDuration(t.getTaskEstimatedDuration()).build();
                result.add(suggestionDTO);
            }
            return result;
        } catch (NotFoundException e) {
//            printer.printError(e.getMessage());
            return null;
        }
    }
}
