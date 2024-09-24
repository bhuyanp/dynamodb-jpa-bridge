package io.pbhuyan.dynamodbjpa.repo;

import io.pbhuyan.dynamodbjpa.entity.DDbEntity;
import io.pbhuyan.dynamodbjpa.exception.DDbRepoException;
import jakarta.persistence.Table;
import org.apache.logging.log4j.util.Strings;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.stream.Stream;

public interface DDbBaseRepository<T extends DDbEntity, R, S>{

    List<Class> ALLOWED_PARTITION_KEY_TYPES = List.of(
            String.class, Integer.class, Long.class, Double.class, Boolean.class
    );

    List<Class> ALLOWED_SORT_KEY_TYPES = List.of(
            String.class, Integer.class, Long.class, Double.class, Boolean.class, Void.class
    );

    String ALLOWED_PARTITION_KEY_TYPES_STR = ALLOWED_PARTITION_KEY_TYPES.stream()
            .map(Class::getSimpleName).reduce((a, b) -> a + "\n" + b).get();

    String ALLOWED_SORT_KEY_TYPES_STR = ALLOWED_SORT_KEY_TYPES.stream()
            .map(Class::getSimpleName).reduce((a, b) -> a + "\n" + b).get();

    default AttributeValue getKeyAttributeValue(Object key) {
        AttributeValue.Builder builder = AttributeValue.builder();
        return switch (key) {
            case String s -> builder.s(s).build();
            case Integer i -> builder.n(i.toString()).build();
            case Long l -> builder.n(l.toString()).build();
            case Double d -> builder.n(d.toString()).build();
            case Boolean b -> builder.bool(b).build();
            default -> throw new UnsupportedOperationException(key.getClass().getSimpleName() +
                    " is not supported. Allowed key types are " + ALLOWED_PARTITION_KEY_TYPES);
        };
    }

    enum GENERIC_TYPE {
        entity, partition_key, sort_key
    }

    default Class<T> getGenericType(GENERIC_TYPE genericType) {
        int typeIndex = switch (genericType) {
            case entity -> 0;
            case partition_key -> 1;
            case sort_key -> 2;
        };
        return (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[typeIndex];
    }

    default Key getKey(R partitionKey) {
        AttributeValue partitionKeyValue = getKeyAttributeValue(partitionKey);
        return Key.builder()
                .partitionValue(partitionKeyValue).build();
    }


    default Key getKey(R partitionKey, S sortKey) {
        AttributeValue partitionKeyValue = getKeyAttributeValue(partitionKey);
        AttributeValue sortKeyValue = getKeyAttributeValue(sortKey);
        return Key.builder()
                .partitionValue(partitionKeyValue)
                .sortValue(sortKeyValue)
                .build();
    }



    default List<T> returnResult(PageIterable<T> result) {
        return result.items().stream().toList();
    }






    default String getTableName(Class<T> entityClass) {
        Table table = entityClass.getAnnotation(Table.class);
        return null == table ? entityClass.getSimpleName() : table.name();
    }


    default String getPartitionKey(Class<T> entityClass) {
        Method partitionKeyMethod = Stream.of(entityClass.getMethods())
                .filter(m -> m.getName().startsWith("get") && m.getAnnotation(DynamoDbPartitionKey.class) != null)
                .findAny().orElseThrow(() -> new IllegalStateException("DynamoDbPartitionKey is not defined in the entity class. Annotate the get method returning the partition key with DynamoDbPartitionKey."));
        Class<T> partitionKeyType = getGenericType(GENERIC_TYPE.partition_key);
        if (!ALLOWED_PARTITION_KEY_TYPES.contains(partitionKeyType)) {
            throw new DDbRepoException("Incorrect type " + partitionKeyType.getSimpleName() + " used as repository partition key.",
                    """
                            Please supply one from the allowed partition key types:
                            %s
                            """.formatted(ALLOWED_PARTITION_KEY_TYPES_STR));
        }

        if (partitionKeyMethod.getReturnType() != partitionKeyType) {
            throw new DDbRepoException("""
                    DynamoDbPartitionKey type mismatch between the entity class and repo class.
                    Partition Key Type(In Entity): %s
                    Partition Key Type(In Repo): %s
                    """.formatted(partitionKeyMethod.getReturnType().getSimpleName(), partitionKeyType.getSimpleName()),
                    """
                            Make sure both types match and one from the following types:
                            %s
                            """.formatted(ALLOWED_PARTITION_KEY_TYPES_STR));
        }

        return Introspector.decapitalize(partitionKeyMethod.getName().substring(3));
    }


    default String getSortKey(Class<T> entityClass) {
        Class<T> sortKeyType = getGenericType(GENERIC_TYPE.sort_key);
        //Sort key marked as void meaning the table has no sort key
        if (!sortKeyType.getName().equalsIgnoreCase(Void.class.getName())) {
            Method sortKeyMethod = Stream.of(entityClass.getMethods())
                    .filter(m -> m.getName().startsWith("get") && m.getAnnotation(DynamoDbSortKey.class) != null)
                    .findAny().orElseThrow(() ->
                            new DDbRepoException("DynamoDbSortKey is not defined in the entity class.",
                                    """
                                            Annotate the get method returning the sort key with DynamoDbSortKey.
                                                                                
                                            If sort key type is set to Void then no need to have a sort key field and it's corresponding get method in the entity class.
                                            """));
            if (!ALLOWED_SORT_KEY_TYPES.contains(sortKeyType)) {
                throw new DDbRepoException("Incorrect type " + sortKeyType.getSimpleName() + " used as repository sort key.",
                        """
                                Please supply one from the allowed sort key types:
                                %s
                                """.formatted(ALLOWED_SORT_KEY_TYPES_STR));
            }
            if (sortKeyMethod.getReturnType() != sortKeyType) {
                throw new IllegalStateException("DynamoDbSortKey type mismatch between the entity class and repo class. Make sure both types match and one of the following types " + ALLOWED_PARTITION_KEY_TYPES);
            }
            return Introspector.decapitalize(sortKeyMethod.getName().substring(3));
        }
        return Strings.EMPTY;
    }
}
