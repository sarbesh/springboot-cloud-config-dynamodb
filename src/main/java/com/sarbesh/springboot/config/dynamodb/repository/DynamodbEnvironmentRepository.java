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
 * DynamodbEnvironmentRepository is a custom implementation of Spring Cloud Config's EnvironmentRepository.
 * It uses AWS DynamoDB as the backend to store and retrieve configuration properties.
 */
public class DynamodbEnvironmentRepository implements EnvironmentRepository, Ordered {

    private final Logger LOGGER = LoggerFactory.getLogger(DynamodbEnvironmentRepository.class);

    private final String tableName;
    private final String region;
    private final int order;
    private ObjectMapper objectMapper;
    private final DynamoDbClient client;
    private String partitionKey;
    private String configAttribute;
    private String delimiter;

    /**
     * Constructs a new DynamodbEnvironmentRepository with the provided environment and properties.
     *
     * @param properties  Configuration properties for DynamoDB connection.
     * @param client      Shared DynamoDbClient bean
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
     *
     * @param application Application name
     * @param profile     Profile name
     * @param label       Label (not used)
     * @return Environment containing property sources from DynamoDB
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
     * Converts a DynamoDB item (with a JSON config attribute) into a Spring PropertySource with flattened keys.
     *
     * @param application Application name
     * @param profile     Profile name
     * @param label       Label (not used)
     * @param item        DynamoDB item map
     * @return PropertySource with dot-separated keys
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
     * Recursively flattens a nested map into dot-separated keys.
     *
     * @param prefix Current key prefix
     * @param source Source nested map
     * @param target Target flat map
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

    // Utility method to convert AttributeValue to Object
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
     * Returns the order of this repository (for precedence).
     *
     * @return int order
     */
    @Override
    public int getOrder() {
        return this.order;
    }
}
