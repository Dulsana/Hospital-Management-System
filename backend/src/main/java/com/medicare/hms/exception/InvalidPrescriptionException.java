package com.medicare.hms.exception;

public class InvalidPrescriptionException extends RuntimeException {
    public InvalidPrescriptionException(String message) {
        super(message);
    }
}
