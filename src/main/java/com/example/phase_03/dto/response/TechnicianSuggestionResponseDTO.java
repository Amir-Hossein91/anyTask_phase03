package com.example.phase_03.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public record TechnicianSuggestionResponseDTO(long id,
                                              long technicianId,
                                              long orderId,
                                              LocalDateTime dateOfSuggestion){
}
