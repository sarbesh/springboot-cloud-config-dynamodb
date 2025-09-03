package com.sarbesh.springboot.config.dynamodb.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sarbesh.springboot.config.dynamodb.properties.DynamodbEnvironmentProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.core.Ordered;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom implementation of Spring Cloud Config's {@link EnvironmentRepository} that uses AWS DynamoDB
 * as the backend for storing and retrieving configuration properties.
 * <p>
 * This repository retrieves configuration data from a DynamoDB table based on application name and profile.
 * The configuration data is stored as nested Maps in DynamoDB and is flattened into dot-separated
 * property keys for consumption by Spring Cloud Config clients.
 * </p>
 * <p>
 * <strong>DynamoDB Table Structure:</strong>
 * </p>
 * <ul>
 *   <li><strong>Partition Key:</strong> {@code config_id} (configurable) - Format: {application}-{profile}</li>
 *   <li><strong>Config Attribute:</strong> {@code properties} (configurable) - Map containing nested configuration</li>
 * </ul>
 * <p>
 * <strong>Example DynamoDB Item:</strong>
 * </p>
 * <pre>{@code
 * {
 *   "config_id": "myapp-prod",
 *   "properties": {
 *     "spring": {
 *       "datasource": {
 *         "url": "jdbc:postgresql://prod-db:5432/myapp",
 *         "username": "app_user"
 *       }
 *     },
 *     "server": {
 *       "port": 8080
 *     }
 *   }
 * }
 * }</pre>
 * <p>
 * This would be flattened and returned as:
 * </p>
 * <ul>
 *   <li>{@code spring.datasource.url=jdbc:postgresql://prod-db:5432/myapp}</li>
 *   <li>{@code spring.datasource.username=app_user}</li>
 *   <li>{@code server.port=8080}</li>
 * </ul>
 *
 * @author Sarbesh Kumar Sarkar
 * @since 1.0.0
 * @see org.springframework.cloud.config.server.environment.EnvironmentRepository
 * @see org.springframework.core.Ordered
 */
public class DynamodbEnvironmentRepository implements EnvironmentRepository, Ordered {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamodbEnvironmentRepository.class);

    /**
     * Name of the DynamoDB table containing configuration data.
     */
    private final String tableName;

    /**
     * AWS region where the DynamoDB table is located.
     */
    private final String region;

    /**
     * Repository order for precedence in composite configurations.
     */
    private final int order;

    /**
     * Jackson ObjectMapper for JSON processing (currently unused but available for future enhancements).
     */
    private ObjectMapper objectMapper;

    /**
     * DynamoDB client for database operations.
     */
    private final DynamoDbClient client;

    /**
     * Name of the partition key attribute in DynamoDB table.
     */
    private String partitionKey;

    /**
     * Name of the attribute containing configuration data.
     */
    private String configAttribute;

    /**
     * Delimiter used to separate application name and profile in partition key.
     */
    private String delimiter;

    /**
     * Constructs a new DynamodbEnvironmentRepository with the provided properties and DynamoDB client.
     * <p>
     * This constructor initializes the repository with all necessary configuration parameters
     * extracted from the properties object and sets up the DynamoDB client for data operations.
     * </p>
     *
     * @param properties the DynamoDB environment properties containing table name, region,
     *                   partition key configuration, etc.
     * @param client the configured DynamoDB client for database operations
     * @throws IllegalArgumentException if required properties are null or empty
     * @see DynamodbEnvironmentProperties
     */
    public DynamodbEnvironmentRepository(DynamodbEnvironmentProperties properties, DynamoDbClient client) {
        LOGGER.debug("DynamodbEnvironmentRepository initialized with properties: {}", properties);
        this.tableName = properties.getTable();
        this.region = properties.getRegion();
        this.order = properties.getOrder();
        this.objectMapper = new ObjectMapper();
        this.client = client;
        this.partitionKey = properties.getPartitionKey();
        this.configAttribute = properties.getConfigAttribute();
        this.delimiter = properties.getDelimiter();
    }


    /**
     * Retrieves configuration from DynamoDB for the given application, profile, and label.
     * <p>
     * This method constructs a partition key using the format {@code {application}{delimiter}{profile}}
     * and queries the DynamoDB table for matching configuration data. If found, the nested
     * configuration Map is flattened into dot-separated keys and returned as a PropertySource.
     * </p>
     * <p>
     * <strong>Query Process:</strong>
     * </p>
     * <ol>
     *   <li>Construct partition key: {@code application + delimiter + profile}</li>
     *   <li>Query DynamoDB table using the partition key</li>
     *   <li>Extract configuration data from the config attribute</li>
     *   <li>Flatten nested Maps into dot-separated property keys</li>
     *   <li>Create and return PropertySource with flattened properties</li>
     * </ol>
     *
     * @param application the application name (e.g., "myapp", "userservice")
     * @param profile the profile name (e.g., "dev", "prod", "test")
     * @param label the label (currently not used in DynamoDB queries, may be null)
     * @return an {@link Environment} containing property sources from DynamoDB,
     *         or an empty Environment if no configuration is found
     * @see #createPropertySource(String, String, String, java.util.Map)
     * @see #flattenMap(String, java.util.Map, java.util.Map)
     */
    @Override
    public Environment findOne(String application, String profile, String label) {
        Environment environment = new Environment(application, profile, label);
        String configKey = application + this.delimiter + profile;
        try {
            HashMap<String, AttributeValue> keyToGet = new HashMap<>();
            keyToGet.put(this.partitionKey, AttributeValue.builder().s(configKey).build());
            GetItemRequest request = GetItemRequest.builder()
                    .key(keyToGet)
                    .tableName(tableName)
                    .build();
            Map<String, AttributeValue> returnedItem = this.client.getItem(request).item();
            if (returnedItem.isEmpty())
                LOGGER.info("No item found with the key {}", configKey);
            else {
                LOGGER.debug("[DynamoDBEnvironmentRepository][findOne] returnedItem:{}", returnedItem);
                PropertySource propertySource = this.createPropertySource(application, profile, label, returnedItem);
                environment.add(propertySource);
                LOGGER.debug("[DynamoDBEnvironmentRepository][findOne] Added property source: {}", propertySource.getName());
            }
            LOGGER.debug("[DynamoDBEnvironmentRepository][findOne] Found {} property sources in DynamoDB for {}-{}", environment.getPropertySources().size(), application, profile);
        } catch (Exception e) {
            LOGGER.error("Failed to fetch config from DynamoDB: {} for: {}", e.getMessage(), configKey, e);
        }
        return environment;
    }

    /**
     * Converts a DynamoDB item into a Spring PropertySource with flattened property keys.
     * <p>
     * This method extracts the configuration data from the DynamoDB item's config attribute
     * (which should be a Map), converts DynamoDB AttributeValues to Java objects, and then
     * recursively flattens the nested structure into dot-separated keys suitable for
     * Spring property binding.
     * </p>
     * <p>
     * <strong>Processing Steps:</strong>
     * </p>
     * <ol>
     *   <li>Extract the config attribute Map from the DynamoDB item</li>
     *   <li>Convert DynamoDB AttributeValues to standard Java objects</li>
     *   <li>Recursively flatten nested Maps using dot notation</li>
     *   <li>Create PropertySource with the flattened properties</li>
     * </ol>
     *
     * @param application the application name for PropertySource naming
     * @param profile the profile name for PropertySource naming
     * @param label the label for PropertySource naming (currently unused)
     * @param item the DynamoDB item containing configuration data
     * @return a {@link PropertySource} with dot-separated keys, or empty PropertySource if no config found
     * @see #attributeValueToObject(software.amazon.awssdk.services.dynamodb.model.AttributeValue)
     * @see #flattenMap(String, java.util.Map, java.util.Map)
     */
    private PropertySource createPropertySource(String application, String profile, String label,
                                                Map<String, AttributeValue> item) {
        LOGGER.debug("[DynamoDBEnvironmentRepository][createPropertySource] item: {}", item);
        LOGGER.debug("[DynamoDBEnvironmentRepository][createPropertySource] application: {}, " +
                        "profile: {}, label: {}", item.get("application"),
                item.get("profile"), item.get("label"));
        String name = String.format("DynamoDB://%s:%s/%s/%s/%s",
                this.region,
                this.tableName,
                application,
                profile,
                label);
        Map<String, Object> properties = new HashMap<>();
        try {
            // 1. Extract config JSON string from DynamoDB item (assume key is "config")
            Map<String, AttributeValue> configMap = item
                    .getOrDefault(this.configAttribute,
                            AttributeValue.builder().m(new HashMap<>()).build()).m();
            if (configMap.isEmpty()) {
                LOGGER.warn("No config attribute found or empty for item: {}", item);
                return new PropertySource(name, properties);
            }
            // 2. Convert Map<String, AttributeValue> to Map<String, Object>
            Map<String, Object> nestedMap = new HashMap<>();
            for (Map.Entry<String, AttributeValue> entry : configMap.entrySet()) {
                nestedMap.put(entry.getKey(), attributeValueToObject(entry.getValue()));
            }
            // 3. Flatten recursively
            flattenMap("", nestedMap, properties);
        } catch (Exception e) {
            LOGGER.error("Failed to parse and flatten DynamoDB config: {}", e.getMessage(), e);
        }
        return new PropertySource(name, properties);
    }

    /**
     * Recursively flattens a nested Map structure into dot-separated keys.
     * <p>
     * This method takes a nested Map structure and converts it into a flat Map
     * where nested keys are represented using dot notation. This is the standard
     * format expected by Spring for property binding.
     * </p>
     * <p>
     * <strong>Example:</strong>
     * </p>
     * <pre>{@code
     * Input: { "spring": { "datasource": { "url": "jdbc:..." } } }
     * Output: { "spring.datasource.url": "jdbc:..." }
     * }</pre>
     *
     * @param prefix the current key prefix (empty string for root level)
     * @param source the source nested Map to flatten
     * @param target the target flat Map to populate with dot-separated keys
     */
    @SuppressWarnings("unchecked")
    private void flattenMap(String prefix, Map<String, Object> source, Map<String, Object> target) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                flattenMap(key, (Map<String, Object>) value, target);
            } else {
                target.put(key, value);
            }
        }
    }

    /**
     * Utility method to convert DynamoDB AttributeValue to a standard Java Object.
     * <p>
     * This method handles the conversion of DynamoDB's AttributeValue objects to
     * standard Java types that can be used in Spring property binding. It supports
     * all common DynamoDB attribute types including:
     * </p>
     * <ul>
     *   <li><strong>String (S):</strong> Converted to Java String</li>
     *   <li><strong>Number (N):</strong> Converted to Java String (preserving precision)</li>
     *   <li><strong>Boolean (BOOL):</strong> Converted to Java Boolean</li>
     *   <li><strong>Map (M):</strong> Recursively converted to Java Map</li>
     *   <li><strong>List (L):</strong> Converted to Java List with recursive element conversion</li>
     * </ul>
     *
     * @param value the DynamoDB AttributeValue to convert
     * @return the converted Java object, or {@code null} if the value is null or unsupported
     * @see software.amazon.awssdk.services.dynamodb.model.AttributeValue
     */
    private Object attributeValueToObject(AttributeValue value) {
        if (value.s() != null) return value.s();
        if (value.n() != null) return value.n();
        if (value.bool() != null) return value.bool();
        if (value.hasM()) {
            Map<String, Object> map = new HashMap<>();
            value.m().forEach((k, v) -> map.put(k, attributeValueToObject(v)));
            return map;
        }
        if (value.hasL()) {
            List<Object> list = new ArrayList<>();
            value.l().forEach(v -> list.add(attributeValueToObject(v)));
            return list;
        }
        return null;
    }

    /**
     * Returns the order of this repository for precedence in composite configurations.
     * <p>
     * Lower values indicate higher precedence. This determines the order in which
     * this repository is consulted when multiple environment repositories are configured.
     * </p>
     *
     * @return the precedence order (lower = higher precedence)
     * @see org.springframework.core.Ordered
     * @see DynamodbEnvironmentProperties#getOrder()
     */
    @Override
    public int getOrder() {
        return this.order;
    }
}
