package com.github.jcustenborder.kafka.connect.utils.data;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.github.jcustenborder.kafka.connect.utils.AssertSchema.assertSchema;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class SchemaBuildersTest {

  @Test
  public void excludeFields() {
    final Schema input = SchemaBuilder.struct()
        .field("firstName", Schema.STRING_SCHEMA)
        .field("lastName", Schema.STRING_SCHEMA)
        .build();
    final Schema expected = SchemaBuilder.struct()
        .field("firstName", Schema.STRING_SCHEMA)
        .build();
    SchemaBuilder builder = SchemaBuilders.of(input, "lastName");
    assertEquals(expected, builder.build());
  }

  @TestFactory
  public Stream<DynamicTest> of() {
    List<Schema> schemas = new ArrayList<>();
    schemas.add(
        SchemaBuilder.struct()
            .optional()
            .field("firstName", Schema.OPTIONAL_STRING_SCHEMA)
            .field("lastName", Schema.OPTIONAL_STRING_SCHEMA)
            .version(32)
            .doc("This is a document")
            .build()
    );
    schemas.add(
        SchemaBuilder.array(Schema.STRING_SCHEMA)
            .optional()
            .version(12)
            .doc("This is a document")
            .build()
    );
    schemas.add(
        SchemaBuilder.map(Schema.STRING_SCHEMA, Schema.STRING_SCHEMA)
            .optional()
            .version(12)
            .doc("This is a document")
            .build()
    );
    Set<Schema.Type> skip = ImmutableSet.of(Schema.Type.ARRAY, Schema.Type.MAP, Schema.Type.STRUCT);
    Arrays.stream(Schema.Type.values())
        .filter(e -> !skip.contains(e))
        .map(e -> SchemaBuilder.type(e).build())
        .forEach(schemas::add);
    Arrays.stream(Schema.Type.values())
        .filter(e -> !skip.contains(e))
        .map(e -> SchemaBuilder.type(e).optional().build())
        .forEach(schemas::add);
    Arrays.stream(Schema.Type.values())
        .filter(e -> !skip.contains(e))
        .map(e -> SchemaBuilder.type(e).version(7).build())
        .forEach(schemas::add);
    Arrays.stream(Schema.Type.values())
        .filter(e -> !skip.contains(e))
        .map(e -> SchemaBuilder.type(e).parameter("foo", "bar").build())
        .forEach(schemas::add);
    Arrays.stream(Schema.Type.values())
        .filter(e -> !skip.contains(e))
        .map(e -> SchemaBuilder.type(e).name("primitive").build())
        .forEach(schemas::add);

    return schemas.stream().map(expected -> dynamicTest(builderOfTestCaseName(expected), () -> {
      SchemaBuilder builder = SchemaBuilders.of(expected);
      assertNotNull(builder, "builder should not be null.");
      Schema actual = builder.build();
      assertSchema(expected, actual);
    }));
  }

  String builderOfTestCaseName(Schema schema) {
    StringBuilder builder = new StringBuilder();
    builder.append(schema.type());
    if (!Strings.isNullOrEmpty(schema.name())) {
      builder.append("(");
      builder.append(schema.name());
      builder.append(")");
    }
    switch (schema.type()) {
      case ARRAY:
        builder.append("[");
        builder.append(builderOfTestCaseName(schema.valueSchema()));
        builder.append("]");
        break;
      case MAP:
        builder.append("[");
        builder.append(builderOfTestCaseName(schema.valueSchema()));
        builder.append(",");
        builder.append(builderOfTestCaseName(schema.valueSchema()));
        builder.append("]");
        break;
    }

    if (schema.isOptional()) {
      builder.append(":optional");
    }
    if (null != schema.version()) {
      builder.append(":version(");
      builder.append(schema.version());
      builder.append(")");
    }
    if (null != schema.parameters() && !schema.parameters().isEmpty()) {
      builder.append(":parameters(");
      builder.append(schema.parameters());
      builder.append(")");
    }


    return builder.toString();
  }
}
