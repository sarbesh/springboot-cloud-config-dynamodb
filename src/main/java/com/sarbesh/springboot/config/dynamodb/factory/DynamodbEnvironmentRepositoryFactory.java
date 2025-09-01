package com.sarbesh.springboot.config.dynamodb.factory;

import com.sarbesh.springboot.config.dynamodb.properties.DynamodbEnvironmentProperties;
import com.sarbesh.springboot.config.dynamodb.repository.DynamodbEnvironmentRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.config.server.environment.EnvironmentRepositoryFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamodbEnvironmentRepositoryFactory
        implements EnvironmentRepositoryFactory<DynamodbEnvironmentRepository, DynamodbEnvironmentProperties> {

    private final DynamoDbClient dynamoDbClient;

    public DynamodbEnvironmentRepositoryFactory(@Qualifier("configDynamodbClient") DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @Override
    public DynamodbEnvironmentRepository build(DynamodbEnvironmentProperties environmentProperties) {
        return new DynamodbEnvironmentRepository(environmentProperties, dynamoDbClient);
    }
}
