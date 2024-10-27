package com.wavemaker.employee.exception.handler;

import com.wavemaker.employee.exception.ErrorResponse;
import com.wavemaker.employee.exception.LeaveDaysExceededException;
import com.wavemaker.employee.exception.ServerUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class MyResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(LeaveDaysExceededException.class)
    public ResponseEntity<Object> handleLeaveDaysExceededException(
            LeaveDaysExceededException ex, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(ex.getErrorMessage(), ex.getStatusCode());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServerUnavailableException.class)
    public ResponseEntity<Object> handleServerUnavailableException(
            ServerUnavailableException ex, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(ex.getErrorMessage(), ex.getStatusCode());

        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
