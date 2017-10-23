/**
 * Copyright © 2016 Jeremy Custenborder (jcustenborder@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jcustenborder.kafka.connect.utils.data;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Timestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class SchemaKeyTest {
  private static final Logger log = LoggerFactory.getLogger(SchemaKeyTest.class);

  List<Test> tests;

  @BeforeEach
  public void before() {
    tests = new ArrayList<>();
    final ImmutableSet<Schema.Type> nonPrimitives = ImmutableSet.of(
        Schema.Type.ARRAY,
        Schema.Type.STRUCT,
        Schema.Type.MAP
    );

    tests.addAll(
        Arrays.stream(Schema.Type.values())
            .filter(type -> !nonPrimitives.contains(type))
            .map(type -> test(
                SchemaBuilder.type(type).build(),
                SchemaBuilder.type(type).build(),
                true
                )
            ).collect(Collectors.toList())
    );

    tests.addAll(
        Arrays.stream(Schema.Type.values())
            .filter(type -> !nonPrimitives.contains(type))
            .map(type -> test(
                SchemaBuilder.array(SchemaBuilder.type(type).build()).build(),
                SchemaBuilder.array(SchemaBuilder.type(type).build()).build(),
                true
                )
            ).collect(Collectors.toList())
    );


//    tests.addAll(
//        Arrays.stream(Schema.Type.values())
//            .filter(type -> !nonPrimitives.contains(type))
//            .map(type -> test(
//                SchemaBuilder.type(type).build(),
//                SchemaBuilder.type(type).optional().build(),
//                false
//                )
//            ).collect(Collectors.toList())
//    );

    tests.addAll(
        Arrays.asList(
            test(Schema.STRING_SCHEMA, Schema.STRING_SCHEMA, true),
            test(Schema.STRING_SCHEMA, Schema.BOOLEAN_SCHEMA, false),
            test(Timestamp.SCHEMA, Timestamp.SCHEMA, true),
            test(Timestamp.SCHEMA, Schema.INT64_SCHEMA, false)
        )
    );
  }

  @TestFactory
  public Stream<DynamicTest> compareTo() {
    return tests.stream().map(test -> dynamicTest(test.toString(), () -> {
      log.trace("left: {}", test.left);
      log.trace("right: {}", test.right);

      final SchemaKey left = SchemaKey.of(test.left);
      final SchemaKey right = SchemaKey.of(test.right);

      final int compare = left.compareTo(right);
      if (test.equals) {
        assertEquals(0, compare, "compareTo should be 0.");
      } else {
        assertNotEquals(0, compare, "compareTo should not be 0.");
      }
    }));
  }

  @TestFactory
  public Stream<DynamicTest> equals() {
    return tests.stream().map(test -> dynamicTest(test.toString(), () -> {
      log.trace("left: {}", test.left);
      log.trace("right: {}", test.right);

      final SchemaKey left = SchemaKey.of(test.left);

      final boolean equals = left.equals(test.right);
      if (test.equals) {
        assertTrue(equals, "should be equal.");
      } else {
        assertFalse(equals, "should not be equal.");
      }
    }));
  }

  static Test test(Schema left, Schema right, boolean equals) {
    return new Test(left, right, equals);
  }

  static class Test {
    public final Schema left;
    public final Schema right;
    public final boolean equals;

    Test(Schema left, Schema right, boolean equals) {
      this.left = left;
      this.right = right;
      this.equals = equals;
    }

    String toString(Schema schema) {
      return MoreObjects.toStringHelper("Schema")
          .add("type", schema.type())
          .add("version", schema.version())
          .add("name", schema.name())
          .omitNullValues()
          .toString();
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper("")
          .add("equals", this.equals)
          .add("left", toString(this.left))
          .add("right", toString(this.right))
          .toString();
    }
  }

}
