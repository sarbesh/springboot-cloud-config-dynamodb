# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Initial release of Spring Cloud Config DynamoDB library
- Bootstrap context support for `spring.cloud.config.server.bootstrap=true`
- Composite environment repository configuration
- Comprehensive JavaDoc documentation
- Community contribution guidelines (CONTRIBUTING.md)
- Example bootstrap.yml configuration

### Features

- **DynamoDB Environment Repository**: Custom environment repository implementation for DynamoDB
- **Auto-Configuration**: Spring Boot auto-configuration for seamless integration
- **Bootstrap Support**: Early factory registration for bootstrap context compatibility
- **Property-Driven Configuration**: Configurable via `spring.cloud.config.server.dynamodb.*`
  properties
- **AWS Integration**: Support for AWS credentials, regions, and connection timeouts
- **Composite Repository**: Works alongside other Spring Cloud Config backends

### Configuration Properties

- `spring.cloud.config.server.dynamodb.region`: AWS region for DynamoDB
- `spring.cloud.config.server.dynamodb.table`: DynamoDB table name
- `spring.cloud.config.server.dynamodb.access-key`: Optional AWS access key
- `spring.cloud.config.server.dynamodb.secret-key`: Optional AWS secret key
- `spring.cloud.config.server.dynamodb.partition-key`: Partition key attribute name (default:
  config_id)
- `spring.cloud.config.server.dynamodb.config-attribute`: Configuration attribute name (default:
  properties)
- `spring.cloud.config.server.dynamodb.delimiter`: Application-profile delimiter (default: -)
- `spring.cloud.config.server.dynamodb.order`: Repository priority order
- `spring.cloud.config.server.dynamodb.connection-timeout`: DynamoDB connection timeout
- `spring.cloud.config.server.dynamodb.timeout`: DynamoDB request timeout

### Technical Architecture

- **Factory Pattern**: `DynamodbEnvironmentRepositoryFactory` implementing
  `EnvironmentRepositoryFactory`
- **Properties Binding**: `DynamodbEnvironmentProperties` for configuration management
- **Bootstrap Configuration**: `DynamodbBootstrapConfiguration` for early context registration
- **Auto-Configuration**: `DynamodbEnvironmentRepositoryAutoConfiguration` for Spring Boot
  integration

### Dependencies

- Spring Cloud Config Server 3.1.x
- Spring Boot 2.7.x / 3.x
- AWS SDK for Java 2.33.0
- Jackson Databind for JSON processing

### DynamoDB Table Structure

- **Partition Key**: Configurable attribute (default: `config_id`)
- **Configuration**: Map-type attribute containing nested properties
- **Key Format**: `{application}-{profile}` (configurable delimiter)
- **Value Structure**: Nested maps supporting dot notation for Spring properties

## [1.0.0] - 2024-01-XX

### Added

- Initial stable release
- Full Spring Cloud Config Server integration
- Bootstrap context support
- Comprehensive documentation
- Example configurations
- Community contribution guidelines

### Dependencies

- Java 17+ compatibility
- Spring Cloud 2021.0.x / 2022.0.x
- Spring Boot 2.7.x / 3.x
- AWS SDK v2

### Breaking Changes

- None (initial release)

### Migration Guide

- This is the initial release, no migration required
- For existing custom implementations, see README.md for migration instructions

---

## Version History Legend

### Types of Changes

- **Added**: New features
- **Changed**: Changes in existing functionality
- **Deprecated**: Soon-to-be removed features
- **Removed**: Removed features
- **Fixed**: Bug fixes
- **Security**: Security improvements

### Version Numbers

- **MAJOR**: Incompatible API changes
- **MINOR**: Backwards-compatible functionality additions
- **PATCH**: Backwards-compatible bug fixes

---

For more details about any release, see the [GitHub Releases](../../releases) page.
