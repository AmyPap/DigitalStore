package com.example.DigitalStore.Service;

public class NegativePriceException extends Exception {
    public NegativePriceException(String errorMessage) {
        super(errorMessage);
    }
}