package gatling.enums;

import lombok.Getter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

public enum BaseURI {
    VIDEO_GAME("https://www.videogamedb.uk:443"),
    PROD("https://api.prod.to-zi.com");

    @Getter
    private final URI baseURI;

    BaseURI(String uriString) {
        try {
            URI parsed = new URI(uriString);
            if (!"https".equalsIgnoreCase(parsed.getScheme()) || parsed.getHost() == null) {
                throw new IllegalArgumentException("BaseURI must use https and contain a valid host: " + uriString);
            }
            this.baseURI = parsed;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URI format: " + uriString, e);
        }
    }

    @Override
    public String toString() {
        return baseURI.toString(); // Allows use like: String url = BaseURI.VIDEO_GAME.toString();
    }

    public String value() {
        return baseURI.toString(); // Optional explicit accessor
    }

    public static BaseURI fromString(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("BaseURI name must not be null or empty");
        }
        return Arrays.stream(values())
                .filter(uri -> uri.name().equalsIgnoreCase(name.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown BaseURI: " + name));
    }
}