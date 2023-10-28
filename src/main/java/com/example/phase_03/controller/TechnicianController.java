package com.example.phase_03.controller;

import com.example.phase_03.dto.request.TechnicianRequestDTO;
import com.example.phase_03.dto.response.TechnicianResponseDTO;
import com.example.phase_03.entity.Technician;
import com.example.phase_03.entity.enums.TechnicianStatus;
import com.example.phase_03.mapper.TechnicianMapper;
import com.example.phase_03.service.impl.PersonServiceImpl;
import com.example.phase_03.service.impl.TechnicianServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/technician")
public class TechnicianController {
    public static int counter = 0;

    private final TechnicianServiceImpl technicianService;
    private final PersonServiceImpl personService;

    public TechnicianController (TechnicianServiceImpl technicianService,
                                 PersonServiceImpl personService){
        this.technicianService = technicianService;
        this.personService = personService;
    }

    @PostMapping(value = "/register")
    public ResponseEntity<TechnicianResponseDTO> saveTechnician (@RequestBody @Valid TechnicianRequestDTO requestDTO) throws IOException {
        Technician technician = TechnicianMapper.INSTANCE.dtoToModel(requestDTO);
        technician.setRegistrationDate(LocalDateTime.now());

        technician.setTechnicianStatus(TechnicianStatus.NEW);

        personService.registerTechnician(technician);

        byte[] image = technician.getImage();
        Path path = Path.of("C:\\Users\\AmirHossein\\IdeaProjects\\anyTask\\image_output\\technician_"+(++counter)+".jpg");
        Files.write(path,image);

        return new ResponseEntity<>(TechnicianMapper.INSTANCE.modelToDto(technician), HttpStatus.CREATED);
    }
}
