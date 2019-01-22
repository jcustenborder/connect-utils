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
package com.github.jcustenborder.kafka.connect.utils.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.PatternFilenameFilter;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.github.jcustenborder.kafka.connect.utils.AssertStruct.assertStruct;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class StructSerializationModuleTest {
  private static final Logger log = LoggerFactory.getLogger(StructSerializationModuleTest.class);

  @BeforeAll
  public static void beforeAll() {
    ObjectMapperFactory.INSTANCE.configure(SerializationFeature.INDENT_OUTPUT, true);
  }


  public static class TestCase {


  }

  File testcaseRoot = new File("src/test/resources/com/github/jcustenborder/kafka/connect/utils/jackson");

  @TestFactory
  public Stream<DynamicTest> parse() {
    File[] testFiles = testcaseRoot.listFiles(new PatternFilenameFilter("^struct\\d+\\.json$"));

    return Arrays.stream(testFiles)
        .map(f -> dynamicTest(f.getName(), () -> {
          Struct struct = ObjectMapperFactory.INSTANCE.readValue(f, Struct.class);
          assertNotNull(struct);

        }));
  }


  @Test
  public void foo() throws IOException {
    File outputPath = new File("src/test/resources/com/github/jcustenborder/kafka/connect/utils/jackson");

    File inputFile = new File(outputPath, "test.cases");
    try (JsonParser parser = ObjectMapperFactory.INSTANCE.getJsonFactory().createParser(inputFile)) {
      MappingIterator<ObjectNode> nodes = ObjectMapperFactory.INSTANCE.readValues(parser, ObjectNode.class);

      int index = 1;
      while (nodes.hasNext()) {
        File outputFile = new File(outputPath, String.format("struct%03d.json", index));
        ObjectNode node = nodes.next();
        ObjectMapperFactory.INSTANCE.writeValue(outputFile, node);
        index++;
      }
    }
  }


  @TestFactory
  public Stream<DynamicTest> roundtrip() {

    Schema innerSchema = SchemaBuilder.struct()
        .name("InnerSchema")
        .optional()
        .field("latitude", Schema.FLOAT32_SCHEMA)
        .field("longitude", Schema.FLOAT32_SCHEMA)
        .build();

    Map<String, Struct> cityMap = ImmutableMap.of(
        "Austin", new Struct(innerSchema).put("latitude", 30.2672F).put("longitude", 97.7431F),
        "San Francisco", new Struct(innerSchema).put("latitude", 37.7749F).put("longitude", 122.4194F)
    );

    List<Struct> cityList = ImmutableList.copyOf(
        cityMap.values()
    );

    List<Struct> testCases = Arrays.asList(
        new Struct(SchemaBuilder.struct().name("Testing").field("firstName", Schema.STRING_SCHEMA).build())
            .put("firstName", "Example"),
        new Struct(SchemaBuilder.struct().name("Nullable").field("firstName", Schema.OPTIONAL_STRING_SCHEMA).build())
            .put("firstName", null),
        new Struct(SchemaBuilder.struct().name("NestedOptionalStructWithValue").field("firstName", Schema.OPTIONAL_STRING_SCHEMA).field("inner", innerSchema).build())
            .put("inner", new Struct(innerSchema).put("latitude", 30.2672F).put("longitude", 97.7431F)),
        new Struct(SchemaBuilder.struct().name("NestedOptionalStructWithoutValue").field("firstName", Schema.OPTIONAL_STRING_SCHEMA).field("inner", innerSchema).build())
            .put("inner", null),
        new Struct(SchemaBuilder.struct().name("NestedMapStruct").field("firstName", Schema.OPTIONAL_STRING_SCHEMA).field("inner", SchemaBuilder.map(Schema.STRING_SCHEMA, innerSchema)).build())
            .put("inner", cityMap),
        new Struct(SchemaBuilder.struct().name("NestedArrayStruct").field("firstName", Schema.OPTIONAL_STRING_SCHEMA).field("inner", SchemaBuilder.array(innerSchema)).build())
            .put("inner", cityList)
    );

    return testCases.stream().map(expected -> dynamicTest(expected.schema().name(), () -> {
      String input = ObjectMapperFactory.INSTANCE.writeValueAsString(expected);
      log.trace(input);
      Struct actual = ObjectMapperFactory.INSTANCE.readValue(input, Struct.class);
      assertStruct(expected, actual);
    }));
  }


}
