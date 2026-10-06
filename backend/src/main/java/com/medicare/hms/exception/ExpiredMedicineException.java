package com.medicare.hms.exception;

public class ExpiredMedicineException extends RuntimeException {
    public ExpiredMedicineException(String message) {
        super(message);
    }
}
