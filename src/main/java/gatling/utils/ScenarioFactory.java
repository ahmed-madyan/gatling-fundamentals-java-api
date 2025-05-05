package gatling.utils;

import gatling.enums.BasePath;
import gatling.enums.HttpMethod;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.http.HttpRequestActionBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;

/**
 * Factory for building reusable Gatling {@link ScenarioBuilder} instances.
 * Supports method/path-driven request steps and custom HTTP actions with full logging and validation.
 */
public class ScenarioFactory {

    private static final Logger LOGGER = Logger.getLogger(ScenarioFactory.class.getName());

    private final String scenarioName;
    private final List<HttpRequestActionBuilder> httpActions;

    /**
     * Constructs a scenario factory for the given name.
     *
     * @param scenarioName a descriptive name for the scenario
     * @throws IllegalArgumentException if the name is null or blank
     */
    public ScenarioFactory(String scenarioName) {
        if (scenarioName == null || scenarioName.isBlank()) {
            String msg = "❌ Scenario name cannot be null or blank.";
            LOGGER.severe(msg);
            throw new IllegalArgumentException(msg);
        }
        this.scenarioName = scenarioName;
        this.httpActions = new ArrayList<>();
        LOGGER.info("▶️ Initialized ScenarioFactory for scenario: \"" + scenarioName + "\"");
    }

    /**
     * Adds one or more custom {@link HttpRequestActionBuilder} steps directly to the scenario.
     *
     * @param actions variable list of HTTP actions (can be null)
     * @return this builder instance
     */
    public ScenarioFactory exec(HttpRequestActionBuilder... actions) {
        if (actions == null || actions.length == 0) {
            LOGGER.warning("⚠️ exec() called with null or empty actions for scenario: \"" + scenarioName + "\"");
            return this;
        }

        this.httpActions.addAll(Arrays.asList(actions));
        LOGGER.info("➕ Added " + actions.length + " custom exec() action(s) to scenario: \"" + scenarioName + "\"");
        return this;
    }

    /**
     * Adds an HTTP request based on an {@link HttpMethod} and a {@link BasePath}.
     *
     * @param method the HTTP method (GET, POST, etc.)
     * @param path   the request path as enum
     * @return this builder instance
     * @throws IllegalArgumentException for unsupported methods
     */
    public ScenarioFactory request(HttpMethod method, BasePath path) {
        if (method == null || path == null) {
            String msg = "❌ Method and path must not be null when adding request.";
            LOGGER.severe(msg);
            throw new IllegalArgumentException(msg);
        }

        String pathString = path.toString();
        HttpRequestActionBuilder request;

        LOGGER.info("⚙️ Adding request to scenario \"" + scenarioName + "\" — Method: " + method + ", Path: " + pathString);

        try {
            switch (method) {
                case GET -> request = http(pathString).get(pathString);
                case POST -> request = http(pathString).post(pathString);
                case PUT -> request = http(pathString).put(pathString);
                case DELETE -> request = http(pathString).delete(pathString);
                case PATCH -> request = http(pathString).patch(pathString);
                case HEAD -> request = http(pathString).head(pathString);
                case OPTIONS -> request = http(pathString).options(pathString);
                default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Failed to create request for method=" + method + ", path=" + pathString, e);
            throw new RuntimeException("Could not create request step", e);
        }

        this.httpActions.add(request);
        LOGGER.info("✅ Request added: " + method + " " + pathString);
        return this;
    }

    /**
     * Builds and returns the final {@link ScenarioBuilder} instance.
     *
     * @return a fully constructed ScenarioBuilder
     */
    public ScenarioBuilder build() {
        if (httpActions.isEmpty()) {
            LOGGER.warning("⚠️ No HTTP actions defined for scenario: \"" + scenarioName + "\". Scenario may do nothing.");
        } else {
            LOGGER.info("🛠️ Building scenario \"" + scenarioName + "\" with " + httpActions.size() + " step(s)");
        }

        try {
            ScenarioBuilder builder = scenario(scenarioName);
            for (HttpRequestActionBuilder action : httpActions) {
                builder = builder.exec(action);
            }

            LOGGER.info("🏁 Scenario \"" + scenarioName + "\" successfully built.");
            return builder;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Failed to build scenario: \"" + scenarioName + "\"", e);
            throw new RuntimeException("Error while building scenario: " + scenarioName, e);
        }
    }
}