package com.sarbesh.springboot.config.dynamodb.configuration;

import com.sarbesh.springboot.config.dynamodb.factory.DynamodbEnvironmentRepositoryFactory;
import com.sarbesh.springboot.config.dynamodb.properties.DynamodbEnvironmentProperties;
import com.sarbesh.springboot.config.dynamodb.repository.DynamodbEnvironmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.cloud.config.server.environment.EnvironmentRepositoryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * Auto-configuration for the DynamoDB EnvironmentRepository.
 * <p>
 * This configuration class enables Spring Cloud Config Server to use AWS DynamoDB as a backend
 * for storing and retrieving configuration properties. It supports both regular and bootstrap
 * contexts, ensuring seamless integration with composite environment repositories.
 * </p>
 * <p>
 * The configuration is activated when the 'dynamodb' profile is active and provides:
 * <ul>
 *   <li>A configured DynamoDbClient with optional credentials</li>
 *   <li>A DynamodbEnvironmentRepository for property retrieval</li>
 *   <li>A DynamodbEnvironmentRepositoryFactory for composite configuration</li>
 *   <li>Bootstrap context support for early factory registration</li>
 * </ul>
 * <p>
 * Configuration properties are bound from {@code spring.cloud.config.server.dynamodb.*}
 * </p>
 *
 * @author Sarbesh Kumar Sarkar
 * @since 1.0.0
 * @see DynamodbEnvironmentRepository
 * @see DynamodbEnvironmentRepositoryFactory
 * @see DynamodbEnvironmentProperties
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(EnvironmentRepository.class)
@Profile("dynamodb")
@EnableConfigurationProperties({DynamodbEnvironmentProperties.class})
public class DynamodbEnvironmentRepositoryAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamodbEnvironmentRepositoryAutoConfiguration.class);
    /**
     * Creates a DynamoDB client bean for the main application context.
     * <p>
     * This client is configured based on the DynamoDB environment properties,
     * including region settings and optional AWS credentials.
     * </p>
     *
     * @param properties the DynamoDB environment properties containing region and credentials
     * @return a configured DynamoDB client instance
     * @see DynamodbEnvironmentProperties
     */
    @Bean(name = "configDynamodbClient")
    public DynamoDbClient dynamoDbClient(DynamodbEnvironmentProperties properties) {
        var builder = DynamoDbClient.builder().region(Region.of(properties.getRegion()));
        if (properties.hasAccessKeyAndSecretKey()) {
            builder.credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(properties.getAccessKey(), properties.getSecretKey())));
        }
        return builder.build();
    }

    /**
     * Creates a DynamoDB environment repository bean if none exists.
     * <p>
     * This repository handles the actual retrieval of configuration properties
     * from DynamoDB tables based on application name and profile.
     * </p>
     *
     * @param dynamodbEnvironmentProperties the configuration properties for DynamoDB access
     * @param dynamoDbClient                the configured DynamoDB client
     * @return a DynamoDB environment repository instance
     * @see DynamodbEnvironmentRepository
     */
    @Bean
    @ConditionalOnMissingBean(DynamodbEnvironmentRepository.class)
    public DynamodbEnvironmentRepository dynamodbEnvironmentRepository(
            DynamodbEnvironmentProperties dynamodbEnvironmentProperties,
            @Qualifier("configDynamodbClient") DynamoDbClient dynamoDbClient) {
        LOGGER.debug("DynamodbEnvironmentRepository initialized");
        return new DynamodbEnvironmentRepository(dynamodbEnvironmentProperties, dynamoDbClient);
    }


    /**
     * Bootstrap configuration specifically for DynamoDB Environment Repository Factory
     * registration in the bootstrap context for composite configuration support.
     * <p>
     * This critical configuration ensures the factory is available during bootstrap condition
     * evaluation, preventing NPE in {@code CompositeUtils.getEnvironmentRepositoryFactoryTypeParams}.
     * This was the key breakthrough solution for enabling bootstrap support.
     * </p>
     * <p>
     * This nested configuration is registered via {@code org.springframework.cloud.bootstrap.BootstrapConfiguration}
     * in {@code META-INF/spring.factories} for early loading during bootstrap context initialization.
     * </p>
     * <p>
     * <strong>Technical Note:</strong> The bootstrap context loads before the main application context,
     * making it essential for composite configuration with {@code spring.cloud.config.server.bootstrap=true}.
     * </p>
     *
     * @author Sarbesh Kumar Sarkar
     * @see org.springframework.cloud.bootstrap.BootstrapConfiguration
     * @since 1.0.0
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(EnvironmentRepositoryFactory.class)
    @Profile("dynamodb")
    @EnableConfigurationProperties({DynamodbEnvironmentProperties.class})
    public static class DynamodbBootstrapConfiguration {

        /**
         * Creates a DynamoDB client for the bootstrap context using properties-based configuration.
         * <p>
         * This client is specifically created for bootstrap context operations and is configured
         * dynamically based on the DynamoDB environment properties, including:
         * <ul>
         *   <li>AWS region (with fallback to us-east-1)</li>
         *   <li>AWS credentials (if provided via access-key and secret-key)</li>
         * </ul>
         * </p>
         *
         * @param properties the DynamoDB environment properties containing configuration
         * @return a configured DynamoDB client for bootstrap context
         * @see DynamodbEnvironmentProperties#hasAccessKeyAndSecretKey()
         */
        @Bean(name = "bootstrapDynamodbClient")
        @ConditionalOnMissingBean(name = "bootstrapDynamodbClient")
        public DynamoDbClient bootstrapDynamodbClient(DynamodbEnvironmentProperties properties) {
            var builder = DynamoDbClient.builder()
                    .region(Region.of(properties.getRegion() != null ? properties.getRegion() : "us-east-1"));

            // Apply credentials if provided
            if (properties.hasAccessKeyAndSecretKey()) {
                builder.credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(properties.getAccessKey(), properties.getSecretKey())));
            }

            return builder.build();
        }

        /**
         * Registers the DynamoDB factory with the exact bean name Spring Cloud Config expects
         * for composite type lookup: {@code {type}EnvironmentRepositoryFactory}.
         * <p>
         * This factory bean is critical for composite configuration support and must be
         * available during bootstrap context initialization to prevent runtime errors
         * during condition evaluation.
         * </p>
         *
         * @param dynamoDbClient the bootstrap DynamoDB client
         * @return a DynamoDB environment repository factory instance
         * @see DynamodbEnvironmentRepositoryFactory
         */
        @Bean
        public DynamodbEnvironmentRepositoryFactory dynamodbEnvironmentRepositoryFactory(
                @Qualifier("bootstrapDynamodbClient") DynamoDbClient dynamoDbClient) {
            return new DynamodbEnvironmentRepositoryFactory(dynamoDbClient);
        }
    }

}
