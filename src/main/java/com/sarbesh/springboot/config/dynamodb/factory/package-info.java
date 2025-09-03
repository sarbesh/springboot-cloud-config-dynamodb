/**
 * Factory classes for creating DynamoDB environment repository instances.
 * <p>
 * This package contains the factory implementation that creates and configures
 * DynamoDB environment repository instances based on provided configuration properties.
 * </p>
 *
 * <h2>Factory Pattern Implementation</h2>
 * <p>
 * The {@link com.sarbesh.springboot.config.dynamodb.factory.DynamodbEnvironmentRepositoryFactory}
 * implements the Spring Cloud Config {@code EnvironmentRepositoryFactory} interface,
 * enabling integration with the composite environment repository system.
 * </p>
 *
 * <h2>Key Features</h2>
 * <ul>
 *   <li><strong>Dual Context Support</strong>: Supports both bootstrap and regular Spring contexts</li>
 *   <li><strong>DynamoDB Client Integration</strong>: Configures repository with appropriate DynamoDB client</li>
 *   <li><strong>Properties-Based Configuration</strong>: Creates repositories based on provided properties</li>
 *   <li><strong>Lazy Initialization</strong>: Repository instances created on-demand</li>
 * </ul>
 *
 * <h2>Usage in Bootstrap Context</h2>
 * <p>
 * In bootstrap context, the factory is created with a pre-configured DynamoDB client:
 * </p>
 * <pre>{@code
 * @Bean
 * public DynamodbEnvironmentRepositoryFactory dynamodbEnvironmentRepositoryFactory(
 *         @Qualifier("bootstrapDynamodbClient") DynamoDbClient dynamoDbClient) {
 *     return new DynamodbEnvironmentRepositoryFactory(dynamoDbClient);
 * }
 * }</pre>
 *
 * <h2>Usage in Regular Context</h2>
 * <p>
 * In regular Spring context, the factory resolves the DynamoDB client from application context:
 * </p>
 * <pre>{@code
 * @Bean
 * public DynamodbEnvironmentRepositoryFactory dynamodbEnvironmentRepositoryFactory() {
 *     return new DynamodbEnvironmentRepositoryFactory();
 * }
 * }</pre>
 *
 * <h2>Repository Creation</h2>
 * <p>
 * The factory creates repository instances with full configuration:
 * </p>
 * <ul>
 *   <li>DynamoDB client (injected or resolved)</li>
 *   <li>Table name and structure configuration</li>
 *   <li>AWS credentials and region settings</li>
 *   <li>Connection timeouts and retry policies</li>
 *   <li>Repository ordering for composite setups</li>
 * </ul>
 *
 * @author Sarbesh Kumar Sarkar
 * @see com.sarbesh.springboot.config.dynamodb.repository.DynamodbEnvironmentRepository
 * @see com.sarbesh.springboot.config.dynamodb.properties.DynamodbEnvironmentProperties
 * @since 1.0.0
 */
package com.sarbesh.springboot.config.dynamodb.factory;
