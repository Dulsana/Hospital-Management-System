package com.medicare.hms.exception;

public class DuplicateMedicineException extends RuntimeException {
    public DuplicateMedicineException(String message) {
        super(message);
    }
}
