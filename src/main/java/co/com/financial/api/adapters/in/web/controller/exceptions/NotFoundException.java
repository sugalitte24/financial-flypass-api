package co.com.financial.api.adapters.in.web.controller.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException( String message ) {
        super(message);
    }
}
