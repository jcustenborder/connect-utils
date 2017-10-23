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

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.github.jcustenborder.kafka.connect.utils.GenericAssertions.assertMap;

public class SchemaLessAbstractConverterTest {
  @Test
  public void innerStruct() {
    final Map<String, Object> inner = new LinkedHashMap<>();
    inner.put("address", "123 Main St");
    inner.put("city", "Beverly Hills");
    inner.put("state", "CA");
    inner.put("zip", "90210");

    final Map<String, Object> expected = new LinkedHashMap<>();
    expected.put("firstName", "Example");
    expected.put("lastName", "User");
    expected.put("address", inner);

    MockMapConverter converter = new MockMapConverter();
    final Map<String, Object> actual = converter.convert(expected);
    assertMap(expected, actual, "");
  }

  static class MockMapConverter extends AbstractConverter<Map<String, Object>> {
    @Override
    protected Map<String, Object> newValue() {
      return new LinkedHashMap<>();
    }

    @Override
    protected void setStringField(Map<String, Object> result, String fieldName, String value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setFloat32Field(Map<String, Object> result, String fieldName, Float value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setFloat64Field(Map<String, Object> result, String fieldName, Double value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setTimestampField(Map<String, Object> result, String fieldName, Date value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setDateField(Map<String, Object> result, String fieldName, Date value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setTimeField(Map<String, Object> result, String fieldName, Date value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setInt8Field(Map<String, Object> result, String fieldName, Byte value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setInt16Field(Map<String, Object> result, String fieldName, Short value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setInt32Field(Map<String, Object> result, String fieldName, Integer value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setInt64Field(Map<String, Object> result, String fieldName, Long value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setBytesField(Map<String, Object> result, String fieldName, byte[] value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setDecimalField(Map<String, Object> result, String fieldName, BigDecimal value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setBooleanField(Map<String, Object> result, String fieldName, Boolean value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setStructField(Map<String, Object> result, String fieldName, Struct value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setArray(Map<String, Object> result, String fieldName, Schema schema, List value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setMap(Map<String, Object> result, String fieldName, Schema schema, Map value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setNullField(Map<String, Object> result, String fieldName) {
      result.put(fieldName, null);
    }
  }
}
