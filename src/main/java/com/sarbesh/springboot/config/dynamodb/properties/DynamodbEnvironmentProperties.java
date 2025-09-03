package com.sarbesh.springboot.config.dynamodb.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.config.server.support.EnvironmentRepositoryProperties;
import org.springframework.core.Ordered;

/**
 * Configuration properties for the DynamoDB EnvironmentRepository.
 * <p>
 * This class defines all configurable properties for DynamoDB-based configuration storage
 * in Spring Cloud Config Server. Properties are bound with the prefix
 * {@code spring.cloud.config.server.dynamodb.*}.
 * </p>
 * <p>
 * <strong>Required Properties:</strong>
 * <ul>
 *   <li>{@code region} - AWS region where DynamoDB table is located</li>
 *   <li>{@code table} - DynamoDB table name containing configuration data</li>
 * </ul>
 * </p>
 * <p>
 * <strong>Optional Properties:</strong>
 * <ul>
 *   <li>{@code access-key} - AWS access key (if not using IAM roles)</li>
 *   <li>{@code secret-key} - AWS secret key (if not using IAM roles)</li>
 *   <li>{@code partition-key} - Custom partition key name (default: config_id)</li>
 *   <li>{@code config-attribute} - Attribute containing config data (default: properties)</li>
 *   <li>{@code delimiter} - Separator for application-profile keys (default: -)</li>
 *   <li>{@code order} - Repository precedence order</li>
 *   <li>{@code connection-timeout} - DynamoDB connection timeout in ms</li>
 *   <li>{@code timeout} - DynamoDB request timeout in ms</li>
 * </ul>
 * </p>
 * <p>
 * <strong>Example Configuration:</strong>
 * </p>
 * <pre>{@code
 * spring:
 *   cloud:
 *     config:
 *       server:
 *         dynamodb:
 *           region: us-east-1
 *           table: config_table
 *           access-key: ${AWS_ACCESS_KEY:}
 *           secret-key: ${AWS_SECRET_KEY:}
 *           partition-key: config_id
 *           config-attribute: properties
 *           delimiter: "-"
 *           order: 1
 * }</pre>
 *
 * @author Sarbesh Kumar Sarkar
 * @since 1.0.0
 * @see org.springframework.boot.context.properties.ConfigurationProperties
 * @see org.springframework.cloud.config.server.support.EnvironmentRepositoryProperties
 */
