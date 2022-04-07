package io.jcervelin.moviebattle.gateways.controllers;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import io.jcervelin.moviebattle.domains.ErrorResponse;
import io.jcervelin.moviebattle.domains.exceptions.SessionNotFoundException;
import io.swagger.annotations.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ControllerAdvice {

  private static final ErrorResponse DEFAULT_ERROR =
      ErrorResponse.of(INTERNAL_SERVER_ERROR.getReasonPhrase(), "Unknown Error");
  private static final String VALIDATION_ERROR_MESSAGE_PATTERN =
      "Field name: [ %s ] - Message: [ %s ]";

  @ExceptionHandler
  @ResponseStatus(INTERNAL_SERVER_ERROR)
  @ApiResponse(code = 500, message = "Service Error")
  public ErrorResponse handleException(Exception e) {
    log.error("Handling Exception", e);
    return ErrorResponse.builder().code(e.getClass().getName()).message(e.getMessage()).build();
  }

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
    return ex.getBindingResult().getAllErrors().stream()
        .findFirst()
        .map(
            error -> {
              String fieldName = ((FieldError) error).getField();
              String defaultMessage = error.getDefaultMessage();

              return ErrorResponse.builder()
                  .code(BAD_REQUEST.getReasonPhrase())
                  .message(format(VALIDATION_ERROR_MESSAGE_PATTERN, fieldName, defaultMessage))
                  .build();
            })
        .orElse(DEFAULT_ERROR);
  }

  @ExceptionHandler(SessionNotFoundException.class)
  @ResponseStatus(value = NOT_FOUND)
  public @ResponseBody ErrorResponse handleSessionNotFoundException(
      final SessionNotFoundException exception) {
    log.error(exception.getMessage());
    return ErrorResponse.of(exception.getMessage(), NOT_FOUND.getReasonPhrase());
  }
}
