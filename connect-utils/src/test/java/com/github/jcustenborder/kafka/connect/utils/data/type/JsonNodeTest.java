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
import com.github.jcustenborder.kafka.connect.utils.data.Parser;
import com.google.common.base.MoreObjects;
import org.apache.kafka.connect.data.Date;
import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.Schema;
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
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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


  @TestFactory
  public Stream<DynamicTest> parseJsonNode() {
    List<TestCase> tests = new ArrayList<>();
    of(tests, Boolean.class, Schema.BOOLEAN_SCHEMA, Boolean.TRUE);
    of(tests, Boolean.class, Schema.BOOLEAN_SCHEMA, Boolean.FALSE);

    //FLOAT32
    of(tests, Float.class, Schema.FLOAT32_SCHEMA, Float.MAX_VALUE);
    of(tests, Float.class, Schema.FLOAT32_SCHEMA, Float.MIN_VALUE);
    for (int i = 0; i < 30; i++) {
      of(tests, Float.class, Schema.FLOAT32_SCHEMA, this.random.nextFloat());
    }

    //FLOAT64
    of(tests, Double.class, Schema.FLOAT64_SCHEMA, Double.MAX_VALUE);
    of(tests, Double.class, Schema.FLOAT64_SCHEMA, Double.MIN_VALUE);
    for (int i = 0; i < 30; i++) {
      of(tests, Double.class, Schema.FLOAT64_SCHEMA, this.random.nextDouble());
    }

    //INT8
    of(tests, Byte.class, Schema.INT8_SCHEMA, Byte.MAX_VALUE);
    of(tests, Byte.class, Schema.INT8_SCHEMA, Byte.MIN_VALUE);
    byte[] buffer = new byte[30];
    this.random.nextBytes(buffer);
    for (Byte b : buffer) {
      of(tests, Byte.class, Schema.INT8_SCHEMA, b);
    }

    //INT16
    of(tests, Short.class, Schema.INT16_SCHEMA, Short.MAX_VALUE);
    of(tests, Short.class, Schema.INT16_SCHEMA, Short.MIN_VALUE);
    for (int i = 0; i < 30; i++) {
      of(tests, Short.class, Schema.INT16_SCHEMA, (short) this.random.nextInt(Short.MAX_VALUE));
    }

    //INT32
    of(tests, Integer.class, Schema.INT32_SCHEMA, Integer.MAX_VALUE);
    of(tests, Integer.class, Schema.INT32_SCHEMA, Integer.MIN_VALUE);
    for (int i = 0; i < 30; i++) {
      of(tests, Integer.class, Schema.INT32_SCHEMA, this.random.nextInt());
    }

    //INT64
    of(tests, Long.class, Schema.INT64_SCHEMA, Long.MAX_VALUE);
    of(tests, Long.class, Schema.INT64_SCHEMA, Long.MIN_VALUE);
    for (int i = 0; i < 30; i++) {
      of(tests, Long.class, Schema.INT64_SCHEMA, this.random.nextLong());
    }

    //String
    of(tests, String.class, Schema.STRING_SCHEMA, "");
    of(tests, String.class, Schema.STRING_SCHEMA, "This is a string");

    //Decimal
    for (int SCALE = 3; SCALE < 30; SCALE++) {
      of(tests, BigDecimal.class, Decimal.schema(SCALE), new BigDecimal("12345").setScale(SCALE));
      of(tests, BigDecimal.class, Decimal.schema(SCALE), new BigDecimal("0").setScale(SCALE));
      of(tests, BigDecimal.class, Decimal.schema(SCALE), new BigDecimal("-12345.001").setScale(SCALE));
    }

    //Timestamp
    java.util.Date expectedDate = new java.util.Date(994204800000L);
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