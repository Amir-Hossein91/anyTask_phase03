package com.example.phase_03.mapper;

import com.example.phase_03.dto.request.TechnicianRequestDTO;
import com.example.phase_03.dto.response.TechnicianResponseDTO;
import com.example.phase_03.entity.Technician;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface TechnicianMapper {

    TechnicianMapper INSTANCe = Mappers.getMapper(TechnicianMapper.class);

    Technician dtoToModel(TechnicianRequestDTO requestDTO);

    TechnicianResponseDTO modelToDto (Technician technician);
}
