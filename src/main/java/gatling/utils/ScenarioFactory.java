package gatling.utils;

import gatling.enums.BasePath;
import gatling.enums.HttpMethod;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.http.HttpRequestActionBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;

public class ScenarioFactory {

    private static final Logger LOGGER = Logger.getLogger(ScenarioFactory.class.getName());

    private final String scenarioName;
    private final List<HttpRequestActionBuilder> httpActions = new ArrayList<>();

    public ScenarioFactory(String scenarioName) {
        if (scenarioName == null || scenarioName.isBlank()) {
            LOGGER.severe("❌ Scenario name cannot be null or blank.");
            throw new IllegalArgumentException("Scenario name is required");
        }
        this.scenarioName = scenarioName;
        LOGGER.info("▶️ Initialized ScenarioFactory: \"" + scenarioName + "\"");
    }

    public ScenarioFactory exec(HttpRequestActionBuilder... actions) {
        if (actions == null || actions.length == 0) {
            LOGGER.warning("⚠️ exec() called with no actions.");
            return this;
        }
        this.httpActions.addAll(Arrays.asList(actions));
        LOGGER.info("➕ Added " + actions.length + " action(s) to scenario: " + scenarioName);
        return this;
    }

    public ScenarioFactory request(HttpMethod method, BasePath path) {
        if (method == null || path == null) {
            LOGGER.severe("❌ Method and path must not be null.");
            throw new IllegalArgumentException("HTTP method and path required");
        }

        String uri = path.toString();
        HttpRequestActionBuilder request;
        LOGGER.info("⚙️ Adding request to \"" + scenarioName + "\" — Method: " + method + ", Path: " + uri);

        switch (method) {
            case GET -> request = http(uri).get(uri);
            case POST -> request = http(uri).post(uri);
            case PUT -> request = http(uri).put(uri);
            case DELETE -> request = http(uri).delete(uri);
            case PATCH -> request = http(uri).patch(uri);
            case HEAD -> request = http(uri).head(uri);
            case OPTIONS -> request = http(uri).options(uri);
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }

        this.httpActions.add(request);
        LOGGER.info("✅ Request added: " + method + " " + uri);
        return this;
    }

    public ScenarioBuilder build() {
        if (httpActions.isEmpty()) {
            LOGGER.warning("⚠️ No HTTP actions in scenario: " + scenarioName);
        }

        try {
            ScenarioBuilder builder = scenario(scenarioName);
            for (HttpRequestActionBuilder action : httpActions) {
                builder = builder.exec(action);
            }
            LOGGER.info("🏁 Scenario \"" + scenarioName + "\" built.");
            return builder;
        } catch (Exception e) {
            LOGGER.severe("❌ Failed to build scenario: " + scenarioName);
            throw new RuntimeException("Scenario build error", e);
        }
    }
}
