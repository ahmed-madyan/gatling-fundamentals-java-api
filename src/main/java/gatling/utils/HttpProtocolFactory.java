package gatling.utils;

import gatling.enums.BaseURI;
import io.gatling.javaapi.http.HttpDsl;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Factory class for constructing and configuring an HttpProtocolBuilder instance
 * for use in Gatling performance simulations.
 */
public class HttpProtocolFactory {

    private static final Logger LOGGER = Logger.getLogger(HttpProtocolFactory.class.getName());

    @Getter
    private final String baseUrl;
    private final Map<String, String> headers = new HashMap<>();
    private HttpProtocolBuilder builder;

    /**
     * Constructs the factory with a required base URI.
     *
     * @param baseURI the BaseURI enum used to initialize the base URL
     * @throws IllegalArgumentException if baseURI is null or blank
     */
    public HttpProtocolFactory(BaseURI baseURI) {
        if (baseURI == null || baseURI.toString().isBlank()) {
            LOGGER.severe("BaseURI must not be null or blank.");
            throw new IllegalArgumentException("BaseURI must not be null or blank.");
        }

        this.baseUrl = baseURI.toString();
        applyDefaultHeaders();
        LOGGER.info("Initialized HttpProtocolFactory with baseUrl: " + baseUrl);
    }

    /**
     * Applies the default headers (Accept, Content-Type).
     */
    private void applyDefaultHeaders() {
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        LOGGER.fine("Default headers applied.");
    }

    /**
     * Sets or overrides the Accept header.
     *
     * @param acceptType MIME type to be accepted
     * @return this instance for fluent API
     */
    public HttpProtocolFactory acceptHeader(String acceptType) {
        if (acceptType != null && !acceptType.isBlank()) {
            headers.put("Accept", acceptType);
            LOGGER.fine("Accept header set to: " + acceptType);
        } else {
            LOGGER.warning("Attempted to set blank or null Accept header. Ignored.");
        }
        return this;
    }

    /**
     * Adds or overrides a specific header.
     *
     * @param key   Header key
     * @param value Header value
     * @return this instance for fluent API
     */
    public HttpProtocolFactory withHeader(String key, String value) {
        if (key != null && value != null) {
            headers.put(key, value);
            LOGGER.fine("Header added: " + key + " = " + value);
        } else {
            LOGGER.warning("Attempted to add null key or value to headers. Skipped.");
        }
        return this;
    }

    /**
     * Adds or overrides multiple headers.
     *
     * @param newHeaders headers to merge
     * @return this instance for fluent API
     */
    public HttpProtocolFactory withHeaders(Map<String, String> newHeaders) {
        if (newHeaders != null) {
            newHeaders.forEach(this::withHeader);
        }
        return this;
    }

    /**
     * Builds the HttpProtocolBuilder instance with the configured base URL and headers.
     *
     * @return built HttpProtocolBuilder
     */
    public HttpProtocolBuilder build() {
        if (builder != null) {
            LOGGER.warning("Returning previously built HttpProtocolBuilder instance.");
            return builder;
        }

        builder = HttpDsl.http.baseUrl(baseUrl).headers(Collections.unmodifiableMap(headers));
        LOGGER.info("HttpProtocolBuilder built with baseUrl: " + baseUrl);
        return builder;
    }

    /**
     * Retrieves an unmodifiable view of all configured headers.
     *
     * @return map of headers
     */
    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }
}