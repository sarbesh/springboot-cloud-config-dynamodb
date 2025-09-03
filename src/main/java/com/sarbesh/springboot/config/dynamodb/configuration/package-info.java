/**
 * Auto-configuration classes for Spring Cloud Config DynamoDB integration.
 * <p>
 * This package contains the Spring Boot auto-configuration classes that enable
 * seamless integration of DynamoDB as a Spring Cloud Config Server backend.
 * </p>
 *
 * <h2>Key Classes</h2>
 * <ul>
 *   <li>{@link com.sarbesh.springboot.config.dynamodb.configuration.DynamodbEnvironmentRepositoryAutoConfiguration}
 *       - Main auto-configuration class for regular Spring context</li>
 *   <li>{@link com.sarbesh.springboot.config.dynamodb.configuration.DynamodbEnvironmentRepositoryAutoConfiguration.DynamodbBootstrapConfiguration}
 *       - Bootstrap configuration class for early factory registration</li>
 * </ul>
 *
 * <h2>Auto-Configuration Features</h2>
 * <ul>
 *   <li><strong>Conditional Bean Creation</strong>: Beans are created only when required classes are present</li>
 *   <li><strong>Properties Binding</strong>: Automatic binding of {@code spring.cloud.config.server.dynamodb.*} properties</li>
 *   <li><strong>DynamoDB Client Configuration</strong>: Automatic creation of DynamoDB client with credentials and region</li>
 *   <li><strong>Bootstrap Support</strong>: Early factory registration for bootstrap context compatibility</li>
 * </ul>
 *
 * <h2>Configuration Order</h2>
 * <ol>
 *   <li>Bootstrap Configuration registers factory early in bootstrap context</li>
 *   <li>Main Auto-Configuration creates beans in regular Spring context</li>
 *   <li>Environment Repository Factory builds repository instances as needed</li>
 * </ol>
 *
 * <h2>Spring Factories Integration</h2>
 * <p>
 * The bootstrap configuration is registered via {@code META-INF/spring.factories}:
 * </p>
 * <pre>
 * org.springframework.cloud.bootstrap.BootstrapConfiguration=\
 * com.sarbesh.springboot.config.dynamodb.configuration.DynamodbEnvironmentRepositoryAutoConfiguration$DynamodbBootstrapConfiguration
 * </pre>
 *
 * @author Sarbesh Kumar Sarkar
 * @see com.sarbesh.springboot.config.dynamodb.properties.DynamodbEnvironmentProperties
 * @see com.sarbesh.springboot.config.dynamodb.factory.DynamodbEnvironmentRepositoryFactory
 * @since 1.0.0
 */
package com.sarbesh.springboot.config.dynamodb.configuration;
