package com.example.phase_03.dto.request;

import com.example.phase_03.entity.enums.OrderStatus;

import java.time.LocalDateTime;

public record OrderRequestDTO(String subAssistanceTitle,
                              long customerId,
                              long customerSuggestedPrice,
                              LocalDateTime customerDesiredDateAndTime,
                              String taskDetails,
                              String address) {
}
