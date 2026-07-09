package com.edu.elearning.exception;

import com.edu.elearning.dto.exception.response.ApiResponse;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ElearningExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ElearningException.class);

    @ExceptionHandler(ElearningException.class)
    public ResponseEntity<?> eLearningExceptionHandler(ElearningException exception) {
        return new ResponseEntity<>(
                ApiResponse.error(exception.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
    }


    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<?> emptyResultExceptionHandler(EmptyResultDataAccessException exception) {
        logger.error("## Empty Result Exception : {} ###", exception.getMessage());
        return new ResponseEntity<>(
                ApiResponse.error("Record not found"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> dataIntegrityExceptionHandler(
            DataIntegrityViolationException exception) {
        logger.error("## Data Integrity Violation Exception : {} ###", exception.getMessage());
        return new ResponseEntity<>(
                ApiResponse.error("Duplicate Entry Not Allowed"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> expiredTokenExceptionHandler(ExpiredJwtException exception) {
        logger.error("## Token Exception : {} ###", exception.getMessage());
        return new ResponseEntity<>(ApiResponse.error(exception.getMessage()), HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(ApiResponse.error(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointerException(NullPointerException ex) {
        return new ResponseEntity<>(ApiResponse.error(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleNoResourceFoundException(NoResourceFoundException ex) {
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex) {
        return new ResponseEntity<>(
                ApiResponse.error("Missing request parameter: " + ex.getParameterName()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        return new ResponseEntity<>(
                ApiResponse.error("An unexpected error occurred: " + ex.getMessage()),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonParseException(HttpMessageNotReadableException ex) {
        Map<String, Object> errorResponse = new HashMap<>();

        if (ex.getCause() instanceof MismatchedInputException mie && !mie.getPath().isEmpty()) {
            String fieldPath = mie.getPath().stream()
                    .map(ref -> ref.getFieldName() != null ? ref.getFieldName() : "[" + ref.getIndex() + "]")
                    .collect(Collectors.joining("."));

            Map<String, Object> fieldError = Map.of(
                    "description", "Invalid data format: Expected correct format for the field"
            );

            errorResponse = buildNestedJson(fieldPath, fieldError);
        }

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private Map<String, Object> buildNestedJson(String jsonPath, Object value) {
        String[] parts = jsonPath.split("\\.");
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> current = result;

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];

            if (part.contains("[")) {
                continue;
            }

            if (part.matches(".*\\[\\d+\\]$")) {
                int startIdx = part.indexOf('[');
                String fieldName = part.substring(0, startIdx);
                int index = Integer.parseInt(part.substring(startIdx + 1, part.length() - 1));

                current.putIfAbsent(fieldName, new ArrayList<>());
                List<Object> list = (List<Object>) current.get(fieldName);

                while (list.size() <= index) list.add(new HashMap<>());

                if (i == parts.length - 1) list.set(index, value);
                else current = (Map<String, Object>) list.get(index);

            } else {
                if (i == parts.length - 1) current.put(part, value);
                else current = (Map<String, Object>) current.computeIfAbsent(part, k -> new HashMap<>());
            }
        }

        return result;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new LinkedHashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            Object rejectedValue = error.getRejectedValue();

            Map<String, Object> errorDetails = new LinkedHashMap<>();
            errorDetails.put("value", rejectedValue);
            errorDetails.put("description", errorMessage);

            insertNestedError(errors, fieldName, errorDetails);
        }

        return ResponseEntity.unprocessableEntity().body(errors);
    }

    private void insertNestedError(Map<String, Object> parent, String fieldPath, Map<String, Object> errorDetails) {
        String[] pathParts = fieldPath.split("\\.");
        Map<String, Object> current = parent;

        for (int i = 0; i < pathParts.length; i++) {
            String key = pathParts[i];

            if (key.matches(".*\\[\\d+\\]$")) {
                String baseKey = key.substring(0, key.indexOf("["));
                int index = Integer.parseInt(key.substring(key.indexOf("[") + 1, key.length() - 1));

                current.computeIfAbsent(baseKey, k -> new ArrayList<>());

                List<Object> list = (List<Object>) current.get(baseKey);
                while (list.size() <= index) {
                    list.add(new LinkedHashMap<>());
                }

                if (i == pathParts.length - 1) {
                    list.set(index, errorDetails);
                } else {
                    current = (Map<String, Object>) list.get(index);
                }
            } else {
                current.computeIfAbsent(key, k -> new LinkedHashMap<>());

                if (i == pathParts.length - 1) {
                    current.put(key, errorDetails);
                } else {
                    current = (Map<String, Object>) current.get(key);
                }
            }
        }
    }
}
