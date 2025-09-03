# Spring Cloud Config DynamoDB Environment Repository

[![Maven Central](https://img.shields.io/maven-central/v/com.sarbesh.springboot/spring-cloud-config-dynamodb.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.sarbesh.springboot%22%20AND%20a:%22spring-cloud-config-dynamodb%22)
[![GitHub](https://img.shields.io/github/license/sarbesh/spring-cloud-config-dynamodb)](https://github.com/sarbesh/spring-cloud-config-dynamodb/blob/main/LICENSE)
[![Java](https://img.shields.io/badge/Java-11%2B-brightgreen.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7%2B-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2021.0%2B-brightgreen.svg)](https://spring.io/projects/spring-cloud)
[![AWS DynamoDB](https://img.shields.io/badge/AWS-DynamoDB-orange.svg)](https://aws.amazon.com/dynamodb/)
[![Build Status](https://img.shields.io/github/workflow/status/sarbesh/spring-cloud-config-dynamodb/CI)](https://github.com/sarbesh/spring-cloud-config-dynamodb/actions)
[![Codecov](https://img.shields.io/codecov/c/github/sarbesh/spring-cloud-config-dynamodb)](https://codecov.io/gh/sarbesh/spring-cloud-config-dynamodb)
[![Contributors](https://img.shields.io/github/contributors/sarbesh/spring-cloud-config-dynamodb)](https://github.com/sarbesh/spring-cloud-config-dynamodb/graphs/contributors)

A pluggable Spring Cloud Config Server EnvironmentRepository implementation that uses AWS DynamoDB as a backend for externalized configuration, similar to Git, SVN, or native sources.

## Features

- **✅ Bootstrap Support**: Works with `spring.cloud.config.server.bootstrap=true`
- **🔌 Drop-in Library**: Just add dependency and configure properties
- **🚀 Composite Configuration**: Supports composite environment repositories
- **🗄️ DynamoDB Backend**: Reads configuration from DynamoDB tables
- **🌳 Property Flattening**: Supports nested JSON config structures
- **⚙️ Auto-Configuration**: Automatic Spring Boot integration
- **🔧 Configurable**: via `spring.cloud.config.server.dynamodb.*` properties

## Usage

### 1. Add Dependency
Add this module as a dependency in your Spring Cloud Config Server's `pom.xml`:
```xml
<dependency>
    <groupId>com.sarbesh.springboot</groupId>
    <artifactId>spring-cloud-config-dynamodb</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. Configure Bootstrap Configuration (✅ **Bootstrap Support**)

**For Bootstrap Mode** - Add to your Config Server's `bootstrap.yml`:
```yaml
spring:
  profiles:
    active: dynamodb  # Enable DynamoDB profile
  cloud:
    config:
      server:
        bootstrap: true  # Enable bootstrap mode
        composite:
          - type: dynamodb
            region: ${AWS_REGION:us-east-1}
            table: ${DYNAMODB_TABLE:config_table}
            access-key: ${AWS_ACCESS_KEY:}
            secret-key: ${AWS_SECRET_KEY:}
            order: 1
          - type: native
            search-locations: classpath:/config
            order: 2
```

**For Regular Mode** - Add to your Config Server's `application.yml`:
```yaml
spring:
  profiles:
    active: dynamodb
  cloud:
    config:
      server:
        dynamodb:
          region: ${AWS_REGION:us-east-1}
          table: ${DYNAMODB_TABLE:config_table}
          access-key: ${AWS_ACCESS_KEY:}
          secret-key: ${AWS_SECRET_KEY:}
```

> **💡 Key Feature**: This library supports **both bootstrap and regular modes** thanks to the
> Bootstrap Configuration approach that registers the factory early in the bootstrap context.

### 3. DynamoDB Table Structure

- Table must have a primary key named `config_id` (default, configurable)
- Each item must have a `properties` attribute (default, configurable) containing nested config as a
  Map
- Key format: `{application}-{profile}` (e.g., `myapp-dev`)

#### Example DynamoDB Item:

| config_id  | properties (Map)                                               |
|------------|----------------------------------------------------------------|
| myapp-dev  | { "spring": { "datasource": { "url": "jdbc:..." } } }          |
| myapp-prod | { "server": { "port": 8080 }, "logging": { "level": "INFO" } } |

### 4. How It Works

1. **Bootstrap Registration**: The library registers a `DynamodbEnvironmentRepositoryFactory` in the
   bootstrap context via `spring.factories`
2. **Config Request**: On config requests, fetches item by `{application}-{profile}` key
3. **Property Flattening**: Nested DynamoDB Map structures are flattened into dot-separated keys
4. **PropertySource Creation**: Properties are exposed as Spring Cloud Config `PropertySource`

### 5. Configuration Properties

All properties use the prefix `spring.cloud.config.server.dynamodb`:

| Property             | Default             | Description                           |
|----------------------|---------------------|---------------------------------------|
| `region`             | `us-east-1`         | AWS region for DynamoDB               |
| `table`              | `config_table`      | DynamoDB table name                   |
| `access-key`         | -                   | AWS access key (optional)             |
| `secret-key`         | -                   | AWS secret key (optional)             |
| `partition-key`      | `config_id`         | DynamoDB partition key name           |
| `config-attribute`   | `properties`        | Attribute containing config data      |
| `delimiter`          | `-`                 | Delimiter for application-profile key |
| `order`              | `LOWEST_PRECEDENCE` | Repository precedence order           |
| `connection-timeout` | `10000`             | Connection timeout (ms)               |
| `timeout`            | `50000`             | Request timeout (ms)                  |

## Project Structure
- `DynamodbEnvironmentRepository` - Core repository logic
- `DynamodbEnvironmentProperties` - Config properties binding
- `DynamodbEnvironmentRepositoryAutoConfiguration` - Auto-registration
- `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` - For Spring Boot auto-config

## Troubleshooting

### Common Issues and Solutions

#### 1. "A component required a bean named 'dynamodbEnvironmentRepositoryFactory' that could not be found"

**Cause**: The `dynamodb` profile is not activated.

**Solution**: Ensure you activate the DynamoDB profile in your Config Server:

```yaml
spring:
  profiles:
    active: dynamodb  # CRITICAL: Must activate this profile
```

#### 2. "Parameter required a single bean, but 2 were found: configDynamodbClient and bootstrapDynamodbClient"

**Cause**: Bean ambiguity between two DynamoDbClient beans in different contexts.

**Solution**: This is already fixed in the library with proper `@Qualifier` annotations. If you
encounter this:

- Ensure you're using the latest version of the library
- Check that you haven't created additional DynamoDbClient beans in your configuration
- Verify you're not manually importing the configuration classes

#### 2. "DynamoDB table not found" or "ResourceNotFoundException"

**Cause**: DynamoDB table doesn't exist or incorrect table name/region.

**Solutions**:

- Verify the DynamoDB table exists in the specified region
- Check table name matches your configuration
- Ensure AWS credentials have DynamoDB read permissions
- Verify region configuration is correct

#### 3. "Access denied" or credential errors

**Solutions**:

- Use IAM roles (preferred) instead of access keys in production
- Verify AWS credentials have DynamoDB `GetItem` permission
- Check if credentials are correctly set via environment variables
- Ensure region is accessible with your AWS account

#### 4. Configuration not loading from DynamoDB

**Solutions**:

- Verify DynamoDB item exists with correct key format: `{application}-{profile}`
- Check the `properties` attribute contains your configuration as a Map
- Enable debug logging: `logging.level.com.sarbesh.springboot.config.dynamodb=DEBUG`
- Verify bootstrap context is enabled: `spring.cloud.config.server.bootstrap=true`

#### 5. Composite configuration not working

**Solution**: Ensure both bootstrap and composite configurations are properly set:

```yaml
spring:
  profiles:
    active: dynamodb
  cloud:
    config:
      server:
        bootstrap: true
        composite:
          - type: dynamodb
            # DynamoDB configuration
          - type: native
            search-locations: classpath:/config
```

### Debug Configuration

Enable debug logging to troubleshoot issues:

```yaml
logging:
  level:
    com.sarbesh.springboot.config.dynamodb: DEBUG
    org.springframework.cloud.config: DEBUG
    software.amazon.awssdk.services.dynamodb: DEBUG
```

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
