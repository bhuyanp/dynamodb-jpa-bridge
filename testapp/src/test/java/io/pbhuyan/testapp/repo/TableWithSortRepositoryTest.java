package io.pbhuyan.testapp.repo;

import io.pbhuyan.dynamodbjpa.exception.DDbRepoException;
import io.pbhuyan.testapp.TestApplicationTests;
import io.pbhuyan.testapp.entity.TableWithSort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class TableWithSortRepositoryTest extends TestApplicationTests {


    @Autowired
    private TableWithSortCrudRepository tableWithSortRepository;

    private static List<TableWithSort> SORT_TABLE_ENTITIES = List.of(
            new TableWithSort("test1", "sort11", "Record Title 1"),
            new TableWithSort("test2", "sort21", "Record Title 2"),
            new TableWithSort("test2", "sort22", "Record Title 3"),
            new TableWithSort("test2", "sort23", "Record Title 4"),
            new TableWithSort("test3", "sort31", "Record Title 5"),
            new TableWithSort("test3", "sort32", "Record Title 6")
    );

    @BeforeEach
    void setUp() {
        tableWithSortRepository.saveAll(SORT_TABLE_ENTITIES);

    }

    @AfterEach
    void tearDown() {
        tableWithSortRepository.delete(SORT_TABLE_ENTITIES);
    }

    @Test
    void findAllByPartitionKey_expectMultipleResults() {
        //given
        String partitionKey = "test2";

        //when
        List<TableWithSort> allBy = tableWithSortRepository.findAllBy(partitionKey);

        //then
        assertThat(allBy).hasSize(3)
                .contains(
                        SORT_TABLE_ENTITIES.get(1),
                        SORT_TABLE_ENTITIES.get(2),
                        SORT_TABLE_ENTITIES.get(3)
                );

    }

    @Test
    void findByPartitionKeyAndSortKey_expectSingleResults() {
        //given
        TableWithSort tableWithSort = SORT_TABLE_ENTITIES.get(0);
        String partitionKey = tableWithSort.getId();
        String sortKey = tableWithSort.getSort();

        //when
        Optional<TableWithSort> result = tableWithSortRepository.findBy(partitionKey, sortKey);

        //then
        assertThat(result).hasValueSatisfying(r->{
                    assertThat(r.getId()).isEqualTo(tableWithSort.getId());
                    assertThat(r.getSort()).isEqualTo(tableWithSort.getSort());
                    assertThat(r.getTitle()).isEqualTo(tableWithSort.getTitle());
                });

    }

    @Test
    void findByPartitionKeyAndSortKey_expectNoResults() {
        //given
        TableWithSort tableWithSort = SORT_TABLE_ENTITIES.get(0);
        String partitionKey = tableWithSort.getId();

        //when
        Optional<TableWithSort> result = tableWithSortRepository.findBy(partitionKey, "sortKey");

        //then
        assertThat(result).isEmpty();

    }

    @Test
    void existsBy_match() {
        //given
        TableWithSort tableWithSort = SORT_TABLE_ENTITIES.get(0);
        String partitionKey = tableWithSort.getId();
        String sortKey = tableWithSort.getSort();

        //when
        boolean existsBy = tableWithSortRepository.existsBy(partitionKey, sortKey);

        //then
        assertThat(existsBy).isTrue();
    }

    @Test
    void existsBy_noMatch() {
        //given
        TableWithSort tableWithSort = SORT_TABLE_ENTITIES.get(0);
        String partitionKey = tableWithSort.getId();
        String sortKey = tableWithSort.getSort();

        //when
        boolean existsBy = tableWithSortRepository.existsBy(partitionKey, "sortKey");

        //then
        assertThat(existsBy).isFalse();
    }

    @Test
    void deleteWithMatch_shouldRemoveTheRecord() {
            //given
            TableWithSort tableWithSort = SORT_TABLE_ENTITIES.get(0);
            String partitionKey = tableWithSort.getId();
            String sortKey = tableWithSort.getSort();

            //when
            tableWithSortRepository.delete(partitionKey, sortKey);
            List<TableWithSort> all = tableWithSortRepository.findAll();

            //then
            assertThat(all).doesNotContain(tableWithSort);
    }

    @Test
    void deleteWithMismatch_shouldThrowsException() {
        //given
        //when
        //then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(()->tableWithSortRepository.delete("dfgdfgdfg","sdf"));
    }

    @Test
    void deleteWithOnlyPartitionKey_shouldThrowsException() {
        //given
        //when
        //then
        assertThatExceptionOfType(DDbRepoException.class)
                .isThrownBy(()->tableWithSortRepository.delete("dfgdfgdfg"));
    }
}