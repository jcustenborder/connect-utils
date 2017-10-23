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
package com.github.jcustenborder.kafka.connect.utils.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jcustenborder.kafka.connect.utils.data.type.BooleanParser;
import com.github.jcustenborder.kafka.connect.utils.data.type.DateTypeParser;
import com.github.jcustenborder.kafka.connect.utils.data.type.DecimalTypeParser;
import com.github.jcustenborder.kafka.connect.utils.data.type.Float32TypeParser;
import com.github.jcustenborder.kafka.connect.utils.data.type.Float64TypeParser;
import com.github.jcustenborder.kafka.connect.utils.data.type.Int16TypeParser;
import com.github.jcustenborder.kafka.connect.utils.data.type.Int32TypeParser;
import com.github.jcustenborder.kafka.connect.utils.data.type.Int64TypeParser;
import com.github.jcustenborder.kafka.connect.utils.data.type.Int8TypeParser;
import com.github.jcustenborder.kafka.connect.utils.data.type.StringTypeParser;
import com.github.jcustenborder.kafka.connect.utils.data.type.TimeTypeParser;
import com.github.jcustenborder.kafka.connect.utils.data.type.TimestampTypeParser;
import com.github.jcustenborder.kafka.connect.utils.data.type.TypeParser;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import org.apache.kafka.connect.data.Date;
import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.data.Time;
import org.apache.kafka.connect.data.Timestamp;
import org.apache.kafka.connect.errors.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Parser {
  private static final Logger log = LoggerFactory.getLogger(Parser.class);
  final Map<ParserKey, TypeParser> typeParsers;

  public Parser() {
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
    registerTypeParser(Date.SCHEMA, new DateTypeParser());
    registerTypeParser(Time.SCHEMA, new TimeTypeParser());
    registerTypeParser(Timestamp.SCHEMA, new TimestampTypeParser());
  }

  /**
   * Method is used to register a TypeParser for a given schema. If the schema is already registered, the new TypeParser
   * will replace the existing one.
   *
   * @param schema     Schema to register to. This supports any logical type by checking Schema.name() and schema.type().
   * @param typeParser The type parser to register for schema.
   */
  public final void registerTypeParser(Schema schema, TypeParser typeParser) {
    Preconditions.checkNotNull(schema, "schema cannot be null.");
    Preconditions.checkNotNull(typeParser, "typeParser cannot be null.");
    this.typeParsers.put(new ParserKey(schema), typeParser);
  }

  /**
   * Method is used to parse String data to the proper Java types.
   *
   * @param schema Input schema to parse the String data by.
   * @param input  Java type specific to the schema supplied.
   * @return Java type for the
   * @throws DataException                 Exception is thrown when there is an exception thrown while parsing the input string.
   * @throws UnsupportedOperationException Exception is thrown if there is no type parser registered for the schema.
   * @throws NullPointerException          Exception is thrown if the schema passed is not optional and a null input value is passed.
   */
  public Object parseString(Schema schema, String input) {
    checkSchemaAndInput(schema, input);

    if (null == input) {
      return null;
    }

    TypeParser parser = findParser(schema);

    try {
      Object result = parser.parseString(input, schema);
      return result;
    } catch (Exception ex) {
      String message = String.format("Could not parse '%s' to '%s'", input, parser.expectedClass().getSimpleName());
      throw new DataException(message, ex);
    }
  }

  void checkSchemaAndInput(Schema schema, Object input) {
    Preconditions.checkNotNull(schema, "schema cannot be null");
    if (!schema.isOptional()) {
      Preconditions.checkNotNull(input, "schema is not optional so input cannot be null.");
    }
  }

  TypeParser findParser(Schema schema) {
    ParserKey parserKey = new ParserKey(schema);
    TypeParser parser = this.typeParsers.get(parserKey);
    if (null == parser) {
      throw new UnsupportedOperationException(
          String.format("Schema %s(%s) is not supported", schema.type(), schema.name())
      );
    }
    return parser;
  }

  public Object parseJsonNode(Schema schema, JsonNode input) {
    checkSchemaAndInput(schema, input);

    if (null == input || input.isNull()) {
      return null;
    }

    log.trace("parseJsonNode() - schema.type() = {}", schema.type());

    Object result;

    if (Schema.Type.STRUCT == schema.type()) {
      Struct struct = new Struct(schema);
      Preconditions.checkState(input.isObject(), "struct schemas require a ObjectNode to be supplied for input.");
      log.trace("parseJsonNode() - Processing as struct.");
      final Set<String> processedFields = Sets.newHashSetWithExpectedSize(schema.fields().size());
      for (Field field : schema.fields()) {
        log.trace("parseJsonNode() - Processing field '{}:{}'", schema.name(), field.name());
        JsonNode fieldInput = input.findValue(field.name());
        try {
          Object convertedValue = parseJsonNode(field.schema(), fieldInput);
          struct.put(field, convertedValue);
        } catch (Exception ex) {
          throw new DataException(
              String.format("Exception thrown while processing %s:%s", schema.name(), field.name()),
              ex
          );
        }
        processedFields.add(field.name());
      }

      if (log.isTraceEnabled()) {
        final Set<String> jsonFieldNames = Sets.newLinkedHashSet(ImmutableList.copyOf(input.fieldNames()));
        Sets.SetView<String> difference = Sets.difference(jsonFieldNames, processedFields);
        if (!difference.isEmpty()) {
          log.trace("parseJsonNode() - Unprocessed fields for {}:\n{}", schema.name(), Joiner.on('\n').join(difference));
        }
      }

      result = struct;
    } else if (Schema.Type.ARRAY == schema.type()) {
      Preconditions.checkState(input.isArray(), "array schemas require a ArrayNode to be supplied for input.");
      log.trace("parseJsonNode() - Processing as array.");
      List<Object> array = new ArrayList<>();
      Iterator<JsonNode> arrayIterator = input.iterator();
      int index = 0;
      while (arrayIterator.hasNext()) {
        log.trace("parseJsonNode() - Processing index {}", index);
        JsonNode arrayInput = arrayIterator.next();
        try {
          Object arrayResult = parseJsonNode(schema.valueSchema(), arrayInput);
          array.add(arrayResult);
        } catch (Exception ex) {
          throw new DataException(
              String.format("Exception thrown while processing index %s", index),
              ex
          );
        }
        index++;
      }
      result = array;
    } else if (Schema.Type.MAP == schema.type()) {
      Preconditions.checkState(input.isObject(), "map schemas require a ObjectNode to be supplied for input.");
      log.trace("parseJsonNode() - Processing as map.");
      Map<Object, Object> map = new LinkedHashMap<>();
      Iterator<String> fieldNameIterator = input.fieldNames();

      while (fieldNameIterator.hasNext()) {
        final String fieldName = fieldNameIterator.next();
        final JsonNode fieldInput = input.findValue(fieldName);
        log.trace("parseJsonNode() - Processing key. Key='{}'", fieldName);
        final Object mapKey;
        try {
          mapKey = parseString(schema.keySchema(), fieldName);
        } catch (Exception ex) {
          throw new DataException(
              String.format("Exception thrown while parsing key. Key='%s'", fieldName),
              ex
          );
        }
        log.trace("parseJsonNode() - Processing value. Key='{}'", fieldName);
        final Object mapValue;
        try {
          mapValue = parseJsonNode(schema.keySchema(), fieldInput);
        } catch (Exception ex) {
          throw new DataException(
              String.format("Exception thrown while parsing value. Key='%s'", fieldName),
              ex
          );
        }
        map.put(mapKey, mapValue);
      }

      result = map;
    } else {
      TypeParser parser = findParser(schema);

      try {
        result = parser.parseJsonNode(input, schema);
      } catch (Exception ex) {
        String message = String.format("Could not parse '%s' to %s", input, parser.expectedClass().getSimpleName());
        throw new DataException(message, ex);
      }
    }

    return result;
  }

}
