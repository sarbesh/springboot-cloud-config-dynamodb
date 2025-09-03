---
name: Bug Report
about: Create a report to help us improve
title: '[BUG] '
labels: 'bug'
assignees: ''
---

## Bug Description

A clear and concise description of what the bug is.

## Environment

- **Java Version**:
- **Spring Boot Version**:
- **Spring Cloud Version**:
- **Library Version**:
- **DynamoDB Setup**: [AWS/Local/TestContainers]

## Steps to Reproduce

Steps to reproduce the behavior:

1. Configure bootstrap.yml with '...'
2. Start application with '...'
3. Call endpoint '...'
4. See error

## Expected Behavior

A clear and concise description of what you expected to happen.

## Actual Behavior

A clear and concise description of what actually happened.

## Configuration

```yaml
# Please share relevant configuration (bootstrap.yml/application.yml)
spring:
  cloud:
    config:
      server:
        dynamodb:
        # Your DynamoDB configuration
```

## Logs and Stack Traces

```
Paste relevant logs and stack traces here
```

## DynamoDB Table Structure

If relevant, describe your DynamoDB table structure:

- Table name:
- Partition key:
- Sample data:

## Additional Context

Add any other context about the problem here, such as:

- Screenshots
- Related issues
- Workarounds you've tried
