package com.sarbesh.springboot.config.dynamodb;

import com.sarbesh.springboot.config.dynamodb.configuration.DynamodbEnvironmentProperties;
import com.sarbesh.springboot.config.dynamodb.repository.DynamodbEnvironmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Auto-configuration for DynamoDB EnvironmentRepository.
 */
@Configuration
@ConditionalOnClass(EnvironmentRepository.class)
@Profile("dynamodb")
public class DynamodbEnvironmentRepositoryAutoConfiguration implements InitializingBean {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final ConfigurableEnvironment environment;
    private final DynamodbEnvironmentProperties dynamodbEnvironmentProperties;

    @Autowired
    public DynamodbEnvironmentRepositoryAutoConfiguration(ConfigurableEnvironment environment,
                                                          DynamodbEnvironmentProperties
                                                                  dynamodbEnvironmentProperties) {
        super();
        this.environment = environment;
        this.dynamodbEnvironmentProperties = dynamodbEnvironmentProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public EnvironmentRepository dynamodbEnvironmentRepository() {
        LOGGER.debug("DynamodbEnvironmentRepository initialized");
        return new DynamodbEnvironmentRepository(this.environment,
                this.dynamodbEnvironmentProperties);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.debug("DynamodbEnvironmentRepositoryAutoConfiguration initialized " +
                "with properties: {}",this);
    }
}
