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
import com.google.common.io.BaseEncoding;
import org.apache.kafka.connect.data.Date;
import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.data.Time;
import org.apache.kafka.connect.data.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.github.jcustenborder.kafka.connect.utils.AssertSchema.assertSchema;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AssertStruct {
  private static final Logger log = LoggerFactory.getLogger(AssertStruct.class);

  static <T> T castAndVerify(Class<T> cls, Struct struct, Field field, boolean expected) {
    final Object value = struct.get(field.name());

    final String prefix = String.format(
        "%s('%s') ",
        expected ? "expected" : "actual",
        field.name()
    );

    if (!field.schema().isOptional()) {
      assertNotNull(
          value,
          prefix + "has a require schema. Should not be null."
      );
    }

    if (null == value) {
      return null;
    }

    assertTrue(
        cls.isInstance(value),
        String.format(
            prefix + "should be a %s",
            cls.getSimpleName()
        )
    );

    return cls.cast(value);
  }

  public static void assertStruct(final Struct expected, final Struct actual, String message) {
    String prefix = Strings.isNullOrEmpty(message) ? "" : message + ": ";

    if (null == expected) {
      assertNull(actual, prefix + "actual should be null.");
      return;
    }

    assertSchema(expected.schema(), actual.schema(), "schema does not match.");
    for (Field expectedField : expected.schema().fields()) {
      log.trace("assertStruct() - testing field '{}'", expectedField.name());
      final Object expectedValue = expected.get(expectedField.name());
      final Object actualValue = actual.get(expectedField.name());

      if (Decimal.LOGICAL_NAME.equals(expectedField.schema().name())) {
        BigDecimal expectedDecimal = castAndVerify(BigDecimal.class, expected, expectedField, true);
        BigDecimal actualDecimal = castAndVerify(BigDecimal.class, actual, expectedField, false);
        assertEquals(expectedDecimal, actualDecimal, prefix + expectedField.name() + " does not match.");
      } else if (Timestamp.LOGICAL_NAME.equals(expectedField.schema().name())
          || Date.LOGICAL_NAME.equals(expectedField.schema().name())
          || Time.LOGICAL_NAME.equals(expectedField.schema().name())) {
        java.util.Date expectedDate = castAndVerify(java.util.Date.class, expected, expectedField, true);
        java.util.Date actualDate = castAndVerify(java.util.Date.class, actual, expectedField, false);
        assertEquals(expectedDate, actualDate, prefix + expectedField.name() + " does not match.");
      } else {
        switch (expectedField.schema().type()) {
          case ARRAY:
            assertTrue(null == expectedValue || expectedValue instanceof List);
            assertTrue(null == actualValue || actualValue instanceof List);
            List<Object> expectedArray = (List<Object>) expectedValue;
            List<Object> actualArray = (List<Object>) actualValue;
            assertEquals(expectedArray, actualArray, prefix + expectedField.name() + " does not match.");
            break;
          case MAP:
            assertTrue(null == expectedValue || expectedValue instanceof Map);
            assertTrue(null == actualValue || actualValue instanceof Map);
            Map<Object, Object> expectedMap = (Map<Object, Object>) expectedValue;
            Map<Object, Object> actualMap = (Map<Object, Object>) actualValue;
            assertEquals(expectedMap, actualMap, prefix + expectedField.name() + " does not match.");
            break;
          case STRUCT:
            assertTrue(null == expectedValue || expectedValue instanceof Struct);
            assertTrue(null == actualValue || actualValue instanceof Struct);
            Struct expectedStruct = (Struct) expectedValue;
            Struct actualStruct = (Struct) actualValue;
            assertStruct(expectedStruct, actualStruct, prefix + expectedField.name() + " does not match.");
            break;
          case BYTES:
            assertTrue(null == expectedValue || expectedValue instanceof byte[]);
            assertTrue(null == actualValue || actualValue instanceof byte[]);
            byte[] expectedByteArray = (byte[]) expectedValue;
            byte[] actualByteArray = (byte[]) actualValue;
            assertEquals(
                null == expectedByteArray ? "" : BaseEncoding.base32Hex().encode(expectedByteArray).toString(),
                null == actualByteArray ? "" : BaseEncoding.base32Hex().encode(actualByteArray).toString(),
                prefix + expectedField.name() + " does not match."
            );
            break;
          default:
            assertEquals(expectedValue, actualValue, prefix + expectedField.name() + " does not match.");
            break;
        }
      }
    }
  }

  public static void assertStruct(final Struct expected, final Struct actual) {
    assertStruct(expected, actual, null);
  }
}
