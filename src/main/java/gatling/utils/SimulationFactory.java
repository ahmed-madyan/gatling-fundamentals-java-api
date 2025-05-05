package gatling.utils;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class SimulationFactory {

    private static final Logger LOGGER = Logger.getLogger(SimulationFactory.class.getName());

    private final ScenarioBuilder scenario;
    private final HttpProtocolBuilder protocol;
    private List<OpenInjectionStep> openSteps;
    private List<ClosedInjectionStep> closedSteps;

    public SimulationFactory(ScenarioBuilder scenario, HttpProtocolBuilder protocol) {
        if (scenario == null || protocol == null) {
            String msg = "❌ Scenario and protocol must not be null.";
            LOGGER.severe(msg);
            throw new IllegalArgumentException(msg);
        }
        this.scenario = scenario;
        this.protocol = protocol;
        LOGGER.info("✅ Initialized SimulationFactory.");
    }

    public SimulationFactory injectOpen(OpenInjectionStep... steps) {
        if (steps == null || steps.length == 0) {
            LOGGER.warning("⚠️ No open injection steps.");
            return this;
        }
        this.openSteps = Arrays.asList(steps);
        LOGGER.info("➕ Configured open injection with " + steps.length + " step(s).");
        return this;
    }

    public SimulationFactory injectClosed(ClosedInjectionStep... steps) {
        if (steps == null || steps.length == 0) {
            LOGGER.warning("⚠️ No closed injection steps.");
            return this;
        }
        this.closedSteps = Arrays.asList(steps);
        LOGGER.info("➕ Configured closed injection with " + steps.length + " step(s).");
        return this;
    }

    public PopulationBuilder build() {
        LOGGER.info("🔧 Building PopulationBuilder...");

        PopulationBuilder builder;

        if (openSteps != null) {
            builder = scenario.injectOpen(openSteps.toArray(new OpenInjectionStep[0]));
            LOGGER.info("✅ Using open model with " + openSteps.size() + " step(s)");
        } else if (closedSteps != null) {
            builder = scenario.injectClosed(closedSteps.toArray(new ClosedInjectionStep[0]));
            LOGGER.info("✅ Using closed model with " + closedSteps.size() + " step(s)");
        } else {
            String msg = "❌ No injection steps provided.";
            LOGGER.severe(msg);
            throw new IllegalStateException(msg);
        }

        return builder.protocols(protocol);
    }
}
