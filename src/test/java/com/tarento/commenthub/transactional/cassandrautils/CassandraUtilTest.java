package com.tarento.commenthub.transactional.cassandrautils;

import com.datastax.oss.driver.api.core.cql.ColumnDefinitions;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CassandraUtilTest {

    private ResultSet mockResultSet;
    private Row mockRow;
    private ColumnDefinitions mockColumnDefinitions;
    private CassandraPropertyReader mockReader;
    private CassandraUtil cassandraUtil;

    @BeforeEach
    void setUp() {
        mockResultSet = mock(ResultSet.class);
        mockRow = mock(Row.class);
        mockColumnDefinitions = mock(ColumnDefinitions.class);
        mockReader = mock(CassandraPropertyReader.class);
        cassandraUtil = new CassandraUtil(mockReader);
    }

    @Test
    void testGetPreparedStatement() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", 1);
        data.put("name", "Mahesh");

        String actual = cassandraUtil.getPreparedStatement("test_keyspace", "test_table", data);
        String expected = "INSERT INTO test_keyspace.test_table(id,name) VALUES (?,?);";
        assertEquals(expected, actual);
    }

    @Test
    void testCreateResponseList() {
        when(mockResultSet.getColumnDefinitions()).thenReturn(mockColumnDefinitions);
        when(mockReader.readProperty("id")).thenReturn("id");

        when(mockResultSet.iterator()).thenReturn(List.of(mockRow).iterator());
        when(mockRow.getObject("id")).thenReturn("123");

        List<Map<String, Object>> result = cassandraUtil.createResponse(mockResultSet);
        assertEquals(1, result.size());
    }

    @Test
    void testCreateResponseMap() {
        when(mockResultSet.getColumnDefinitions()).thenReturn(mockColumnDefinitions);
        when(mockReader.readProperty("id")).thenReturn("id");

        when(mockResultSet.iterator()).thenReturn(List.of(mockRow).iterator());
        when(mockRow.getObject("id")).thenReturn("123");

        Map<String, Object> result = cassandraUtil.createResponse(mockResultSet, "id");
        assertEquals(1, result.size());
    }
}
