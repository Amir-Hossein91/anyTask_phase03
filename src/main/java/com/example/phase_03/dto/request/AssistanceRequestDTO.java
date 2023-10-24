package com.example.phase_03.dto.request;

import jakarta.validation.constraints.NotNull;

public record AssistanceRequestDTO(
        @NotNull(message = "Assistance title can not be null")
        String title) {
}
