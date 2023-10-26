package com.example.phase_03.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PersonRequestDTO(
                               @Pattern(regexp = "^[^\\d]{3,}$", message = "first name should be at least three characters and " +
                                        "no digits are allowed")
                               String firstName,
                               @Pattern(regexp = "^[^\\d]{3,}$", message = "last name should be at least three characters and " +
                                       "no digits are allowed")
                               String lastName,
                               @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Invalid email address format")
                               String email,
                               @NotNull(message = "Username can not be null")
                               @Pattern(regexp = "^[^\\s]+$", message = "Username can not be empty")
                               String username,
                               @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$", message = "Password must be at least " +
                                       "8 characters containing digits and letters")
                               String password) {
}
