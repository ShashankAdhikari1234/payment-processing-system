package com.org.payment_processing_system.exception;

public class PayPalServiceException extends RuntimeException {
    public PayPalServiceException(String message) {
        super(message);
    }

    public PayPalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
