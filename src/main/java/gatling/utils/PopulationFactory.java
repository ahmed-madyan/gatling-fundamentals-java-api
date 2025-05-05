package gatling.utils;

import io.gatling.javaapi.core.PopulationBuilder;
import java.util.logging.Logger;

public final class PopulationFactory {

    private static final Logger LOGGER = Logger.getLogger(PopulationFactory.class.getName());

    private PopulationFactory() {
        LOGGER.warning("⚠️ Do not instantiate PopulationFactory — utility class.");
        throw new UnsupportedOperationException("Cannot instantiate PopulationFactory");
    }

    public static PopulationBuilder with(PopulationBuilder population) {
        LOGGER.fine("🔍 Validating PopulationBuilder...");
        if (population == null) {
            String msg = "❌ PopulationBuilder is null.";
            LOGGER.severe(msg);
            throw new IllegalArgumentException(msg);
        }
        LOGGER.info("✅ PopulationBuilder validated.");
        return population;
    }
}
