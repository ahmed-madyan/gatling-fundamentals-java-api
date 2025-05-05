package gatling.utils;

import gatling.enums.BaseURI;
import io.gatling.javaapi.http.HttpDsl;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Factory for constructing a reusable and configurable {@link HttpProtocolBuilder}
 * with validated base URL, headers, and logging for transparency and debugging.
 */
public class HttpProtocolFactory {

    private static final Logger LOGGER = Logger.getLogger(HttpProtocolFactory.class.getName());

    @Getter
    private final String baseUrl;

    private final Map<String, String> headers = new HashMap<>();
    private HttpProtocolBuilder builder;

    /**
     * Initializes the factory with a base URI and applies default headers.
     *
     * @param baseURI the enum constant representing the base URI
     * @throws IllegalArgumentException if baseURI is null or blank
     */
    public HttpProtocolFactory(BaseURI baseURI) {
        if (baseURI == null || baseURI.toString().isBlank()) {
            String msg = "BaseURI must not be null or blank.";
            LOGGER.severe("❌ " + msg);
            throw new IllegalArgumentException(msg);
        }

        this.baseUrl = baseURI.toString();
        applyDefaults();

        LOGGER.info("✅ Initialized HttpProtocolFactory with baseUrl: " + baseUrl);
    }

    /**
     * Applies default headers for JSON API usage.
     * Includes "Accept" and "Content-Type" as "application/json".
     */
    private void applyDefaults() {
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        LOGGER.fine("🧰 Default headers applied: Accept + Content-Type = application/json");
    }

    /**
     * Sets or overrides the "Accept" header.
     *
     * @param acceptType the desired MIME type (e.g., "application/json")
     * @return this factory for method chaining
     */
    public HttpProtocolFactory acceptHeader(String acceptType) {
        if (acceptType == null || acceptType.isBlank()) {
            LOGGER.warning("⚠️ Ignored blank Accept header input.");
            return this;
        }

        headers.put("Accept", acceptType);
        LOGGER.fine("📝 Accept header set to: " + acceptType);
        return this;
    }

    /**
     * Adds a single header to the request. Nulls are ignored with a warning.
     *
     * @param key   header name
     * @param value header value
     * @return this factory for method chaining
     */
    public HttpProtocolFactory withHeader(String key, String value) {
        if (key == null || value == null) {
            LOGGER.warning("⚠️ Attempted to add null header entry: " + key + " = " + value);
            return this;
        }

        headers.put(key, value);
        LOGGER.fine("➕ Header added: " + key + " = " + value);
        return this;
    }

    /**
     * Adds multiple headers from a map. Entries with null keys or values are skipped.
     *
     * @param newHeaders map of headers to add
     * @return this factory for method chaining
     */
    public HttpProtocolFactory withHeaders(Map<String, String> newHeaders) {
        if (newHeaders == null || newHeaders.isEmpty()) {
            LOGGER.warning("⚠️ Empty or null headers map ignored.");
            return this;
        }

        newHeaders.forEach((k, v) -> {
            if (k != null && v != null) {
                headers.put(k, v);
                LOGGER.fine("➕ Header added: " + k + " = " + v);
            } else {
                LOGGER.warning("⚠️ Skipped null header pair: " + k + " = " + v);
            }
        });

        return this;
    }

    /**
     * Builds the {@link HttpProtocolBuilder} using the configured base URL and headers.
     * Logs and reuses the builder if already created.
     *
     * @return the built and reusable HttpProtocolBuilder
     * @throws RuntimeException if build fails
     */
    public HttpProtocolBuilder build() {
        if (builder != null) {
            LOGGER.warning("⚠️ HttpProtocolBuilder already built. Returning existing instance.");
            return builder;
        }

        try {
            builder = HttpDsl.http
                    .baseUrl(baseUrl)
                    .headers(Collections.unmodifiableMap(headers));

            LOGGER.info("🏗️ HttpProtocolBuilder successfully built for baseUrl: " + baseUrl);
            return builder;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Failed to build HttpProtocolBuilder", e);
            throw new RuntimeException("Error during HttpProtocolBuilder construction", e);
        }
    }

    /**
     * Returns a read-only view of all configured headers.
     *
     * @return unmodifiable header map
     */
    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }
}