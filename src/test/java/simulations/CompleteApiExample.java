package simulations;

import gatling.builders.ChainBuilderFactory;
import gatling.enums.HttpMethod;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.util.Map;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class CompleteApiExample extends Simulation {

    // HTTP Protocol Configuration
    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://api.example.com")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // === Authentication Chain ===
    private final ChainBuilder login = new ChainBuilderFactory("Login")
            .post("/auth/login")
            .withBody("""
                    {
                        "username": "testuser",
                        "password": "password123"
                    }
                    """)
            .withCheck(status().is(200))
            .saveAs("$.accessToken", "accessToken")
            .build();

    // === User Management Chains ===
    // Create User
    private final ChainBuilder createUser = new ChainBuilderFactory("Create User")
            .post("/users")
            .withBody("""
                    {
                        "name": "John Doe",
                        "email": "john@example.com",
                        "role": "user"
                    }
                    """)
            .withHeader("Authorization", "Bearer ${accessToken}")
            .withCheck(status().is(201))
            .saveAs("$.id", "userId")
            .build();

    // Get User Details
    private final ChainBuilder getUser = new ChainBuilderFactory("Get User")
            .get("/users/#{userId}")
            .withHeader("Authorization", "Bearer ${accessToken}")
            .withCheck(status().is(200))
            .withCheck(jsonPath("$.name").is("John Doe"))
            .build();

    // Update User
    private final ChainBuilder updateUser = new ChainBuilderFactory("Update User")
            .put("/users/#{userId}")
            .withBody("""
                    {
                        "name": "John Updated",
                        "email": "john.updated@example.com"
                    }
                    """)
            .withHeader("Authorization", "Bearer ${accessToken}")
            .withCheck(status().is(200))
            .build();

    // Delete User
    private final ChainBuilder deleteUser = new ChainBuilderFactory("Delete User")
            .delete("/users/#{userId}")
            .withHeader("Authorization", "Bearer ${accessToken}")
            .withCheck(status().is(204))
            .build();

    // === Product Management Chains ===
    // Get Products with Query Parameters
    private final ChainBuilder getProducts = new ChainBuilderFactory("Get Products")
            .get("/products")
            .withHeader("Authorization", "Bearer ${accessToken}")
            .withHeaders(Map.of(
                "X-Page", "1",
                "X-Size", "10"
            ))
            .withCheck(status().is(200))
            .saveAs("$[0].id", "productId")
            .build();

    // Create Product
    private final ChainBuilder createProduct = new ChainBuilderFactory("Create Product")
            .request(HttpMethod.POST, "/products")
            .withBody("""
                    {
                        "name": "Test Product",
                        "price": 99.99,
                        "category": "electronics"
                    }
                    """)
            .withHeader("Authorization", "Bearer ${accessToken}")
            .withCheck(status().is(201))
            .build();

    // === Order Management Chains ===
    // Create Order
    private final ChainBuilder createOrder = new ChainBuilderFactory("Create Order")
            .post("/orders")
            .withBody("""
                    {
                        "productId": "#{productId}",
                        "quantity": 1
                    }
                    """)
            .withHeader("Authorization", "Bearer ${accessToken}")
            .withCheck(status().is(201))
            .saveAs("$.orderId", "orderId")
            .build();

    // Get Order Status
    private final ChainBuilder getOrderStatus = new ChainBuilderFactory("Get Order Status")
            .get("/orders/#{orderId}/status")
            .withHeader("Authorization", "Bearer ${accessToken}")
            .withCheck(status().is(200))
            .build();

    // === Complete Scenarios ===
    // User Management Flow
    private final ScenarioBuilder userManagementFlow = scenario("User Management Flow")
            .exec(login)
            .pause(1)
            .exec(createUser)
            .pause(1)
            .exec(getUser)
            .pause(1)
            .exec(updateUser)
            .pause(1)
            .exec(deleteUser);

    // Product Order Flow
    private final ScenarioBuilder productOrderFlow = scenario("Product Order Flow")
            .exec(login)
            .pause(1)
            .exec(getProducts)
            .pause(1)
            .exec(createProduct)
            .pause(1)
            .exec(createOrder)
            .pause(1)
            .exec(getOrderStatus);

    // === Load Simulation Configuration ===
    {
        setUp(
            userManagementFlow.injectOpen(
                atOnceUsers(1)
            ),
            productOrderFlow.injectOpen(
                atOnceUsers(1)
            )
        ).protocols(httpProtocol);
    }
} 