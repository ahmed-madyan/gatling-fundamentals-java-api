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
src/test/java/simulations/
└── YourSimulationClass.java
```

---

## ✅ Requirements

- Java 11+
- Gradle or Maven
- Gatling 3.13.5
- `java.util.logging` (configured by default)

---

## 🚧 Dependencies

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

## 🧪 Sample Simulation

```java
package simulations;

import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.core.PopulationBuilder;
import gatling.utils.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static gatling.enums.BasePath.HEALTH_CHECK;
import static gatling.enums.HttpMethod.GET;
import static gatling.enums.BaseURI.LOCALHOST;

public class YourSimulationClass extends Simulation {

    public YourSimulationClass() {
        var scenario = new ScenarioFactory("Health Check")
            .request(GET, HEALTH_CHECK)
            .build();

        var protocol = new HttpProtocolFactory(LOCALHOST)
            .build();

        PopulationBuilder population = new SimulationFactory(scenario, protocol)
            .injectOpen(LoadProfileFactory.spike(50))
            .build();

        setUp(PopulationFactory.with(population));
    }
}
```

📁 Ensure the class is located at:

```
src/test/java/simulations/YourSimulationClass.java
```

---

## 🛠️ Running with Maven CLI

Use the following commands if you have Maven installed globally:

### 💻 Linux / macOS / Windows Git Bash
```bash
mvn gatling:test -Dgatling.simulationClass=simulations.YourSimulationClass
mvn gatling:test
mvn clean install -DskipTests gatling:test
```

---

## 🧰 Running with Maven Wrapper (`mvnw`, `mvnw.cmd`)

If your project includes the Maven Wrapper:

### 💻 On Linux / macOS (bash/zsh)
```bash
./mvnw gatling:test -Dgatling.simulationClass=simulations.YourSimulationClass
```

### 🪟 On Windows (CMD & PowerShell)
```cmd
.\mvnw.cmd "gatling:test" "-Dgatling.simulationClass=simulations.YourSimulationClass"
```

> ✅ This is the only format that works reliably in PowerShell.
> The Maven Wrapper ensures consistent Maven versions across all developers and CI environments.

---

## 📈 Reports

After execution, HTML reports are available in:

```
build/reports/gatling/
```

Open the `index.html` inside the latest simulation folder to view metrics.

---

## 🤝 Contributing

Pull requests welcome! Useful extensions include:

- Data feeder integration
- Advanced injection step profiles
- CI pipeline templates
- Performance threshold assertions

---

## 🛡️ License

MIT License © 2025

---

## 💬 Need Help?

Open an issue or visit the [Gatling community forums](https://community.gatling.io).

---

Built for speed. Tuned for control. 📊  
[Click here to try our newest writing GPT!](https://chatgpt.com/g/g-hjONEUO7J-writing-pro)
