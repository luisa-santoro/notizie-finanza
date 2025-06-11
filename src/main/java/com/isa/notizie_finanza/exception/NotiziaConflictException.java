package com.isa.notizie_finanza.exception;

public class NotiziaConflictException extends RuntimeException {
    
    public NotiziaConflictException() {
        super();
    }

    public NotiziaConflictException(String message) {
        super(message);
    }

    public NotiziaConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotiziaConflictException(Throwable cause) {
        super(cause);
    }
}
