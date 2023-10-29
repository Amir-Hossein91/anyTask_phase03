package com.example.phase_03.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record SubAssistanceResponseDTO4Manager(long id,
                                               String title,
                                               long basePrice,
                                               String assistanceTitle,
                                               String about,
                                               List<TechnicianResponseDTO> technicians) {
}
