package com.example.phase_03.entity.dto;

import com.example.phase_03.entity.base.BaseEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class TechnicianSuggestionDTO {
    private long suggestionId;
    private LocalDateTime suggestionRegistrationDate;
    private String technicianFirstname;
    private String technicianLastname;
    private long technicianId;
    private int technicianScore;
    private int numberOfFinishedTasks;
    private long suggestedPrice;
    private LocalDateTime suggestedDate;
    private int taskEstimatedDuration;

    @Override
    public String toString() {
        return  "\tsuggestion_id = " + suggestionId +
                "\n\tsuggestion_Registration_Date = " + BaseEntity.getPersianDateTime(suggestionRegistrationDate) +
                "\n\ttechnician_Firstname = '" + technicianFirstname + '\'' +
                "\n\ttechnician_Lastname = '" + technicianLastname + '\'' +
                "\n\ttechnician_Id = " + technicianId +
                "\n\ttechnician_Score = " + technicianScore +
                "\n\tnumber_Of_Finished_Tasks = " + numberOfFinishedTasks +
                "\n\tsuggested_Price = " + suggestedPrice +
                "\n\tsuggested_Date = " + BaseEntity.getPersianDateTime(suggestedDate) +
                "\n\ttask_Estimated)Duration = " + taskEstimatedDuration + " h";
    }
}
