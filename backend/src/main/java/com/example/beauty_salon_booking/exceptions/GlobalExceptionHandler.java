package com.example.beauty_salon_booking.exceptions;

import com.example.beauty_salon_booking.dto.ResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 1. Ошибки безопасности (логин, доступ)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseDTO> handleBadCredentials(BadCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid username or password");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseDTO> handleAccessDenied(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Access denied");
    }

    // 2. Ошибки бизнес-логики
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseDTO> handleEntityNotFound(EntityNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Entity not found: " + ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ResponseDTO> handleIllegalState(IllegalStateException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Conflict: " + ex.getMessage());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ResponseDTO> handleInvalidToken(InvalidTokenException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }


    // 3. Ошибки базы данных
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseDTO> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Database constraint error: " + ex.getRootCause().getMessage());
    }

    // 4. Ошибки валидации запроса
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        String message = "Validation failed for fields: " + errors.toString();
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseDTO> handleMissingParams(MissingServletRequestParameterException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Missing parameter: " + ex.getParameterName());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ResponseDTO> handleValidationException(ValidationException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // 5. Ошибки HTTP запросов
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseDTO> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, "HTTP method not allowed: " + ex.getMethod());
    }

    // 6. Общий обработчик всех остальных ошибок
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO> handleAllOtherExceptions(Exception ex) {
        logger.error("Unexpected error occurred", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error: " + ex.getMessage());
    }

    // Вспомогательный метод для создания ответа
    private ResponseEntity<ResponseDTO> buildResponse(HttpStatus status, String message) {
        ResponseDTO errorResponse = new ResponseDTO(
                LocalDateTime.now().toString(),
                status.value(),
                status.getReasonPhrase(),
                message
        );
        return new ResponseEntity<>(errorResponse, status);
    }
}