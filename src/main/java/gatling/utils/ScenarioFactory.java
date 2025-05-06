package gatling.utils;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import static io.gatling.javaapi.core.CoreDsl.scenario;

/**
 * Factory class to build a Gatling ScenarioBuilder with an ordered list of ChainBuilder steps.
 */
public class ScenarioFactory {

    private static final Logger LOGGER = Logger.getLogger(ScenarioFactory.class.getName());

    /**
     * -- GETTER --
     *  Returns the scenario name.
     *
     * @return name of the scenario
     */
    @Getter
    private final String scenarioName;
    private final List<ChainBuilder> chainSteps = new ArrayList<>();

    /**
     * Constructor requiring a scenario name.
     *
     * @param scenarioName name of the scenario
     * @throws IllegalArgumentException if name is null or blank
     */
    public ScenarioFactory(String scenarioName) {
        if (scenarioName == null || scenarioName.isBlank()) {
            LOGGER.severe("Scenario name must not be null or blank.");
            throw new IllegalArgumentException("Scenario name is required.");
        }

        this.scenarioName = scenarioName;
        LOGGER.info("Initialized ScenarioFactory with scenario name: " + scenarioName);
    }

    /**
     * Appends one or more ChainBuilder steps to the scenario.
     *
     * @param chains varargs list of ChainBuilder instances
     * @return this instance for fluent API usage
     */
    public ScenarioFactory execChain(ChainBuilder... chains) {
        if (chains == null || chains.length == 0) {
            LOGGER.warning("No ChainBuilder steps provided to execChain; operation skipped.");
            return this;
        }

        chainSteps.addAll(Arrays.asList(chains));
        LOGGER.info("Added " + chains.length + " chain step(s) to scenario: " + scenarioName);
        return this;
    }

    /**
     * Builds and returns the ScenarioBuilder instance with all chained steps.
     *
     * @return the fully constructed ScenarioBuilder
     */
    public ScenarioBuilder build() {
        if (chainSteps.isEmpty()) {
            LOGGER.warning("Building scenario with no chain steps: " + scenarioName);
        }

        ScenarioBuilder builder = scenario(scenarioName);
        for (ChainBuilder chain : chainSteps) {
            builder = builder.exec(chain);
        }

        LOGGER.info("Scenario \"" + scenarioName + "\" built with " + chainSteps.size() + " step(s).");
        return builder;
    }

    /**
     * Exposes an unmodifiable list of configured chain steps.
     *
     * @return list of chain steps
     */
    public List<ChainBuilder> getChainSteps() {
        return Collections.unmodifiableList(chainSteps);
    }

}