package co.com.financial.api.adapters.in.web.controller.exceptions.handler;

import co.com.financial.api.adapters.in.web.controller.exceptions.AlreadyExistException;
import co.com.financial.api.adapters.in.web.controller.exceptions.GenericException;
import co.com.financial.api.adapters.in.web.controller.exceptions.NotFoundException;
import co.com.financial.api.adapters.in.web.controller.exceptions.UnauthorizedException;
import co.com.financial.api.adapters.in.web.controller.exceptions.UnderAgeException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorized( UnauthorizedException ex, HttpServletRequest request ) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric( GenericException ex, HttpServletRequest request ) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleNotFound( NotFoundException ex, HttpServletRequest request ) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler({UnderAgeException.class})
    public ResponseEntity<ApiErrorResponse> underAge( UnderAgeException ex, HttpServletRequest request ) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler({AlreadyExistException.class})
    public ResponseEntity<ApiErrorResponse> alreadyExist( AlreadyExistException ex, HttpServletRequest request ) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalState( IllegalStateException ex, HttpServletRequest request ) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch( MethodArgumentTypeMismatchException ex, HttpServletRequest request ) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation( ConstraintViolationException ex, HttpServletRequest request ) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationRequest( MethodArgumentNotValidException ex, HttpServletRequest request ) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce(( a, b ) -> a + ", " + b)
                .orElse("Datos inválidos");
        return buildResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
    }

    private ResponseEntity<ApiErrorResponse> buildResponse( HttpStatus status, String message, String path ) {
        return ResponseEntity.status(status).body(
                new ApiErrorResponse(
                        message, status.value(), path
                )
        );
    }
}