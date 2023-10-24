package com.example.phase_03.dto.response;

import java.time.LocalDateTime;

public record TechnicianSuggestionResponseDTO(long id,
                                              long technicianId,
                                              long orderId,
                                              LocalDateTime DateAndTimeOfTechSuggestion){
}
