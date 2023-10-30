package com.example.phase_03.controller;

import com.example.phase_03.controller.requestObjects.AssignTechnician;
import com.example.phase_03.controller.requestObjects.ChangeBasePrice;
import com.example.phase_03.controller.requestObjects.ChangeDescription;
import com.example.phase_03.dto.request.AssistanceRequestDTO;
import com.example.phase_03.dto.request.ManagerRequestDTO;
import com.example.phase_03.dto.request.SubAssistanceRequestDTO;
import com.example.phase_03.dto.response.*;
import com.example.phase_03.entity.Assistance;
import com.example.phase_03.entity.Manager;
import com.example.phase_03.entity.SubAssistance;
import com.example.phase_03.entity.Technician;
import com.example.phase_03.entity.enums.TechnicianStatus;
import com.example.phase_03.mapper.*;
import com.example.phase_03.service.impl.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        technicianService.addTechnicianToSubAssistance(techUsername,subAssistanceTitle,assistanceTitle);

        return new ResponseEntity<>("Technician assigned successfully", HttpStatus.CREATED);
    }

    @PostMapping("/resign")
    public ResponseEntity<String> removeTechnicianFromSubAssistance(@RequestBody AssignTechnician request){

        String techUsername = request.getTechnicianUsername();
        String subAssistanceTitle = request.getSubAssistanceTitle();
        String assistanceTitle = request.getAssistanceTitle();

        technicianService.removeTechnicianFromSubAssistance(techUsername,subAssistanceTitle,assistanceTitle);

        return new ResponseEntity<>("Technician resigned successfully", HttpStatus.CREATED);
    }

    @GetMapping("/getSubAssistances/{username}")
    @Transactional
    public ResponseEntity<List<SubAssistanceResponseForManagerDTO>> getSubAssistances (@PathVariable String username){
        List<SubAssistance> subAssistances = subAssistanceService.showSubAssistancesToManager(username);

        Map<SubAssistanceResponseDTO,List<TechnicianResponseDTO>> resultsMap = new HashMap<>();
        for(SubAssistance s : subAssistances){
            SubAssistanceResponseDTO key = SubAssistanceMapper.INSTANCE.modelToDto(s);
            List<TechnicianResponseDTO> value = new ArrayList<>();
            for(Technician t : s.getTechnicians()){
                TechnicianResponseDTO responseDTO = TechnicianMapper.INSTANCE.modelToDto(t);
                value.add(responseDTO);
            }
            resultsMap.put(key,value);
        }

        List<SubAssistanceResponseForManagerDTO> result = new ArrayList<>();
        for(Map.Entry<SubAssistanceResponseDTO,List<TechnicianResponseDTO>> e : resultsMap.entrySet()){
            result.add(SubAssistanceResponseForManagerDTO.builder()
                    .id(e.getKey().id())
                    .title(e.getKey().title())
                    .basePrice(e.getKey().basePrice())
                    .assistanceTitle(e.getKey().assistanceTitle())
                    .about(e.getKey().about())
                    .technicians(e.getValue())
                    .build());
        }
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @PostMapping("/changeBasePrice")
    public ResponseEntity<SubAssistanceResponseDTO> changeBasePrice(@RequestBody ChangeBasePrice request){

        String subAssistanceTitle = request.getSubAssistanceTitle();
        String assistanceTitle = request.getAssistanceTitle();
        long basePrice = request.getNewBasePrice();

        subAssistanceService.changeBasePrice(subAssistanceTitle,assistanceTitle,basePrice);

        SubAssistanceResponseDTO responseDTO = SubAssistanceMapper.INSTANCE
                .modelToDto(subAssistanceService.findSubAssistance(subAssistanceTitle,assistanceService.findAssistance(assistanceTitle)));

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PostMapping("/changeDescription")
    public ResponseEntity<SubAssistanceResponseDTO> changeDescription(@RequestBody ChangeDescription request){

        String subAssistanceTitle = request.getSubAssistanceTitle();
        String assistanceTitle = request.getAssistanceTitle();
        String description = request.getNewDescription();

        subAssistanceService.changeDescription(subAssistanceTitle,assistanceTitle,description);

        SubAssistanceResponseDTO responseDTO = SubAssistanceMapper.INSTANCE
                .modelToDto(subAssistanceService.findSubAssistance(subAssistanceTitle,assistanceService.findAssistance(assistanceTitle)));

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/getDeactivated/{username}")
    public ResponseEntity<List<TechnicianResponseDTO>> findDeactivatedTechnicians (@PathVariable String username){
        List<Technician> deactivatedList = technicianService.seeDeactivatedTechnicians(username);
        List<TechnicianResponseDTO> responseDTOS = new ArrayList<>();

        for(Technician t : deactivatedList)
            responseDTOS.add(TechnicianMapper.INSTANCE.modelToDto(t));

        return new ResponseEntity<>(responseDTOS,HttpStatus.OK);
    }


}
