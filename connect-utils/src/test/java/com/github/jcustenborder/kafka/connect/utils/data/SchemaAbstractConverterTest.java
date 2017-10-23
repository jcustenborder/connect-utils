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
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.github.jcustenborder.kafka.connect.utils.AssertStruct.assertStruct;

public class SchemaAbstractConverterTest {

  @Test
  public void innerStruct() {
    final Schema innerSchema = SchemaBuilder.struct()
        .optional()
        .name("com.github.jcustenborder.kafka.connect.solr.Address")
        .field("address", Schema.OPTIONAL_STRING_SCHEMA)
        .field("city", Schema.OPTIONAL_STRING_SCHEMA)
        .field("state", Schema.OPTIONAL_STRING_SCHEMA)
        .field("zip", Schema.OPTIONAL_STRING_SCHEMA)
        .build();

    final Schema outerSchema = SchemaBuilder.struct()
        .name("com.github.jcustenborder.kafka.connect.solr.Outer")
        .field("firstName", Schema.OPTIONAL_STRING_SCHEMA)
        .field("lastName", Schema.OPTIONAL_STRING_SCHEMA)
        .field("address", innerSchema)
        .build();

    final Struct innerStruct = new Struct(innerSchema)
        .put("address", "123 Main St")
        .put("city", "Beverly Hills")
        .put("state", "CA")
        .put("zip", "90210");

    final Struct expected = new Struct(outerSchema)
        .put("firstName", "Example")
        .put("lastName", "User")
        .put("address", innerStruct);

    MockStructConverter converter = new MockStructConverter(outerSchema);
    final Struct actual = converter.convert(expected);
    assertStruct(expected, actual);
  }

  static class MockStructConverter extends AbstractConverter<Struct> {
    final Schema schema;

    MockStructConverter(Schema schema) {
      this.schema = schema;
    }

    @Override
    protected Struct newValue() {
      return new Struct(this.schema);
    }

    @Override
    protected void setStringField(Struct result, String fieldName, String value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setFloat32Field(Struct result, String fieldName, Float value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setFloat64Field(Struct result, String fieldName, Double value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setTimestampField(Struct result, String fieldName, Date value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setDateField(Struct result, String fieldName, Date value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setTimeField(Struct result, String fieldName, Date value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setInt8Field(Struct result, String fieldName, Byte value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setInt16Field(Struct result, String fieldName, Short value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setInt32Field(Struct result, String fieldName, Integer value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setInt64Field(Struct result, String fieldName, Long value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setBytesField(Struct result, String fieldName, byte[] value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setDecimalField(Struct result, String fieldName, BigDecimal value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setBooleanField(Struct result, String fieldName, Boolean value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setStructField(Struct result, String fieldName, Struct value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setArray(Struct result, String fieldName, Schema schema, List value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setMap(Struct result, String fieldName, Schema schema, Map value) {
      result.put(fieldName, value);
    }

    @Override
    protected void setNullField(Struct result, String fieldName) {
      result.put(fieldName, null);
    }
  }


}
