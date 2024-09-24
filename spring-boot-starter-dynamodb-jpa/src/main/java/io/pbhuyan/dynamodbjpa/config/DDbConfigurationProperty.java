package io.pbhuyan.dynamodbjpa.config;

import io.micrometer.common.util.StringUtils;
import io.pbhuyan.dynamodbjpa.exception.DDbConfigException;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "aws")
public class DDbConfigurationProperty implements InitializingBean {
    private String region;
    private DynamoDB dynamodb;

    @Override
    public void afterPropertiesSet() {
        if (StringUtils.isBlank(region) &&
                (null == dynamodb || StringUtils.isBlank(dynamodb.getRegion()))) {
            throw new DDbConfigException("AWS region is missing.");
        }
    }

    @Data
    public static class DynamoDB {
        private String region;
    }
}
