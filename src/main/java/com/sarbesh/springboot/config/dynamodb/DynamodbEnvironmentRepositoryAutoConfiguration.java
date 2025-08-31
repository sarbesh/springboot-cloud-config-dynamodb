package com.sarbesh.springboot.config.dynamodb;

import com.sarbesh.springboot.config.dynamodb.configuration.DynamodbEnvironmentProperties;
import com.sarbesh.springboot.config.dynamodb.repository.DynamodbEnvironmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Auto-configuration for the DynamoDB EnvironmentRepository.
 * Registers a bean for Spring Cloud Config Server to use DynamoDB as a backend.
 */
@Configuration
@ConditionalOnClass(EnvironmentRepository.class)
@Profile("dynamodb")
public class DynamodbEnvironmentRepositoryAutoConfiguration implements InitializingBean {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final ConfigurableEnvironment environment;
    private final DynamodbEnvironmentProperties dynamodbEnvironmentProperties;

    /**
     * Constructor for auto-configuration.
     *
     * @param environment                   Spring environment
     * @param dynamodbEnvironmentProperties Properties for DynamoDB config
     */
    @Autowired
    public DynamodbEnvironmentRepositoryAutoConfiguration(ConfigurableEnvironment environment,
                                                          DynamodbEnvironmentProperties dynamodbEnvironmentProperties) {
        super();
        this.environment = environment;
        this.dynamodbEnvironmentProperties = dynamodbEnvironmentProperties;
    }

    /**
     * Registers the DynamoDB EnvironmentRepository bean if missing.
     *
     * @return EnvironmentRepository implementation
     */
    @Bean
    @ConditionalOnMissingBean
    public EnvironmentRepository dynamodbEnvironmentRepository() {
        LOGGER.debug("DynamodbEnvironmentRepository initialized");
        return new DynamodbEnvironmentRepository(this.environment,
                this.dynamodbEnvironmentProperties);
    }

    /**
     * Logs initialization after properties are set.
     */
    @Override
    public void afterPropertiesSet() {
        LOGGER.debug("DynamodbEnvironmentRepositoryAutoConfiguration initialized with properties: {}", this);
    }
}
