package co.com.financial.api.adapters.in.web.controller.exceptions;

public class AlreadyExistException extends RuntimeException {
    public AlreadyExistException( String message ) {
        super(message);
    }
}
