package gatling.builders;

import gatling.enums.HttpMethod;
import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.CheckBuilder;
import io.gatling.javaapi.http.HttpRequestActionBuilder;
import lombok.Getter;

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
    }

    /**
     * Sets the HTTP method and path for the request.
     *
     * @param method The HTTP method enum
     * @param path The request path
     * @return this instance for fluent API
     */
    public ChainBuilderFactory request(HttpMethod method, String path) {
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
        this.checks.add(jsonPath(jsonPath).saveAs(sessionKey));
        return this;
    }

    /**
     * Builds the final ChainBuilder with all configured options.
     *
     * @return A ChainBuilder instance
     * @throws IllegalStateException if method or path is not set
     */
    public ChainBuilder build() {
        if (method == null || path == null) {
            throw new IllegalStateException("Method and path must be set before building the chain");
        }

        HttpRequestActionBuilder request;
        switch (method) {
            case GET -> request = http(name).get(path);
            case POST -> request = http(name).post(path);
            case PUT -> request = http(name).put(path);
            case DELETE -> request = http(name).delete(path);
            case PATCH -> request = http(name).patch(path);
            case HEAD -> request = http(name).head(path);
            case OPTIONS -> request = http(name).options(path);
            default -> throw new IllegalStateException("Unsupported HTTP method: " + method);
        }

        if (!headers.isEmpty()) {
            request.headers(headers);
        }

        if (body != null) {
            request.body(StringBody(body.toString()));
        }

        if (!checks.isEmpty()) {
            request.check(checks.toArray(new CheckBuilder[0]));
        }

        return exec(request);
    }

    // Convenience methods for common HTTP methods
    public ChainBuilderFactory get(String path) {
        return request(HttpMethod.GET, path);
    }

    public ChainBuilderFactory post(String path) {
        return request(HttpMethod.POST, path);
    }

    public ChainBuilderFactory put(String path) {
        return request(HttpMethod.PUT, path);
    }

    public ChainBuilderFactory delete(String path) {
        return request(HttpMethod.DELETE, path);
    }

    public ChainBuilderFactory patch(String path) {
        return request(HttpMethod.PATCH, path);
    }
} 