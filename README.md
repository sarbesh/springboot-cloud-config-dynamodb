# Spring Cloud Config DynamoDB Environment Repository

A pluggable Spring Cloud Config Server EnvironmentRepository implementation that uses AWS DynamoDB as a backend for externalized configuration, similar to Git, SVN, or native sources.

## Features
- Drop-in dependency for Spring Cloud Config Server
- Reads configuration from DynamoDB tables
- Supports property flattening for nested JSON configs
- Auto-configuration for easy integration (Spring Boot 3.x compatible)
- Configurable via `spring.cloud.config.server.dynamodb.*` properties

## Usage

### 1. Add Dependency
Add this module as a dependency in your Spring Cloud Config Server's `pom.xml`:
```xml
<dependency>
    <groupId>com.sarbesh.springboot</groupId>
    <artifactId>spring-cloud-config-dynamodb</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2. Enable the DynamoDB Profile
In your Config Server, activate the `dynamodb` profile:
```yaml
spring:
  profiles:
    active: dynamodb
```

### 3. Configure DynamoDB Properties
Add the following to your Config Server's `application.yml` or `bootstrap.yml`:
```yaml
spring:
  cloud:
    config:
      server:
        dynamodb:
          region: <AWS_REGION>
          table: <TABLE_NAME>
          access-key: <YOUR_AWS_ACCESS_KEY>
          secret-key: <YOUR_AWS_SECRET_KEY>
```

### 4. DynamoDB Table Structure
- Table must have a primary key named `configKey` (e.g., `myapp-dev`)
- Each item must have a `config` attribute containing the config as a JSON string

#### Example Item:
| configKey   | config (String, JSON)                                      |
|-------------|-----------------------------------------------------------|
| myapp-dev   | { "spring": { "datasource": { "url": "..." } } }        |

### 5. How It Works
- On config requests, the repository fetches the item by `configKey` (application-profile)
- The `config` JSON is flattened into dot-separated property keys
- Properties are exposed as a Spring Cloud Config `PropertySource`

## Project Structure
- `DynamodbEnvironmentRepository` - Core repository logic
- `DynamodbEnvironmentProperties` - Config properties binding
- `DynamodbEnvironmentRepositoryAutoConfiguration` - Auto-registration
- `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` - For Spring Boot auto-config

## Areas of Improvement
- Add integration/unit tests
- Support for label/versioning (currently only application-profile)
- Error handling/logging enhancements
- Optionally support YAML config in addition to JSON
- Add CI/CD and publish to Maven Central or internal repo
- Add sample Config Server project for demonstration

## License
MIT or your preferred license

---

For questions or contributions, open an issue or PR.
