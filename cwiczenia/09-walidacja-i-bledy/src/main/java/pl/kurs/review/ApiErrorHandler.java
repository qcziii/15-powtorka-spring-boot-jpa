package pl.kurs.review;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.TreeMap;

/** Turns exceptions into HTTP responses in the ProblemDetail format (RFC 9457). */
@RestControllerAdvice
public class ApiErrorHandler {

    /** 400 with the invalid fields: {"status":400,"detail":"...","errors":{"email":"...","age":"..."}} */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail invalid(MethodArgumentNotValidException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Niepoprawne dane");
        Map<String, String> errors = new TreeMap<>();
        e.getBindingResult().getFieldErrors().forEach(f -> errors.put(f.getField(), f.getDefaultMessage()));
        problem.setProperty("errors", errors);
        return problem;
    }

    /** 404 when the user does not exist. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail notFound(RuntimeException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /** 409 when the e-mail is taken. */
    @ExceptionHandler(EmailTakenException.class)
    public ProblemDetail conflict(EmailTakenException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }
}
