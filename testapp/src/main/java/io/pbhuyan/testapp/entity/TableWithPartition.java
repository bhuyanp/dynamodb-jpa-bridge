package io.pbhuyan.testapp.entity;

import io.pbhuyan.dynamodbjpa.entity.DDbEntity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@DynamoDbBean
@Table(name="Test")
@AllArgsConstructor
@NoArgsConstructor
public class TableWithPartition implements DDbEntity {
    private String id;
    private String test;
    @DynamoDbPartitionKey
    public String getId(){
        return id;
    }

}
