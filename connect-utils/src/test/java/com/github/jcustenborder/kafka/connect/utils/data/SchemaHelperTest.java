package com.github.jcustenborder.kafka.connect.utils.data;

import com.google.common.base.MoreObjects;
import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Timestamp;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Stream;

import static com.github.jcustenborder.kafka.connect.utils.AssertSchema.assertSchema;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class SchemaHelperTest {

  static class TestCase {
    final Object input;
    final Schema expectedSchema;

    TestCase(Object input, Schema expectedSchema) {
      this.input = input;
      this.expectedSchema = expectedSchema;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("input", this.input.getClass().getSimpleName())
          .toString();
    }
  }

  static TestCase of(Object input, Schema expectedSchema) {
    return new TestCase(input, expectedSchema);
  }

  @TestFactory
  public Stream<DynamicTest> schema() {
    return Arrays.asList(
        of("", Schema.OPTIONAL_STRING_SCHEMA),
        of(Byte.MAX_VALUE, Schema.OPTIONAL_INT8_SCHEMA),
        of(Short.MAX_VALUE, Schema.OPTIONAL_INT16_SCHEMA),
        of(Integer.MAX_VALUE, Schema.OPTIONAL_INT32_SCHEMA),
        of(Long.MAX_VALUE, Schema.OPTIONAL_INT64_SCHEMA),
        of(Float.MAX_VALUE, Schema.OPTIONAL_FLOAT32_SCHEMA),
        of(Double.MAX_VALUE, Schema.OPTIONAL_FLOAT64_SCHEMA),
        of(new Date(), Timestamp.builder().optional().build()),
        of(BigDecimal.ONE, Decimal.builder(0).optional().build())
    )
        .stream()
        .map(t -> dynamicTest(t.toString(), () -> {
          final Schema actual = SchemaHelper.schema(t.input);
          assertSchema(t.expectedSchema, actual);
        }));
  }
}
