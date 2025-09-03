package com.sarbesh.springboot.config.dynamodb.configuration;

import com.sarbesh.springboot.config.dynamodb.factory.DynamodbEnvironmentRepositoryFactory;
import com.sarbesh.springboot.config.dynamodb.properties.DynamodbEnvironmentProperties;
import com.sarbesh.springboot.config.dynamodb.repository.DynamodbEnvironmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test cases for DynamodbEnvironmentRepositoryAutoConfiguration covering different
 * Spring profiles, bootstrap configurations, and composite scenarios.
 *
 * @author Sarbesh Kumar Sarkar
 */
class DynamodbEnvironmentRepositoryAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DynamodbEnvironmentRepositoryAutoConfiguration.class));

    /**
     * Test Case 1: Profile dynamodb active, bootstrap false
     * Validates that main configuration beans are created when profile is active
     * but bootstrap is disabled.
     */
    @Test
    void whenDynamodbProfileActiveAndBootstrapFalse_shouldCreateMainBeans() {
        contextRunner
                .withPropertyValues(
                        "spring.profiles.active=dynamodb",
                        "spring.cloud.config.server.bootstrap=false",
                        "spring.cloud.config.server.dynamodb.region=us-east-1",
                        "spring.cloud.config.server.dynamodb.table=test_table"
                )
                .run(context -> {
                    // Main configuration beans should be created
                    assertThat(context).hasSingleBean(DynamodbEnvironmentProperties.class);
                    assertThat(context).hasBean("configDynamodbClient");
                    assertThat(context).hasSingleBean(DynamodbEnvironmentRepository.class);

                    // Verify properties are bound correctly
                    DynamodbEnvironmentProperties properties = context.getBean(DynamodbEnvironmentProperties.class);
                    assertThat(properties.getRegion()).isEqualTo("us-east-1");
                    assertThat(properties.getTable()).isEqualTo("test_table");

                    // Verify repository is properly configured
                    EnvironmentRepository repository = context.getBean(DynamodbEnvironmentRepository.class);
                    assertThat(repository).isNotNull();
                });
    }

    /**
     * Test Case 2: Profile not activated - beans should not be created
     * Validates that no DynamoDB beans are created when profile is not active.
     */
    @Test
    void whenDynamodbProfileNotActive_shouldNotCreateBeans() {
        contextRunner
                .withPropertyValues(
                        "spring.profiles.active=native", // Different profile
                        "spring.cloud.config.server.dynamodb.region=us-east-1"
                )
                .run(context -> {
                    // No DynamoDB beans should be created
                    assertThat(context).doesNotHaveBean(DynamodbEnvironmentProperties.class);
                    assertThat(context).doesNotHaveBean(DynamoDbClient.class);
                    assertThat(context).doesNotHaveBean(DynamodbEnvironmentRepository.class);
                    assertThat(context).doesNotHaveBean(DynamodbEnvironmentRepositoryFactory.class);
                });
    }

    /**
     * Test Case 3: Configuration properties binding and validation
     * Tests that all configuration properties are properly bound and validated.
     */
    @Test
    void shouldBindConfigurationPropertiesCorrectly() {
        contextRunner
                .withPropertyValues(
                        "spring.profiles.active=dynamodb",
                        "spring.cloud.config.server.dynamodb.region=eu-west-1",
                        "spring.cloud.config.server.dynamodb.table=config_store",
                        "spring.cloud.config.server.dynamodb.access-key=test-access-key",
                        "spring.cloud.config.server.dynamodb.secret-key=test-secret-key",
                        "spring.cloud.config.server.dynamodb.partition-key=app_profile",
                        "spring.cloud.config.server.dynamodb.config-attribute=config_data",
                        "spring.cloud.config.server.dynamodb.delimiter=_",
                        "spring.cloud.config.server.dynamodb.order=5",
                        "spring.cloud.config.server.dynamodb.connection-timeout=10000",
                        "spring.cloud.config.server.dynamodb.timeout=30000"
                )
                .run(context -> {
                    DynamodbEnvironmentProperties properties = context.getBean(DynamodbEnvironmentProperties.class);

                    assertThat(properties.getRegion()).isEqualTo("eu-west-1");
                    assertThat(properties.getTable()).isEqualTo("config_store");
                    assertThat(properties.getAccessKey()).isEqualTo("test-access-key");
                    assertThat(properties.getSecretKey()).isEqualTo("test-secret-key");
                    assertThat(properties.getPartitionKey()).isEqualTo("app_profile");
                    assertThat(properties.getConfigAttribute()).isEqualTo("config_data");
                    assertThat(properties.getDelimiter()).isEqualTo("_");
                    assertThat(properties.getOrder()).isEqualTo(5);
                    assertThat(properties.getConnectionTimeout()).isEqualTo(10000);
                    assertThat(properties.getTimeout()).isEqualTo(30000);

                    // Test credential detection
                    assertThat(properties.hasAccessKeyAndSecretKey()).isTrue();
                });
    }

    /**
     * Test Case 4: Default property values
     * Validates that default values are applied when properties are not specified.
     */
    @Test
    void shouldUseDefaultPropertyValues() {
        contextRunner
                .withPropertyValues(
                        "spring.profiles.active=dynamodb",
                        "spring.cloud.config.server.dynamodb.region=us-east-1",
                        "spring.cloud.config.server.dynamodb.table=config_table"
                )
                .run(context -> {
                    DynamodbEnvironmentProperties properties = context.getBean(DynamodbEnvironmentProperties.class);

                    // Test default values
                    assertThat(properties.getPartitionKey()).isEqualTo("config_id");
                    assertThat(properties.getConfigAttribute()).isEqualTo("properties");
                    assertThat(properties.getDelimiter()).isEqualTo("-");
                    assertThat(properties.getOrder()).isEqualTo(Integer.MAX_VALUE);
                    assertThat(properties.getConnectionTimeout()).isEqualTo(10000);
                    assertThat(properties.getTimeout()).isEqualTo(50000);

                    // No credentials should be detected
                    assertThat(properties.hasAccessKeyAndSecretKey()).isFalse();
                });
    }

    /**
     * Test Case 5: Bean creation with proper qualifiers
     * Verifies that all beans are created correctly with proper qualifiers
     * and no ambiguity issues exist.
     */
    @Test
    void shouldCreateBeansWithProperQualifiers() {
        contextRunner
                .withPropertyValues(
                        "spring.profiles.active=dynamodb",
                        "spring.cloud.config.server.dynamodb.region=us-east-1",
                        "spring.cloud.config.server.dynamodb.table=test_table"
                )
                .run(context -> {
                    // Verify main configuration DynamoDB client exists
                    assertThat(context).hasBean("configDynamodbClient");

                    // Verify repository can be created without ambiguity
                    DynamodbEnvironmentRepository repository = context.getBean(DynamodbEnvironmentRepository.class);
                    assertThat(repository).isNotNull();

                    // Verify no bean ambiguity by checking context startup
                    assertThat(context.getStartupFailure()).isNull();
                });
    }

    /**
     * Test Case 6: Conditional bean creation
     * Tests that beans are only created when required conditions are met.
     */
    @Test
    void shouldRespectConditionalAnnotations() {
        contextRunner
                .withPropertyValues(
                        "spring.profiles.active=dynamodb",
                        "spring.cloud.config.server.dynamodb.region=us-east-1",
                        "spring.cloud.config.server.dynamodb.table=test_table"
                )
                .run(context -> {
                    // Should have exactly one of each bean type (may have multiple DynamoDB clients for bootstrap + main)
                    assertThat(context.getBeansOfType(DynamodbEnvironmentProperties.class)).hasSize(1);
                    assertThat(context.getBeansOfType(DynamoDbClient.class)).hasSizeGreaterThanOrEqualTo(1);
                    assertThat(context.getBeansOfType(DynamodbEnvironmentRepository.class)).hasSize(1);
                });
    }
}
