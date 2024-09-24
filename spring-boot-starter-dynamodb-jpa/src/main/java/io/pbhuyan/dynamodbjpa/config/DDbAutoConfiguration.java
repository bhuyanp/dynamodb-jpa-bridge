package io.pbhuyan.dynamodbjpa.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Slf4j
@AutoConfiguration
@RequiredArgsConstructor
@EnableConfigurationProperties(DDbConfigurationProperty.class)
public class DDbAutoConfiguration {
    private final DDbConfigurationProperty dDbConfigurationProperty;

    @Bean
    DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        log.info("Initializing DynamoDBEnhancedClient");
        String awsRegion = dDbConfigurationProperty.getRegion();
        DDbConfigurationProperty.DynamoDB dynamodb = dDbConfigurationProperty.getDynamodb();
        String dynamoDbRegion = null!=dynamodb?dynamodb.getRegion():"";
        String finalRegion = StringUtils.hasText(dynamoDbRegion)?dynamoDbRegion:awsRegion;
        log.info("DynamoDBClient is connected to region: {}", finalRegion);
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .region(Region.of(finalRegion))
                .build();
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }
}



