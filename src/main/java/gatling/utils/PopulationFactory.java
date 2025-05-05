package gatling.utils;

import io.gatling.javaapi.core.PopulationBuilder;

import java.util.logging.Logger;

/**
 * Utility for validating and preparing {@link PopulationBuilder} instances
 * for safe usage in {@code Gatling.setUp(...)} blocks.
 * <p>
 * Prevents null usage and provides traceable logging to support debugging
 * and test diagnostics in performance simulations.
 */
public final class PopulationFactory {

    private static final Logger LOGGER = Logger.getLogger(PopulationFactory.class.getName());

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private PopulationFactory() {
        LOGGER.warning("⚠️ Attempted instantiation of PopulationFactory — utility class should not be instantiated.");
        throw new UnsupportedOperationException("PopulationFactory is a utility class and cannot be instantiated.");
    }

    /**
     * Validates a {@link PopulationBuilder} before it is used in {@code setUp()}.
     * Logs diagnostic information to help identify configuration or build issues.
     *
     * @param population the {@link PopulationBuilder} to validate
     * @return the same {@code PopulationBuilder} if valid
     * @throws IllegalArgumentException if the input is null
     */
    public static PopulationBuilder with(PopulationBuilder population) {
        LOGGER.fine("🔍 Validating PopulationBuilder...");

        if (population == null) {
            String msg = "❌ PopulationBuilder is null. Cannot proceed with setUp().";
            LOGGER.severe(msg);
            throw new IllegalArgumentException(msg);
        }

        LOGGER.info("✅ PopulationBuilder validation passed. Ready for setUp().");
        return population;
    }
}
