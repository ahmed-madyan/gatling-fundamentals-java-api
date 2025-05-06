package gatling.utils;

import io.gatling.javaapi.core.OpenInjectionStep;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.gatling.javaapi.core.CoreDsl.*;

/**
 * Provides factory methods to create various user load injection profiles
 * for open Gatling simulation models.
 */
public final class LoadProfileFactory {

    private static final Logger LOGGER = Logger.getLogger(LoadProfileFactory.class.getName());

    private LoadProfileFactory() {
        // Prevent instantiation
        throw new UnsupportedOperationException("LoadProfileFactory is a utility class and cannot be instantiated.");
    }

    /**
     * Creates a spike load injection with all users injected at once.
     *
     * @param users the number of users to inject
     * @return an OpenInjectionStep configured for spike
     * @throws IllegalArgumentException if users <= 0
     */
    public static OpenInjectionStep spike(int users) {
        validate(users, "SPIKE");
        LOGGER.info("Creating SPIKE profile with " + users + " users injected immediately.");
        return atOnceUsers(users);
    }

    /**
     * Creates a ramp-up load injection over time.
     *
     * @param users           the total number of users
     * @param durationSeconds duration in seconds for ramp-up
     * @return configured OpenInjectionStep
     * @throws IllegalArgumentException if users <= 0 or duration <= 0
     */
    public static OpenInjectionStep rampUp(int users, int durationSeconds) {
        validate(users, durationSeconds, "RAMP-UP");
        LOGGER.info("Creating RAMP-UP profile with " + users + " users over " + durationSeconds + " seconds.");
        return rampUsers(users).during(Duration.ofSeconds(durationSeconds));
    }

    /**
     * Creates a steady load injection with constant users per second.
     *
     * @param usersPerSec     the number of users per second
     * @param durationSeconds duration of the steady load
     * @return configured OpenInjectionStep
     * @throws IllegalArgumentException if usersPerSec <= 0 or duration <= 0
     */
    public static OpenInjectionStep steadyUsers(int usersPerSec, int durationSeconds) {
        validate(usersPerSec, durationSeconds, "STEADY");
        LOGGER.info("Creating STEADY profile with " + usersPerSec + " users/sec for " + durationSeconds + " seconds.");
        return constantUsersPerSec(usersPerSec).during(Duration.ofSeconds(durationSeconds));
    }

    /**
     * Creates a stress ramp load injection, gradually increasing user load.
     *
     * @param fromUsers       starting user rate
     * @param toUsers         ending user rate
     * @param durationSeconds duration over which to ramp up
     * @return configured OpenInjectionStep
     * @throws IllegalArgumentException for invalid user ranges or duration
     */
    public static OpenInjectionStep stressRamp(int fromUsers, int toUsers, int durationSeconds) {
        if (fromUsers < 0 || toUsers <= fromUsers || durationSeconds <= 0) {
            String msg = String.format("Invalid STRESS RAMP: from=%d, to=%d, duration=%d",
                    fromUsers, toUsers, durationSeconds);
            LOGGER.severe(msg);
            throw new IllegalArgumentException(msg);
        }

        LOGGER.info("Creating STRESS RAMP from " + fromUsers + " to " + toUsers +
                " users/sec over " + durationSeconds + " seconds.");
        return rampUsersPerSec(fromUsers).to(toUsers).during(Duration.ofSeconds(durationSeconds));
    }

    /**
     * Validates positive user count.
     */
    private static void validate(int users, String profile) {
        if (users <= 0) {
            String msg = String.format("Invalid %s profile: users=%d", profile.toUpperCase(), users);
            LOGGER.log(Level.SEVERE, msg);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Validates user and duration values.
     */
    private static void validate(int users, int duration, String profile) {
        if (users <= 0 || duration <= 0) {
            String msg = String.format("Invalid %s profile: users=%d, duration=%d",
                    profile.toUpperCase(), users, duration);
            LOGGER.log(Level.SEVERE, msg);
            throw new IllegalArgumentException(msg);
        }
    }
}