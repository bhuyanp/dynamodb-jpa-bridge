package io.pbhuyan.dynamodbjpa.repo;

import io.pbhuyan.dynamodbjpa.entity.DDbEntity;
import io.pbhuyan.dynamodbjpa.exception.DDbRepoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.annotations.NotNull;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
public abstract class DDbReadRepository<T extends DDbEntity, R, S> implements DDbBaseRepository<T, R, S> {

    DynamoDbTable<T> table;
    String entityClassName;
    private String partitionKeyName;
    private String sortKeyName;
    private boolean consistentRead = false;

    public DDbReadRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        initRepository(dynamoDbEnhancedClient);
    }

    private void initRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        Class<T> entityClass = getGenericType(GENERIC_TYPE.entity);
        entityClassName = entityClass.getSimpleName();
        String tableName = getTableName(entityClass);
        table = dynamoDbEnhancedClient.table(tableName, TableSchema.fromClass(entityClass));
        log.info("DynamoDB entity {} is mapped to table {}.", entityClass, tableName);
        partitionKeyName = getPartitionKey(entityClass);
        sortKeyName = getSortKey(entityClass);
    }

    public DDbReadRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient, boolean consistentRead) {
        this.consistentRead = consistentRead;
        initRepository(dynamoDbEnhancedClient);
    }


    /**
     * Fetches all the entities present in the mapped table.
     * <br><br/>
     * Example:
     * <pre>
     * {@code
     *
     * dynamoDbRepository.findAll();
     * }
     * </pre>
     *
     * @return List of all entities.
     */
    public List<T> findAll() {
        PageIterable<T> result = table.scan(r -> r.consistentRead(consistentRead));
        return returnResult(result);
    }

    /**
     * This is a convenience method that creates an instance of the request builder avoiding the need to create one
     * manually via {@link ScanEnhancedRequest#builder()}.
     *
     * <p>
     * Example:
     * <pre>
     * {@code
     *
     * List<MyItem> results = dynamoDbRepository.findAllBy(r -> r.limit(5));
     * }
     * </pre>
     *
     * @param requestConsumer A {@link Consumer} of {@link ScanEnhancedRequest} defining the query conditions and how to
     *                        handle the results.
     * @return an iterator of type {@link SdkIterable} with paginated results (see {@link Page}).
     */
    public List<T> findAllBy(Consumer<ScanEnhancedRequest.Builder> requestConsumer) {
        PageIterable<T> result = table.scan(requestConsumer.andThen(r -> r.consistentRead(consistentRead)));
        return returnResult(result);
    }

    /**
     * Fetches all entities matching the partition key when sort key is present.
     * <br><br/>
     * Example:
     * <pre>
     * {@code
     *
     * dynamoDbRepository.findAllBy("partitionKey");
     * }
     * </pre>
     *
     * @return List of entities matching the partition key
     */
    public List<T> findAllBy(@NotNull R partitionKey) {
        Expression filterExpression = Expression.builder()
                .expression("#a = :b")
                .putExpressionName("#a", partitionKeyName)
                .putExpressionValue(":b", getKeyAttributeValue(partitionKey))
                .build();

        PageIterable<T> result = table.scan(r -> r
                .filterExpression(filterExpression)
                .consistentRead(consistentRead));
        return returnResult(result);
    }


    /**
     * Fetches the entity matching the partition key. DDbRepoException will be thrown if a
     * sort key is detected in the entity class.
     * <br><br/>
     * Example:
     * <pre>
     * {@code
     *
     * dynamoDbRepository.findBy("partitionKey");
     * }
     * </pre>
     *
     * @param partitionKey of the entity.
     * @return Optional of the entity matching the partition key.
     */
    public Optional<T> findBy(@NotNull R partitionKey) {
        if (StringUtils.hasText(sortKeyName)) {
            throw new DDbRepoException("""
                                        
                    Reason:
                        Sort key detected in the entity class %s.
                                        
                    Recommended Action:
                        For entities with sort key use following method
                                
                            public Optional<T> findBy(@NotNull R partitionKey, @NotNull S sortKey);
                        
                        instead of
                                        
                            public Optional<T> findBy(@NotNull R partitionKey);
                    """.formatted(entityClassName));

        }
        T item = table.getItem(r -> r
                .consistentRead(consistentRead)
                .key(getKey(partitionKey)));
        return Optional.ofNullable(item);
    }

    /**
     * Fetches the entity matching the partition key and sort key. DDbRepoException will be thrown if a
     * sort key is missing in the entity class.
     * <br><br/>
     * Example:
     * <pre>
     * {@code
     *
     * dynamoDbRepository.findBy("partitionKey", "sortKey");
     * }
     * </pre>
     *
     * @param partitionKey of the entity.
     * @param sortKey      of the entity.
     * @return Optional of the entity matching the partition key and sort key.
     */
    public Optional<T> findBy(@NotNull R partitionKey, @NotNull S sortKey) {
        if (!StringUtils.hasText(sortKeyName)) {
            throw new DDbRepoException("""
                                        
                    Reason:
                        Sort key is missing in the entity class %s.
                                        
                    Recommended Action:
                        For entities without sort key use following method
                                
                            public Optional<T> findBy(@NotNull R partitionKey);
                        
                        instead of
                                        
                            public Optional<T> findBy(@NotNull R partitionKey, @NotNull S sortKey);
                    """.formatted(entityClassName));

        }
        T item = table.getItem(r -> r
                .consistentRead(consistentRead)
                .key(getKey(partitionKey, sortKey)));
        return Optional.ofNullable(item);
    }

    /**
     * Checks if the entity exists for a given partition key. If the entity has a sort
     * key then this method will throw DDbRepoException.
     * <br><br/>
     * Example:
     * <pre>
     * {@code
     *
     * dynamoDbRepository.existsBy("partitionKey");
     * }
     * </pre>
     *
     * @param partitionKey of the entity.
     * @return true if the entity exists.
     */
    public boolean existsBy(@NotNull R partitionKey) {
        return findBy(partitionKey).isPresent();
    }

    /**
     * Checks if the entity exists for a given partition key and sort key. If the entity does not have a sort
     * key then this method will throw DDbRepoException.
     * <br><br/>
     * Example:
     * <pre>
     * {@code
     *
     * dynamoDbRepository.existsBy("partitionKey", "sortKey");
     * }
     * </pre>
     *
     * @param partitionKey of the entity.
     * @param sortKey      of the entity.
     * @return true if the entity exists.
     */
    public boolean existsBy(@NotNull R partitionKey, @NotNull S sortKey) {
        return findBy(partitionKey, sortKey).isPresent();
    }


}
