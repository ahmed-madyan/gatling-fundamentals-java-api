package api.base_uris;

import lombok.Getter;

@Getter
public enum BaseURI {
    VIDEO_GAME("https://www.videogamedb.uk:443"),
    PROD("https://api.prod.to-zi.com");

    private final String baseURI;

    BaseURI(String baseURI) {
        this.baseURI = baseURI;
    }
}