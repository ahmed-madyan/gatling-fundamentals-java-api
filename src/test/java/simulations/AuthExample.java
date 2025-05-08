package simulations;

import gatling.builders.ChainBuilderFactory;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class AuthExample extends Simulation {

    // HTTP Protocol Configuration
    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://api.example.com")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // Login request that saves the access token
    private final ChainBuilder login = new ChainBuilderFactory("Login")
            .post("/auth/login")
            .withBody("""
                    {
                        "username": "testuser",
                        "password": "password123"
                    }
                    """)
            .withCheck(status().is(200))
            .saveAs("$.accessToken", "accessToken")  // Saves the access token to session
            .build();

    // Authenticated request using the saved token
    private final ChainBuilder getProfile = new ChainBuilderFactory("Get Profile")
            .get("/profile")
            .withHeader("Authorization", "Bearer ${accessToken}")  // Uses the saved token
            .withCheck(status().is(200))
            .build();

    // Complete authentication flow
    private final ScenarioBuilder authScenario = scenario("Authentication Flow")
            .exec(login)
            .pause(1)
            .exec(getProfile);

    // Load Simulation Configuration
    {
        setUp(
            authScenario.injectOpen(
                atOnceUsers(1)
            )
        ).protocols(httpProtocol);
    }
} 