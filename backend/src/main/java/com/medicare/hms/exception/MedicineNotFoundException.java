package com.medicare.hms.exception;

public class MedicineNotFoundException extends RuntimeException {
    public MedicineNotFoundException(String message) {
        super(message);
    }

    public MedicineNotFoundException(Long id) {
        super("Medicine with ID " + id + " was not found.");
    }
}
