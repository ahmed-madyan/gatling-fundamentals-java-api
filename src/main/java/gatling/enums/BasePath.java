package gatling.enums;

import lombok.Getter;

@Getter
public enum BasePath {
    LIST_VIDEO_GAMES("/api/videogame"),
    USER("/user"),
    ADMIN("/admin"),
    RESOURCES("/data/api/resource"),
    DUMMY_LOGIN("/auth/login"),
    DUMMY_USER("/auth/me");

    private final String path;

    BasePath(String path) {
        if (path == null || !path.startsWith("/")) {
            throw new IllegalArgumentException("BasePath must start with '/' and not be null: " + path);
        }
        this.path = path;
    }

    @Override
    public String toString() {
        return path;
    }

    public String value() {
        return path;
    }

    public static BasePath fromString(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("BasePath name must not be null or empty");
        }
        for (BasePath bp : values()) {
            if (bp.name().equalsIgnoreCase(name.trim())) {
                return bp;
            }
        }
        throw new IllegalArgumentException("Unknown BasePath: " + name);
    }
}