package gatling.utils;

import io.gatling.javaapi.core.OpenInjectionStep;

import java.util.logging.Level;
import java.util.logging.Logger;

import static io.gatling.javaapi.core.CoreDsl.*;

/**
 * Factory class to create reusable and validated OpenInjectionStep profiles for Gatling.
 * Provides standard load models: spike, ramp-up, steady, and stress.
 */
public final class LoadProfileFactory {

    private static final Logger LOGGER = Logger.getLogger(LoadProfileFactory.class.getName());

    private LoadProfileFactory() {
        // Prevent instantiation
    }

    /**
     * Creates a SPIKE load profile — all users are injected instantly.
     *
     * @param users number of users to inject at once
     * @return OpenInjectionStep
     * @throws IllegalArgumentException if users <= 0
     */
    public static OpenInjectionStep spike(int users) {
        if (users <= 0) {
            String msg = "❌ SPIKE profile must have a positive user count. Given: " + users;
            LOGGER.severe(msg);
            throw new IllegalArgumentException(msg);
        }

        LOGGER.info("⚡ Creating SPIKE profile: injecting " + users + " user(s) at once");
        return atOnceUsers(users);
    }

    /**
     * Creates a RAMP-UP profile — users ramp up gradually over a duration.
     *
     * @param users           total users to inject
     * @param durationSeconds time in seconds to ramp them up
     * @return OpenInjectionStep
     * @throws IllegalArgumentException if inputs are invalid
     */
    public static OpenInjectionStep rampUp(int users, int durationSeconds) {
        validate(users, durationSeconds, "RAMP-UP");
        LOGGER.info("📈 Creating RAMP-UP profile: " + users + " user(s) over " + durationSeconds + " second(s)");
        return rampUsers(users).during(durationSeconds);
    }

    /**
     * Creates a STEADY profile — constant user injection per second over time.
     *
     * @param usersPerSec     users per second
     * @param durationSeconds duration in seconds
     * @return OpenInjectionStep
     * @throws IllegalArgumentException if inputs are invalid
     */
    public static OpenInjectionStep steadyUsers(int usersPerSec, int durationSeconds) {
        validate(usersPerSec, durationSeconds, "STEADY");
        LOGGER.info("📊 Creating STEADY profile: " + usersPerSec + " user(s)/sec for " + durationSeconds + " second(s)");
        return constantUsersPerSec(usersPerSec).during(durationSeconds);
    }

    /**
     * Creates a STRESS RAMP — gradually increases users/sec from one level to another.
     *
     * @param fromUsers       starting rate (users/sec)
     * @param toUsers         peak rate (users/sec)
     * @param durationSeconds duration in seconds to ramp
     * @return OpenInjectionStep
     * @throws IllegalArgumentException if fromUsers < 0, toUsers <= fromUsers, or durationSeconds <= 0
     */
    public static OpenInjectionStep stressRamp(int fromUsers, int toUsers, int durationSeconds) {
        if (fromUsers < 0 || toUsers <= fromUsers || durationSeconds <= 0) {
            String msg = String.format("❌ Invalid STRESS RAMP profile: from=%d, to=%d, duration=%d",
                    fromUsers, toUsers, durationSeconds);
            LOGGER.severe(msg);
            throw new IllegalArgumentException(msg);
        }

        LOGGER.info("🔥 Creating STRESS RAMP: from " + fromUsers + " to " + toUsers +
                " user(s)/sec over " + durationSeconds + " second(s)");
        return rampUsersPerSec(fromUsers).to(toUsers).during(durationSeconds);
    }

    /**
     * Internal validator for basic load profiles (non-stress).
     *
     * @param users    number of users or rate
     * @param duration duration in seconds
     * @param profile  the name of the profile for logging
     */
    private static void validate(int users, int duration, String profile) {
        if (users <= 0 || duration <= 0) {
            String msg = String.format("❌ Invalid %s profile input: users=%d, duration=%d",
                    profile.toUpperCase(), users, duration);
            LOGGER.log(Level.SEVERE, msg);
            throw new IllegalArgumentException(msg);
        }
    }
}