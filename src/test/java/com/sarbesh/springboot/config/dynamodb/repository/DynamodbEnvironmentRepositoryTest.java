package com.sarbesh.springboot.config.dynamodb.repository;

import com.sarbesh.springboot.config.dynamodb.properties.DynamodbEnvironmentProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Test cases for DynamodbEnvironmentRepository covering repository operations
 * with mocked DynamoDB interactions.
 *
 * @author Sarbesh Kumar Sarkar
 */
@ExtendWith(MockitoExtension.class)
class DynamodbEnvironmentRepositoryTest {

    @Mock
    private DynamoDbClient mockDynamoDbClient;

    private DynamodbEnvironmentProperties properties;
    private DynamodbEnvironmentRepository repository;

    @BeforeEach
    void setUp() {
        properties = new DynamodbEnvironmentProperties();
        properties.setRegion("us-east-1");
        properties.setTable("config_table");
        properties.setPartitionKey("config_id");
        properties.setConfigAttribute("properties");
        properties.setDelimiter("-");

        repository = new DynamodbEnvironmentRepository(properties, mockDynamoDbClient);
    }

    /**
     * Test Case: Successful configuration retrieval
     * Validates that repository can successfully retrieve and flatten configuration.
     */
    @Test
    void whenConfigExists_shouldReturnEnvironmentWithProperties() {
        // Arrange
        String application = "myapp";
        String profile = "dev";
        String label = "master";

        Map<String, AttributeValue> configData = new HashMap<>();
        configData.put("database.host", AttributeValue.builder().s("localhost").build());
        configData.put("database.port", AttributeValue.builder().n("5432").build());
        configData.put("app.name", AttributeValue.builder().s("My Application").build());
        configData.put("feature.enabled", AttributeValue.builder().bool(true).build());

        GetItemResponse mockResponse = GetItemResponse.builder()
                .item(Map.of(
                        "config_id", AttributeValue.builder().s("myapp-dev").build(),
                        "properties", AttributeValue.builder().m(configData).build()
                ))
                .build();

        when(mockDynamoDbClient.getItem(any(GetItemRequest.class))).thenReturn(mockResponse);

        // Act
        Environment environment = repository.findOne(application, profile, label);

        // Assert
        assertThat(environment).isNotNull();
        assertThat(environment.getName()).isEqualTo(application);
        assertThat(environment.getProfiles()).contains(profile);
        assertThat(environment.getLabel()).isEqualTo(label);
        assertThat(environment.getPropertySources()).hasSize(1);

        PropertySource propertySource = environment.getPropertySources().get(0);
        assertThat(propertySource.getName()).contains("DynamoDB://");

        Map<?, ?> source = propertySource.getSource();
        assertThat(source.get("database.host")).isEqualTo("localhost");
        assertThat(source.get("database.port")).isEqualTo("5432");
        assertThat(source.get("app.name")).isEqualTo("My Application");
        assertThat(source.get("feature.enabled")).isEqualTo(true);
    }

    /**
     * Test Case: Configuration not found
     * Validates that repository handles missing configuration gracefully.
     */
    @Test
    void whenConfigNotFound_shouldReturnEmptyEnvironment() {
        // Arrange
        GetItemResponse mockResponse = GetItemResponse.builder().build(); // Empty response
        when(mockDynamoDbClient.getItem(any(GetItemRequest.class))).thenReturn(mockResponse);

        // Act
        Environment environment = repository.findOne("nonexistent", "prod", "master");

        // Assert
        assertThat(environment).isNotNull();
        assertThat(environment.getName()).isEqualTo("nonexistent");
        assertThat(environment.getProfiles()).contains("prod");
        assertThat(environment.getLabel()).isEqualTo("master");
        assertThat(environment.getPropertySources()).isEmpty();
    }

    /**
     * Test Case: Custom delimiter configuration
     * Validates that repository uses custom delimiter for config ID construction.
     */
    @Test
    void whenCustomDelimiter_shouldUseCustomDelimiterForConfigId() {
        // Arrange
        properties.setDelimiter("_");
        repository = new DynamodbEnvironmentRepository(properties, mockDynamoDbClient);

        Map<String, AttributeValue> configData = new HashMap<>();
        configData.put("custom.property", AttributeValue.builder().s("custom-value").build());

        GetItemResponse mockResponse = GetItemResponse.builder()
                .item(Map.of(
                        "config_id", AttributeValue.builder().s("app_staging").build(),
                        "properties", AttributeValue.builder().m(configData).build()
                ))
                .build();

        when(mockDynamoDbClient.getItem(any(GetItemRequest.class))).thenReturn(mockResponse);

        // Act
        Environment environment = repository.findOne("app", "staging", "master");

        // Assert
        assertThat(environment.getPropertySources()).hasSize(1);
        PropertySource propertySource = environment.getPropertySources().get(0);
        assertThat(propertySource.getName()).contains("DynamoDB://");
        assertThat(propertySource.getSource().get("custom.property")).isEqualTo("custom-value");
    }

