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
import org.apache.kafka.connect.errors.DataException;

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

  /**
   * Method is used to register a TypeParser for a given schema. If the schema is already registered, the new TypeParser
   * will replace the existing one.
   *
   * @param schema     Schema to register to. This supports any logical type by checking Schema.name() and schema.type().
   * @param typeParser
   */
  public final void registerTypeParser(Schema schema, TypeParser typeParser) {
    Preconditions.checkNotNull(schema, "schema cannot be null.");
    Preconditions.checkNotNull(typeParser, "typeParser cannot be null.");
    this.typeParsers.put(new ParserKey(schema), typeParser);
  }

  /**
   * Method is used to parse String data to the proper Java types.
   * @param schema Input schema to parse the String data by.
   * @param input Java type specific to the schema supplied.
   * @return Java type for the
   * @exception DataException Exception is thrown when there is an exception thrown while parsing the input string.
   * @exception UnsupportedOperationException Exception is thrown if there is no type parser registered for the schema.
   * @exception NullPointerException Exception is thrown if the schema passed is not optional and a null input value is passed.
   */
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

    try {
      Object result = parser.parseString(input, schema);
      return result;
    } catch (Exception ex) {
      String message = String.format("Could not parse '%s' to '%s'", input, parser.expectedClass());
      throw new DataException(message, ex);
    }
  }

}
