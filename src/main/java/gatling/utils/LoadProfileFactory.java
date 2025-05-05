package gatling.utils;

import io.gatling.javaapi.core.OpenInjectionStep;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.gatling.javaapi.core.CoreDsl.*;

public final class LoadProfileFactory {

    private static final Logger LOGGER = Logger.getLogger(LoadProfileFactory.class.getName());

    private LoadProfileFactory() {
        // Prevent instantiation
    }

    public static OpenInjectionStep spike(int users) {
        if (users <= 0) {
            String msg = "❌ SPIKE profile must have positive users. Given: " + users;
            LOGGER.severe(msg);
            throw new IllegalArgumentException(msg);
        }
        LOGGER.info("⚡ Creating SPIKE load: " + users + " users at once");
        return atOnceUsers(users);
    }

    public static OpenInjectionStep rampUp(int users, int durationSeconds) {
        validate(users, durationSeconds, "RAMP-UP");
        LOGGER.info("📈 RAMP-UP: " + users + " users over " + durationSeconds + "s");
        return rampUsers(users).during(java.time.Duration.ofSeconds(durationSeconds));
    }

    public static OpenInjectionStep steadyUsers(int usersPerSec, int durationSeconds) {
        validate(usersPerSec, durationSeconds, "STEADY");
        LOGGER.info("📊 STEADY: " + usersPerSec + " users/sec for " + durationSeconds + "s");
        return constantUsersPerSec(usersPerSec).during(java.time.Duration.ofSeconds(durationSeconds));
    }

    public static OpenInjectionStep stressRamp(int fromUsers, int toUsers, int durationSeconds) {
        if (fromUsers < 0 || toUsers <= fromUsers || durationSeconds <= 0) {
            String msg = String.format("❌ Invalid STRESS RAMP: from=%d, to=%d, duration=%d",
                    fromUsers, toUsers, durationSeconds);
            LOGGER.severe(msg);
            throw new IllegalArgumentException(msg);
        }

        LOGGER.info("🔥 STRESS RAMP: from " + fromUsers + " to " + toUsers +
                " users/sec over " + durationSeconds + "s");
        return rampUsersPerSec(fromUsers).to(toUsers).during(java.time.Duration.ofSeconds(durationSeconds));
    }

    private static void validate(int users, int duration, String profile) {
        if (users <= 0 || duration <= 0) {
            String msg = String.format("❌ Invalid %s profile: users=%d, duration=%d",
                    profile.toUpperCase(), users, duration);
            LOGGER.log(Level.SEVERE, msg);
            throw new IllegalArgumentException(msg);
        }
    }
}