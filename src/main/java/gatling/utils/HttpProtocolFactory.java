package gatling.utils;

import gatling.enums.BaseURI;
import io.gatling.javaapi.http.HttpDsl;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class HttpProtocolFactory {

    private static final Logger LOGGER = Logger.getLogger(HttpProtocolFactory.class.getName());

    @Getter
    private final String baseUrl;
    private final Map<String, String> headers = new HashMap<>();
    private HttpProtocolBuilder builder;

    public HttpProtocolFactory(BaseURI baseURI) {
        if (baseURI == null || baseURI.toString().isBlank()) {
            LOGGER.severe("❌ BaseURI must not be null or blank.");
            throw new IllegalArgumentException("BaseURI must not be null or blank.");
        }

        this.baseUrl = baseURI.toString();
        applyDefaults();
        LOGGER.info("✅ Initialized HttpProtocolFactory with baseUrl: " + baseUrl);
    }

    private void applyDefaults() {
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        LOGGER.fine("🧰 Default headers applied.");
    }

    public HttpProtocolFactory acceptHeader(String acceptType) {
        if (acceptType != null && !acceptType.isBlank()) {
            headers.put("Accept", acceptType);
            LOGGER.fine("📝 Accept header set: " + acceptType);
        } else {
            LOGGER.warning("⚠️ Ignored blank Accept header input.");
        }
        return this;
    }

    public HttpProtocolFactory withHeader(String key, String value) {
        if (key != null && value != null) {
            headers.put(key, value);
            LOGGER.fine("➕ Header added: " + key + " = " + value);
        } else {
            LOGGER.warning("⚠️ Skipped null header entry: " + key + "=" + value);
        }
        return this;
    }

    public HttpProtocolFactory withHeaders(Map<String, String> newHeaders) {
        if (newHeaders != null) {
            newHeaders.forEach(this::withHeader);
        }
        return this;
    }

    public HttpProtocolBuilder build() {
        if (builder != null) {
            LOGGER.warning("⚠️ Reusing existing HttpProtocolBuilder.");
            return builder;
        }

        builder = HttpDsl.http.baseUrl(baseUrl).headers(Collections.unmodifiableMap(headers));
        LOGGER.info("🏗️ HttpProtocolBuilder built for baseUrl: " + baseUrl);
        return builder;
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }
}
