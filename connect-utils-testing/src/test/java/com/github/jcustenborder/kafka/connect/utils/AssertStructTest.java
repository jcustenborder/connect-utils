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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.kafka.connect.data.Date;
import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.data.Time;
import org.apache.kafka.connect.data.Timestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.opentest4j.AssertionFailedError;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AssertStructTest {

  Schema pointSchema;
  Schema simple;
  Schema arraySchema;
  Schema mapSchema;
  Schema nestedSchema;
  Schema structBytesSchema;
  Schema decimalSchema;
  Schema timestampSchema;
  Schema dateSchema;
  Schema timeSchema;

  Map<String, Struct> mapOfStructs;
  List<Struct> arrayOfStructs;
  byte[] buffer;
  java.util.Date date = new java.util.Date(1494023828123L);

  @BeforeEach
  public void createSchemas() {
    this.decimalSchema = SchemaBuilder.struct()
        .name("com.github.jcustenborder.kafka.connect.utils.Decimal")
        .field("decimal", Decimal.builder(1).optional().build())
        .build();
    this.timestampSchema = SchemaBuilder.struct()
        .name("com.github.jcustenborder.kafka.connect.utils.Timestamp")
        .field("timestamp", Timestamp.builder().optional().build())
        .build();
    this.dateSchema = SchemaBuilder.struct()
        .name("com.github.jcustenborder.kafka.connect.utils.Date")
        .field("date", Date.builder().optional().build())
        .build();
    this.timeSchema = SchemaBuilder.struct()
        .name("com.github.jcustenborder.kafka.connect.utils.Time")
        .field("time", Time.builder().optional().build())
        .build();

    this.simple = SchemaBuilder.struct()
        .name("com.github.jcustenborder.kafka.connect.utils.Simple")
        .optional()
        .field("text", Schema.OPTIONAL_STRING_SCHEMA)
        .build();

    this.pointSchema = SchemaBuilder.struct()
        .name("com.github.jcustenborder.kafka.connect.utils.Point")
        .optional()
        .field("latitude", Schema.FLOAT32_SCHEMA)
        .field("longitude", Schema.FLOAT32_SCHEMA)
        .build();

    this.nestedSchema = SchemaBuilder.struct()
        .name("com.github.jcustenborder.kafka.connect.utils.Nested")
        .field("point", this.pointSchema)
        .build();

    this.arraySchema = SchemaBuilder.struct()
        .name("com.github.jcustenborder.kafka.connect.utils.ArrayOfPoint")
        .field("array", SchemaBuilder.array(this.pointSchema).optional().build())
        .build();

    this.mapSchema = SchemaBuilder.struct()
        .name("com.github.jcustenborder.kafka.connect.utils.MapOfPoint")
        .field("map", SchemaBuilder.map(Schema.STRING_SCHEMA, this.pointSchema).optional().build())
        .build();

    this.structBytesSchema = SchemaBuilder.struct()
        .name("com.github.jcustenborder.kafka.connect.utils.Bytes")
        .field("bytes", Schema.OPTIONAL_BYTES_SCHEMA)
        .build();
    this.buffer = new byte[10];
    new Random().nextBytes(this.buffer);
  }

  @BeforeEach
  public void createStructs() {
    this.mapOfStructs = ImmutableMap.of(
        "Austin", new Struct(pointSchema).put("latitude", 30.2672F).put("longitude", 97.7431F),
        "San Francisco", new Struct(pointSchema).put("latitude", 37.7749F).put("longitude", 122.4194F)
    );

    this.arrayOfStructs = ImmutableList.copyOf(
        mapOfStructs.values()
    );
  }


  void test(Struct expected, Struct actual, boolean matches) {
    if (matches) {
      AssertStruct.assertStruct(expected, actual);
    } else {
      assertThrows(AssertionFailedError.class, () -> {
        AssertStruct.assertStruct(expected, actual);
      });
    }
  }

  @TestFactory
  public Stream<DynamicTest> assertStruct() {
    List<DynamicTest> tests = Arrays.asList(
        DynamicTest.dynamicTest("simpleSame", () -> {
          final Struct expected = new Struct(simple).put("text", "this is the same.");
          final Struct actual = new Struct(simple).put("text", "this is the same.");
          test(expected, actual, true);
        }),
        DynamicTest.dynamicTest("simpleDiff", () -> {
          final Struct expected = new Struct(simple).put("text", "this is the same.");
          final Struct actual = new Struct(simple).put("text", "this is different.");
          test(expected, actual, false);
        }),
        DynamicTest.dynamicTest("simpleNullExpected", () -> {
          final Struct expected = new Struct(simple).put("text", null);
          final Struct actual = new Struct(simple).put("text", "this is the same.");
          test(expected, actual, false);
        }),
        DynamicTest.dynamicTest("simpleNullActual", () -> {
          final Struct expected = new Struct(simple).put("text", "this is the same.");
          final Struct actual = new Struct(simple).put("text", null);
          test(expected, actual, false);
        }),
        DynamicTest.dynamicTest("mapSame", () -> {
          final Struct expected = new Struct(this.mapSchema).put("map", mapOfStructs);
          final Struct actual = new Struct(this.mapSchema).put("map", mapOfStructs);
          test(expected, actual, true);
        }),
        DynamicTest.dynamicTest("mapNullExpected", () -> {
          final Struct expected = new Struct(this.mapSchema).put("map", null);
          final Struct actual = new Struct(this.mapSchema).put("map", mapOfStructs);
          test(expected, actual, false);
        }),
        DynamicTest.dynamicTest("mapNullActual", () -> {
          final Struct expected = new Struct(this.mapSchema).put("map", mapOfStructs);
          final Struct actual = new Struct(this.mapSchema).put("map", null);
          test(expected, actual, false);
        }),
        DynamicTest.dynamicTest("arraySame", () -> {
          final Struct expected = new Struct(this.arraySchema).put("array", arrayOfStructs);
          final Struct actual = new Struct(this.arraySchema).put("array", arrayOfStructs);
          test(expected, actual, true);
        }),
        DynamicTest.dynamicTest("arrayNullExpected", () -> {
          final Struct expected = new Struct(this.arraySchema).put("array", null);
          final Struct actual = new Struct(this.arraySchema).put("array", arrayOfStructs);
          test(expected, actual, false);
        }),
        DynamicTest.dynamicTest("arrayNullActual", () -> {
          final Struct expected = new Struct(this.arraySchema).put("array", arrayOfStructs);
          final Struct actual = new Struct(this.arraySchema).put("array", null);
          test(expected, actual, false);
        }),
        DynamicTest.dynamicTest("arrayDiff", () -> {
          final Struct expected = new Struct(this.arraySchema).put("array", arrayOfStructs);
          final Struct actual = new Struct(this.arraySchema).put("array", Lists.reverse(arrayOfStructs));
          test(expected, actual, false);
        }),
        DynamicTest.dynamicTest("structBytesSchemaSame", () -> {
          final Struct expected = new Struct(this.structBytesSchema).put("bytes", this.buffer);
          final Struct actual = new Struct(this.structBytesSchema).put("bytes", this.buffer);
          test(expected, actual, true);
        }),
        DynamicTest.dynamicTest("structBytesSchemaDiff", () -> {
          final Struct expected = new Struct(this.structBytesSchema).put("bytes", this.buffer);
          final Struct actual = new Struct(this.structBytesSchema).put("bytes", new byte[10]);
          test(expected, actual, false);
        }),
        DynamicTest.dynamicTest("structBytesSchemaActualNull", () -> {
          final Struct expected = new Struct(this.structBytesSchema).put("bytes", this.buffer);
          final Struct actual = new Struct(this.structBytesSchema).put("bytes", null);
          test(expected, actual, false);
        }),
        DynamicTest.dynamicTest("structDecimal", () -> {
          final Struct expected = new Struct(this.decimalSchema).put("decimal", BigDecimal.valueOf(1234, 1));
          final Struct actual = new Struct(this.decimalSchema).put("decimal", BigDecimal.valueOf(1234, 1));
          test(expected, actual, true);
        }),
        DynamicTest.dynamicTest("structDecimalActualNull", () -> {
          final Struct expected = new Struct(this.decimalSchema).put("decimal", BigDecimal.valueOf(1234, 1));
          final Struct actual = new Struct(this.decimalSchema).put("decimal", null);
          test(expected, actual, false);
        }),
        DynamicTest.dynamicTest("structDecimalActualDiff", () -> {
          final Struct expected = new Struct(this.decimalSchema).put("decimal", BigDecimal.valueOf(1234, 1));
          final Struct actual = new Struct(this.decimalSchema).put("decimal", BigDecimal.valueOf(12345, 1));
          test(expected, actual, false);
        }),
        DynamicTest.dynamicTest("structTimestamp", () -> {
          final Struct expected = new Struct(this.timestampSchema).put("timestamp", date);
          final Struct actual = new Struct(this.timestampSchema).put("timestamp", date);
          test(expected, actual, true);
        }),
        DynamicTest.dynamicTest("structTimestampActualNull", () -> {
          final Struct expected = new Struct(this.timestampSchema).put("timestamp", date);
          final Struct actual = new Struct(this.timestampSchema).put("timestamp", null);
          test(expected, actual, false);
        }),
        DynamicTest.dynamicTest("structTimestampActualDiff", () -> {
          final Struct expected = new Struct(this.timestampSchema).put("timestamp", date);
          final Struct actual = new Struct(this.timestampSchema).put("timestamp", new java.util.Date(date.getTime() + (1000L * 60L * 5L)));
          test(expected, actual, false);
        })
    );

    return tests.stream();
  }


}
