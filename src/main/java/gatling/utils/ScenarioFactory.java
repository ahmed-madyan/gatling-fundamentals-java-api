package gatling.utils;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static io.gatling.javaapi.core.CoreDsl.*;

public class ScenarioFactory {

    private static final Logger LOGGER = Logger.getLogger(ScenarioFactory.class.getName());

    private final String scenarioName;
    private final List<ChainBuilder> chainSteps = new ArrayList<>();

    public ScenarioFactory(String scenarioName) {
        if (scenarioName == null || scenarioName.isBlank()) {
            LOGGER.severe("❌ Scenario name cannot be null or blank.");
            throw new IllegalArgumentException("Scenario name is required");
        }
        this.scenarioName = scenarioName;
        LOGGER.info("▶️ Initialized ScenarioFactory: \"" + scenarioName + "\"");
    }

    public ScenarioFactory execChain(ChainBuilder... chains) {
        if (chains == null || chains.length == 0) {
            LOGGER.warning("⚠️ execChain called with no chains.");
            return this;
        }

        this.chainSteps.addAll(Arrays.asList(chains));
        LOGGER.info("🔗 Added " + chains.length + " ChainBuilder step(s) to scenario: " + scenarioName);
        return this;
    }


    public ScenarioBuilder build() {
        if (chainSteps.isEmpty()) {
            LOGGER.warning("⚠️ No steps added to scenario: " + scenarioName);
        }

        ScenarioBuilder builder = scenario(scenarioName);
        for (ChainBuilder chain : chainSteps) {
            builder = builder.exec(chain);
        }

        LOGGER.info("🏁 Scenario \"" + scenarioName + "\" built with " + chainSteps.size() + " steps.");
        return builder;
    }
}