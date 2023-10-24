package com.example.phase_03.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record OrderRequestDTO(String subAssistanceTitle,
                              long customerId,
                              long customerSuggestedPrice,
                              @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
                              LocalDateTime customerDesiredDateAndTime,
                              String taskDetails,
                              String address,
                              int technicianScore) {
}
