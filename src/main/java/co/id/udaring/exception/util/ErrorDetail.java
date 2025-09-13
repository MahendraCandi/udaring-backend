package co.id.udaring.exception.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents detailed information about a validation error for a specific field.
 * <p>
 * This class is used to encapsulate field-level error details, such as the
 * error message (`detail`), the field name (`field`), and optionally a JSON pointer (`pointer`)
 * to help map the error location in a request body.
 * </p>
 * <p>
 * This class is immutable and marked as final.
 * </p>
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ErrorDetail implements Serializable {
    @Serial
    private static final long serialVersionUID = 112341324L;

    private final String detail;
    private final String field;
    private final String pointer;

    private ErrorDetail(String detail, String field, String pointer) {
        this.detail = detail;
        this.field = field;
        this.pointer = pointer;
    }

    public static ErrorDetail of(String detail, String field, String pointer) {
        return new ErrorDetail(detail, field, pointer);
    }

    public static ErrorDetail of(String detail, String field) {
        return new ErrorDetail(detail, field, null);
    }
}
