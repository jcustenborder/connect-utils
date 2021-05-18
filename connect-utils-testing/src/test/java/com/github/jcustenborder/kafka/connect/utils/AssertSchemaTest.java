/**
 * Copyright © 2016 Jeremy Custenborder (jcustenborder@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jcustenborder.kafka.connect.utils;

import com.google.common.base.MoreObjects;
import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.Schema;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.opentest4j.AssertionFailedError;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class AssertSchemaTest {

  class TestCase {
    public final Schema expected;
    public final Schema actual;
    public final boolean isEqual;


    TestCase(Schema expected, Schema actual, boolean isEqual) {
      this.expected = expected;
      this.actual = actual;
      this.isEqual = isEqual;
    }

    String toString(Schema schema) {
      return MoreObjects.toStringHelper(schema)
          .add("type", schema.type())
          .add("name", schema.name())
          .add("isOptional", schema.isOptional())
          .add("version", schema.version())
          .omitNullValues()
          .toString();
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(TestCase.class)
          .add("isEqual", this.isEqual)
          .add("expected", null != this.expected ? toString(this.expected) : null)
          .add("actual", null != this.actual ? toString(this.actual) : null)
          .toString();
    }
  }

  void of(List<TestCase> tests, Schema expected, Schema actual, boolean isEqual) {
    tests.add(new TestCase(expected, actual, isEqual));
  }

  @TestFactory
  public Stream<DynamicTest> assertSchema() {
    List<TestCase> tests = new ArrayList<>();
    of(tests, Schema.STRING_SCHEMA, Schema.STRING_SCHEMA, true);
    of(tests, Schema.STRING_SCHEMA, Schema.OPTIONAL_STRING_SCHEMA, false);
    of(tests, Schema.BYTES_SCHEMA, Decimal.schema(4), false);
    of(tests, null, null, true);
    of(tests, Schema.STRING_SCHEMA, null, false);
    of(tests, null, Schema.STRING_SCHEMA, false);

    return tests.stream().map(testCase -> dynamicTest(testCase.toString(), () -> {
      if (testCase.isEqual) {
        AssertSchema.assertSchema(testCase.expected, testCase.actual);
      } else {
        assertThrows(AssertionFailedError.class, () -> {
          AssertSchema.assertSchema(testCase.expected, testCase.actual);
        });
      }
    }));
  }
}
