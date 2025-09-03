# Contributing to Spring Cloud Config DynamoDB

Thank you for considering contributing to Spring Cloud Config DynamoDB! We welcome contributions
from the community including bug reports, feature requests, documentation improvements, and code
contributions.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Environment](#development-environment)
- [How to Contribute](#how-to-contribute)
- [Coding Standards](#coding-standards)
- [Testing Guidelines](#testing-guidelines)
- [Documentation](#documentation)
- [Pull Request Process](#pull-request-process)
- [Issue Reporting](#issue-reporting)

## Code of Conduct

This project adheres to a [Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected
to uphold this code.

## Getting Started

### Prerequisites

- **Java 17 or higher** (Java 21 recommended)
- **Maven 3.6+**
- **Git**
- **AWS Account** (for testing with real DynamoDB)

### Development Environment

1. **Fork and Clone**
   ```bash
   git clone https://github.com/your-username/spring-cloud-config-dynamodb.git
   cd spring-cloud-config-dynamodb
   ```

2. **Build the Project**
   ```bash
   mvn clean compile
   ```

3. **Run Tests**
   ```bash
   mvn test
   ```

4. **Generate Documentation**
   ```bash
   mvn javadoc:javadoc
   ```

## How to Contribute

### Types of Contributions

- **Bug Reports**: Help us identify and fix bugs
- **Feature Requests**: Suggest new functionality
- **Code Contributions**: Implement bug fixes or new features
- **Documentation**: Improve README, JavaDoc, or examples
- **Testing**: Add test cases or improve test coverage

### Contribution Workflow

1. **Create an Issue**: For significant changes, create an issue first to discuss the approach
2. **Fork the Repository**: Create your own fork of the project
3. **Create a Branch**: Use a descriptive branch name
   ```bash
   git checkout -b feature/your-feature-name
   # or
   git checkout -b bugfix/issue-number
   ```
4. **Make Changes**: Implement your changes following our coding standards
5. **Test Thoroughly**: Ensure all tests pass and add new tests as needed
6. **Commit Changes**: Use clear, descriptive commit messages
7. **Push and Create PR**: Submit a pull request with detailed description

## Coding Standards

### Java Code Style

- **Indentation**: 4 spaces (no tabs)
- **Line Length**: Maximum 120 characters
- **Naming Conventions**:
    - Classes: PascalCase (`DynamodbEnvironmentRepository`)
    - Methods/Variables: camelCase (`findOne`, `dynamoDbClient`)
    - Constants: UPPER_SNAKE_CASE (`DEFAULT_PARTITION_KEY`)
    - Packages: lowercase with dots (`com.sarbesh.springboot.config.dynamodb`)

### Documentation Requirements

- **JavaDoc**: All public classes, methods, and fields must have comprehensive JavaDoc
- **Inline Comments**: Complex logic should be commented
- **README Updates**: Update documentation for user-facing changes

### Code Organization

- **Package Structure**: Follow existing package conventions
- **Dependency Injection**: Use constructor injection where possible
- **Configuration Properties**: Use `@ConfigurationProperties` for external configuration
- **Error Handling**: Provide meaningful error messages and proper exception handling

## Testing Guidelines

### Test Categories

- **Unit Tests**: Test individual components in isolation
- **Integration Tests**: Test component interactions
- **Configuration Tests**: Verify auto-configuration works correctly

### Test Requirements

- **Coverage**: Maintain or improve test coverage
- **Naming**: Use descriptive test method names
- **Assertions**: Use meaningful assertion messages
- **Mock Objects**: Use mocking frameworks appropriately

### Test Configuration

- Use `@SpringBootTest` for integration tests
- Use `@MockBean` for mocking Spring components
- Provide test configuration files in `src/test/resources`

## Documentation

### JavaDoc Standards

- **Class Documentation**: Purpose, usage examples, configuration
- **Method Documentation**: Parameters, return values, exceptions
- **Field Documentation**: Purpose and valid values
- **Example Code**: Include usage examples where helpful

### README Updates

Update the README.md when making changes that affect:

- Installation or setup instructions
- Configuration properties
- Usage examples
- Compatibility requirements

## Pull Request Process

### Before Submitting

- [ ] All tests pass locally
- [ ] Code follows project style guidelines
- [ ] Documentation is updated
- [ ] Commit messages are clear and descriptive
- [ ] Branch is up-to-date with main

### PR Description

Include in your pull request:

- **Summary**: Brief description of changes
- **Motivation**: Why this change is needed
- **Testing**: How the changes were tested
- **Breaking Changes**: Any backwards compatibility concerns
- **Related Issues**: Reference any related GitHub issues

### Review Process

1. **Automated Checks**: CI/CD pipeline will run tests and checks
2. **Code Review**: Maintainers will review your code
3. **Feedback**: Address any requested changes
4. **Approval**: Once approved, maintainers will merge your PR

## Issue Reporting

### Bug Reports

When reporting bugs, include:

- **Description**: Clear description of the issue
- **Steps to Reproduce**: Detailed steps to recreate the bug
- **Expected Behavior**: What you expected to happen
- **Actual Behavior**: What actually happened
- **Environment**:
    - Java version
    - Spring Boot version
    - Spring Cloud version
    - DynamoDB setup (local/AWS)
- **Logs**: Relevant error messages or stack traces
- **Configuration**: Relevant configuration properties

### Feature Requests

When requesting features:

- **Use Case**: Describe the problem you're trying to solve
- **Proposed Solution**: Your idea for implementation
- **Alternatives**: Other approaches you've considered
- **Additional Context**: Screenshots, examples, etc.

## Development Tips

### Local DynamoDB Testing

For local development, consider using:

- **DynamoDB Local**: AWS-provided local DynamoDB instance
- **TestContainers**: Docker-based testing with DynamoDB
- **Mocking**: Mock DynamoDB operations for unit tests

### Debugging

- Enable debug logging: `logging.level.com.sarbesh.springboot.config.dynamodb=DEBUG`
- Use IDE debugging with breakpoints
- Check Spring Boot actuator endpoints for configuration details

## Getting Help

- **GitHub Issues**: For questions about usage or development
- **Discussions**: For general questions and community support
- **Documentation**: Check README.md and JavaDoc

## Recognition

Contributors will be recognized in:

- CHANGELOG.md for their contributions
- GitHub contributors page
- Release notes for significant contributions

Thank you for contributing to Spring Cloud Config DynamoDB!
