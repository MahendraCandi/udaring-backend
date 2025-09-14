package co.id.udaring.util;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;

public class Constant {
    private Constant() {}

    public static final Instant INSTANT_NOW = Instant.now(Clock.systemDefaultZone());

    public static Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.atZone(Clock.systemDefaultZone().getZone()).toInstant();
    }
}
