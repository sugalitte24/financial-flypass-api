package co.com.financial.api.adapters.in.web.controller.exceptions;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException( String message ) {
        super(message);
    }
}
