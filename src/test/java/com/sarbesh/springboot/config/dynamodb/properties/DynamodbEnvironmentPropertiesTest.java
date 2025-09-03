package com.sarbesh.springboot.config.dynamodb.properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test cases for DynamodbEnvironmentProperties covering property binding,
 * validation, and default values.
 *
 * @author Sarbesh Kumar Sarkar
 */
class DynamodbEnvironmentPropertiesTest {

    private DynamodbEnvironmentProperties properties;

    @BeforeEach
    void setUp() {
        properties = new DynamodbEnvironmentProperties();
    }

    /**
     * Test Case: Default property values
     * Validates that all properties have correct default values.
     */
    @Test
    void shouldHaveCorrectDefaultValues() {
        assertThat(properties.getRegion()).isNull();
        assertThat(properties.getTable()).isNull();
        assertThat(properties.getAccessKey()).isNull();
        assertThat(properties.getSecretKey()).isNull();
        assertThat(properties.getPartitionKey()).isEqualTo("config_id");
        assertThat(properties.getConfigAttribute()).isEqualTo("properties");
        assertThat(properties.getDelimiter()).isEqualTo("-");
        assertThat(properties.getOrder()).isEqualTo(Integer.MAX_VALUE);
        assertThat(properties.getConnectionTimeout()).isEqualTo(10000);
        assertThat(properties.getTimeout()).isEqualTo(50000);
    }

    /**
     * Test Case: Property setters and getters
     * Validates that all setters and getters work correctly.
     */
    @Test
    void shouldSetAndGetPropertiesCorrectly() {
        // Set all properties
        properties.setRegion("eu-west-1");
        properties.setTable("config_store");
        properties.setAccessKey("test-access-key");
        properties.setSecretKey("test-secret-key");
        properties.setPartitionKey("app_config");
        properties.setConfigAttribute("config_data");
        properties.setDelimiter("_");
        properties.setOrder(5);
        properties.setConnectionTimeout(10000);
        properties.setTimeout(30000);

        // Verify all properties
        assertThat(properties.getRegion()).isEqualTo("eu-west-1");
        assertThat(properties.getTable()).isEqualTo("config_store");
        assertThat(properties.getAccessKey()).isEqualTo("test-access-key");
        assertThat(properties.getSecretKey()).isEqualTo("test-secret-key");
        assertThat(properties.getPartitionKey()).isEqualTo("app_config");
        assertThat(properties.getConfigAttribute()).isEqualTo("config_data");
        assertThat(properties.getDelimiter()).isEqualTo("_");
        assertThat(properties.getOrder()).isEqualTo(5);
        assertThat(properties.getConnectionTimeout()).isEqualTo(10000);
        assertThat(properties.getTimeout()).isEqualTo(30000);
    }

    /**
     * Test Case: Credentials detection - both present
     * Validates that hasAccessKeyAndSecretKey returns true when both credentials are present.
     */
    @Test
    void whenBothCredentialsPresent_shouldDetectCredentials() {
        properties.setAccessKey("access-key");
        properties.setSecretKey("secret-key");

        assertThat(properties.hasAccessKeyAndSecretKey()).isTrue();
    }

    /**
     * Test Case: Credentials detection - access key missing
     * Validates that hasAccessKeyAndSecretKey returns false when access key is missing.
     */
    @Test
    void whenAccessKeyMissing_shouldNotDetectCredentials() {
        properties.setSecretKey("secret-key");

        assertThat(properties.hasAccessKeyAndSecretKey()).isFalse();
    }

    /**
     * Test Case: Credentials detection - secret key missing
     * Validates that hasAccessKeyAndSecretKey returns false when secret key is missing.
     */
    @Test
    void whenSecretKeyMissing_shouldNotDetectCredentials() {
        properties.setAccessKey("access-key");

        assertThat(properties.hasAccessKeyAndSecretKey()).isFalse();
    }

    /**
     * Test Case: Credentials detection - both missing
     * Validates that hasAccessKeyAndSecretKey returns false when both credentials are missing.
     */
    @Test
    void whenBothCredentialsMissing_shouldNotDetectCredentials() {
        assertThat(properties.hasAccessKeyAndSecretKey()).isFalse();
    }

    /**
     * Test Case: Credentials detection - empty strings
     * Validates that hasAccessKeyAndSecretKey returns false for empty string credentials.
     */
    @Test
    void whenCredentialsAreEmptyStrings_shouldNotDetectCredentials() {
        properties.setAccessKey("");
        properties.setSecretKey("");

        assertThat(properties.hasAccessKeyAndSecretKey()).isFalse();
    }

    /**
     * Test Case: Credentials detection - whitespace strings
     * Validates that hasAccessKeyAndSecretKey returns false for whitespace-only credentials.
     */
    @Test
    void whenCredentialsAreWhitespace_shouldNotDetectCredentials() {
        properties.setAccessKey("   ");
        properties.setSecretKey("   ");

        assertThat(properties.hasAccessKeyAndSecretKey()).isFalse();
    }

    /**
     * Test Case: Mixed whitespace and valid credentials
     * Validates behavior with mixed valid and invalid credentials.
     */
    @Test
    void whenMixedValidAndInvalidCredentials_shouldNotDetectCredentials() {
        properties.setAccessKey("valid-access-key");
        properties.setSecretKey("   "); // Whitespace secret key

        assertThat(properties.hasAccessKeyAndSecretKey()).isFalse();

        properties.setSecretKey("valid-secret-key");
        properties.setAccessKey(""); // Empty access key

        assertThat(properties.hasAccessKeyAndSecretKey()).isFalse();
    }

    /**
     * Test Case: Property validation for negative timeouts
     * Validates behavior with negative timeout values.
     */
    @Test
    void shouldHandleNegativeTimeouts() {
        properties.setConnectionTimeout(-1000);
        properties.setTimeout(-5000);

        // Properties should accept negative values (validation would be in configuration)
        assertThat(properties.getConnectionTimeout()).isEqualTo(-1000);
        assertThat(properties.getTimeout()).isEqualTo(-5000);
    }

    /**
     * Test Case: Property validation for negative order
     * Validates behavior with negative order values.
     */
    @Test
    void shouldHandleNegativeOrder() {
        properties.setOrder(-5);

        assertThat(properties.getOrder()).isEqualTo(-5);
    }

    /**
     * Test Case: Null safety for string properties
     * Validates that string properties can be set to null.
     */
    @Test
    void shouldHandleNullStringProperties() {
        properties.setRegion("test-region");
        properties.setTable("test-table");
        properties.setAccessKey("test-key");
        properties.setSecretKey("test-secret");
        properties.setPartitionKey("test-partition");
        properties.setConfigAttribute("test-config");
        properties.setDelimiter("test-delimiter");

        // Set all to null
        properties.setRegion(null);
        properties.setTable(null);
        properties.setAccessKey(null);
        properties.setSecretKey(null);
        properties.setPartitionKey(null);
        properties.setConfigAttribute(null);
        properties.setDelimiter(null);

        // Verify all are null
        assertThat(properties.getRegion()).isNull();
        assertThat(properties.getTable()).isNull();
        assertThat(properties.getAccessKey()).isNull();
        assertThat(properties.getSecretKey()).isNull();
        assertThat(properties.getPartitionKey()).isNull();
        assertThat(properties.getConfigAttribute()).isNull();
        assertThat(properties.getDelimiter()).isNull();

        // Credentials should not be detected with null values
        assertThat(properties.hasAccessKeyAndSecretKey()).isFalse();
    }
}
