package simulations;

import gatling.enums.BasePath;
import gatling.enums.BaseURI;
import gatling.utils.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.PopulationBuilder;

import java.util.logging.Logger;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.http.HttpDsl.http;

/**
 * Basic Gatling simulation for fetching a list of video games.
 * Demonstrates protocol setup, scenario creation, user injection, and execution.
 */
public class Basics extends Simulation {

    private static final Logger LOGGER = Logger.getLogger(Basics.class.getName());

    // === HTTP Protocol Configuration ===
    // Sets up the base URI and default headers for the HTTP protocol.
    private final HttpProtocolBuilder httpProtocolFactory = new HttpProtocolFactory(BaseURI.VIDEO_GAME)
            .acceptHeader("application/json")
            .build();


    // === Scenario Definition ===
    // Defines the scenario to perform a GET request on the list video games endpoint.
    private final ChainBuilder games = exec(
            http("List")
                    .get(BasePath.LIST_VIDEO_GAMES.toString())
    );
    private final ScenarioBuilder gameList = new ScenarioFactory("Get Games")
            .execChain(games)
            .build();

    // === Load Profile Configuration ===
    // Configures the user load: an initial spike followed by a ramp-up phase.
    private final PopulationBuilder population = new SimulationFactory(gameList, httpProtocolFactory)
            .injectOpen(
                    LoadProfileFactory.spike(1000),        // Instant spike: 10 users
                    LoadProfileFactory.rampUp(20, 10)    // Gradually ramp up 20 users over 10 seconds
            )
            .build();

    // === Simulation Execution ===
    // Executes the simulation using the validated population builder.
    {
        try {
            LOGGER.info("Executing simulation setup for: Get Games");
            setUp(PopulationFactory.with(population));
            LOGGER.info("Simulation setup completed successfully.");
        } catch (Exception e) {
            LOGGER.severe("Simulation setup failed: " + e.getMessage());
            throw e;
        }
    }
}