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

import com.github.jcustenborder.kafka.connect.utils.data.Parser;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
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

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class StringParserTest {

  Parser parser;
  Calendar calendar;

  @BeforeEach
  public void before() {
    this.parser = new Parser();
    this.calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
  }

  @Test
  public void nullableTests() {
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
      Object actual = this.parser.parseString(schema, null);
      assertNull(actual);
    }

  }

  <T> Map<String, T> assertConversion(Schema schema, final Class<T> expectedClass, Map<String, ?> tests) {
    Map<String, T> results = new LinkedHashMap<>();
    for (Map.Entry<String, ?> kvp : tests.entrySet()) {
      Object expected = kvp.getValue();
      Object actual = this.parser.parseString(schema, kvp.getKey());
      String message = String.format("Could not parse '%s' to '%s'", kvp.getKey(), expectedClass.getName());
      assertNotNull(actual, message);
      final Class<?> actualClass = actual.getClass();
      assertEquals(expectedClass, actualClass, message);
      assertEquals(expected, actual, message);
      results.put(kvp.getKey(), (T) actual);
    }
    return results;
  }

  @Test
  public void booleanTests() {
    Map<String, ?> tests = ImmutableMap.of(
        "true", Boolean.TRUE,
        "TRUE", Boolean.TRUE,
        "false", Boolean.FALSE,
        "FALSE", Boolean.FALSE
    );
    assertConversion(Schema.BOOLEAN_SCHEMA, Boolean.class, tests);
  }

  @Test
  public void float32Tests() {
    Map<String, ?> tests = ImmutableMap.of(
        new Float(Float.MAX_VALUE).toString(), new Float(Float.MAX_VALUE),
        new Float(Float.MIN_VALUE).toString(), new Float(Float.MIN_VALUE)
    );
    assertConversion(Schema.FLOAT32_SCHEMA, Float.class, tests);
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
          assertThrows(DataException.class, () -> {
            parser.parseString(schema, "asdf");
          });
        })
    );
  }

  class TestCase {
    public final Schema schema;
    public final String input;
    public final Object expected;

    TestCase(Schema schema, String input, Object expected) {
      this.schema = schema;
      this.input = input;
      this.expected = expected;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(TestCase.class)
          .omitNullValues()
          .add("schemaType", schema.type())
          .add("schemaName", schema.name())
          .add("input", input)
          .toString();
    }
  }

  void of(List<TestCase> tests, Schema schema, String input, Object expected) {
    tests.add(new TestCase(schema, input, expected));
  }

  @TestFactory
  Stream<DynamicTest> parseString() {
    List<TestCase> tests = new ArrayList<>();
    of(tests, Schema.FLOAT64_SCHEMA, new Double(Double.MAX_VALUE).toString(), new Double(Double.MAX_VALUE));
    of(tests, Schema.FLOAT64_SCHEMA, new Double(Double.MIN_VALUE).toString(), new Double(Double.MIN_VALUE));

    of(tests, Schema.INT8_SCHEMA, new Byte(Byte.MAX_VALUE).toString(), new Byte(Byte.MAX_VALUE));
    of(tests, Schema.INT8_SCHEMA, new Byte(Byte.MIN_VALUE).toString(), new Byte(Byte.MIN_VALUE));

    of(tests, Schema.INT16_SCHEMA, new Short(Short.MAX_VALUE).toString(), new Short(Short.MAX_VALUE));
    of(tests, Schema.INT16_SCHEMA, new Short(Short.MIN_VALUE).toString(), new Short(Short.MIN_VALUE));

    of(tests, Schema.INT32_SCHEMA, new Integer(Integer.MAX_VALUE).toString(), new Integer(Integer.MAX_VALUE));
    of(tests, Schema.INT32_SCHEMA, new Integer(Integer.MIN_VALUE).toString(), new Integer(Integer.MIN_VALUE));

    of(tests, Schema.INT64_SCHEMA, new Long(Long.MAX_VALUE).toString(), new Long(Long.MAX_VALUE));
    of(tests, Schema.INT64_SCHEMA, new Long(Long.MIN_VALUE).toString(), new Long(Long.MIN_VALUE));

    of(tests, Schema.STRING_SCHEMA, "", "");
    of(tests, Schema.STRING_SCHEMA, "mirror", "mirror");

    for (int SCALE = 3; SCALE < 30; SCALE++) {
      Schema schema = Decimal.schema(SCALE);
      of(tests, schema, "12345", new BigDecimal("12345").setScale(SCALE));
      of(tests, schema, "0", new BigDecimal("0").setScale(SCALE));
      of(tests, schema, "-12345.001", new BigDecimal("-12345.001").setScale(SCALE));
    }

    return tests.stream().map(testCase -> dynamicTest(testCase.toString(), () -> {
      final Object actual = parser.parseString(testCase.schema, testCase.input);
      assertEquals(testCase.expected, actual);
    }));
  }

  @Test
  public void timestampTests() throws ParseException {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
    Map<String, java.util.Date> tests = ImmutableMap.of(
        "2001-07-04 12:08:56", dateFormat.parse("2001-07-04 12:08:56")
    );
    assertConversion(Timestamp.SCHEMA, java.util.Date.class, tests);
  }

  @Test
  public void dateTests() throws ParseException {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    this.calendar.set(Calendar.HOUR, 0);
    this.calendar.set(Calendar.MINUTE, 0);
    this.calendar.set(Calendar.SECOND, 0);

    Map<String, ?> tests = ImmutableMap.of(
        "2001-07-04", dateFormat.parse("2001-07-04")
    );
    Map<String, java.util.Date> results = assertConversion(Date.SCHEMA, java.util.Date.class, tests);
    for (Map.Entry<String, java.util.Date> kvp : results.entrySet()) {
      Date.fromLogical(Date.SCHEMA, kvp.getValue());
    }
  }

  @Test
  public void timeTests() throws ParseException {
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    Map<String, ?> tests = ImmutableMap.of(
        "12:08:56", dateFormat.parse("12:08:56")
    );
    Map<String, java.util.Date> results = assertConversion(Time.SCHEMA, java.util.Date.class, tests);
    for (Map.Entry<String, java.util.Date> kvp : results.entrySet()) {
      Time.fromLogical(Time.SCHEMA, kvp.getValue());
    }
  }
}
