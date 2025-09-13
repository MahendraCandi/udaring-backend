package co.id.udaring.exception;

import co.id.udaring.exception.util.ErrorDetail;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

import java.util.Arrays;
import java.util.List;

public class InvalidParametersException extends ErrorResponseException {

    @Getter
    private final List<ErrorDetail> errorDetail;

    public InvalidParametersException(ErrorDetail... errorDetail) {
        super(HttpStatus.BAD_REQUEST);
        this. errorDetail = Arrays.asList(errorDetail);
        this.setTitle("Invalid parameters");
        this.setDetail("Invalid parameters");
    }
}
