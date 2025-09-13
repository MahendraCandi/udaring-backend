package co.id.udaring.exception.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for converting Spring-style dot notation field paths
 * (e.g. {@code user.address[0].street}) into RFC 6901-compliant JSON Pointer
 * strings (e.g. {@code /user/address/0/street}).
 *
 * <p>Example usage:</p>
 * <pre>{@code
 *     String springPath = "tables[0].desc";
 *     String pointer = JsonPointerUtil.toJsonPointer(springPath);
 *     // pointer = "/tables/0/desc"
 * }</pre>
 * <p>
 * This class is not instantiable.
 */
public class JsonPointerUtil {

    private static final Pattern SPRING_DOT_NOTATION_PATTERN = Pattern.compile("([a-zA-Z_]\\w*)(?:\\[\\d+])*+");
    private static final Pattern ARRAY_INDEX_NOTATION = Pattern.compile("\\[(\\d+)]");
    private static final String SLASH = "/";

    private JsonPointerUtil() {
    }

    public static String toJsonPointer(String springDotNotation) {
        if (springDotNotation == null || springDotNotation.isEmpty()) {
            throw new IllegalArgumentException("Path must not be null or empty");
        }

        List<String> parts = new ArrayList<>();

        // Split by dot, and parse each segment
        for (String segment : springDotNotation.split("\\.")) {
            Matcher matcher = SPRING_DOT_NOTATION_PATTERN.matcher(segment);
            if (matcher.find()) {
                String baseName = matcher.group(1);
                parts.add(escape(baseName));

                // Find all indices in this segment
                Matcher indexMatcher = ARRAY_INDEX_NOTATION.matcher(segment);
                while (indexMatcher.find()) {
                    parts.add(indexMatcher.group(1)); // No need to escape indices
                }
            } else {
                throw new IllegalArgumentException("Invalid path segment: " + segment);
            }
        }

        return SLASH + String.join(SLASH, parts);
    }

    // Escapes special characters per JSON Pointer spec
    private static String escape(String s) {
        return s.replace("~", "~0")
                .replace("/", "~1");
    }
}
