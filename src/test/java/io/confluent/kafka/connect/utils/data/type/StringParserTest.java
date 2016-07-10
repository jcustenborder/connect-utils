/**
 * Copyright (C) 2016 Jeremy Custenborder (jcustenborder@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.confluent.kafka.connect.utils.data.type;

import com.google.common.collect.ImmutableMap;
import io.confluent.kafka.connect.utils.data.Parser;
import org.apache.kafka.connect.data.Date;
import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Time;
import org.apache.kafka.connect.data.Timestamp;
import org.apache.kafka.connect.errors.DataException;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

public class StringParserTest {

  Parser parser;
  Calendar calendar;

  @Before
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
      Assert.assertNull(actual);
    }

  }

  void assertConversion(Schema schema, final Class expectedClass, Map<String, ?> tests) {
    for (Map.Entry<String, ?> kvp : tests.entrySet()) {
      Object expected = kvp.getValue();
      Object actual = this.parser.parseString(schema, kvp.getKey());
      String message = String.format("Could not parse '%s' to '%s'", kvp.getKey(), expectedClass.getName());
      Assert.assertNotNull(message, actual);
      final Class actualClass = actual.getClass();
      Assert.assertThat(message, actualClass, IsEqual.equalTo(expectedClass));
      Assert.assertEquals(message, expected, actual);
    }
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

  @Test(expected = DataException.class)
  public void float32BadData() {
    parser.parseString(Schema.FLOAT32_SCHEMA, "asdf");
  }

  @Test
  public void float64Tests() {
    Map<String, ?> tests = ImmutableMap.of(
        new Double(Double.MAX_VALUE).toString(), new Double(Double.MAX_VALUE),
        new Double(Double.MIN_VALUE).toString(), new Double(Double.MIN_VALUE)
    );
    assertConversion(Schema.FLOAT64_SCHEMA, Double.class, tests);
  }

  @Test(expected = DataException.class)
  public void float64BadData() {
    parser.parseString(Schema.FLOAT64_SCHEMA, "asdf");
  }

  @Test
  public void int8Tests() {
    Map<String, ?> tests = ImmutableMap.of(
        new Byte(Byte.MAX_VALUE).toString(), new Byte(Byte.MAX_VALUE),
        new Byte(Byte.MIN_VALUE).toString(), new Byte(Byte.MIN_VALUE)
    );
    assertConversion(Schema.INT8_SCHEMA, Byte.class, tests);
  }

  @Test(expected = DataException.class)
  public void int8BadData() {
    parser.parseString(Schema.INT8_SCHEMA, "asdf");
  }

  @Test
  public void int16Tests() {
    Map<String, ?> tests = ImmutableMap.of(
        new Short(Short.MAX_VALUE).toString(), new Short(Short.MAX_VALUE),
        new Short(Short.MIN_VALUE).toString(), new Short(Short.MIN_VALUE)
    );
    assertConversion(Schema.INT16_SCHEMA, Short.class, tests);
  }

  @Test(expected = DataException.class)
  public void int16BadData() {
    parser.parseString(Schema.INT16_SCHEMA, "asdf");
  }

  @Test
  public void int32Tests() {
    Map<String, ?> tests = ImmutableMap.of(
        new Integer(Integer.MAX_VALUE).toString(), new Integer(Integer.MAX_VALUE),
        new Integer(Integer.MIN_VALUE).toString(), new Integer(Integer.MIN_VALUE)
    );
    assertConversion(Schema.INT32_SCHEMA, Integer.class, tests);
  }

  @Test(expected = DataException.class)
  public void int32BadData() {
    parser.parseString(Schema.INT32_SCHEMA, "asdf");
  }

  @Test
  public void int64Tests() {
    Map<String, ?> tests = ImmutableMap.of(
        new Long(Long.MAX_VALUE).toString(), new Long(Long.MAX_VALUE),
        new Long(Long.MIN_VALUE).toString(), new Long(Long.MIN_VALUE)
    );
    assertConversion(Schema.INT64_SCHEMA, Long.class, tests);
  }

  @Test(expected = DataException.class)
  public void int64BadData() {
    parser.parseString(Schema.INT64_SCHEMA, "asdf");
  }

  @Test
  public void stringTests() {
    Map<String, ?> tests = ImmutableMap.of(
        "", "",
        "mirror", "mirror"
    );
    assertConversion(Schema.STRING_SCHEMA, String.class, tests);
  }

  @Test
  public void decimalTests() {
    for (int SCALE = 3; SCALE < 30; SCALE++) {
      Map<String, ?> tests = ImmutableMap.of(
          "12345", new BigDecimal("12345").setScale(SCALE),
          "0", new BigDecimal("0").setScale(SCALE),
          "-12345.001", new BigDecimal("-12345.001").setScale(SCALE)
      );
      assertConversion(Decimal.builder(SCALE).build(), BigDecimal.class, tests);
    }
  }

  @Test
  public void timestampTests() throws ParseException {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
    Map<String, ?> tests = ImmutableMap.of(
        "2001-07-04 12:08:56", dateFormat.parse("2001-07-04 12:08:56")
    );
    assertConversion(Timestamp.SCHEMA, java.util.Date.class, tests);
  }

  @Test
  public void dateTests() throws ParseException {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    this.calendar.set(Calendar.HOUR, 0);
    this.calendar.set(Calendar.MINUTE, 0);
    this.calendar.set(Calendar.SECOND, 0);

    Map<String, ?> tests = ImmutableMap.of(
        "2001-07-04", dateFormat.parse("2001-07-04")
    );
    assertConversion(Date.SCHEMA, java.util.Date.class, tests);
  }

  @Test
  public void timeTests() throws ParseException {
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    Map<String, ?> tests = ImmutableMap.of(
        "12:08:56", dateFormat.parse("12:08:56")
    );
    assertConversion(Time.SCHEMA, java.util.Date.class, tests);
  }
}
