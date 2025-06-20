package me.universi.api;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import me.universi.api.exceptions.UniversiException;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> genericException(@NotNull Exception ex) {
        ex.printStackTrace();
        ApiError apiError = ApiError
                .builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .errors(ex.getMessage() == null ? null : List.of(ex.getMessage()))
                .build();

        return apiError.toResponseEntity();
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

        return apiError.toResponseEntity();
    }

    private String invalidArgumentMessage( FieldError err ) {
        if ( err.getRejectedValue() == null )
            return "O parâmetro '" + err.getField() + "' não foi informado";

        return err.getField() + ": " + err.getDefaultMessage();
    }

    @ExceptionHandler( UniversiException.class )
    public ResponseEntity<ApiError> universiExceptionHandler( UniversiException ex ) {
        ex.printStackTrace();
        return ex.toApiError().toResponseEntity();
    }
}
