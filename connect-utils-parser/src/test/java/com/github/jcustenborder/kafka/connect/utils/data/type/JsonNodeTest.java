/**
 * Copyright © 2016 Jeremy Custenborder (jcustenborder@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jcustenborder.kafka.connect.utils.data.type;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jcustenborder.kafka.connect.utils.AssertStruct;
import com.github.jcustenborder.kafka.connect.utils.data.Parser;
import com.github.jcustenborder.kafka.connect.utils.jackson.ObjectMapperFactory;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import org.apache.kafka.connect.data.Date;
import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.data.Time;
import org.apache.kafka.connect.data.Timestamp;
import org.apache.kafka.connect.errors.DataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.stream.Stream;

import static com.github.jcustenborder.kafka.connect.utils.AssertStruct.assertStruct;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class JsonNodeTest {
  private static final Logger log = LoggerFactory.getLogger(JsonNodeTest.class);
  Parser parser;
  Calendar calendar;

  Random random;

  @BeforeEach
  public void before() {
    this.parser = new Parser();
    this.calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    this.random = new Random();
  }

  @Test
  public void nullableTests() throws IOException {
    final Schema[] schemas = new Schema[]{
        Schema.OPTIONAL_BOOLEAN_SCHEMA,
        Schema.OPTIONAL_FLOAT32_SCHEMA,
        Schema.OPTIONAL_FLOAT64_SCHEMA,
        Schema.OPTIONAL_INT8_SCHEMA,
        Schema.OPTIONAL_INT16_SCHEMA,
        Schema.OPTIONAL_INT32_SCHEMA,
        Schema.OPTIONAL_INT64_SCHEMA,
        Schema.OPTIONAL_STRING_SCHEMA,
        Decimal.builder(1).optional().build(),
        Timestamp.builder().optional().build(),
        Date.builder().optional().build(),
        Time.builder().optional().build(),
    };

    for (Schema schema : schemas) {
      JsonNode inputNode = null;
      Object actual = this.parser.parseJsonNode(schema, inputNode);
      assertNull(actual);
      inputNode = objectMapper.readTree("{\"foo\": null}");
      inputNode = inputNode.findValue("foo");
      actual = this.parser.parseJsonNode(schema, inputNode);
      assertNull(actual);
    }

  }

  static final ObjectMapper objectMapper = new ObjectMapper();

  void badDataTest(Schema schema) {
    assertThrows(DataException.class, () -> {
      parser.parseJsonNode(schema, objectMapper.valueToTree("bad"));
    });
  }

  static class TestCase {
    public final Class<?> expectedClass;
    public final Schema schema;
    public final Object input;
    public final Object expected;

    TestCase(Class<?> expectedClass, Schema schema, Object input, Object expected) {
      this.expectedClass = expectedClass;
      this.schema = schema;
      this.input = input;
      this.expected = expected;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("expectedClass", this.expectedClass.getSimpleName())
          .add("schemaType", this.schema.type())
          .add("schemaName", this.schema.name())
          .add("expected", this.expected)
          .omitNullValues()
          .toString();
    }
  }

  static void of(List<TestCase> tests, Class<?> expectedClass, Schema schema, Object expected) {
    TestCase testCase = new TestCase(expectedClass, schema, expected, expected);
    tests.add(testCase);
  }

  static void of(List<TestCase> tests, Class<?> expectedClass, Schema schema, Object input, Object expected) {
    TestCase testCase = new TestCase(expectedClass, schema, input, expected);
    tests.add(testCase);
  }

  void parseJsonNode(TestCase testCase) {
    JsonNode valueNode = objectMapper.valueToTree(testCase.input);
    ObjectNode objectNode = objectMapper.createObjectNode();
    objectNode.set("foo", valueNode);

    JsonNode propertyNode = objectNode.findValue("foo");
    Object actual = this.parser.parseJsonNode(testCase.schema, propertyNode);
    assertNotNull(valueNode, "Could not create valueNode value");
    String message = String.format("Could not parse '%s' to '%s'", valueNode, testCase.expectedClass.getSimpleName());
    assertNotNull(actual, message);
    assertEquals(testCase.expectedClass, actual.getClass(), message);
    assertEquals(testCase.expected, actual, message);

    actual = this.parser.parseJsonNode(testCase.schema, valueNode);
    assertNotNull(valueNode, "Could not create valueNode value");
    message = String.format("Could not parse '%s' to '%s'", valueNode, testCase.expectedClass.getName());
    assertNotNull(actual, message);
    assertEquals(testCase.expectedClass, actual.getClass(), message);
    assertEquals(testCase.expected, actual, message);
  }

  <T> void of(List<TestCase> tests, Class<T> cls, Schema schema, List<T> values) {
    for (T t : values) {
      of(tests, cls, schema, t);
      of(tests, cls, schema, t.toString(), t);
    }
  }

  @Test
  public void struct() {

    Map<String, Object> map = new LinkedHashMap<>();
    map.put("first", (Object) 1234);
    map.put("second", true);
    map.put("third", "testing");
    map.put("fourth", Arrays.asList("one", "two", "three"));
    map.put("fifth", ImmutableMap.of("a", "a", "b", "b", "c", "c"));
    map.put("sixth", ImmutableMap.of("a", "a", "b", "b", "c", "c"));

    Schema childSchema = SchemaBuilder.struct()
        .field("a", Schema.OPTIONAL_STRING_SCHEMA)
        .field("b", Schema.OPTIONAL_STRING_SCHEMA)
        .field("c", Schema.OPTIONAL_STRING_SCHEMA)
        .build();

    Schema schema = SchemaBuilder.struct()
        .field("first", Schema.OPTIONAL_INT64_SCHEMA)
        .field("second", Schema.OPTIONAL_BOOLEAN_SCHEMA)
        .field("third", Schema.OPTIONAL_STRING_SCHEMA)
        .field("fourth", SchemaBuilder.array(Schema.STRING_SCHEMA))
        .field("fifth", SchemaBuilder.map(Schema.STRING_SCHEMA, Schema.STRING_SCHEMA))
        .field("sixth", childSchema)
        .build();

    final Struct expected = new Struct(schema)
        .put("first", 1234L)
        .put("second", true)
        .put("third", "testing")
        .put("fourth", Arrays.asList("one", "two", "three"))
        .put("fifth", ImmutableMap.of("a", "a", "b", "b", "c", "c"))
        .put("sixth", new Struct(childSchema)
            .put("a", "a")
            .put("b", "b")
            .put("c", "c")
        );


    JsonNode input = ObjectMapperFactory.INSTANCE.convertValue(map, ObjectNode.class);

    Object result = parser.parseJsonNode(schema, input);
    assertNotNull(result);
    assertTrue(result instanceof Struct, "result should be a struct");
    AssertStruct.assertStruct(expected, (Struct) result);
  }

  @TestFactory
  public Stream<DynamicTest> parseJsonNode() {
    List<TestCase> tests = new ArrayList<>();
    of(tests, Boolean.class, Schema.BOOLEAN_SCHEMA, Boolean.TRUE);
    of(tests, Boolean.class, Schema.BOOLEAN_SCHEMA, Boolean.FALSE);

    List<Float> floats = new ArrayList<>();
    floats.add(Float.MAX_VALUE);
    floats.add(Float.MIN_VALUE);
    for (int i = 0; i < 30; i++) {
      floats.add(this.random.nextFloat());
    }
    of(tests, Float.class, Schema.FLOAT32_SCHEMA, floats);

    List<Double> doubles = new ArrayList<>();
    doubles.add(Double.MAX_VALUE);
    doubles.add(Double.MIN_VALUE);
    for (int i = 0; i < 30; i++) {
      doubles.add(this.random.nextDouble());
    }
    of(tests, Double.class, Schema.FLOAT64_SCHEMA, doubles);

    List<Byte> bytes = new ArrayList<>();
    bytes.add(Byte.MAX_VALUE);
    bytes.add(Byte.MIN_VALUE);
    byte[] buffer = new byte[30];
    this.random.nextBytes(buffer);
    for (Byte b : buffer) {
      bytes.add(b);
    }
    of(tests, Byte.class, Schema.INT8_SCHEMA, bytes);

    List<Short> shorts = new ArrayList<>();
    shorts.add(Short.MAX_VALUE);
    shorts.add(Short.MIN_VALUE);
    for (int i = 0; i < 30; i++) {
      shorts.add((short) this.random.nextInt(Short.MAX_VALUE));
    }
    of(tests, Short.class, Schema.INT16_SCHEMA, shorts);

    List<Integer> ints = new ArrayList<>();
    ints.add(Integer.MAX_VALUE);
    ints.add(Integer.MIN_VALUE);
    for (int i = 0; i < 30; i++) {
      ints.add(this.random.nextInt());
    }
    of(tests, Integer.class, Schema.INT32_SCHEMA, ints);

    List<Long> longs = new ArrayList<>();
    longs.add(Long.MAX_VALUE);
    longs.add(Long.MIN_VALUE);
    for (int i = 0; i < 30; i++) {
      longs.add(this.random.nextLong());
    }
    of(tests, Long.class, Schema.INT64_SCHEMA, longs);

    //String
    of(tests, String.class, Schema.STRING_SCHEMA, "");
    of(tests, String.class, Schema.STRING_SCHEMA, "This is a string");

    //Decimal
    for (int SCALE = 3; SCALE < 30; SCALE++) {
      of(tests, BigDecimal.class, Decimal.schema(SCALE), new BigDecimal("12345").setScale(SCALE));
      of(tests, BigDecimal.class, Decimal.schema(SCALE), new BigDecimal("0").setScale(SCALE));
      of(tests, BigDecimal.class, Decimal.schema(SCALE), new BigDecimal("-12345.001").setScale(SCALE));
      of(tests, BigDecimal.class, Decimal.schema(SCALE), new BigDecimal("12345").setScale(SCALE).toString(), new BigDecimal("12345").setScale(SCALE));
      of(tests, BigDecimal.class, Decimal.schema(SCALE), new BigDecimal("0").setScale(SCALE).toString(), new BigDecimal("0").setScale(SCALE));
      of(tests, BigDecimal.class, Decimal.schema(SCALE), new BigDecimal("-12345.001").setScale(SCALE).toString(), new BigDecimal("-12345.001").setScale(SCALE));
    }

    //Timestamp
    java.util.Date expectedDate = new java.util.Date(1494855736000L);
    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
    of(tests, java.util.Date.class, Timestamp.SCHEMA, expectedDate);
    of(tests, java.util.Date.class, Timestamp.SCHEMA, inputFormat.format(expectedDate), expectedDate);
    of(tests, java.util.Date.class, Timestamp.SCHEMA, expectedDate.getTime(), expectedDate);

    //Date
    expectedDate = new java.util.Date(994204800000L);
    inputFormat = new SimpleDateFormat("yyyy-MM-dd");
    inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    of(tests, java.util.Date.class, Date.SCHEMA, expectedDate);
    of(tests, java.util.Date.class, Date.SCHEMA, inputFormat.format(expectedDate), expectedDate);
    of(tests, java.util.Date.class, Date.SCHEMA, expectedDate.getTime(), expectedDate);

    //Time
    expectedDate = new java.util.Date(65336000L);
    inputFormat = new SimpleDateFormat("HH:mm:ss");
    of(tests, java.util.Date.class, Time.SCHEMA, new java.util.Date(65336000L));
    of(tests, java.util.Date.class, Time.SCHEMA, inputFormat.format(expectedDate), expectedDate);
    of(tests, java.util.Date.class, Time.SCHEMA, expectedDate.getTime(), expectedDate);

    return tests.stream().map(testCase -> dynamicTest(testCase.toString(), () -> {
      parseJsonNode(testCase);
    }));
  }

  @TestFactory
  Stream<DynamicTest> badData() {
    List<Schema> schemas = Arrays.asList(
        Schema.INT8_SCHEMA,
        Schema.INT16_SCHEMA,
        Schema.INT32_SCHEMA,
        Schema.INT64_SCHEMA,
        Schema.FLOAT32_SCHEMA,
        Schema.FLOAT64_SCHEMA
    );

    return schemas.stream().map(schema ->
        dynamicTest(schema.type().name(), () -> {
          badDataTest(schema);
        })
    );
  }
}