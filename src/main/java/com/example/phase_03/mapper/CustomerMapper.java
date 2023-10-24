package com.example.phase_03.mapper;

import com.example.phase_03.dto.request.CustomerRequestDTO;
import com.example.phase_03.dto.response.CustomerResponseDTO;
import com.example.phase_03.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface CustomerMapper {

    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    Customer dtoToModel (CustomerRequestDTO requestDTO);

    CustomerResponseDTO modelToDto (Customer customer);
}