    /**
     * Test Case: Complex nested configuration
     * Validates that repository handles nested configuration structures.
     */
    @Test
    void whenNestedConfig_shouldFlattenCorrectly() {
        // Arrange
        Map<String, AttributeValue> nestedConfig = new HashMap<>();
        nestedConfig.put("host", AttributeValue.builder().s("db-server").build());
        nestedConfig.put("port", AttributeValue.builder().n("3306").build());

        Map<String, AttributeValue> configData = new HashMap<>();
        configData.put("database", AttributeValue.builder().m(nestedConfig).build());
        configData.put("app.version", AttributeValue.builder().s("1.0.0").build());

        GetItemResponse mockResponse = GetItemResponse.builder()
                .item(Map.of(
                        "config_id", AttributeValue.builder().s("myapp-prod").build(),
                        "properties", AttributeValue.builder().m(configData).build()
                ))
                .build();

        when(mockDynamoDbClient.getItem(any(GetItemRequest.class))).thenReturn(mockResponse);

        // Act
        Environment environment = repository.findOne("myapp", "prod", "master");

        // Assert
        PropertySource propertySource = environment.getPropertySources().get(0);
        Map<?, ?> source = propertySource.getSource();

        assertThat(source.get("database.host")).isEqualTo("db-server");
        assertThat(source.get("database.port")).isEqualTo("3306");
        assertThat(source.get("app.version")).isEqualTo("1.0.0");
    }

    /**
     * Test Case: DynamoDB exception handling
     * Validates that repository handles DynamoDB exceptions gracefully.
     */
    @Test
    void whenDynamoDbThrowsException_shouldHandleGracefully() {
        // Arrange
        when(mockDynamoDbClient.getItem(any(GetItemRequest.class)))
                .thenThrow(ResourceNotFoundException.builder()
                        .message("Table not found")
                        .build());

        // Act - Repository should handle exception gracefully and return empty environment
        Environment environment = repository.findOne("app", "prod", "master");

        // Assert - Should return empty environment instead of throwing
        assertThat(environment).isNotNull();
        assertThat(environment.getName()).isEqualTo("app");
        assertThat(environment.getProfiles()).contains("prod");
        assertThat(environment.getLabel()).isEqualTo("master");
        assertThat(environment.getPropertySources()).isEmpty();
    }

    /**
     * Test Case: Custom partition key and config attribute
     * Validates that repository uses custom partition key and config attribute names.
     */
    @Test
    void whenCustomKeys_shouldUseCustomPartitionKeyAndConfigAttribute() {
        // Arrange
        properties.setPartitionKey("app_config");
        properties.setConfigAttribute("config_data");
        repository = new DynamodbEnvironmentRepository(properties, mockDynamoDbClient);

        Map<String, AttributeValue> configData = new HashMap<>();
        configData.put("custom.setting", AttributeValue.builder().s("custom-value").build());

        GetItemResponse mockResponse = GetItemResponse.builder()
                .item(Map.of(
                        "app_config", AttributeValue.builder().s("service-test").build(),
                        "config_data", AttributeValue.builder().m(configData).build()
                ))
                .build();

        when(mockDynamoDbClient.getItem(any(GetItemRequest.class))).thenReturn(mockResponse);

        // Act
        Environment environment = repository.findOne("service", "test", "master");

        // Assert
        PropertySource propertySource = environment.getPropertySources().get(0);
        assertThat(propertySource.getSource().get("custom.setting")).isEqualTo("custom-value");
    }

    /**
     * Test Case: Repository constructor validation
     * Validates that repository constructor handles null parameters properly.
     */
    @Test
    void whenNullParameters_shouldThrowException() {
        // Act & Assert - Only null properties cause NPE when accessing properties.getTable()
        assertThatThrownBy(() -> new DynamodbEnvironmentRepository(null, mockDynamoDbClient))
                .isInstanceOf(NullPointerException.class);

        // Note: Null client doesn't throw exception - constructor just assigns null
        // This matches the actual implementation which doesn't validate client parameter
        DynamodbEnvironmentRepository repositoryWithNullClient =
                new DynamodbEnvironmentRepository(properties, null);
        assertThat(repositoryWithNullClient).isNotNull();
    }
}
