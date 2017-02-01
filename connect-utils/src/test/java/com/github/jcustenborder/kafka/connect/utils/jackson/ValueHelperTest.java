package com.github.jcustenborder.kafka.connect.utils.jackson;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class ValueHelperTest {

  class TestCase {
    public final Schema schema;
    public final Object input;
    public final Object expected;

    TestCase(Schema schema, Object input, Object expected) {
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

  void of(List<TestCase> tests, Schema schema, Object input, Object expected) {
    tests.add(new TestCase(schema, input, expected));
  }

  @TestFactory
  Stream<DynamicTest> value() {
    List<TestCase> tests = new ArrayList<>();
    of(tests, Schema.FLOAT64_SCHEMA, new Double(Double.MAX_VALUE), new Double(Double.MAX_VALUE));
    of(tests, Schema.FLOAT64_SCHEMA, new Double(Double.MIN_VALUE), new Double(Double.MIN_VALUE));

    of(tests, Schema.INT8_SCHEMA, new Byte(Byte.MAX_VALUE), new Byte(Byte.MAX_VALUE));
    of(tests, Schema.INT8_SCHEMA, new Byte(Byte.MIN_VALUE), new Byte(Byte.MIN_VALUE));

    of(tests, Schema.INT16_SCHEMA, new Short(Short.MAX_VALUE), new Short(Short.MAX_VALUE));
    of(tests, Schema.INT16_SCHEMA, new Short(Short.MIN_VALUE), new Short(Short.MIN_VALUE));

    of(tests, Schema.INT32_SCHEMA, new Integer(Integer.MAX_VALUE), new Integer(Integer.MAX_VALUE));
    of(tests, Schema.INT32_SCHEMA, new Integer(Integer.MIN_VALUE), new Integer(Integer.MIN_VALUE));

    of(tests, Schema.INT64_SCHEMA, new Long(Long.MAX_VALUE), new Long(Long.MAX_VALUE));
    of(tests, Schema.INT64_SCHEMA, new Long(Long.MIN_VALUE), new Long(Long.MIN_VALUE));

    of(tests, Schema.STRING_SCHEMA, "", "");
    of(tests, Schema.STRING_SCHEMA, "mirror", "mirror");

    for (int SCALE = 3; SCALE < 30; SCALE++) {
      Schema schema = Decimal.schema(SCALE);
      of(tests, schema, new BigDecimal("12345").setScale(SCALE), new BigDecimal("12345").setScale(SCALE));
      of(tests, schema, new BigDecimal("0").setScale(SCALE), new BigDecimal("0").setScale(SCALE));
      of(tests, schema, new BigDecimal("-12345.001").setScale(SCALE), new BigDecimal("-12345.001").setScale(SCALE));
    }

    Schema structSchema = SchemaBuilder.struct()
        .field("firstName", Schema.STRING_SCHEMA)
        .field("lastName", Schema.STRING_SCHEMA)
        .build();

    of(
        tests,
        structSchema,
        ImmutableMap.of("firstName", "example", "lastName", "user"),
        new Struct(structSchema)
            .put("firstName", "example")
            .put("lastName", "user")
    );

    of(
        tests,
        structSchema,
        new Struct(structSchema)
            .put("firstName", "example")
            .put("lastName", "user"),
        new Struct(structSchema)
            .put("firstName", "example")
            .put("lastName", "user")
    );

    return tests.stream().map(testCase -> dynamicTest(testCase.toString(), () -> {
      final Object actual = ValueHelper.value(testCase.schema, testCase.input);
      assertEquals(testCase.expected, actual);
    }));
  }
}
