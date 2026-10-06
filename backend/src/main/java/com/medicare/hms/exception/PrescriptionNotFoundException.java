package com.medicare.hms.exception;

public class PrescriptionNotFoundException extends RuntimeException {
    public PrescriptionNotFoundException(String message) {
        super(message);
    }

    public PrescriptionNotFoundException(Long id) {
        super("Prescription with ID " + id + " was not found.");
    }
}
