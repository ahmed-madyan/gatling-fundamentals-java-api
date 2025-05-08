package simulations;

import gatling.builders.ChainBuilderFactory;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.util.Map;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class ChainBuilderExample extends Simulation {

    // HTTP Protocol Configuration
    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://api.example.com")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // Example 1: Simple GET request
    private final ChainBuilder getUsers = new ChainBuilderFactory("Get Users")
            .get("/users")
            .withCheck(status().is(200))
            .build();

    // Example 2: POST request with body and headers
    private final ChainBuilder createUser = new ChainBuilderFactory("Create User")
            .post("/users")
            .withBody("""
                    {
                        "name": "John Doe",
                        "email": "john@example.com"
                    }
                    """)
            .withHeader("Authorization", "Bearer #{token}")
            .withCheck(status().is(201))
            .withCheck(jsonPath("$.id").saveAs("userId"))
            .build();

    // Example 3: PUT request with multiple checks
    private final ChainBuilder updateUser = new ChainBuilderFactory("Update User")
            .put("/users/#{userId}")
            .withBody("""
                    {
                        "name": "John Updated",
                        "email": "john.updated@example.com"
                    }
                    """)
            .withCheck(status().is(200))
            .withCheck(jsonPath("$.name").is("John Updated"))
            .build();

    // Example 4: DELETE request with custom headers
    private final ChainBuilder deleteUser = new ChainBuilderFactory("Delete User")
            .delete("/users/#{userId}")
            .withHeaders(Map.of(
                "Authorization", "Bearer #{token}",
                "X-Request-ID", "#{requestId}"
            ))
            .withCheck(status().is(204))
            .build();

    // Example 5: Complex scenario with multiple requests
    private final ScenarioBuilder userManagementScenario = scenario("User Management Flow")
            .exec(getUsers)
            .pause(1)
            .exec(createUser)
            .pause(1)
            .exec(updateUser)
            .pause(1)
            .exec(deleteUser);

    // Load Simulation Configuration
    {
        setUp(
            userManagementScenario.injectOpen(
                atOnceUsers(1)
            )
        ).protocols(httpProtocol);
    }
} 