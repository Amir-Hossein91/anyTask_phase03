package com.example.phase_03.dto.request;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;

public record TechnicianSuggestionRequestDTO (long technicianId,
                                              long orderId,
                                              @Range(min = 0, message = "Price can not be negative")
                                              long suggestedPrice,
                                              @NotNull(message = "A technician suggested start date must be set")
                                              LocalDateTime suggestedDate,
                                              @Range(min = 0, message = "Task duration can not be negative")
                                              int estimatedTime) {
}
