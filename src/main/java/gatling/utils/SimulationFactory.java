package gatling.utils;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Factory class for building reusable Gatling simulation blocks.
 * <p>
 * Builds {@link PopulationBuilder} from a {@link ScenarioBuilder} and HTTP protocol configuration
 * using either open or closed injection models. Execution must be handled in a separate class
 * that extends {@link Simulation}.
 */
public class SimulationFactory {

    private static final Logger LOGGER = Logger.getLogger(SimulationFactory.class.getName());

    private final ScenarioBuilder scenario;
    private final HttpProtocolBuilder protocol;
    private List<OpenInjectionStep> openSteps;
    private List<ClosedInjectionStep> closedSteps;

    /**
     * Constructs a SimulationFactory with required scenario and HTTP protocol.
     *
     * @param scenario the scenario builder
     * @param protocol the HTTP protocol configuration
     * @throws IllegalArgumentException if scenario or protocol is null
     */
    public SimulationFactory(ScenarioBuilder scenario, HttpProtocolBuilder protocol) {
        if (scenario == null || protocol == null) {
            String msg = "❌ Scenario and protocol must not be null.";
            LOGGER.severe(msg);
            throw new IllegalArgumentException(msg);
        }
        this.scenario = scenario;
        this.protocol = protocol;
        LOGGER.info("✅ SimulationFactory initialized with scenario and protocol.");
    }

    /**
     * Configures the open model injection steps (e.g. spike, ramp, steady).
     *
     * @param steps one or more {@link OpenInjectionStep}
     * @return this instance for fluent chaining
     */
    public SimulationFactory injectOpen(OpenInjectionStep... steps) {
        if (steps == null || steps.length == 0) {
            LOGGER.warning("⚠️ injectOpen() called with null or empty steps.");
            return this;
        }
        this.openSteps = Arrays.asList(steps);
        LOGGER.info("➕ Configured open injection model with " + steps.length + " step(s).");
        return this;
    }

    /**
     * Configures the closed model injection steps (e.g. constant concurrent users).
     *
     * @param steps one or more {@link ClosedInjectionStep}
     * @return this instance for fluent chaining
     */
    public SimulationFactory injectClosed(ClosedInjectionStep... steps) {
        if (steps == null || steps.length == 0) {
            LOGGER.warning("⚠️ injectClosed() called with null or empty steps.");
            return this;
        }
        this.closedSteps = Arrays.asList(steps);
        LOGGER.info("➕ Configured closed injection model with " + steps.length + " step(s).");
        return this;
    }

    /**
     * Builds the final {@link PopulationBuilder} using the configured scenario and injection model.
     *
     * @return a complete PopulationBuilder with attached protocol
     * @throws IllegalStateException if no injection model is configured
     */
    public PopulationBuilder build() {
        LOGGER.info("🔧 Building PopulationBuilder...");

        try {
            PopulationBuilder builder;

            if (openSteps != null) {
                builder = scenario.injectOpen(openSteps.toArray(new OpenInjectionStep[0]));
                LOGGER.info("✅ Built with open model (" + openSteps.size() + " step(s))");
            } else if (closedSteps != null) {
                builder = scenario.injectClosed(closedSteps.toArray(new ClosedInjectionStep[0]));
                LOGGER.info("✅ Built with closed model (" + closedSteps.size() + " step(s))");
            } else {
                String msg = "❌ No injection steps provided. Use injectOpen() or injectClosed().";
                LOGGER.severe(msg);
                throw new IllegalStateException(msg);
            }

            LOGGER.info("🏁 PopulationBuilder successfully constructed.");
            return builder.protocols(protocol);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Failed to build PopulationBuilder", e);
            throw new RuntimeException("Failed to build PopulationBuilder", e);
        }
    }
}