# 🚀 Gatling Java DSL Performance Testing Framework

A modular and extensible performance testing framework using **Gatling 3.13.5 (Java DSL)**. Designed for **readability**, **reusability**, and **maintainability**, it provides factories for building protocol configurations, load profiles, HTTP scenarios, and simulations.

---

## 📦 Project Structure

```
gatling/
├── enums/                # Constants for HTTP method, URI paths, base URIs
├── utils/                # Factory and builder utilities for Gatling DSL
│   ├── HttpProtocolFactory.java
│   ├── LoadProfileFactory.java
│   ├── PopulationFactory.java
│   ├── ScenarioFactory.java
│   └── SimulationFactory.java
```

---

## 🚧 Dependencies

Ensure you have the following in your `build.gradle` or `pom.xml`:

### **Gradle**
```groovy
dependencies {
    implementation 'io.gatling.highcharts:gatling-charts-highcharts:3.13.5'
    implementation 'io.gatling:gatling-java:3.13.5'
    testImplementation 'org.slf4j:slf4j-simple:2.0.9'
}
```

### **Maven**
```xml
<dependencies>
    <dependency>
        <groupId>io.gatling.highcharts</groupId>
        <artifactId>gatling-charts-highcharts</artifactId>
        <version>3.13.5</version>
    </dependency>
    <dependency>
        <groupId>io.gatling</groupId>
        <artifactId>gatling-java</artifactId>
        <version>3.13.5</version>
    </dependency>
</dependencies>
```

---

## 🧱 Key Components

| Class                   | Purpose                                                                 |
|------------------------|-------------------------------------------------------------------------|
| `HttpProtocolFactory`  | Creates and configures HTTP protocol with base URL + headers            |
| `LoadProfileFactory`   | Generates open injection steps (e.g., spike, ramp, stress)              |
| `ScenarioFactory`      | Dynamically builds scenarios with exec steps and paths                  |
| `PopulationFactory`    | Validates and wraps `PopulationBuilder` for `setUp()`                   |
| `SimulationFactory`    | Combines scenario + protocol + injection model to create simulations    |

---

## 🚀 Getting Started

```java
// Example: Create a scenario and run with spike load

ScenarioBuilder scenario = new ScenarioFactory("Simple GET")
    .request(HttpMethod.GET, BasePath.HEALTH_CHECK)
    .build();

HttpProtocolBuilder protocol = new HttpProtocolFactory(BaseURI.LOCALHOST)
    .withHeader("Authorization", "Bearer token")
    .build();

PopulationBuilder population = new SimulationFactory(scenario, protocol)
    .injectOpen(LoadProfileFactory.spike(100))
    .build();

setUp(PopulationFactory.with(population));
```

---

## ✅ Requirements

- Java 11+
- Gradle/Maven
- Gatling 3.13.5
- JDK logging or SLF4J compatible logger (already configured via `java.util.logging`)

---

## 🧪 Run Simulations

```bash
./gradlew gatlingRun
# or with Maven
mvn gatling:test
```

---

## 📈 Output

After execution, reports will be located under:

```
build/reports/gatling/
```

Open `index.html` inside the scenario folder to view the interactive report.

---

## 🤝 Contributions

Pull requests welcome. Feel free to suggest extensions like:
- Feeder support
- Closed injection enhancements
- Custom metrics integration

---

## 💬 Questions?

Open an issue or ping in the Gatling community forums.

---