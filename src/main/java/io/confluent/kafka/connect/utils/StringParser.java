/**
 * Copyright (C) 2016 Jeremy Custenborder (jcustenborder@gmail.com)
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
package io.confluent.kafka.connect.utils;

import com.google.common.base.Preconditions;
import io.confluent.kafka.connect.utils.type.BooleanParser;
import io.confluent.kafka.connect.utils.type.DateTypeParser;
import io.confluent.kafka.connect.utils.type.DecimalTypeParser;
import io.confluent.kafka.connect.utils.type.Float32TypeParser;
import io.confluent.kafka.connect.utils.type.Float64TypeParser;
import io.confluent.kafka.connect.utils.type.Int16TypeParser;
import io.confluent.kafka.connect.utils.type.Int32TypeParser;
import io.confluent.kafka.connect.utils.type.Int64TypeParser;
import io.confluent.kafka.connect.utils.type.Int8TypeParser;
import io.confluent.kafka.connect.utils.type.StringTypeParser;
import io.confluent.kafka.connect.utils.type.TypeParser;
import org.apache.kafka.connect.data.Date;
import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Time;
import org.apache.kafka.connect.data.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class StringParser {
  final Map<ParserKey, TypeParser> typeParsers;

  public StringParser() {
    this.typeParsers = new HashMap<>();
    registerTypeParser(Schema.BOOLEAN_SCHEMA, new BooleanParser());
    registerTypeParser(Schema.BOOLEAN_SCHEMA, new BooleanParser());
    registerTypeParser(Schema.FLOAT32_SCHEMA, new Float32TypeParser());
    registerTypeParser(Schema.FLOAT64_SCHEMA, new Float64TypeParser());
    registerTypeParser(Schema.INT8_SCHEMA, new Int8TypeParser());
    registerTypeParser(Schema.INT16_SCHEMA, new Int16TypeParser());
    registerTypeParser(Schema.INT32_SCHEMA, new Int32TypeParser());
    registerTypeParser(Schema.INT64_SCHEMA, new Int64TypeParser());
    registerTypeParser(Schema.STRING_SCHEMA, new StringTypeParser());
    registerTypeParser(Decimal.schema(1), new DecimalTypeParser());
    registerTypeParser(Date.SCHEMA, DateTypeParser.createDefaultDateConverter());
    registerTypeParser(Time.SCHEMA, DateTypeParser.createDefaultTimeConverter());
    registerTypeParser(Timestamp.SCHEMA, DateTypeParser.createDefaultTimestampConverter());
  }

  public final void registerTypeParser(Schema schema, TypeParser typeParser) {
    this.typeParsers.put(new ParserKey(schema), typeParser);
  }

  public Object parseString(Schema schema, String input) {
    Preconditions.checkNotNull(schema, "schema cannot be null");
    ParserKey parserKey = new ParserKey(schema);
    TypeParser parser = this.typeParsers.get(parserKey);

    if (!schema.isOptional()) {
      Preconditions.checkNotNull(input, "schema is not optional so input cannot be null.");
    }

    if (null == input && schema.isOptional()) {
      return null;
    }

    if (null == parser) {
      throw new UnsupportedOperationException(
          String.format("Schema %s(%s) is not supported", schema.type(), schema.name())
      );
    }
    Object result = parser.parseString(input, schema);
    return result;
  }

}
