# SpringBoot DynamoDB JPA Starter

## Background
DynamoDB is a very popular and fully managed NoSQL database from AWS. SpringBoot developers
are familiar with Spring Data Jpa or Spring Data Mongo. But unfortunately there is nothing like
this for DynamoDB. AWS provides it's own Enhanced DB Client. But that is not in familiar Spring Data pattern.
This project tries to bridge the gap and give us a more familar JPA style
DynamoDB client.

Enhanced DB Client: https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBEnhanced.html


PRs are welcome.


## BOM
- Java21
- SpringBoot 3.3.3
- DynamoDB Enhanced Client 2.26.29
  - Commons Logging excluded as it conflicts with Spring's own implementation of commons logging spring-jcl


## Getting Started

### Build
You will have to temporarily build it like this. 

```
cd spring-boot-starter-dynamodb-jpa

mvn clean install
```


Once we push the library to Maven repo, you can directly get it from there.

### Add to your project
You will have to temporarily build it like this.

```
<dependency>
    <groupId>io.pbhuyan</groupId>
    <artifactId>spring-boot-starter-dynamodb-jpa</artifactId>
    <version>${spring-boot-starter-dynamodb-jpa.version}</version>
</dependency>
```

### Configuration
Following entries are needed in your application property/yaml file.
```
aws.region=us-east-1
```
Use this incase DynamoDB is in a different region than rest of the services
```
aws.dynamodb.region=us-east-1
```
If both are provided then the second one takes precedence

### Use in your project

#### Define Entity

Entity with a partition key.
[TableWithPartition](testapp%2Fsrc%2Fmain%2Fjava%2Fio%2Fpbhuyan%2Ftestapp%2Fentity%2FTableWithPartition.java)

Entity with partition and sort key.
[TableWithSort](testapp%2Fsrc%2Fmain%2Fjava%2Fio%2Fpbhuyan%2Ftestapp%2Fentity%2FTableWithSort.java)

#### Define Repository
There are two types of Repository to choose from.

[DDbReadRepository](spring-boot-starter-dynamodb-jpa%2Fsrc%2Fmain%2Fjava%2Fio%2Fpbhuyan%2Fdynamodbjpa%2Frepo%2FDDbReadRepository.java) has several read only methods.

[DDbCrudRepository](spring-boot-starter-dynamodb-jpa%2Fsrc%2Fmain%2Fjava%2Fio%2Fpbhuyan%2Fdynamodbjpa%2Frepo%2FDDbCrudRepository.java) has all the read methods of the above and several crud methods.

NOTE: 
1. Partition key and sort key(if available) type provided in the repo and entity must match with each other.
2. Entity with only primary key need to set the sort key type as Void.
3. For consistent read for more reliable transactions call
   super(dynamoDbEnhancedClient, true);

```
@Repository
public class CustomerCrudRepository extends DDbCrudRepository<Customer, String, Void> {

    @Autowired
    public CustomerCrudRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        super(dynamoDbEnhancedClient);
        // For consistent read use below 
        //super(dynamoDbEnhancedClient, true);
    }
}
```
```
@Repository
public class CustomerReadRepository extends DDbReadRepository<Customer, String, Void> {

    @Autowired
    public CustomerCrudRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        super(dynamoDbEnhancedClient);
    }
}
```
```
@Repository
public class ProductCrudRepository extends DDbCrudRepository<Product, String, String> {

    @Autowired
    public ProductCrudRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        super(dynamoDbEnhancedClient);
    }
}
```
```
@Repository
public class ProductReadRepository extends DDbReadRepository<Product, String, String> {

    @Autowired
    public ProductCrudRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        super(dynamoDbEnhancedClient);
    }
}
```

Read more about partition key and sort key here.
https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/HowItWorks.CoreComponents.html#HowItWorks.CoreComponents.PrimaryKey




