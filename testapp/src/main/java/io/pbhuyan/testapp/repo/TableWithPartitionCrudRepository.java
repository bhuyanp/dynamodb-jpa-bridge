package io.pbhuyan.testapp.repo;

import io.pbhuyan.dynamodbjpa.repo.DDbCrudRepository;
import io.pbhuyan.testapp.entity.TableWithPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

@Repository
public class TableWithPartitionCrudRepository extends DDbCrudRepository<TableWithPartition, String, Void> {

    @Autowired
    public TableWithPartitionCrudRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        super(dynamoDbEnhancedClient);
    }
}
