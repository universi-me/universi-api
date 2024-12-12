package me.universi.api;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> genericException(Exception ex) {
        ApiError apiError = ApiError
                .builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .errors(List.of(ex.getMessage()))
                .build();
        return new ResponseEntity<>(apiError, apiError.status());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> argumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> errorList = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map( this::invalidArgumentMessage )
                .toList();

        ApiError apiError = ApiError
                .builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST)
                .errors(errorList)
                .build();
        return new ResponseEntity<>(apiError, apiError.status());
    }

    private String invalidArgumentMessage( FieldError err ) {
        if ( err.getRejectedValue() == null )
            return "O parâmetro '" + err.getField() + "' não foi informado";

        return err.getField() + ": " + err.getDefaultMessage();
    }
}
