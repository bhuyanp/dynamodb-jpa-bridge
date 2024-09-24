package io.pbhuyan.testapp.repo;

import io.pbhuyan.dynamodbjpa.repo.DDbReadRepository;
import io.pbhuyan.testapp.entity.TableWithPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

@Repository
public class TableWithPartitionReadRepository extends DDbReadRepository<TableWithPartition, String, Void> {
    @Autowired
    public TableWithPartitionReadRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        super(dynamoDbEnhancedClient, true);
    }
}
