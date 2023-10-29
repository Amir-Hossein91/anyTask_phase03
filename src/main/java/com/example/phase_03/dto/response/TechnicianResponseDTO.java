package com.example.phase_03.dto.response;


public record TechnicianResponseDTO (long id,
                                     String firstName,
                                     String lastName,
                                     String email,
                                     String username,
                                     String technicianStatus,
                                     int score,
                                     int numberOfFinishedTasks,
                                     boolean isActive){
}
