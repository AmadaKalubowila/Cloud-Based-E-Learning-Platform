package com.edu.elearning.exception;

public class ElearningException extends RuntimeException {
    public ElearningException() {
        super();
    }

    public ElearningException(String message) {
        super(message);
    }

    public ElearningException(String message, Throwable cause) {
        super(message, cause);
    }

    private String errorCode;
    private boolean disable;

    public ElearningException(String message, String errorCode, boolean status) {
        super(message);
        this.errorCode = errorCode;
        this.disable = status;
    }

    public Object getErrorCode() {
        return errorCode;
    }

    public Object getStatus() {
        return disable;
    }
}
