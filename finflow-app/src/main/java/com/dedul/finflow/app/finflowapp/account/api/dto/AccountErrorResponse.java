package com.dedul.finflow.app.finflowapp.account.api.dto;

import com.dedul.finflow.app.finflowapp.shared.exception.BusinessRuleViolationException;
import com.dedul.finflow.app.finflowapp.shared.exception.NotFoundException;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AccountErrorResponse {
  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ProblemDetail handleNotFound(NotFoundException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());

    problem.setTitle("Resource not found");
    problem.setType(URI.create("https://finflow.local/problems/not-found"));
    return problem;
  }

  @ExceptionHandler(BusinessRuleViolationException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ProblemDetail handleBusinessRule(BusinessRuleViolationException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());

    problem.setTitle("Business rule violation");
    problem.setType(URI.create("https://finflow.local/problems/business-rule-violation"));
    return problem;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {

    String detail =
        ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .findFirst()
            .orElse("Validation failed");
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
    problem.setTitle("Validation failed");
    problem.setType(URI.create("https://finflow.local/problems/validation"));
    return problem;
  }
}
