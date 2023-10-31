package com.example.phase_03.controller.requestObjects;

import lombok.Getter;

@Getter
public class PayThePrice {
    private String customerUsername;
    private long orderId;
    private String howToPay;
}
