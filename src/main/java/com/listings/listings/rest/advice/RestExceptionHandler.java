package com.listings.listings.rest.advice;

import com.listings.listings.rest.dto.error.ErrorResponse;
import com.listings.listings.util.ListingsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedList;
import java.util.List;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        ErrorResponse errorResponse = new ErrorResponse(List.of(String.format("Error occurred. Error message %s", exception.getMessage())));
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleHandlerMethodValidationException(HandlerMethodValidationException exception) {
        List<String> messages = new LinkedList<>();
        messages.add("Request body not valid.");
        messages.addAll(exception
                .getAllErrors()
                .stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .toList());
        return new ResponseEntity<>(
                new ErrorResponse(messages),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException methodArgumentTypeMismatchException) {
        ErrorResponse errorResponse = new ErrorResponse(
                List.of(
                        String.format(
                                "%s request parameter mapping failed. %s value not allowed.",
                                methodArgumentTypeMismatchException.getName(),
                                methodArgumentTypeMismatchException.getValue()
                        )
                )
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ListingsException.class)
    public ResponseEntity<ErrorResponse> handleListingsException(ListingsException listingsException) {
        ErrorResponse errorResponse = new ErrorResponse(List.of(listingsException.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<String> messages = new LinkedList<>();
        messages.add("Request body not valid.");
        messages.addAll(exception
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList());
        return new ResponseEntity<>(
                new ErrorResponse(messages),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(value = HttpMessageConversionException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageConversionException(HttpMessageConversionException exception) {
        ErrorResponse errorResponse = new ErrorResponse(List.of(String.format("Error on parsing request body. Error message %s", exception.getMessage())));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
