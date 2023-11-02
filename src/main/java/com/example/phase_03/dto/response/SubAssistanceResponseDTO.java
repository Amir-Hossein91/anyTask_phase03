package com.example.phase_03.dto.response;


public record SubAssistanceResponseDTO(long id,
                                       String title,
                                       long basePrice,
                                       String assistanceTitle,
                                       String about) {
}
