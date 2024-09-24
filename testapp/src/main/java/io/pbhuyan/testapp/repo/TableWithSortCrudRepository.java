package io.pbhuyan.testapp.repo;

import io.pbhuyan.dynamodbjpa.repo.DDbCrudRepository;
import io.pbhuyan.testapp.entity.TableWithSort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

@Repository
public class TableWithSortCrudRepository extends DDbCrudRepository<TableWithSort, String, String> {

    @Autowired
    public TableWithSortCrudRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        super(dynamoDbEnhancedClient);
    }
}
