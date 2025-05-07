package gatling.utils;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Factory class to construct a complete Gatling PopulationBuilder with
 * scenario, protocol, and injection steps.
 */
public class SimulationFactory {

    private static final Logger LOGGER = Logger.getLogger(SimulationFactory.class.getName());

    private final ScenarioBuilder scenario;
    private final HttpProtocolBuilder protocol;

    private List<OpenInjectionStep> openSteps = new ArrayList<>();
    private List<ClosedInjectionStep> closedSteps = new ArrayList<>();

    /**
     * Constructs the factory with required scenario and protocol.
     *
     * @param scenario the ScenarioBuilder
     * @param protocol the HttpProtocolBuilder
     * @throws IllegalArgumentException if either argument is null
     */
    public SimulationFactory(ScenarioBuilder scenario, HttpProtocolBuilder protocol) {
        if (scenario == null || protocol == null) {
            String msg = "Scenario and protocol must not be null.";
            LOGGER.severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.scenario = scenario;
        this.protocol = protocol;
        LOGGER.info("SimulationFactory initialized with valid scenario and protocol.");
    }

    /**
     * Sets the open model injection steps (e.g., at-once, ramp-up).
     * If previously defined, the list will be replaced.
     *
     * @param steps array of OpenInjectionStep
     * @return this instance for fluent API
     */
    public SimulationFactory injectOpen(OpenInjectionStep... steps) {
        if (steps == null || steps.length == 0) {
            LOGGER.warning("No open injection steps provided. Injection not set.");
            return this;
        }
        this.openSteps = Arrays.asList(steps);
        this.closedSteps.clear(); // enforce mutual exclusivity
        LOGGER.info("Open injection configured with " + openSteps.size() + " step(s).");
        return this;
    }

    /**
     * Sets the closed model injection steps (e.g., constant concurrent users).
     * If previously defined, the list will be replaced.
     *
     * @param steps array of ClosedInjectionStep
     * @return this instance for fluent API
     */
    public SimulationFactory injectClosed(ClosedInjectionStep... steps) {
        if (steps == null || steps.length == 0) {
            LOGGER.warning("No closed injection steps provided. Injection not set.");
            return this;
        }
        this.closedSteps = Arrays.asList(steps);
        this.openSteps.clear(); // enforce mutual exclusivity
        LOGGER.info("Closed injection configured with " + closedSteps.size() + " step(s).");
        return this;
    }

    /**
     * Builds the final PopulationBuilder with configured scenario, injection, and protocol.
     *
     * @return a fully configured PopulationBuilder
     * @throws IllegalStateException if no injection steps are defined
     */
    public PopulationBuilder build() {
        LOGGER.info("Building PopulationBuilder...");

        PopulationBuilder builder;

        if (!openSteps.isEmpty()) {
            builder = scenario.injectOpen(openSteps.toArray(new OpenInjectionStep[0]));
            LOGGER.info("Using open model with " + openSteps.size() + " step(s).");
        } else if (!closedSteps.isEmpty()) {
            builder = scenario.injectClosed(closedSteps.toArray(new ClosedInjectionStep[0]));
            LOGGER.info("Using closed model with " + closedSteps.size() + " step(s).");
        } else {
            String msg = "No injection steps configured. Cannot build simulation.";
            LOGGER.severe(msg);
            throw new IllegalStateException(msg);
        }

        return builder.protocols(protocol);
    }
}
