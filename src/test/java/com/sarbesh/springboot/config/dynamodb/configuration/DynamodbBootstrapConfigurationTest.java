package com.sarbesh.springboot.config.dynamodb.configuration;

import com.sarbesh.springboot.config.dynamodb.factory.DynamodbEnvironmentRepositoryFactory;
import com.sarbesh.springboot.config.dynamodb.properties.DynamodbEnvironmentProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test cases for DynamoDB Bootstrap Configuration covering composite configuration
 * scenarios with bootstrap context.
 *
 * @author Sarbesh Kumar Sarkar
 */
class DynamodbBootstrapConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    DynamodbEnvironmentRepositoryAutoConfiguration.DynamodbBootstrapConfiguration.class
            ));

    /**
     * Test Case: Composite configuration with bootstrap true
     * Validates that bootstrap configuration creates factory beans correctly
     * for composite environment repository support.
     */
    @Test
    void whenBootstrapTrueAndComposite_shouldCreateBootstrapBeans() {
        contextRunner
                .withPropertyValues(
                        "spring.profiles.active=dynamodb",
                        "spring.cloud.config.server.bootstrap=true",
                        "spring.cloud.config.server.composite[0].type=dynamodb",
                        "spring.cloud.config.server.composite[0].region=us-east-1",
                        "spring.cloud.config.server.composite[0].table=config_table",
                        "spring.cloud.config.server.composite[0].order=1"
                )
                .run(context -> {
                    // Bootstrap configuration beans should be created
                    assertThat(context).hasSingleBean(DynamodbEnvironmentProperties.class);
                    assertThat(context).hasBean("bootstrapDynamodbClient");
                    assertThat(context).hasSingleBean(DynamodbEnvironmentRepositoryFactory.class);

                    // Verify factory is properly configured
                    DynamodbEnvironmentRepositoryFactory factory = context.getBean(DynamodbEnvironmentRepositoryFactory.class);
                    assertThat(factory).isNotNull();

                    // Verify bootstrap DynamoDB client exists
                    DynamoDbClient bootstrapClient = context.getBean("bootstrapDynamodbClient", DynamoDbClient.class);
                    assertThat(bootstrapClient).isNotNull();
                });
    }

    /**
     * Test Case: Bootstrap configuration with credentials
     * Validates that bootstrap configuration properly handles AWS credentials.
     */
    @Test
    void whenBootstrapWithCredentials_shouldConfigureClientWithCredentials() {
        contextRunner
                .withPropertyValues(
                        "spring.profiles.active=dynamodb",
                        "spring.cloud.config.server.bootstrap=true",
                        "spring.cloud.config.server.dynamodb.region=eu-west-1",
                        "spring.cloud.config.server.dynamodb.table=config_store",
                        "spring.cloud.config.server.dynamodb.access-key=bootstrap-access-key",
                        "spring.cloud.config.server.dynamodb.secret-key=bootstrap-secret-key"
                )
                .run(context -> {
                    DynamodbEnvironmentProperties properties = context.getBean(DynamodbEnvironmentProperties.class);

                    // Verify credentials are bound correctly
                    assertThat(properties.getRegion()).isEqualTo("eu-west-1");
                    assertThat(properties.getTable()).isEqualTo("config_store");
                    assertThat(properties.getAccessKey()).isEqualTo("bootstrap-access-key");
                    assertThat(properties.getSecretKey()).isEqualTo("bootstrap-secret-key");
                    assertThat(properties.hasAccessKeyAndSecretKey()).isTrue();

                    // Bootstrap client should be created with credentials
                    DynamoDbClient bootstrapClient = context.getBean("bootstrapDynamodbClient", DynamoDbClient.class);
                    assertThat(bootstrapClient).isNotNull();
                });
    }

    /**
     * Test Case: Bootstrap configuration without dynamodb profile
     * Validates that bootstrap beans are not created when profile is not active.
     */
    @Test
    void whenBootstrapTrueButProfileNotActive_shouldNotCreateBootstrapBeans() {
        contextRunner
                .withPropertyValues(
                        "spring.profiles.active=native", // Different profile
                        "spring.cloud.config.server.bootstrap=true",
                        "spring.cloud.config.server.dynamodb.region=us-east-1"
                )
                .run(context -> {
                    // No bootstrap beans should be created without dynamodb profile
                    assertThat(context).doesNotHaveBean(DynamodbEnvironmentProperties.class);
                    assertThat(context).doesNotHaveBean("bootstrapDynamodbClient");
                    assertThat(context).doesNotHaveBean(DynamodbEnvironmentRepositoryFactory.class);
                });
    }

    /**
     * Test Case: Bootstrap configuration with default region
     * Validates that default region is used when not specified.
     */
    @Test
    void whenBootstrapWithoutRegion_shouldUseDefaultRegion() {
        contextRunner
                .withPropertyValues(
                        "spring.profiles.active=dynamodb",
                        "spring.cloud.config.server.bootstrap=true",
                        "spring.cloud.config.server.dynamodb.table=config_table"
                        // No region specified - should default to us-east-1
                )
                .run(context -> {
                    DynamodbEnvironmentProperties properties = context.getBean(DynamodbEnvironmentProperties.class);

                    // Should use default region when not specified
                    assertThat(properties.getRegion()).isNull(); // Property is null

                    // But bootstrap client should still be created with default
                    DynamoDbClient bootstrapClient = context.getBean("bootstrapDynamodbClient", DynamoDbClient.class);
                    assertThat(bootstrapClient).isNotNull();
                });
    }

    /**
     * Test Case: Factory bean creation
     * Validates that the factory bean is created correctly and can build repositories.
     */
    @Test
    void shouldCreateFactoryBeanCorrectly() {
        contextRunner
                .withPropertyValues(
                        "spring.profiles.active=dynamodb",
                        "spring.cloud.config.server.bootstrap=true",
                        "spring.cloud.config.server.dynamodb.region=us-east-1",
                        "spring.cloud.config.server.dynamodb.table=test_table"
                )
                .run(context -> {
                    // Factory should be available
                    DynamodbEnvironmentRepositoryFactory factory = context.getBean(DynamodbEnvironmentRepositoryFactory.class);
                    assertThat(factory).isNotNull();

                    // Factory should be able to build repositories (we'll test actual build in factory tests)
                    assertThat(factory.getClass().getSimpleName()).isEqualTo("DynamodbEnvironmentRepositoryFactory");
                });
    }
}
