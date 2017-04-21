/**
 * Copyright © 2016 Jeremy Custenborder (jcustenborder@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jcustenborder.kafka.connect.utils;

import com.google.common.base.Strings;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;

import java.util.List;

import static com.github.jcustenborder.kafka.connect.utils.GenericAssertions.assertMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AssertSchema {
  private AssertSchema() {
  }

  public static void assertSchema(final Schema expected, final Schema actual) {
    assertSchema(expected, actual, null);
  }

  public static void assertSchema(final Schema expected, final Schema actual, String message) {
    final String prefix = Strings.isNullOrEmpty(message) ? "" : message + ": ";

    if (null == expected) {
      assertNull(actual, prefix + "actual should not be null.");
      return;
    }

    assertNotNull(expected, prefix + "expected schema should not be null.");
    assertNotNull(actual, prefix + "actual schema should not be null.");
    assertEquals(expected.name(), actual.name(), prefix + "schema.name() should match.");
    assertEquals(expected.type(), actual.type(), prefix + "schema.type() should match.");
    assertEquals(expected.defaultValue(), actual.defaultValue(), prefix + "schema.defaultValue() should match.");
    assertEquals(expected.isOptional(), actual.isOptional(), prefix + "schema.isOptional() should match.");
    assertEquals(expected.doc(), actual.doc(), prefix + "schema.doc() should match.");
    assertEquals(expected.version(), actual.version(), prefix + "schema.version() should match.");
    assertMap(expected.parameters(), actual.parameters(), prefix + "schema.parameters() should match.");

    if (null != expected.defaultValue()) {
      assertNotNull(actual.defaultValue(), "actual.defaultValue() should not be null.");

      Class<?> expectedType = null;

      switch (expected.type()) {
        case INT8:
          expectedType = Byte.class;
          break;
        case INT16:
          expectedType = Short.class;
          break;
        case INT32:
          expectedType = Integer.class;
          break;
        case INT64:
          expectedType = Long.class;
          break;
        case FLOAT32:
          expectedType = Float.class;
          break;
        case FLOAT64:
          expectedType = Float.class;
          break;
        default:
          break;
      }
      if (null != expectedType) {
        assertTrue(
            actual.defaultValue().getClass().isAssignableFrom(expectedType),
            String.format("actual.defaultValue() should be a %s", expectedType.getSimpleName())
        );
      }
    }

    switch (expected.type()) {
      case ARRAY:
        assertSchema(expected.valueSchema(), actual.valueSchema(), message + "valueSchema does not match.");
        break;
      case MAP:
        assertSchema(expected.keySchema(), actual.keySchema(), message + "keySchema does not match.");
        assertSchema(expected.valueSchema(), actual.valueSchema(), message + "valueSchema does not match.");
        break;
      case STRUCT:
        List<Field> expectedFields = expected.fields();
        List<Field> actualFields = actual.fields();
        assertEquals(expectedFields.size(), actualFields.size(), prefix + "Number of fields do not match.");
        for (int i = 0; i < expectedFields.size(); i++) {
          Field expectedField = expectedFields.get(i);
          Field actualField = actualFields.get(i);
          assertField(expectedField, actualField, "index " + i);
        }
        break;
    }
  }


  public static void assertField(final Field expected, final Field actual, String message) {
    String prefix = Strings.isNullOrEmpty(message) ? "" : message + ": ";
    if (null == expected) {
      assertNull(actual, prefix + "actual should be null.");
      return;
    }
    assertEquals(expected.name(), actual.name(), prefix + "name does not match");
    assertEquals(expected.index(), actual.index(), prefix + "name does not match");
    assertSchema(expected.schema(), actual.schema(), prefix + "schema does not match");
  }

  public static void assertField(final Field expected, final Field actual) {
    assertField(expected, actual, null);
  }

}
