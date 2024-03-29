package com.example.phase_03.controller.requestObjects;

import lombok.Getter;
import org.hibernate.validator.constraints.Range;

@Getter
public class ScoreTheTechnician {
    private String customerUsername;
    private long orderId;
    @Range(min = 1, max = 5, message = "Technician score should be in range of (1-5)")
    private int score;
    private String opinion;
}
