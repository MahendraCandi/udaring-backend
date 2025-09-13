package co.id.udaring.exception.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import lombok.Getter;
import org.hibernate.validator.internal.engine.path.NodeImpl;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;

import java.util.*;

@JsonPropertyOrder(value = {
        "type",
        "title",
        "status",
        "detail",
        "instance",
        "errors"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class ProblemDetailMultipleError extends ProblemDetail {
    private final List<ErrorDetail> errors = new ArrayList<>();

    private ProblemDetailMultipleError(ProblemDetail other, List<ErrorDetail> errors) {
        super(other);
        this.errors.addAll(errors);
    }

    public static ProblemDetailMultipleError of(ProblemDetail problemDetail, List<FieldError> fieldErrors) {
        return new ProblemDetailMultipleError(problemDetail, toList(fieldErrors));
    }

    public static ProblemDetailMultipleError of(ProblemDetail problemDetail, Set<ConstraintViolation<?>> constraintViolations) {
        return new ProblemDetailMultipleError(problemDetail, toList(constraintViolations));
    }

    public static ProblemDetailMultipleError of(ProblemDetail problemDetail, ErrorDetail... errorDetail) {
        return new ProblemDetailMultipleError(problemDetail, Arrays.asList(errorDetail));
    }

    /**
     * Converts a list of {@link FieldError} instances to a list of {@link ErrorDetail}.
     * <p>
     * This is useful when processing Spring validation errors.
     * The {@link JsonPointerUtil} is used to generate the `pointer` value.
     * </p>
     *
     * @param fieldErrors the list of {@link FieldError} instances
     * @return a list of {@link ErrorDetail}
     */
    private static List<ErrorDetail> toList(List<FieldError> fieldErrors) {
        List<ErrorDetail> errorDetails = new ArrayList<>();
        for (final FieldError fieldError : fieldErrors) {
            final String jsonPointer = JsonPointerUtil.toJsonPointer(fieldError.getField());
            errorDetails.add(ErrorDetail.of(fieldError.getDefaultMessage(), fieldError.getField(), jsonPointer));
        }
        return errorDetails;
    }

    /**
     * Converts a set of {@link ConstraintViolation} instances to a list of {@link ErrorDetail}.
     * <p>
     * Only the last node in the property path is used as the field name.
     * This is useful when processing bean validation violations.
     * </p>
     *
     * @param constraintViolations the set of {@link ConstraintViolation}
     * @return a list of {@link ErrorDetail}
     */
    private static List<ErrorDetail> toList(Set<ConstraintViolation<?>> constraintViolations) {
        List<ErrorDetail> errorDetails = new ArrayList<>();
        for (final ConstraintViolation<?> constraintViolation : constraintViolations) {
            Path.Node latestPath = null;
            for (final Path.Node node : constraintViolation.getPropertyPath()) {
                latestPath = node;
            }

            if (latestPath != null) {
                String fieldName;
                if (latestPath instanceof NodeImpl node) {
                    fieldName = toSpringDotNotation(node, node.asString());
                } else
                    fieldName = latestPath.getName();

                final String jsonPointer = JsonPointerUtil.toJsonPointer(fieldName);
                errorDetails.add(ErrorDetail.of(constraintViolation.getMessage(), fieldName, jsonPointer));
            }
        }
        return errorDetails;
    }

    private static String toSpringDotNotation(NodeImpl node, String concateName) {
        if (node.getParent() != null && node.getParent().isIterable()) {
            concateName = node.getParent().asString() + "." + concateName;
            return toSpringDotNotation(node.getParent(), concateName);
        }
        return concateName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final ProblemDetailMultipleError that = (ProblemDetailMultipleError) o;
        return Objects.equals(errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), errors);
    }
}
