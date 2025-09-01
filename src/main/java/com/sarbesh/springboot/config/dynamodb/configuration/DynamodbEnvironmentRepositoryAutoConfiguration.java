package com.sarbesh.springboot.config.dynamodb.configuration;

import com.sarbesh.springboot.config.dynamodb.factory.DynamodbEnvironmentRepositoryFactory;
import com.sarbesh.springboot.config.dynamodb.properties.DynamodbEnvironmentProperties;
import com.sarbesh.springboot.config.dynamodb.repository.DynamodbEnvironmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * Auto-configuration for the DynamoDB EnvironmentRepository.
 * Registers a bean for Spring Cloud Config Server to use DynamoDB as a backend.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(EnvironmentRepository.class)
@Profile("dynamodb")
@EnableConfigurationProperties({DynamodbEnvironmentProperties.class})
public class DynamodbEnvironmentRepositoryAutoConfiguration {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    /**
     * Registers the DynamoDB EnvironmentRepository bean if missing.
     *
     * @return EnvironmentRepository implementation
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

    @Bean
    @ConditionalOnMissingBean(DynamodbEnvironmentRepository.class)
    public DynamodbEnvironmentRepository dynamodbEnvironmentRepository(
            DynamodbEnvironmentProperties dynamodbEnvironmentProperties,
            DynamoDbClient dynamoDbClient) {
        LOGGER.debug("DynamodbEnvironmentRepository initialized");
        return new DynamodbEnvironmentRepository(dynamodbEnvironmentProperties, dynamoDbClient);
    }

    @Configuration(proxyBeanMethods = false)
    static class DynamodbFactoryConfig {

        @Autowired
        @Qualifier("configDynamodbClient")
        private DynamoDbClient dynamoDbClient;

        @Bean
        @ConditionalOnMissingBean(DynamodbEnvironmentRepositoryFactory.class)
        public DynamodbEnvironmentRepositoryFactory dynamodbEnvironmentRepositoryFactory() {
            return new DynamodbEnvironmentRepositoryFactory(dynamoDbClient);
        }
    }

}
