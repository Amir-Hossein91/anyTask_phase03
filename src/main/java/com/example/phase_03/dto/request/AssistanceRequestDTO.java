package com.example.phase_03.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AssistanceRequestDTO(
                                @NotBlank(message = "Assistance title can not be blank")
                                String title) {
}
