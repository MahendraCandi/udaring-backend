package co.id.udaring.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

public class AuthenticationException extends ErrorResponseException {
    public AuthenticationException() {
        super(HttpStatus.FORBIDDEN);
        this.setDetail("Invalid credentials");
    }
}
