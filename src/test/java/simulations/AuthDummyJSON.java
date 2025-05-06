package simulations;

import gatling.enums.BaseURI;
import gatling.utils.*;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.logging.Logger;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class AuthDummyJSON extends Simulation {

    private static final Logger LOGGER = Logger.getLogger(AuthDummyJSON.class.getName());

    // === Load Static Test Data ===
    // Loads the login request payload from a JSON file at runtime.
    private final String loginPayload = readJsonFromFile("src/test/resources/data/loginPayload.json");

    private String readJsonFromFile(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load JSON file: " + path, e);
        }
    }

    // === Configure HTTP Protocol ===
    // Sets the base URI and common headers for all requests in this simulation.
    private final HttpProtocolBuilder httpProtocolFactory = new HttpProtocolFactory(BaseURI.DUMMY_JSON)
            .acceptHeader("application/json")
            .build();

    // === Define Request Chains ===

    // Performs login and extracts the access token from the response body.
    private final ChainBuilder loginAndExtractToken = exec(
            http("Login Request")
                    .post("/auth/login")
                    .body(StringBody(loginPayload))
                    .check(
                            status().is(200),
                            jsonPath("$.accessToken").saveAs("accessToken")
                    )
    );

    // Makes an authenticated request using the access token to fetch user info.
    private final ChainBuilder getAuthUser = exec(
            http("Get Auth User")
                    .get("/auth/me")
                    .header("Authorization", "Bearer #{accessToken}")
                    .check(status().is(200))
    );

    // === Build Scenario ===
    // Combines the login and user request chains into a complete scenario.
    private final ScenarioBuilder scn = new ScenarioFactory("Auth Workflow")
            .execChain(loginAndExtractToken, pause(1), getAuthUser)
            .build();

    // === Define Load Profile ===
    // Specifies the user injection model for this performance test:
    //  - Spike: instantly adds 10 users to simulate sudden traffic
    //  - Ramp-up: gradually adds 20 users over 10 seconds
    private final PopulationBuilder population = new SimulationFactory(scn, httpProtocolFactory)
            .injectOpen(
                    LoadProfileFactory.spike(10),
                    LoadProfileFactory.rampUp(20, 10)
            )
            .build();

    // === Simulation Lifecycle ===
    // Configures and launches the Gatling simulation using the prepared scenario and load profile.
    {
        try {
            LOGGER.info("Executing simulation setup for: Auth Workflow");
            setUp(PopulationFactory.with(population));
            LOGGER.info("Simulation setup completed successfully.");
        } catch (Exception e) {
            LOGGER.severe("Simulation setup failed: " + e.getMessage());
            throw e;
        }
    }
}