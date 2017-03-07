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
package com.github.jcustenborder.kafka.connect.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.opentest4j.AssertionFailedError;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AssertStructTest {

  Schema pointSchema;
  Schema simple;
  Schema arraySchema;
  Schema mapSchema;
  Schema nestedSchema;
  Map<String, Struct> mapOfStructs;
  List<Struct> arrayOfStructs;

  @BeforeEach
  public void createSchemas() {
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
        })
    );

    return tests.stream();
  }


}
