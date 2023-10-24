package com.example.phase_03.dto.response;

import java.util.List;

public record SubAssistanceResponseDTO(long id,
                                       String title,
                                       long basePrice,
                                       List<Long> techniciansIds,
                                       String assistanceTitle,
                                       String about) {
}
