package videogamedbuk;

import java.util.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class RecordedSimulation extends Simulation {

    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://www.videogamedb.uk")
            .inferHtmlResources(AllowList(), DenyList(".*\\.js", ".*\\.css", ".*\\.gif", ".*\\.jpeg", ".*\\.jpg", ".*\\.ico", ".*\\.woff", ".*\\.woff2", ".*\\.(t|o)tf", ".*\\.png", ".*detectportal\\.firefox\\.com.*"))
            .acceptHeader("application/json")
            .acceptEncodingHeader("gzip, deflate")
            .acceptLanguageHeader("en-US,en;q=0.9")
            .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36 Edg/136.0.0.0");

    private final Map<CharSequence, String> headers_0 = Map.ofEntries(
            Map.entry("priority", "u=1, i"),
            Map.entry("sec-ch-ua", "Chromium\";v=\"136\", \"Microsoft Edge\";v=\"136\", \"Not.A/Brand\";v=\"99"),
            Map.entry("sec-ch-ua-mobile", "?0"),
            Map.entry("sec-ch-ua-platform", "Windows"),
            Map.entry("sec-fetch-dest", "empty"),
            Map.entry("sec-fetch-mode", "cors"),
            Map.entry("sec-fetch-site", "same-origin")
    );

    private final Map<CharSequence, String> headers_2 = Map.ofEntries(
            Map.entry("content-type", "application/json"),
            Map.entry("origin", "https://www.videogamedb.uk"),
            Map.entry("priority", "u=1, i"),
            Map.entry("sec-ch-ua", "Chromium\";v=\"136\", \"Microsoft Edge\";v=\"136\", \"Not.A/Brand\";v=\"99"),
            Map.entry("sec-ch-ua-mobile", "?0"),
            Map.entry("sec-ch-ua-platform", "Windows"),
            Map.entry("sec-fetch-dest", "empty"),
            Map.entry("sec-fetch-mode", "cors"),
            Map.entry("sec-fetch-site", "same-origin")
    );

    private final Map<CharSequence, String> headers_4 = Map.ofEntries(
            Map.entry("authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc0NjMwODQxNywiZXhwIjoxNzQ2MzEyMDE3fQ.nirZZYtrII2O9AcWzbSR7SMZCtmfROtMG1ZR8vkqBkI"),
            Map.entry("content-type", "application/json"),
            Map.entry("origin", "https://www.videogamedb.uk"),
            Map.entry("priority", "u=1, i"),
            Map.entry("sec-ch-ua", "Chromium\";v=\"136\", \"Microsoft Edge\";v=\"136\", \"Not.A/Brand\";v=\"99"),
            Map.entry("sec-ch-ua-mobile", "?0"),
            Map.entry("sec-ch-ua-platform", "Windows"),
            Map.entry("sec-fetch-dest", "empty"),
            Map.entry("sec-fetch-mode", "cors"),
            Map.entry("sec-fetch-site", "same-origin")
    );


    private final ScenarioBuilder scn = scenario("RecordedSimulation")
            .exec(
                    http("request_0")
                            .get("/api/videogame")
                            .headers(headers_0)
            )
            .pause(55)
            .exec(
                    http("request_1")
                            .get("/api/videogame/4")
                            .headers(headers_0)
            )
            .pause(60)
            .exec(
                    http("request_2")
                            .post("/api/authenticate")
                            .headers(headers_2)
                            .body(RawFileBody("videogamedbuk/recordedsimulation/0002_request.json"))
            )
            .pause(83)
            .exec(
                    http("request_3")
                            .post("/api/authenticate")
                            .headers(headers_2)
                            .body(RawFileBody("videogamedbuk/recordedsimulation/0003_request.json"))
            )
            .pause(37)
            .exec(
                    http("request_4")
                            .post("/api/videogame")
                            .headers(headers_4)
                            .body(RawFileBody("videogamedbuk/recordedsimulation/0004_request.json"))
            );

    {
        setUp(scn.injectOpen(atOnceUsers(1))).protocols(httpProtocol);
    }
}