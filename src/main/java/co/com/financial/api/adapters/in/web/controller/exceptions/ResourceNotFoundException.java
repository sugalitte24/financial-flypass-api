package co.com.financial.api.adapters.in.web.controller.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException( String message ) {
        super(message);
    }
}
