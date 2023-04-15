package com.example.medicalrecordsproject.exceptions;

public class NegativeIncomeException extends RuntimeException{

    public NegativeIncomeException(String message) {
        super(message);
    }
}
