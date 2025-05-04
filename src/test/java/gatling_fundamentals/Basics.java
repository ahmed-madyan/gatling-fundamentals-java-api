package gatling_fundamentals;

import api.base_paths.BasePath;
import api.base_uris.BaseURI;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class Basics extends Simulation {

    // 1. Request Configuration
    public static final HttpProtocolBuilder httpProtocol = http
            .baseUrl(BaseURI.VIDEO_GAME.getBaseURI())
            .acceptHeader("application/json");

    // 2. Scenario Builder
    public static final ScenarioBuilder scenarioBuilder = scenario("List of Video Games Workflow")
            .exec(http("Vide Game")
                    .get(BasePath.LIST_VIDEO_GAMES.getBasePath())
            );

    // 3. Load Simulation
    {
        setUp(scenarioBuilder.injectOpen(atOnceUsers(1000)))
                        .protocols(httpProtocol);
    }
}