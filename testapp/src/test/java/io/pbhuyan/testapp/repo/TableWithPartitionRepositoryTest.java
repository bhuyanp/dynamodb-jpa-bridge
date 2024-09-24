package io.pbhuyan.testapp.repo;

import io.pbhuyan.testapp.TestApplicationTests;
import io.pbhuyan.testapp.entity.TableWithPartition;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.enhanced.dynamodb.Expression;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class TableWithPartitionRepositoryTest extends TestApplicationTests {
    @Autowired
    private TableWithPartitionCrudRepository tableWithPartitionRepository;

    private static List<TableWithPartition> TABLE_WITH_PARTITIONS = List.of(
            new TableWithPartition("test1","some value goes here"),
            new TableWithPartition("test2","some value goes here"),
            new TableWithPartition("test3","some value goes here")
    );


    @BeforeEach
    public void setUp(){
        tableWithPartitionRepository.saveAll(TABLE_WITH_PARTITIONS);
    }

    @AfterEach
    public void tearDown(){
        tableWithPartitionRepository.delete(TABLE_WITH_PARTITIONS);
    }

    @Test
    void findAll() {
        //when
        List<TableWithPartition> all = tableWithPartitionRepository.findAll();
        //then
        assertThat(all).hasSizeGreaterThanOrEqualTo(TABLE_WITH_PARTITIONS.size());
    }

    @Test
    void findAllByMatchingPartitionKey() {
        //when
        List<TableWithPartition> all = tableWithPartitionRepository.findAllBy("test1");
        //then
        assertThat(all).hasSize(1);
    }

    @Test
    void findAllByNonMatchingPartitionKey() {
        //when
        List<TableWithPartition> all = tableWithPartitionRepository.findAllBy("sdfsdtest1");
        //then
        assertThat(all).hasSize(0);
    }


    @Test
    void findByMatchingPartitionKey() {
        //given
        String test1 = "test1";
        //when
        Optional<TableWithPartition> all = tableWithPartitionRepository.findBy(test1);
        //then
        assertThat(all).contains(TABLE_WITH_PARTITIONS.stream().filter(r->r.getId().equalsIgnoreCase(test1)).findFirst().get());
    }

    @Test
    void findByNonMatchingPartitionKey() {
        //given
        String test1 = "tessdfsdfsdt1";
        //when
        Optional<TableWithPartition> all = tableWithPartitionRepository.findBy(test1);
        //then
        assertThat(all.isPresent()).isFalse();
    }

    @Test
    void findAllByRequestBuilder() {
        //given
        Expression filterExpression = Expression.builder()
                .expression("#a = :a")
                .putExpressionName("#a","test")
                .putExpressionValue(":a",tableWithPartitionRepository.getKeyAttributeValue("some value goes here"))
                .build();
        //when
        List<TableWithPartition> all = tableWithPartitionRepository.findAllBy(r->r.filterExpression(filterExpression));
        //then
        assertThat(all).hasSize(3).containsAll(TABLE_WITH_PARTITIONS);
    }







    @Test
    void existsBy() {
        //when
        boolean exists = tableWithPartitionRepository.existsBy("test1");
        //then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByFalse() {
        //when
        boolean exists = tableWithPartitionRepository.existsBy("test1sdfsdfsd");
        //then
        assertThat(exists).isFalse();
    }



    @Test
    void saveNew() {
        //given
        TableWithPartition subject = new TableWithPartition("somenewid","");
        //when
        tableWithPartitionRepository.save(subject);
        //then
        assertThat(tableWithPartitionRepository.findBy(subject.getId()).isPresent()).isTrue();
    }

    @Test
    void saveExisting() {
        //given
        TableWithPartition subject = TABLE_WITH_PARTITIONS.get(0);
        String somethingNew = "Something New";
        subject.setTest(somethingNew);
        //when
        tableWithPartitionRepository.save(subject);
        //then
        Optional<TableWithPartition> optional = tableWithPartitionRepository.findBy(subject.getId());
        assertThat(optional.isPresent()).isTrue();
        assertThat(optional)
                .hasValueSatisfying(r-> assertThat(r.getTest()).isEqualTo(somethingNew));
    }

    @Test
    void saveAll() {
        //given
        List<TableWithPartition> subjects =List.of(
                new TableWithPartition("somenewid1",""),
                new TableWithPartition("somenewid2",""),
                new TableWithPartition("somenewid3",""));
        //when
        tableWithPartitionRepository.saveAll(subjects);
        //then
        assertThat(tableWithPartitionRepository.findBy("somenewid1").isPresent()).isTrue();
        assertThat(tableWithPartitionRepository.findBy("somenewid2").isPresent()).isTrue();
        assertThat(tableWithPartitionRepository.findBy("somenewid3").isPresent()).isTrue();
    }

    @Test
    void deleteAll() {
        //when
        tableWithPartitionRepository.delete();
        //then
        assertThat(tableWithPartitionRepository.findAll()).hasSize(0);
    }

    @Test
    void deleteAllByList() {
        //given
        List<TableWithPartition> subjects =List.of(
                TABLE_WITH_PARTITIONS.get(0),
                TABLE_WITH_PARTITIONS.get(1));
        //when
        tableWithPartitionRepository.delete(subjects);
        //then
        assertThat(tableWithPartitionRepository.findAll())
                .doesNotContain(subjects.get(0))
                .doesNotContain(subjects.get(1));
    }

    @Test
    void deleteByEntity() {
        //given
        TableWithPartition subject = TABLE_WITH_PARTITIONS.get(0);
        //when
        tableWithPartitionRepository.delete(subject);
        //then
        Optional<TableWithPartition> optional = tableWithPartitionRepository.findBy(subject.getId());
        assertThat(optional.isPresent()).isFalse();
    }

    @Test
    void deleteByPartitionKey() {
        //given
        TableWithPartition subject = TABLE_WITH_PARTITIONS.get(0);
        //when
        tableWithPartitionRepository.delete(subject.getId());
        //then
        Optional<TableWithPartition> optional = tableWithPartitionRepository.findBy(subject.getId());
        assertThat(optional.isPresent()).isFalse();
    }

    @Test
    void deleteByMissingPartitionKey_shouldThrowException() {
        //given
        //when
        //then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(()->tableWithPartitionRepository.delete("dfgdfgdfg"));
    }



    @Test
    void testDeleteAll() {
    }
}