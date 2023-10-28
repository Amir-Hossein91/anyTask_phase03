package com.example.phase_03.controller;

import com.example.phase_03.controller.jsonClasses.AssignTechnician;
import com.example.phase_03.dto.request.AssistanceRequestDTO;
import com.example.phase_03.dto.request.ManagerRequestDTO;
import com.example.phase_03.dto.request.SubAssistanceRequestDTO;
import com.example.phase_03.dto.response.AssistanceResponseDTO;
import com.example.phase_03.dto.response.ManagerResponseDTO;
import com.example.phase_03.dto.response.SubAssistanceResponseDTO;
import com.example.phase_03.dto.response.TechnicianResponseDTO;
import com.example.phase_03.entity.Assistance;
import com.example.phase_03.entity.Manager;
import com.example.phase_03.entity.SubAssistance;
import com.example.phase_03.entity.Technician;
import com.example.phase_03.entity.enums.TechnicianStatus;
import com.example.phase_03.mapper.AssistanceMapper;
import com.example.phase_03.mapper.ManagerMapper;
import com.example.phase_03.mapper.SubAssistanceMapper;
import com.example.phase_03.mapper.TechnicianMapper;
import com.example.phase_03.service.impl.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/manager")
public class ManagerController {

    private final ManagerServiceImpl managerService;
    private final PersonServiceImpl personService;
    private final AssistanceServiceImpl assistanceService;
    private final SubAssistanceServiceImpl subAssistanceService;
    private final TechnicianServiceImpl technicianService;

    public ManagerController(ManagerServiceImpl managerService,
                             PersonServiceImpl personService,
                             AssistanceServiceImpl assistanceService,
                             SubAssistanceServiceImpl subAssistanceService,
                             TechnicianServiceImpl technicianService) {
        this.managerService = managerService;
        this.personService = personService;
        this.assistanceService = assistanceService;
        this.subAssistanceService = subAssistanceService;
        this.technicianService = technicianService;
    }

    @PostMapping("/register")
    public ResponseEntity<ManagerResponseDTO> saveManager (@RequestBody @Valid
                                                           ManagerRequestDTO requestDTO){
        Manager manager = ManagerMapper.INSTANCE.dtoToModel(requestDTO);
        return new ResponseEntity<>(ManagerMapper.INSTANCE.modelToDto(personService.registerManager(manager)),HttpStatus.CREATED);
    }

    @PostMapping("/addAssistance")
    public ResponseEntity<AssistanceResponseDTO> addAssistance (@RequestBody @Valid
                                                               AssistanceRequestDTO requestDTO){
        Assistance assistance = AssistanceMapper.INSTANCE.dtoToModel(requestDTO);
        return new ResponseEntity<>(AssistanceMapper.INSTANCE.modelToDto(assistanceService.addAssistance(assistance)),HttpStatus.CREATED);
    }

    @PostMapping("/addSubAssistance")
    public ResponseEntity<SubAssistanceResponseDTO> addSubAssistance (@RequestBody @Valid
                                                                      SubAssistanceRequestDTO requestDTO){
        SubAssistance subAssistance = SubAssistanceMapper.INSTANCE.dtoToModel(requestDTO);
        return new ResponseEntity<>(SubAssistanceMapper.INSTANCE
                .modelToDto(subAssistanceService.addSubAssistance(subAssistance,subAssistance.getAssistance().getTitle())),HttpStatus.CREATED);
    }

    @GetMapping("/unapprovedTechnicians")
    public ResponseEntity<List<TechnicianResponseDTO>> seeNewTechnicians(){

        List<Technician> technicians = technicianService.seeUnapprovedTechnicians();
        List<TechnicianResponseDTO> responseDTOS = new ArrayList<>();

        boolean isListChanged = false;

        for(Technician t : technicians){
            responseDTOS.add(TechnicianMapper.INSTANCE.modelToDto(t));
            if (t.getTechnicianStatus() == TechnicianStatus.NEW) {
                t.setTechnicianStatus(TechnicianStatus.PENDING);
                isListChanged = true;
            }
        }
        if (isListChanged)
            technicianService.saveOrUpdate(technicians);
        return new ResponseEntity<>(responseDTOS,HttpStatus.OK);
    }

    @PostMapping("/assign")
    public ResponseEntity<String> assignTechnicianToSubAssistance(@RequestBody AssignTechnician request){

        String techUsername = request.getTechnicianUsername();
        String subAssistanceTitle = request.getSubAssistanceTitle();
        String assistanceTitle = request.getAssistanceTitle();
        System.out.println(techUsername + subAssistanceTitle + assistanceTitle);

        technicianService.addTechnicianToSubAssistance(techUsername,subAssistanceTitle,assistanceTitle);

        return new ResponseEntity<>("Technician assigned successfully", HttpStatus.CREATED);
    }

    @PostMapping("/resign")
    public ResponseEntity<String> removeTechnicianFromSubAssistance(@RequestBody AssignTechnician request){

        String techUsername = request.getTechnicianUsername();
        String subAssistanceTitle = request.getSubAssistanceTitle();
        String assistanceTitle = request.getAssistanceTitle();
        System.out.println(techUsername + subAssistanceTitle + assistanceTitle);

        technicianService.removeTechnicianFromSubAssistance(techUsername,subAssistanceTitle,assistanceTitle);

        return new ResponseEntity<>("Technician resigned successfully", HttpStatus.CREATED);
    }
}
