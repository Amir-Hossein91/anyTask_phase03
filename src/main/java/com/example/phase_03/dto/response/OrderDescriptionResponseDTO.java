package com.example.phase_03.dto.response;

import java.time.LocalDateTime;

public record OrderDescriptionResponseDTO(long customerSuggestedPrice,
                                          LocalDateTime customerDesiredDateAndTime,
                                          String taskDetails,
                                          String address) {
}