@ConfigurationProperties(prefix = "spring.cloud.config.server.dynamodb")
public class DynamodbEnvironmentProperties implements EnvironmentRepositoryProperties {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamodbEnvironmentProperties.class);

    /**
     * AWS region where the DynamoDB table is located.
     * <p>
     * This is a required property that specifies which AWS region contains
     * the DynamoDB table storing configuration data.
     * </p>
     * <p>
     * Example: {@code us-east-1}, {@code eu-west-1}, {@code ap-south-1}
     * </p>
     */
    private String region;
    /**
     * Name of the DynamoDB table containing configuration data.
     * <p>
     * This table must have a partition key (default name: {@code config_id})
     * and contain configuration data in a Map attribute (default name: {@code properties}).
     * </p>
     * <p>
     * The table structure should follow this pattern:
     * <ul>
     *   <li>Partition Key: {@code config_id} (String) - Format: {application}-{profile}</li>
     *   <li>Config Data: {@code properties} (Map) - Nested configuration properties</li>
     * </ul>
     * </p>
     */
    private String table;
    /**
     * AWS access key for explicit credential authentication.
     * <p>
     * This property is optional and should only be used when not relying on
     * IAM roles, instance profiles, or other AWS credential providers.
     * Both {@code access-key} and {@code secret-key} must be provided together.
     * </p>
     * <p>
     * <strong>Security Note:</strong> Consider using IAM roles or environment
     * variables instead of hardcoding credentials in configuration files.
     * </p>
     *
     * @see #hasAccessKeyAndSecretKey()
     */
    private String accessKey;
    /**
     * AWS secret key for explicit credential authentication.
     * <p>
     * This property is optional and should only be used when not relying on
     * IAM roles, instance profiles, or other AWS credential providers.
     * Both {@code access-key} and {@code secret-key} must be provided together.
     * </p>
     * <p>
     * <strong>Security Note:</strong> Consider using IAM roles or environment
     * variables instead of hardcoding credentials in configuration files.
     * </p>
     *
     * @see #hasAccessKeyAndSecretKey()
     */
    private String secretKey;
    /**
     * Connection timeout for DynamoDB client operations in milliseconds.
     * <p>
     * This timeout controls how long the client waits when establishing
     * a connection to DynamoDB. Default value is 10,000ms (10 seconds).
     * </p>
     */
    private int connectionTimeout = 10000;
    /**
     * Request timeout for DynamoDB operations in milliseconds.
     * <p>
     * This timeout controls how long the client waits for a DynamoDB
     * operation to complete. Default value is 50,000ms (50 seconds).
     * </p>
     */
    private int timeout = 50000;
    /**
     * Repository order for precedence in composite configurations.
     * <p>
     * Lower values indicate higher precedence. This property determines
     * the order in which this repository is consulted when multiple
     * environment repositories are configured.
     * </p>
     * <p>
     * Default value is {@link Ordered#LOWEST_PRECEDENCE} which means
     * this repository will be consulted last unless explicitly configured.
     * </p>
     *
     * @see org.springframework.core.Ordered
     */
    private int order = Ordered.LOWEST_PRECEDENCE;

    /**
     * Name of the DynamoDB partition key attribute.
     * <p>
     * This attribute serves as the primary key for the DynamoDB table
     * and contains values in the format {@code {application}-{profile}}.
     * Default value is {@code "config_id"}.
     * </p>
     * <p>
     * Example values: {@code "myapp-dev"}, {@code "userservice-prod"}
     * </p>
     */
    private String partitionKey = "config_id";

    /**
     * Name of the DynamoDB attribute containing configuration data.
     * <p>
     * This attribute should contain a Map with nested configuration
     * properties that will be flattened into dot-separated keys.
     * Default value is {@code "properties"}.
     * </p>
     * <p>
     * Example structure:
     * </p>
     * <pre>{@code
     * {
     *   "spring": {
     *     "datasource": {
     *       "url": "jdbc:postgresql://localhost/mydb"
     *     }
     *   }
     * }
     * }</pre>
     * <p>
     * This would be flattened to: {@code spring.datasource.url=jdbc:postgresql://localhost/mydb}
     * </p>
     */
    private String configAttribute = "properties";

    /**
     * Delimiter used to separate application name and profile in the partition key.
     * <p>
     * This delimiter is used to construct the partition key value from
     * application name and profile. Default value is {@code "-"}.
     * </p>
     * <p>
     * Example: With delimiter {@code "-"}, application {@code "myapp"} and
     * profile {@code "dev"} would create partition key {@code "myapp-dev"}.
     * </p>
     */
    private String delimiter = "-";

    /**
     * Sets the order for repository precedence in composite configurations.
     * <p>
     * Lower values indicate higher precedence. This determines the order
     * in which this repository is consulted when multiple environment
     * repositories are configured.
     * </p>
     *
     * @param order the precedence order (lower = higher precedence)
     * @see org.springframework.core.Ordered
     */
    @Override
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * Returns a string representation of this configuration properties object.
     * <p>
     * Note: The secret key is excluded from the string representation for security reasons.
     * </p>
     *
     * @return a string representation containing configuration details (excluding secret key)
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
     * Checks if both access key and secret key are provided and non-blank.
     * <p>
     * This method is used to determine whether explicit AWS credentials
     * should be used for DynamoDB client authentication. If this returns
     * {@code false}, the client will rely on other credential providers
     * such as IAM roles, instance profiles, or environment variables.
     * </p>
     *
     * @return {@code true} if both access key and secret key are provided and non-blank,
     *         {@code false} otherwise
     * @see #getAccessKey()
     * @see #getSecretKey()
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
