package com.example.phase_03.dto.request;

import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;

public record TechnicianSuggestionRequestDTO (long technicianId,
                                              long orderId,
                                              @Range(min = 0, message = "Price can not be negative")
                                              long suggestedPrice,
                                              LocalDateTime suggestedDate,
                                              @Range(min = 0, message = "Task duration can not be negative")
                                              int estimatedTime) {
}
