package io.confluent.kafka.connect.conversion;

import com.google.common.collect.ImmutableMap;
import org.apache.kafka.connect.data.Date;
import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Time;
import org.apache.kafka.connect.data.Timestamp;
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

public class ConverterTest {

  Converter converter;
  Calendar calendar;

  @Before
  public void before(){
    this.converter = new Converter();
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

    for(Schema schema:schemas){
      Object actual = this.converter.convert(schema, null);
      Assert.assertNull(actual);
    }

  }

  void assertConversion(Schema schema, final Class expectedClass, Map<String, ?> tests) {
    for(Map.Entry<String, ?> kvp:tests.entrySet()){
      Object expected = kvp.getValue();
      Object actual = this.converter.convert(schema, kvp.getKey());
      String message = String.format("Could not parse '%s' to '%s'", kvp.getKey(), expectedClass.getName());
      Assert.assertNotNull(message, actual);
      final Class actualClass = actual.getClass();
      Assert.assertThat(message, actualClass, IsEqual.equalTo(expectedClass));
      Assert.assertEquals(message, expected, actual);
    }
  }

  @Test
  public void booleanTests(){
    Map<String, ?> tests = ImmutableMap.of(
        "true", Boolean.TRUE,
        "TRUE", Boolean.TRUE,
        "false", Boolean.FALSE,
        "FALSE", Boolean.FALSE
    );
    assertConversion(Schema.BOOLEAN_SCHEMA, Boolean.class, tests);
  }

  @Test
  public void float32Tests(){
    Map<String, ?> tests = ImmutableMap.of(
        new Float(Float.MAX_VALUE).toString(), new Float(Float.MAX_VALUE),
        new Float(Float.MIN_VALUE).toString(), new Float(Float.MIN_VALUE)
    );
    assertConversion(Schema.FLOAT32_SCHEMA, Float.class, tests);
  }

  @Test
  public void float64Tests(){
    Map<String, ?> tests = ImmutableMap.of(
        new Double(Double.MAX_VALUE).toString(), new Double(Double.MAX_VALUE),
        new Double(Double.MIN_VALUE).toString(), new Double(Double.MIN_VALUE)
    );
    assertConversion(Schema.FLOAT64_SCHEMA, Double.class, tests);
  }

  @Test
  public void int8Tests(){
    Map<String, ?> tests = ImmutableMap.of(
        new Byte(Byte.MAX_VALUE).toString(), new Byte(Byte.MAX_VALUE),
        new Byte(Byte.MIN_VALUE).toString(), new Byte(Byte.MIN_VALUE)
    );
    assertConversion(Schema.INT8_SCHEMA, Byte.class, tests);
  }

  @Test
  public void int16Tests(){
    Map<String, ?> tests = ImmutableMap.of(
        new Short(Short.MAX_VALUE).toString(), new Short(Short.MAX_VALUE),
        new Short(Short.MIN_VALUE).toString(), new Short(Short.MIN_VALUE)
    );
    assertConversion(Schema.INT16_SCHEMA, Short.class, tests);
  }

  @Test
  public void int32Tests(){
    Map<String, ?> tests = ImmutableMap.of(
        new Integer(Integer.MAX_VALUE).toString(), new Integer(Integer.MAX_VALUE),
        new Integer(Integer.MIN_VALUE).toString(), new Integer(Integer.MIN_VALUE)
    );
    assertConversion(Schema.INT32_SCHEMA, Integer.class, tests);
  }

  @Test
  public void int64Tests(){
    Map<String, ?> tests = ImmutableMap.of(
        new Long(Long.MAX_VALUE).toString(), new Long(Long.MAX_VALUE),
        new Long(Long.MIN_VALUE).toString(), new Long(Long.MIN_VALUE)
    );
    assertConversion(Schema.INT64_SCHEMA, Long.class, tests);
  }

  @Test
  public void decimalTests(){
    Map<String, ?> tests = ImmutableMap.of(
        "12345", new BigDecimal("12345"),
        "0", new BigDecimal("0"),
        "-12345.001", new BigDecimal("-12345.001")
    );
    assertConversion(Decimal.builder(10).build(), BigDecimal.class, tests);
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
