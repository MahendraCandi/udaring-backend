package co.id.udaring.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

public class AuthenticationException extends ErrorResponseException {
    public AuthenticationException() {
        this("Invalid credentials");
    }

    public AuthenticationException(String message) {
        super(HttpStatus.FORBIDDEN);
        this.setDetail(message);
    }
}
