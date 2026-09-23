package com.tarento.commenthub.transactional.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CassandraPropertyReaderExceptionTest {

    @Test
    void testConstructor_setsMessageAndCause() {
        Throwable cause = new RuntimeException("root cause");
        CassandraPropertyReaderException exception =
                new CassandraPropertyReaderException("Error loading properties", cause);

        assertEquals("Error loading properties", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
