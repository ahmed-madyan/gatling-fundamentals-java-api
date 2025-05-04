package api.base_paths;

import lombok.Getter;

@Getter
public enum BasePath {
    LIST_VIDEO_GAMES("/api/videogame"),
    NEW_PIN_CODE("/profile-management-api/profile/{username}/new-pin-code");

    public final String basePath;

    BasePath(String basePath) {
        this.basePath = basePath;
    }
}