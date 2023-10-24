package com.example.phase_03.mapper;

import com.example.phase_03.dto.request.TechnicianSuggestionRequestDTO;
import com.example.phase_03.dto.response.TechnicianSuggestionResponseDTO;
import com.example.phase_03.entity.TechnicianSuggestion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface TechnicianSuggestionMapper {

    TechnicianSuggestionMapper INSTANCE = Mappers.getMapper(TechnicianSuggestionMapper.class);

    @Mapping(target = "technician.id" , source = "requestDTO.technicianId")
    @Mapping(target = "order.id" , source = "requestDTO.orderId")
    TechnicianSuggestion dtoToModel (TechnicianSuggestionRequestDTO requestDTO);

    @Mapping(target = "technicianId", source = "technicianSuggestion.technician.id")
    @Mapping(target = "orderId", source = "technicianSuggestion.order.id")
    TechnicianSuggestionResponseDTO modelToDto (TechnicianSuggestion technicianSuggestion);
}
