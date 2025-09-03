/**
 * Configuration properties classes for DynamoDB environment repository settings.
 * <p>
 * This package contains the configuration properties classes that enable
 * external configuration of the DynamoDB environment repository through
 * Spring Boot's configuration property binding mechanism.
 * </p>
 *
 * <h2>Configuration Properties</h2>
 * <p>
 * The {@link com.sarbesh.springboot.config.dynamodb.properties.DynamodbEnvironmentProperties}
 * class binds properties with the prefix {@code spring.cloud.config.server.dynamodb.*}
 * and implements {@code EnvironmentRepositoryProperties} for Spring Cloud Config integration.
 * </p>
 *
 * <h2>Property Categories</h2>
 * <ul>
 *   <li><strong>AWS Configuration</strong>: region, access-key, secret-key</li>
 *   <li><strong>DynamoDB Table</strong>: table, partition-key, config-attribute</li>
 *   <li><strong>Application Mapping</strong>: delimiter for application-profile keys</li>
 *   <li><strong>Connection Settings</strong>: connection-timeout, timeout</li>
 *   <li><strong>Repository Order</strong>: order for composite configurations</li>
 * </ul>
 *
 * <h2>Example Configuration</h2>
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
 *           connection-timeout: 5000
 *           timeout: 10000
 * }</pre>
 *
 * <h2>Security Considerations</h2>
 * <p>
 * Sensitive properties like AWS credentials should be provided via:
 * </p>
 * <ul>
 *   <li>Environment variables</li>
 *   <li>External configuration files</li>
 *   <li>AWS IAM roles (preferred)</li>
 *   <li>AWS credential providers</li>
 * </ul>
 *
 * <h2>Property Validation</h2>
 * <p>
 * The properties class includes validation for:
 * </p>
 * <ul>
 *   <li>Required properties (region, table)</li>
 *   <li>Optional credentials (access-key, secret-key as pair)</li>
 *   <li>Default values for optional settings</li>
 *   <li>Timeout ranges and connection limits</li>
 * </ul>
 *
 * @author Sarbesh Kumar Sarkar
 * @see org.springframework.boot.context.properties.ConfigurationProperties
 * @see org.springframework.cloud.config.server.support.EnvironmentRepositoryProperties
 * @since 1.0.0
 */
package com.sarbesh.springboot.config.dynamodb.properties;
