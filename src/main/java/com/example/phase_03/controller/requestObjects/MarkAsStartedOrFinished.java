package com.example.phase_03.controller.requestObjects;

import lombok.Getter;

@Getter
public class MarkAsStartedOrFinished {

    private String customerUsername;
    private long orderId;
}
