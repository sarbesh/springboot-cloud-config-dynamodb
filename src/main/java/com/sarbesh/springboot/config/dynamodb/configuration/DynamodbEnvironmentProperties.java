package com.sarbesh.springboot.config.dynamodb.configuration;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.config.server.support.EnvironmentRepositoryProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * Configuration properties for the DynamoDB EnvironmentRepository.
 * Binds properties with prefix 'spring.cloud.config.server.dynamodb'.
 */
@Configuration
@ConfigurationProperties(prefix = "spring.cloud.config.server.dynamodb")
@Getter
@Setter
public class DynamodbEnvironmentProperties implements EnvironmentRepositoryProperties, InitializingBean {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    /**
     * AWS region to use for DynamoDB.
     */
    private String region;
    /**
     * DynamoDB table name.
     */
    private String table;
    /**
     * AWS access key (optional, for explicit credentials).
     */
    private String accessKey;
    /**
     * AWS secret key (optional, for explicit credentials).
     */
    private String secretKey;
    /**
     * Connection timeout in milliseconds.
     */
    private int connectionTimeout = 10000;
    /**
     * Request timeout in milliseconds.
     */
    private int timeout = 50000;
    /**
     * Repository order (lower = higher precedence).
     */
    private int order = Ordered.LOWEST_PRECEDENCE;

    /**
     * Logs initialization after properties are set.
     */
    @Override
    public void afterPropertiesSet() {
        LOGGER.debug("DynamodbEnvironmentProperties initialized with properties: {}", this);
    }

    /**
     * Sets the order for repository precedence.
     *
     * @param order int order
     */
    @Override
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "DynamodbEnvironmentProperties{" +
                "region='" + region + '\'' +
                ", table='" + table + '\'' +
                ", accessKey='" + accessKey + '\'' +
                ", connectionTimeout=" + connectionTimeout +
                ", timeout=" + timeout +
                ", order=" + order +
                '}';
    }

    /**
     * Checks if both accessKey and secretKey are provided.
     *
     * @return true if both are present and non-blank
     */
    public boolean hasAccessKeyAndSecretKey() {
        return this.accessKey != null && !this.accessKey.isBlank()
                && this.secretKey != null && !this.secretKey.isBlank();
    }
}
