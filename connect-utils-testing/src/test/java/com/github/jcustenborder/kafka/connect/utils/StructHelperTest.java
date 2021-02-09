package com.github.jcustenborder.kafka.connect.utils;

import shaded.com.google.common.collect.ImmutableMap;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Schema.Type;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.github.jcustenborder.kafka.connect.utils.StructHelper.struct;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StructHelperTest {

  SchemaAndValue generate(int count) {
    SchemaBuilder builder = SchemaBuilder.struct();
    for (int i = 1; i <= count; i++) {
      String fieldName = String.format("f%s", i);
      builder.field(fieldName, Schema.INT32_SCHEMA);
    }

    final Schema schema = builder.build();
    final Struct struct = new Struct(schema);
    for (int i = 1; i <= count; i++) {
      String fieldName = String.format("f%s", i);
      struct.put(fieldName, i);
    }

    return new SchemaAndValue(schema, struct);
  }

  static final String SCHEMA_NAME = "foo";

  void assertFoo(int count, Struct struct) {
    assertEquals(SCHEMA_NAME, struct.schema().name(), "struct.schema().name() does not match.");
    assertNotNull(struct, "struct should not be null");

    assertEquals(count, struct.schema().fields().size(), "struct.schema().fields().size() does not match.");
    for (int i = 1; i <= count; i++) {
      final String fieldName = String.format("f%s", i);
      Field field = struct.schema().field(fieldName);
      assertNotNull(field, "schema should have field " + fieldName);
      assertEquals(Type.INT32, field.schema().type(), "schema().type() for " + fieldName + " does not match.");

      final Integer expectedValue = i;
      final Integer actualValue = struct.getInt32(fieldName);
      assertEquals(
          expectedValue,
          actualValue,
          String.format("value for field %s does not match", fieldName)
      );
    }

  }

  @Test
  public void asMap() {
    final Struct input = struct(
        "test",
        "f1", Type.INT32, false, 1,
        "f2", Type.STRUCT, false, struct("test2", "c1", Type.INT32, false, 11)
    );
    final Map<String, Object> expected = ImmutableMap.of(
        "f1", 1,
        "f2", ImmutableMap.of("c1", 11)
    );
    final Map<String, Object> actual = StructHelper.asMap(input);
    assertNotNull(actual, "actual should not be null");
    assertEquals(expected, actual);
  }

  @Test
  public void innerStruct() {
    final Struct actual = struct(
        "test",
        "f1", Type.INT32, false, 1,
        "f2", Type.STRUCT, false, struct("test2", "c1", Type.INT32, false, 11)
    );
    assertEquals("test", actual.schema().name());
    assertEquals(Type.INT32, actual.schema().field("f1").schema().type());
    assertEquals(Type.STRUCT, actual.schema().field("f2").schema().type());
    assertEquals("test2", actual.schema().field("f2").schema().name());
  }

  @Test
  public void struct1() {
    assertFoo(1,
        struct(SCHEMA_NAME, "f1", Type.INT32, false, 1)
    );
  }

  @Test
  public void struct2() {
    assertFoo(2,
        struct(SCHEMA_NAME,
            "f1", Type.INT32, false, 1,
            "f2", Type.INT32, false, 2
        )
    );
  }

  @Test
  public void struct3() {
    assertFoo(3,
        struct(SCHEMA_NAME,
            "f1", Type.INT32, false, 1,
            "f2", Type.INT32, false, 2,
            "f3", Type.INT32, false, 3
        )
    );
  }

  @Test
  public void struct4() {
    assertFoo(4,
        struct(SCHEMA_NAME,
            "f1", Type.INT32, false, 1,
            "f2", Type.INT32, false, 2,
            "f3", Type.INT32, false, 3,
            "f4", Type.INT32, false, 4
        )
    );
  }

  @Test
  public void struct5() {
    assertFoo(5,
        struct(SCHEMA_NAME,
            "f1", Type.INT32, false, 1,
            "f2", Type.INT32, false, 2,
            "f3", Type.INT32, false, 3,
            "f4", Type.INT32, false, 4,
            "f5", Type.INT32, false, 5
        )
    );
  }

  @Test
  public void struct6() {
    assertFoo(6,
        struct(SCHEMA_NAME,
            "f1", Type.INT32, false, 1,
            "f2", Type.INT32, false, 2,
            "f3", Type.INT32, false, 3,
            "f4", Type.INT32, false, 4,
            "f5", Type.INT32, false, 5,
            "f6", Type.INT32, false, 6
        )
    );
  }

  @Test
  public void struct7() {
    assertFoo(7,
        struct(SCHEMA_NAME,
            "f1", Type.INT32, false, 1,
            "f2", Type.INT32, false, 2,
            "f3", Type.INT32, false, 3,
            "f4", Type.INT32, false, 4,
            "f5", Type.INT32, false, 5,
            "f6", Type.INT32, false, 6,
            "f7", Type.INT32, false, 7
        )
    );
  }

  @Test
  public void struct8() {
    assertFoo(8,
        struct(SCHEMA_NAME,
            "f1", Type.INT32, false, 1,
            "f2", Type.INT32, false, 2,
            "f3", Type.INT32, false, 3,
            "f4", Type.INT32, false, 4,
            "f5", Type.INT32, false, 5,
            "f6", Type.INT32, false, 6,
            "f7", Type.INT32, false, 7,
            "f8", Type.INT32, false, 8
        )
    );
  }

  @Test
  public void struct9() {
    assertFoo(9,
        struct(SCHEMA_NAME,
            "f1", Type.INT32, false, 1,
            "f2", Type.INT32, false, 2,
            "f3", Type.INT32, false, 3,
            "f4", Type.INT32, false, 4,
            "f5", Type.INT32, false, 5,
            "f6", Type.INT32, false, 6,
            "f7", Type.INT32, false, 7,
            "f8", Type.INT32, false, 8,
            "f9", Type.INT32, false, 9
        )
    );
  }

  @Test
  public void struct10() {
    assertFoo(10,
        struct(SCHEMA_NAME,
            "f1", Type.INT32, false, 1,
            "f2", Type.INT32, false, 2,
            "f3", Type.INT32, false, 3,
            "f4", Type.INT32, false, 4,
            "f5", Type.INT32, false, 5,
            "f6", Type.INT32, false, 6,
            "f7", Type.INT32, false, 7,
            "f8", Type.INT32, false, 8,
            "f9", Type.INT32, false, 9,
            "f10", Type.INT32, false, 10
        )
    );
  }
}
