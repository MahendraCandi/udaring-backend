package co.id.udaring.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Constant {
    private Constant() {}

    public static Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }
}
