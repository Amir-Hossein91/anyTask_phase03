package com.example.phase_03.mapper;

import com.example.phase_03.dto.request.SubAssistanceRequestDTO;
import com.example.phase_03.dto.response.SubAssistanceResponseDTO;
import com.example.phase_03.entity.SubAssistance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface SubAssistanceMapper {

    SubAssistanceMapper INSTANCE = Mappers.getMapper(SubAssistanceMapper.class);

    @Mapping(target = "assistance.title" , source = "requestDTO.assistanceTitle")
    SubAssistance dtoToModel (SubAssistanceRequestDTO requestDTO);

    @Mapping(target = "assistanceTitle" , source = "subAssistance.assistance.title")
    SubAssistanceResponseDTO moderToDto (SubAssistance subAssistance);
}
