package com.example.medicalrecordsproject.exceptions;

public class NoPatientsException extends RuntimeException {

    public NoPatientsException(String message) {
        super(message);
    }
}
