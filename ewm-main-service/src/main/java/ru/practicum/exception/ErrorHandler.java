//package ru.practicum.exception;
//
//import jakarta.validation.ConstraintViolationException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//import java.util.Map;
//
//@Slf4j
//@RestControllerAdvice
//public class ErrorHandler {
//
//    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class, ValidationException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
//    public Map<String, String> handleValidation(final Exception e) {
//        log.debug("Получен статус 400 Bad Request {}", e.getMessage(), e);
//        return Map.of("error", e.getMessage());
//    }
//
//    @ExceptionHandler //404
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public Map<String, String> handleNotFound(final NotFoundException e) {
//        log.debug("Получен статус 404 Not Found {}", e.getMessage(), e);
//        return Map.of("error", e.getMessage());
//    }
//
//    @ExceptionHandler({DuplicatedDataException.class, ConflictException .class}) //409
//    @ResponseStatus(HttpStatus.CONFLICT)
//    public Map<String, String> handleDuplicatedAndConflictData(final Exception e) {
//        log.debug("Получен статус 409 Conflict {}", e.getMessage(), e);
//        return Map.of("error", e.getMessage());
//    }
//
//    @ExceptionHandler //500
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public Map<String, String> handleInternalServerError(final Throwable e) {
//        log.debug("Получен статус 500 Internal Server Error {}", e.getMessage(), e);
//        return Map.of("error", e.getMessage());
//    }
//}
package ru.practicum.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleInternalServerError(Exception e) {
        log.error(stackTraceToString(e));
        return ApiError.builder()
                .message(e.getMessage())
                .status("INTERNAL_SERVER_ERROR")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler({ValidationException.class, NumberFormatException.class,
            MethodArgumentTypeMismatchException.class, MethodArgumentNotValidException.class,
            MissingServletRequestParameterException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(RuntimeException e) {
        log.error(stackTraceToString(e));
        return ApiError.builder()
                .message(e.getMessage())
                .reason("Incorrectly made request.")
                .status("BAD_REQUEST")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(NotFoundException e) {
        log.error(stackTraceToString(e));
        return ApiError.builder()
                .message(e.getMessage())
                .reason("The required object was not found.")
                .status("NOT_FOUND")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError conflict(DataIntegrityViolationException e) {
        log.error(stackTraceToString(e));
        return ApiError.builder()
                .message(e.getMessage())
                .reason("Integrity constraint has been violated.")
                .status("CONFLICT")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler({DuplicatedDataException.class, ConflictException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError conflict(RuntimeException e) {
        log.error(stackTraceToString(e));
        return ApiError.builder()
                .message(e.getMessage())
                .reason("Integrity constraint has been violated.")
                .status("CONFLICT")
                .timestamp(LocalDateTime.now())
                .build();
    }


//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.CONFLICT)
//    public ApiError conflictEvent(ForbiddenException e) {
//        log.error(stackTraceToString(e));
//        return ApiError.builder()
//                .message(e.getMessage())
//                .reason("For the requested operation the conditions are not met.")
//                .status("FORBIDDEN")
//                .timestamp(LocalDateTime.now())
//                .build();
//    }

    private static String stackTraceToString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
