package io.pbhuyan.testapp.entity;

import io.pbhuyan.dynamodbjpa.entity.DDbEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Data
@DynamoDbBean
@AllArgsConstructor
@NoArgsConstructor
public class TableWithSort implements DDbEntity {
    private String id;
    private String sort;
    private String title;
    @DynamoDbPartitionKey
    public String getId(){
        return id;
    }

    @DynamoDbSortKey
    public String getSort(){
        return sort;
    }
}
