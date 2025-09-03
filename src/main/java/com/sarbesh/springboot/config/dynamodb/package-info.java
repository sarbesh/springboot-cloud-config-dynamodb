/**
 * Spring Cloud Config DynamoDB Integration Library.
 * <p>
 * This package provides a complete Spring Cloud Config Server integration for AWS DynamoDB,
 * enabling configuration storage and retrieval from DynamoDB tables with full bootstrap context support.
 * </p>
 *
 * <h2>Key Features</h2>
 * <ul>
 *   <li>Bootstrap context support with {@code spring.cloud.config.server.bootstrap=true}</li>
 *   <li>Composite environment repository configuration alongside other backends</li>
 *   <li>Property-driven configuration via {@code spring.cloud.config.server.dynamodb.*}</li>
 *   <li>AWS SDK v2 integration with configurable credentials and regions</li>
 *   <li>Nested property flattening for Spring Boot compatibility</li>
 * </ul>
 *
 * <h2>Quick Start</h2>
 * <p>
 * Add the dependency to your Spring Cloud Config Server:
 * </p>
 * <pre>{@code
 * <dependency>
 *     <groupId>com.sarbesh.springboot</groupId>
 *     <artifactId>spring-cloud-config-dynamodb</artifactId>
 *     <version>1.0.0</version>
 * </dependency>
 * }</pre>
 *
 * <p>
 * Configure bootstrap.yml:
 * </p>
 * <pre>{@code
 * spring:
 *   cloud:
 *     config:
 *       server:
 *         bootstrap: true
 *         composite:
 *           - type: dynamodb
 *             region: us-east-1
 *             table: config_table
 *             order: 1
 * }</pre>
 *
 * <h2>Package Structure</h2>
 * <ul>
 *   <li>{@link com.sarbesh.springboot.config.dynamodb.configuration} - Auto-configuration classes</li>
 *   <li>{@link com.sarbesh.springboot.config.dynamodb.factory} - Environment repository factory</li>
 *   <li>{@link com.sarbesh.springboot.config.dynamodb.properties} - Configuration properties</li>
 *   <li>{@link com.sarbesh.springboot.config.dynamodb.repository} - DynamoDB repository implementation</li>
 * </ul>
 *
 * <h2>DynamoDB Table Structure</h2>
 * <p>
 * The library expects a DynamoDB table with the following structure:
 * </p>
 * <ul>
 *   <li><strong>Partition Key</strong>: String attribute (default: {@code config_id})</li>
 *   <li><strong>Configuration Attribute</strong>: Map attribute (default: {@code properties})</li>
 *   <li><strong>Key Format</strong>: {@code {application}-{profile}} (configurable delimiter)</li>
 * </ul>
 *
 * @author Sarbesh Kumar Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
package com.sarbesh.springboot.config.dynamodb;
