package com.example.phase_03.exceptions;

public class NotEnoughCreditException extends RuntimeException{
    public NotEnoughCreditException(String message) {
        super(message);
    }
}
