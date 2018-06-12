package com.github.jcustenborder.kafka.connect.utils;

import com.github.jcustenborder.kafka.connect.utils.data.SchemaKey;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.header.Header;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.opentest4j.AssertionFailedError;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static com.github.jcustenborder.kafka.connect.utils.AssertConnectRecord.assertHeader;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AssertConnectRecordTest {
  Schema struct = SchemaBuilder.struct().field("foo", Schema.STRING_SCHEMA).build();
  Map<Schema, Object> genericTests = new LinkedHashMap<>();

  @BeforeEach
  public void before() {
    genericTests = new LinkedHashMap<>();
    genericTests.put(Schema.STRING_SCHEMA, "asodfnasidf");
    genericTests.put(Schema.BYTES_SCHEMA, new byte[]{0x00, 0x01});
    genericTests.put(Schema.BOOLEAN_SCHEMA, true);
    genericTests.put(Schema.FLOAT32_SCHEMA, Float.MAX_VALUE);
    genericTests.put(Schema.FLOAT64_SCHEMA, Double.MAX_VALUE);
    genericTests.put(Schema.INT8_SCHEMA, Byte.MAX_VALUE);
    genericTests.put(Schema.INT16_SCHEMA, Short.MAX_VALUE);
    genericTests.put(Schema.INT32_SCHEMA, Integer.MAX_VALUE);
    genericTests.put(Schema.INT64_SCHEMA, Long.MAX_VALUE);
    genericTests.put(struct, new Struct(struct).put("foo", "bar"));
  }


  @TestFactory
  public Stream<DynamicTest> headerEquals() {
    return genericTests.entrySet().stream()
        .map(e -> dynamicTest(SchemaKey.of(e.getKey()).toString(), () -> {
          Header expected = mock(Header.class);
          when(expected.schema()).thenReturn(e.getKey());
          when(expected.value()).thenReturn(e.getValue());
          when(expected.key()).thenReturn("key");
          assertHeader(expected, expected);
        }));
  }

  @TestFactory
  public Stream<DynamicTest> headerValueDifferent() {
    return genericTests.entrySet().stream()
        .map(e -> dynamicTest(SchemaKey.of(e.getKey()).toString(), () -> {
          assertThrows(AssertionFailedError.class, () -> {
            Header expected = mock(Header.class);
            when(expected.schema()).thenReturn(e.getKey());
            when(expected.value()).thenReturn(e.getValue());
            when(expected.key()).thenReturn("key");
            Header actual = mock(Header.class);
            when(actual.schema()).thenReturn(e.getKey());
            when(actual.value()).thenReturn("adisfnbasd");
            when(actual.key()).thenReturn("key");
            assertHeader(expected, actual);
          });
        }));
  }

  @TestFactory
  public Stream<DynamicTest> headerExpectedNull() {
    return genericTests.entrySet().stream()
        .map(e -> dynamicTest(SchemaKey.of(e.getKey()).toString(), () -> {
          assertThrows(AssertionFailedError.class, () -> {
            Header expected = mock(Header.class);
            when(expected.schema()).thenReturn(e.getKey());
            when(expected.value()).thenReturn(e.getValue());
            when(expected.key()).thenReturn("key");
            assertHeader(null, expected);
          });
        }));
  }

  @TestFactory
  public Stream<DynamicTest> headerActualNull() {
    return genericTests.entrySet().stream()
        .map(e -> dynamicTest(SchemaKey.of(e.getKey()).toString(), () -> {
          assertThrows(AssertionFailedError.class, () -> {
            Header expected = mock(Header.class);
            when(expected.schema()).thenReturn(e.getKey());
            when(expected.value()).thenReturn(e.getValue());
            when(expected.key()).thenReturn("key");
            assertHeader(expected, null);
          });
        }));
  }
}
