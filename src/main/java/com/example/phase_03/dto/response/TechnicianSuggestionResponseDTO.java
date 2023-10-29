package com.example.phase_03.dto.response;

import java.time.LocalDateTime;

public record TechnicianSuggestionResponseDTO(long id,
                                              String technicianUsername,
                                              int technicianScore,
                                              long orderId,
                                              LocalDateTime dateAndTimeOfTechSuggestion,
                                              long techSuggestedPrice,
                                              LocalDateTime techSuggestedDate,
                                              int taskEstimatedDuration){
}
