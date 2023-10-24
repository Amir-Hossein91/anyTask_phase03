package com.example.phase_03.dto.response;

import java.util.List;

public record TechnicianResponseDTO (long id,
                                     String firstName,
                                     String lastName,
                                     String email,
                                     String username,
                                     String password,
                                     List<Long> subAssistanceIds,
                                     String technicianStatus,
                                     int numberOfFinishedTasks,
                                     boolean isActive){
}
