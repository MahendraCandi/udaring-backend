package co.id.udaring.exception;

import co.id.udaring.exception.util.ErrorDetail;
import co.id.udaring.exception.util.ProblemDetailMultipleError;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex, HttpServletRequest request) {
        log.error(ex.getMessage(), ex);
        final var httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        var problemDetail = ProblemDetail.forStatus(httpStatus);
        problemDetail.setType(buildProblemTypeURI("error"));
        problemDetail.setTitle(httpStatus.getReasonPhrase());
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setInstance(buildInstanceURI(request));
        return new ResponseEntity<>(problemDetail, httpStatus);
    }

    @ExceptionHandler(InvalidParametersException.class)
    public ResponseEntity<Object> handleInvalidParametersException(InvalidParametersException ex, HttpServletRequest request) {
        var problemDetail = ex.getBody();
        problemDetail.setType(buildProblemTypeURI("error", "invalid-parameters"));
        problemDetail.setInstance(buildInstanceURI(request));
        return new ResponseEntity<>(
                ProblemDetailMultipleError.of(problemDetail, ex.getErrorDetail().toArray(new ErrorDetail[0])),
                ex.getStatusCode());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        final var servletRequest = ((ServletWebRequest) request).getRequest();
        var problemDetail = ex.getBody();
        problemDetail.setType(buildProblemTypeURI("error", "invalid-parameters"));
        problemDetail.setTitle("Invalid parameters");
        problemDetail.setInstance(buildInstanceURI(servletRequest));
        return new ResponseEntity<>(
                ProblemDetailMultipleError.of(problemDetail, ex.getFieldErrors()),
                status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();
        var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setType(buildProblemTypeURI("error", "invalid-format"));
        problemDetail.setTitle("Bad Request");
        problemDetail.setDetail(ex.getMostSpecificCause().getMessage());
        problemDetail.setInstance(buildInstanceURI(servletRequest));

        ErrorDetail errorDetail = null;
        if (ex.getCause() instanceof InvalidFormatException ife) {
            problemDetail.setDetail("Invalid format");
            errorDetail = ErrorDetail.of(
                    "Invalid value %s".formatted(ife.getValue()),
                    ife.getPath().get(0).getFieldName(),
                    null);
        }

        return new ResponseEntity<>(
                errorDetail != null ? ProblemDetailMultipleError.of(problemDetail, errorDetail) : problemDetail,
                HttpStatus.BAD_REQUEST);
    }

    private static URI buildProblemTypeURI(String... segment) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .pathSegment(segment)
                .build().toUri();
    }

    private static URI buildInstanceURI(HttpServletRequest request) {
        return URI.create(request.getRequestURI());
    }
}
