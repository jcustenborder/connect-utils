package io.confluent.kafka.connect.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

public class JsonNodeTest {
  Parser parser;
  Calendar calendar;

  Random random;

  @Before
  public void before() {
    this.parser = new Parser();
    this.calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    this.random = new Random();
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
      Object actual = this.parser.parseJsonNode(schema, null);
      Assert.assertNull(actual);
    }

  }

  void assertConversion(Schema schema, final Class expectedClass, List<?> tests) {
    for (Object expected : tests) {
      JsonNode input = objectMapper.valueToTree(expected);
      Assert.assertNotNull("Could not create input value", input);
      Object actual = this.parser.parseJsonNode(schema, input);
      String message = String.format("Could not parse '%s' to '%s'", input, expectedClass.getName());
      Assert.assertNotNull(message, actual);
      final Class actualClass = actual.getClass();
      Assert.assertThat(message, actualClass, IsEqual.equalTo(expectedClass));
      Assert.assertEquals(message, expected, actual);
    }
  }

  static final ObjectMapper objectMapper = new ObjectMapper();

  void badDataTest(Schema schema) {
    parser.parseJsonNode(schema, objectMapper.valueToTree("bad"));
  }

  @Test
  public void booleanTests() {
    List<?> tests = Arrays.asList(Boolean.TRUE, Boolean.FALSE);
    assertConversion(Schema.BOOLEAN_SCHEMA, Boolean.class, tests);
  }

  @Test
  public void float32Tests() {
    List<Float> tests = new ArrayList<>();
    tests.add(Float.MAX_VALUE);
    tests.add(Float.MIN_VALUE);
    for (int i = 0; i < 30; i++) {
      tests.add(this.random.nextFloat());
    }
    assertConversion(Schema.FLOAT32_SCHEMA, Float.class, tests);
  }

  @Test(expected = DataException.class)
  public void float32BadData() {
    badDataTest(Schema.FLOAT32_SCHEMA);
  }

  @Test
  public void float64Tests() {
    List<Double> tests = new ArrayList<>();
    tests.add(Double.MAX_VALUE);
    tests.add(Double.MIN_VALUE);

    for (int i = 0; i < 30; i++) {
      tests.add(this.random.nextDouble());
    }
    assertConversion(Schema.FLOAT64_SCHEMA, Double.class, tests);
  }

  @Test(expected = DataException.class)
  public void float64BadData() {
    badDataTest(Schema.FLOAT64_SCHEMA);
  }

  @Test
  public void int8Tests() {
    List<Byte> tests = new ArrayList<>();
    tests.add(Byte.MAX_VALUE);
    tests.add(Byte.MIN_VALUE);
    byte[] buffer = new byte[30];
    this.random.nextBytes(buffer);
    for (Byte b : buffer) {
      tests.add(b);
    }
    assertConversion(Schema.INT8_SCHEMA, Byte.class, tests);
  }

  @Test(expected = DataException.class)
  public void int8BadData() {
    badDataTest(Schema.INT8_SCHEMA);
  }

  @Test
  public void int16Tests() {
    List<Short> tests = new ArrayList<>();
    tests.add(Short.MAX_VALUE);
    tests.add(Short.MIN_VALUE);
    for (int i = 0; i < 30; i++) {
      tests.add((short) this.random.nextInt(Short.MAX_VALUE));
    }
    assertConversion(Schema.INT16_SCHEMA, Short.class, tests);
  }

  @Test(expected = DataException.class)
  public void int16BadData() {
    badDataTest(Schema.INT16_SCHEMA);
  }

  @Test
  public void int32Tests() {
    List<Integer> tests = new ArrayList<>();
    tests.add(Integer.MIN_VALUE);
    tests.add(Integer.MIN_VALUE);
    for (int i = 0; i < 30; i++) {
      tests.add(this.random.nextInt());
    }
    assertConversion(Schema.INT32_SCHEMA, Integer.class, tests);
  }

  @Test(expected = DataException.class)
  public void int32BadData() {
    badDataTest(Schema.INT32_SCHEMA);
  }

  @Test
  public void int64Tests() {
    List<Long> tests = new ArrayList<>();
    tests.add(Long.MAX_VALUE);
    tests.add(Long.MIN_VALUE);
    for (int i = 0; i < 30; i++) {
      tests.add(this.random.nextLong());
    }
    assertConversion(Schema.INT64_SCHEMA, Long.class, tests);
  }

  @Test(expected = DataException.class)
  public void int64BadData() {
    badDataTest(Schema.INT64_SCHEMA);
  }

  @Test
  public void stringTests() {
    List<?> tests = Arrays.asList("", "mirror");
    assertConversion(Schema.STRING_SCHEMA, String.class, tests);
  }

  @Test
  public void decimalTests() {
    for (int SCALE = 3; SCALE < 30; SCALE++) {
      List<?> tests = Arrays.asList(
          new BigDecimal("12345").setScale(SCALE),
          new BigDecimal("0").setScale(SCALE),
          new BigDecimal("-12345.001").setScale(SCALE)
      );

      assertConversion(Decimal.builder(SCALE).build(), BigDecimal.class, tests);
    }
  }

  @Test
  public void timestampTests() throws ParseException {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
    List<?> tests = Arrays.asList(dateFormat.parse("2001-07-04 12:08:56"));
    assertConversion(Timestamp.SCHEMA, java.util.Date.class, tests);
  }

  @Test
  public void dateTests() throws ParseException {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    this.calendar.set(Calendar.HOUR, 0);
    this.calendar.set(Calendar.MINUTE, 0);
    this.calendar.set(Calendar.SECOND, 0);

    List<?> tests = Arrays.asList(dateFormat.parse("2001-07-04"));
    assertConversion(Date.SCHEMA, java.util.Date.class, tests);
  }

  @Test
  public void timeTests() throws ParseException {
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    List<?> tests = Arrays.asList(dateFormat.parse("12:08:56"));
    assertConversion(Time.SCHEMA, java.util.Date.class, tests);
  }
}