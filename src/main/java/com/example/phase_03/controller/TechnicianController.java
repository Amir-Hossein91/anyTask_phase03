package com.example.phase_03.controller;

import com.example.phase_03.dto.request.TechnicianRequestDTO;
import com.example.phase_03.dto.response.TechnicianResponseDTO;
import com.example.phase_03.entity.Technician;
import com.example.phase_03.entity.enums.TechnicianStatus;
import com.example.phase_03.mapper.TechnicianMapper;
import com.example.phase_03.service.impl.TechnicianServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;

@RestController
@RequestMapping("/technician")
public class TechnicianController {

    @Autowired
    TechnicianServiceImpl technicianService;

    @PostMapping(value = "/save")
    public ResponseEntity<TechnicianResponseDTO> saveTechnician (@RequestBody @Valid TechnicianRequestDTO requestDTO) throws IOException {
        Technician technician = TechnicianMapper.INSTANCe.dtoToModel(requestDTO);
        technician.setRegistrationDate(LocalDateTime.now());

        byte[] image = technician.getImage();
        Path path = Path.of("C:\\Users\\AmirHossein\\IdeaProjects\\anyTask\\image_output\\technician_01.jpg");
        Files.write(path,image);

        technician.setTechnicianStatus(TechnicianStatus.NEW);

        technicianService.saveOrUpdate(technician);
        return new ResponseEntity<>(TechnicianMapper.INSTANCe.modelToDto(technician), HttpStatus.CREATED);
    }
}
