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
import org.apache.kafka.connect.data.Struct;

import static com.github.jcustenborder.kafka.connect.utils.AssertSchema.assertSchema;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AssertStruct {
  public static void assertStruct(final Struct expected, final Struct actual, String message) {
    String prefix = Strings.isNullOrEmpty(message) ? "" : message + ": ";

    if (null == expected) {
      assertNull(actual, prefix + "actual should be null.");
      return;
    }

    assertSchema(expected.schema(), actual.schema(), "schema does not match.");
    for (Field field : expected.schema().fields()) {
      final Object expectedValue = expected.get(field.name());
      final Object actualValue = expected.get(field.name());

      switch (field.schema().type()) {
        case ARRAY:
          break;
        case MAP:
          break;
        case STRUCT:
          break;
        default:
          assertEquals(expectedValue, actualValue, prefix + field.name() + " does not match.");
          break;
      }
    }
  }

  public static void assertStruct(final Struct expected, final Struct actual) {
    assertStruct(expected, actual, null);
  }
}
