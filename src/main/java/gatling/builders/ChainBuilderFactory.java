package gatling.builders;

import gatling.enums.HttpMethod;
import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.CheckBuilder;
import io.gatling.javaapi.http.HttpRequestActionBuilder;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.http.HttpDsl.http;

/**
 * Factory class for building Gatling chains with a fluent API.
 * Provides methods to create HTTP requests with various HTTP methods,
 * headers, body, and checks.
 */
public class ChainBuilderFactory {
    private static final Logger logger = LoggerFactory.getLogger(ChainBuilderFactory.class);

    @Getter
    private final String name;
    private HttpMethod method;
    private String path;
    private Object body;
    private final List<CheckBuilder> checks = new ArrayList<>();
    private final Map<String, String> headers = new java.util.HashMap<>();

    /**
     * Creates a new ChainBuilderFactory with the given request name.
     *
     * @param name The name of the request
     */
    public ChainBuilderFactory(String name) {
        this.name = name;
        logger.debug("Created new ChainBuilderFactory with name: {}", name);
    }

    /**
     * Sets the HTTP method and path for the request.
     *
     * @param method The HTTP method enum
     * @param path The request path
     * @return this instance for fluent API
     */
    public ChainBuilderFactory request(HttpMethod method, String path) {
        logger.debug("Setting request method: {} and path: {} for chain: {}", method, path, name);
        this.method = method;
        this.path = path;
        return this;
    }

    /**
     * Sets the request body.
     *
     * @param body The request body
     * @return this instance for fluent API
     */
    public ChainBuilderFactory withBody(Object body) {
        logger.debug("Setting request body for chain: {}. Body: {}", name, body);
        this.body = body;
        return this;
    }

    /**
     * Adds a header to the request.
     *
     * @param key The header name
     * @param value The header value
     * @return this instance for fluent API
     */
    public ChainBuilderFactory withHeader(String key, String value) {
        logger.debug("Adding header {}: {} for chain: {}", key, value, name);
        this.headers.put(key, value);
        return this;
    }

    /**
     * Adds multiple headers to the request.
     *
     * @param headers Map of header names to values
     * @return this instance for fluent API
     */
    public ChainBuilderFactory withHeaders(Map<String, String> headers) {
        logger.debug("Adding multiple headers for chain: {}. Headers: {}", name, headers);
        this.headers.putAll(headers);
        return this;
    }

    /**
     * Adds a check to the request.
     *
     * @param check The check to add
     * @return this instance for fluent API
     */
    public ChainBuilderFactory withCheck(CheckBuilder check) {
        logger.debug("Adding check for chain: {}", name);
        this.checks.add(check);
        return this;
    }

    /**
     * Adds multiple checks to the request.
     *
     * @param checks Array of checks to add
     * @return this instance for fluent API
     */
    public ChainBuilderFactory withChecks(CheckBuilder... checks) {
        logger.debug("Adding {} checks for chain: {}", checks.length, name);
        this.checks.addAll(List.of(checks));
        return this;
    }

    /**
     * Saves a value from a JSON path to a session variable.
     *
     * @param jsonPath The JSON path expression
     * @param sessionKey The key to save the value as in the session
     * @return this instance for fluent API
     */
    public ChainBuilderFactory saveAs(String jsonPath, String sessionKey) {
        logger.debug("Adding saveAs check for chain: {}. JSON Path: {}, Session Key: {}", name, jsonPath, sessionKey);
        this.checks.add(
            jsonPath(jsonPath)
                .exists()
                .saveAs(sessionKey)
        );
        return this;
    }

    /**
     * Saves a value from a JSON path to a session variable with validation.
     *
     * @param jsonPath The JSON path expression
     * @param sessionKey The key to save the value as in the session
     * @param expectedValue The expected value to validate against
     * @return this instance for fluent API
     */
    public ChainBuilderFactory saveAs(String jsonPath, String sessionKey, String expectedValue) {
        logger.debug("Adding saveAs check with validation for chain: {}. JSON Path: {}, Session Key: {}, Expected Value: {}", 
            name, jsonPath, sessionKey, expectedValue);
        this.checks.add(
            jsonPath(jsonPath)
                .is(expectedValue)
                .saveAs(sessionKey)
        );
        return this;
    }

    /**
     * Builds the final ChainBuilder with all configured options.
     *
     * @return A ChainBuilder instance
     * @throws IllegalStateException if method or path is not set
     */
    public ChainBuilder build() {
        logger.info("Building chain: {}", name);
        
        if (method == null || path == null) {
            String error = "Method and path must be set before building the chain";
            logger.error("{} for chain: {}", error, name);
            throw new IllegalStateException(error);
        }

        logger.debug("Building request with method: {} and path: {}", method, path);
        HttpRequestActionBuilder request;
        switch (method) {
            case GET -> {
                logger.debug("Creating GET request for chain: {}", name);
                request = http(name).get(path);
            }
            case POST -> {
                logger.debug("Creating POST request for chain: {}", name);
                request = http(name).post(path);
            }
            case PUT -> {
                logger.debug("Creating PUT request for chain: {}", name);
                request = http(name).put(path);
            }
            case DELETE -> {
                logger.debug("Creating DELETE request for chain: {}", name);
                request = http(name).delete(path);
            }
            case PATCH -> {
                logger.debug("Creating PATCH request for chain: {}", name);
                request = http(name).patch(path);
            }
            case HEAD -> {
                logger.debug("Creating HEAD request for chain: {}", name);
                request = http(name).head(path);
            }
            case OPTIONS -> {
                logger.debug("Creating OPTIONS request for chain: {}", name);
                request = http(name).options(path);
            }
            default -> {
                String error = "Unsupported HTTP method: " + method;
                logger.error("{} for chain: {}", error, name);
                throw new IllegalStateException(error);
            }
        }

        if (!headers.isEmpty()) {
            logger.debug("Adding headers for chain: {}. Headers: {}", name, headers);
            request.headers(headers);
        }

        if (body != null) {
            logger.debug("Adding body for chain: {}. Body: {}", name, body);
            request.body(StringBody(body.toString()));
        }

        if (!checks.isEmpty()) {
            logger.debug("Adding {} checks for chain: {}", checks.size(), name);
            request.check(checks.toArray(new CheckBuilder[0]));
        }

        logger.info("Successfully built chain: {}", name);
        return exec(request);
    }

    // Convenience methods for common HTTP methods
    public ChainBuilderFactory get(String path) {
        logger.debug("Creating GET request for chain: {} with path: {}", name, path);
        return request(HttpMethod.GET, path);
    }

    public ChainBuilderFactory post(String path) {
        logger.debug("Creating POST request for chain: {} with path: {}", name, path);
        return request(HttpMethod.POST, path);
    }

    public ChainBuilderFactory put(String path) {
        logger.debug("Creating PUT request for chain: {} with path: {}", name, path);
        return request(HttpMethod.PUT, path);
    }

    public ChainBuilderFactory delete(String path) {
        logger.debug("Creating DELETE request for chain: {} with path: {}", name, path);
        return request(HttpMethod.DELETE, path);
    }

    public ChainBuilderFactory patch(String path) {
        logger.debug("Creating PATCH request for chain: {} with path: {}", name, path);
        return request(HttpMethod.PATCH, path);
    }
} 