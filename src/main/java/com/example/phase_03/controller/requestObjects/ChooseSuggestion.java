package com.example.phase_03.controller.requestObjects;

import lombok.Getter;

@Getter
public class ChooseSuggestion {
    private String customerUsername;
    private long orderId;
    private long suggestionId;
}
