package io.pbhuyan.dynamodbjpa.repo;

import io.pbhuyan.dynamodbjpa.entity.DDbEntity;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.annotations.NotNull;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

import java.util.NoSuchElementException;

@Slf4j
public abstract class DDbCrudRepository<T extends DDbEntity, R, S> extends DDbReadRepository<T, R, S>{



    public DDbCrudRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        super(dynamoDbEnhancedClient);
    }
    public DDbCrudRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient, boolean consistentRead) {
        super(dynamoDbEnhancedClient, consistentRead);
    }




    /**
     * Puts a single item in the mapped table. If the table contains an item with the same primary key, it will be
     * replaced with this item.
     * <br><br/>
     * Example:
     * <pre>
     * {@code
     *
     * dynamoDbRepository.save(new Customer("Prasanta Bhuyan"));
     * }
     * </pre>
     *
     * @param entity to be inserted into or overwritten in the database table.
     */
    public void save(@NotNull T entity) {
        table.putItem(entity);
    }

    /**
     * Puts all the supplied items in the mapped table. If the table contains an item with the same primary key, it will be
     * replaced with this item.
     * <br><br/>
     * Example:
     * <pre>
     * {@code
     *
     * dynamoDbRepository.saveAll(List.of(
     *      new Customer("Prasanta Bhuyan"),
     *      new Customer("Prasanta B"),
     * ));
     * }
     * </pre>
     *
     * @param entities Iterable of entities.
     */
    public void saveAll(@NotNull Iterable<T> entities) {
        entities.forEach(table::putItem);
    }


    /**
     * Deletes a single item from the mapped table.
     * <br><br/>
     * Example:
     * <pre>
     * {@code
     *
     * dynamoDbRepository.delete(new Customer("Prasanta Bhuyan"));
     * }
     * </pre>
     *
     * @param entity to be deleted from the database table.
     */
    public void delete(@NotNull T entity) {
        table.deleteItem(entity);
    }

    /**
     * Deletes a single item from the mapped table matching the partitionKey.
     * NoSuchElementException is thrown if no matching entity found.
     * <br><br/>
     * Example:
     * <pre>
     * {@code
     *
     * dynamoDbRepository.delete("partitionKey");
     * }
     * </pre>
     *
     * @param partitionKey of the entity.
     */
    public void delete(@NotNull R partitionKey) {
        T entity = findBy(partitionKey).orElseThrow(() ->
                new NoSuchElementException("""
                        No %s found matching the partitionKey %s""".formatted(entityClassName, partitionKey)));
        table.deleteItem(entity);
    }

    /**
     * Deletes a single item from the mapped table matching the partitionKey and sortKey.
     * NoSuchElementException is thrown if no matching entity found.
     * <br><br/>
     * Example:
     * <pre>
     * {@code
     *
     * dynamoDbRepository.delete("partitionKey");
     * }
     * </pre>
     *
     * @param partitionKey of the entity.
     * @param sortKey of the entity.
     */
    public void delete(@NotNull R partitionKey, S sortKey) {
        T entity = findBy(partitionKey, sortKey).orElseThrow(() ->
                new NoSuchElementException("""
                        No %s found matching the partitionKey %s and sortKey %s
                """.formatted(entityClassName, partitionKey, sortKey)));
        table.deleteItem(entity);
    }

    /**
     * Deletes all the items from the mapped table.
     * <br><br/>
     * Example:
     * <pre>
     * {@code
     *
     * dynamoDbRepository.delete();
     * }
     * </pre>
     *
     */
    public void delete() {
        findAll().forEach(table::deleteItem);
    }

    /**
     * Deletes all the supplied items from the mapped table.
     * <br><br/>
     * Example:
     * <pre>
     * {@code
     *
     * dynamoDbRepository.delete(List.of(
     *      new Entity("partitionKey1"),
     *      new Entity("partitionKey2")
     * ));
     * }
     * </pre>
     *
     * @param entities Iterable of entities
     */
    public void delete(@NotNull Iterable<T> entities) {
        entities.forEach(table::deleteItem);
    }













}
