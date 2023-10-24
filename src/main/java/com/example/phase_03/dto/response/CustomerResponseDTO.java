package com.example.phase_03.dto.response;

import java.time.LocalDateTime;

public record CustomerResponseDTO(long id,
                                  String firstName,
                                  String lastName,
                                  String email,
                                  String username,
                                  LocalDateTime registrationDate) {
}
