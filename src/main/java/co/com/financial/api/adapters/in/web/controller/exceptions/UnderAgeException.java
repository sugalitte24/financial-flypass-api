package co.com.financial.api.adapters.in.web.controller.exceptions;

public class UnderAgeException extends RuntimeException {
    public UnderAgeException( String message ) {
        super(message);
    }
}
