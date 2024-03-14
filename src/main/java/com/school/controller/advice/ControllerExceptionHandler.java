package com.school.controller.advice;

import com.school.exception.NotAccessException;
import com.school.exception.InvalidCredentialsException;
import com.school.util.ErrorModel;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler({InvalidCredentialsException.class, HttpMessageNotReadableException.class, ConstraintViolationException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorModel> handleIncorrectDataException(Exception e) {
        ErrorModel errorModel = new ErrorModel("Bad request", "Invalid credentials or data", "Здається ви ввели неккоректні дані!");
        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotAccessException.class)
    public ResponseEntity<ErrorModel> handleNotAccessException(Exception e) {
        ErrorModel errorModel = new ErrorModel("Forbidden", "You don`t have access to this!", "У вас немає доступу!");
        return new ResponseEntity<>(errorModel, HttpStatus.FORBIDDEN);
    }

}