package io.pbhuyan.dynamodbjpa.exception.failure;

import io.pbhuyan.dynamodbjpa.exception.DDbConfigException;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

public class DDbConfigFailureAnalyzer
        extends AbstractFailureAnalyzer<DDbConfigException> {
    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, DDbConfigException cause) {
        String action = """
                Consider adding aws.region or aws.dynamodb.region to your application properties.
                    
                If both are present then aws.dynamodb.region will take precedence.
                    
                Provide aws.region when all resources are in the same region.
                Provide aws.dynamodb.region when dynamo db is in a different region that the rest of the services.
                """;
        return new FailureAnalysis(cause.getMessage(), action, cause);
    }
}