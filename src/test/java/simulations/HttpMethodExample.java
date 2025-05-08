package simulations;

import gatling.builders.ChainBuilderFactory;
import gatling.enums.HttpMethod;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class HttpMethodExample extends Simulation {

    // HTTP Protocol Configuration
    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://api.example.com")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // Example 1: Using convenience methods
    private final ChainBuilder getUsers = new ChainBuilderFactory("Get Users")
            .get("/users")
            .withCheck(status().is(200))
            .build();

    // Example 2: Using HttpMethod enum directly
    private final ChainBuilder createUser = new ChainBuilderFactory("Create User")
            .request(HttpMethod.POST, "/users")
            .withBody("""
                    {
                        "name": "John Doe",
                        "email": "john@example.com"
                    }
                    """)
            .withCheck(status().is(201))
            .build();

    // Example 3: Using HttpMethod enum with custom method
    private final ChainBuilder customRequest = new ChainBuilderFactory("Custom Request")
            .request(HttpMethod.OPTIONS, "/custom-endpoint")
            .withHeader("Custom-Header", "value")
            .withCheck(status().is(200))
            .build();

    // Complete scenario with different HTTP methods
    private final ScenarioBuilder httpMethodsScenario = scenario("HTTP Methods Example")
            .exec(getUsers)
            .pause(1)
            .exec(createUser)
            .pause(1)
            .exec(customRequest);

    // Load Simulation Configuration
    {
        setUp(
            httpMethodsScenario.injectOpen(
                atOnceUsers(1)
            )
        ).protocols(httpProtocol);
    }
} 