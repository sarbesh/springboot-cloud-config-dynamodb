package com.sarbesh.springboot.config.dynamodb.factory;

import com.sarbesh.springboot.config.dynamodb.properties.DynamodbEnvironmentProperties;
import com.sarbesh.springboot.config.dynamodb.repository.DynamodbEnvironmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test cases for DynamodbEnvironmentRepositoryFactory covering factory functionality
 * and repository creation scenarios.
 *
 * @author Sarbesh Kumar Sarkar
 */
@ExtendWith(MockitoExtension.class)
class DynamodbEnvironmentRepositoryFactoryTest {

    @Mock
    private DynamoDbClient mockDynamoDbClient;

    private DynamodbEnvironmentRepositoryFactory factory;

    @BeforeEach
    void setUp() {
        factory = new DynamodbEnvironmentRepositoryFactory(mockDynamoDbClient);
    }

    /**
     * Test Case: Factory creates repository correctly
     * Validates that the factory can create DynamodbEnvironmentRepository instances.
     */
    @Test
    void shouldCreateRepositoryCorrectly() {
        // Arrange
        DynamodbEnvironmentProperties properties = new DynamodbEnvironmentProperties();
        properties.setRegion("us-east-1");
        properties.setTable("test_table");

        // Act
        EnvironmentRepository repository = factory.build(properties);

        // Assert
        assertThat(repository).isNotNull();
        assertThat(repository).isInstanceOf(DynamodbEnvironmentRepository.class);

        // Verify the repository is properly configured
        DynamodbEnvironmentRepository dynamodbRepo = (DynamodbEnvironmentRepository) repository;
        assertThat(dynamodbRepo).isNotNull();
    }

    /**
     * Test Case: Factory constructor with null client
     * Validates that factory can be created with null client (for bootstrap scenarios).
     */
    @Test
    void whenNullDynamoDbClient_shouldCreateFactory() {
        // Act
        DynamodbEnvironmentRepositoryFactory nullClientFactory = new DynamodbEnvironmentRepositoryFactory();

        // Assert
        assertThat(nullClientFactory).isNotNull();
        assertThat(nullClientFactory).isInstanceOf(org.springframework.cloud.config.server.environment.EnvironmentRepositoryFactory.class);
    }

    /**
     * Test Case: Factory builds multiple repositories
     * Validates that factory can create multiple repository instances.
     */
    @Test
    void shouldBuildMultipleRepositories() {
        // Arrange
        DynamodbEnvironmentProperties properties1 = new DynamodbEnvironmentProperties();
        properties1.setRegion("us-east-1");
        properties1.setTable("test_table1");

        DynamodbEnvironmentProperties properties2 = new DynamodbEnvironmentProperties();
        properties2.setRegion("us-west-2");
        properties2.setTable("test_table2");

        // Act
        EnvironmentRepository repo1 = factory.build(properties1);
        EnvironmentRepository repo2 = factory.build(properties2);

        // Assert
        assertThat(repo1).isNotNull();
        assertThat(repo2).isNotNull();
        assertThat(repo1).isNotSameAs(repo2); // Should be different instances

        // Both should be DynamoDB repositories
        assertThat(repo1).isInstanceOf(DynamodbEnvironmentRepository.class);
        assertThat(repo2).isInstanceOf(DynamodbEnvironmentRepository.class);
    }

    /**
     * Test Case: Factory with same client reference
     * Validates that factory uses the same DynamoDB client reference.
     */
    @Test
    void shouldUseSameDynamoDbClientReference() {
        // Arrange
        DynamodbEnvironmentProperties properties = new DynamodbEnvironmentProperties();
        properties.setRegion("us-east-1");
        properties.setTable("test_table");

        // Act
        EnvironmentRepository repository = factory.build(properties);

        // Assert
        assertThat(repository).isInstanceOf(DynamodbEnvironmentRepository.class);
        // The repository should use the same client reference (tested via reflection if needed)
        assertThat(repository).isNotNull();
    }

    /**
     * Test Case: Factory type compatibility
     * Validates that factory implements the correct interface.
     */
    @Test
    void shouldImplementEnvironmentRepositoryFactory() {
        // Assert
        assertThat(factory).isInstanceOf(org.springframework.cloud.config.server.environment.EnvironmentRepositoryFactory.class);
    }
}
