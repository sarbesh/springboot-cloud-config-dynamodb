package com.sarbesh.springboot.config.dynamodb.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.config.server.support.EnvironmentRepositoryProperties;
import org.springframework.core.Ordered;

/**
 * Configuration properties for the DynamoDB EnvironmentRepository.
 * Binds properties with prefix 'spring.cloud.config.server.dynamodb'.
 */
@ConfigurationProperties(prefix = "spring.cloud.config.server.dynamodb")
public class DynamodbEnvironmentProperties implements EnvironmentRepositoryProperties {

    private final Logger LOGGER = LoggerFactory.getLogger(DynamodbEnvironmentProperties.class);

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

    private String partitionKey = "config_id";

    private String configAttribute = "properties";

    private String delimiter = "-";

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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getOrder() {
        return order;
    }

    public String getPartitionKey() {
        return partitionKey;
    }

    public void setPartitionKey(String partitionKey) {
        this.partitionKey = partitionKey;
    }

    public String getConfigAttribute() {
        return configAttribute;
    }

    public void setConfigAttribute(String configAttribute) {
        this.configAttribute = configAttribute;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String dilimiter) {
        this.delimiter = dilimiter;
    }
}
