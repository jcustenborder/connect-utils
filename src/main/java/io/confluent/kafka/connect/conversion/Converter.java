/**
 * Copyright (C) 2016 Jeremy Custenborder (jcustenborder@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.confluent.kafka.connect.conversion;

import com.google.common.base.Preconditions;
import io.confluent.kafka.connect.conversion.type.BooleanConverter;
import io.confluent.kafka.connect.conversion.type.DateTypeConverter;
import io.confluent.kafka.connect.conversion.type.DecimalTypeConverter;
import io.confluent.kafka.connect.conversion.type.Float32TypeConverter;
import io.confluent.kafka.connect.conversion.type.Float64TypeConverter;
import io.confluent.kafka.connect.conversion.type.Int16TypeConverter;
import io.confluent.kafka.connect.conversion.type.Int32TypeConverter;
import io.confluent.kafka.connect.conversion.type.Int64TypeConverter;
import io.confluent.kafka.connect.conversion.type.Int8TypeConverter;
import io.confluent.kafka.connect.conversion.type.TypeConverter;
import org.apache.kafka.connect.data.Date;
import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Time;
import org.apache.kafka.connect.data.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class Converter {
  final Map<ConverterKey, TypeConverter> converters;

  public Converter() {
    this.converters = new HashMap<>();
    registerTypeConverter(Schema.BOOLEAN_SCHEMA, new BooleanConverter());
    registerTypeConverter(Schema.BOOLEAN_SCHEMA, new BooleanConverter());
    registerTypeConverter(Schema.FLOAT32_SCHEMA, new Float32TypeConverter());
    registerTypeConverter(Schema.FLOAT64_SCHEMA, new Float64TypeConverter());
    registerTypeConverter(Schema.INT8_SCHEMA, new Int8TypeConverter());
    registerTypeConverter(Schema.INT16_SCHEMA, new Int16TypeConverter());
    registerTypeConverter(Schema.INT32_SCHEMA, new Int32TypeConverter());
    registerTypeConverter(Schema.INT64_SCHEMA, new Int64TypeConverter());
    registerTypeConverter(Decimal.schema(1), new DecimalTypeConverter());
    registerTypeConverter(Date.SCHEMA, DateTypeConverter.createDefaultDateConverter());
    registerTypeConverter(Time.SCHEMA, DateTypeConverter.createDefaultTimeConverter());
    registerTypeConverter(Timestamp.SCHEMA, DateTypeConverter.createDefaultTimestampConverter());
  }

  public final void registerTypeConverter(Schema schema, TypeConverter typeConverter) {
    this.converters.put(new ConverterKey(schema), typeConverter);
  }

  public Object convert(Schema schema, String input) {
    Preconditions.checkNotNull(schema, "schema cannot be null");
    ConverterKey converterKey = new ConverterKey(schema);
    TypeConverter converter = this.converters.get(converterKey);

    if (!schema.isOptional()) {
      Preconditions.checkNotNull(input, "schema is not optional so input cannot be null.");
    }

    if (null == input && schema.isOptional()) {
      return null;
    }

    if (null == converter) {
      throw new UnsupportedOperationException(
          String.format("Schema %s(%s) is not supported", schema.type(), schema.name())
      );
    }
    Object result = converter.convert(input);
    return result;
  }

}
