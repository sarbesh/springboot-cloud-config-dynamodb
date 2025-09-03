package com.sarbesh.springboot.config.dynamodb.factory;

import com.sarbesh.springboot.config.dynamodb.properties.DynamodbEnvironmentProperties;
import com.sarbesh.springboot.config.dynamodb.repository.DynamodbEnvironmentRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.config.server.environment.EnvironmentRepositoryFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * Factory implementation for creating {@link DynamodbEnvironmentRepository} instances.
 * <p>
 * This factory is responsible for creating DynamoDB environment repositories based on
 * configuration properties. It implements the Spring Cloud Config Server's
 * {@link EnvironmentRepositoryFactory} interface to enable seamless integration
 * with composite environment repository configurations.
 * </p>
 * <p>
 * The factory supports both bootstrap and regular application context initialization,
 * making it compatible with {@code spring.cloud.config.server.bootstrap=true} configurations.
 * </p>
 * <p>
 * <strong>Usage in Composite Configuration:</strong>
 * </p>
 * <pre>{@code
 * spring:
 *   cloud:
 *     config:
 *       server:
 *         composite:
 *           - type: dynamodb
 *             region: us-east-1
 *             table: config_table
 *             order: 1
 * }</pre>
 *
 * @author Sarbesh Kumar Sarkar
 * @see DynamodbEnvironmentRepository
 * @see DynamodbEnvironmentProperties
 * @see EnvironmentRepositoryFactory
 * @since 1.0.0
 */
public class DynamodbEnvironmentRepositoryFactory
        implements EnvironmentRepositoryFactory<DynamodbEnvironmentRepository, DynamodbEnvironmentProperties> {

    /**
     * The DynamoDB client used for database operations.
     * May be null during bootstrap context initialization.
     */
    private DynamoDbClient dynamoDbClient;

    /**
     * Creates a factory instance with the specified DynamoDB client.
     * <p>
     * This constructor is used in the main application context where the
     * DynamoDB client is fully configured and injected.
     * </p>
     *
     * @param dynamoDbClient the configured DynamoDB client for database operations
     */
    public DynamodbEnvironmentRepositoryFactory(@Qualifier("configDynamodbClient") DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    /**
     * Default constructor for bootstrap context initialization.
     * <p>
     * This constructor is used during bootstrap context loading where
     * the DynamoDB client may not be immediately available. The client
     * will be resolved when the {@link #build(DynamodbEnvironmentProperties)}
     * method is called.
     * </p>
     */
    public DynamodbEnvironmentRepositoryFactory() {
        this.dynamoDbClient = null;
    }

    /**
     * Builds a new {@link DynamodbEnvironmentRepository} instance using the provided properties.
     * <p>
     * This method creates a repository instance configured with the specified DynamoDB
     * environment properties. If no DynamoDB client was provided during construction,
     * this method will create a new client based on the properties.
     * </p>
     *
     * @param environmentProperties the DynamoDB environment properties containing
     *                              configuration such as region, table name, credentials, etc.
     * @return a configured DynamoDB environment repository instance
     * @see DynamodbEnvironmentRepository
     * @see DynamodbEnvironmentProperties
     */
    @Override
    public DynamodbEnvironmentRepository build(DynamodbEnvironmentProperties environmentProperties) {
        return new DynamodbEnvironmentRepository(environmentProperties, dynamoDbClient);
    }
}
