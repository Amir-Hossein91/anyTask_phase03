package com.example.phase_03.controller.requestObjects;

import lombok.Getter;

@Getter
public class MarkAsStarted {

    private String customerUsername;
    private long orderId;
}
