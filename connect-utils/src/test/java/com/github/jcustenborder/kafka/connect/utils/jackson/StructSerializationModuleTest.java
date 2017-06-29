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
package com.github.jcustenborder.kafka.connect.utils.jackson;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.github.jcustenborder.kafka.connect.utils.AssertStruct.assertStruct;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class StructSerializationModuleTest {
  private static final Logger log = LoggerFactory.getLogger(StructSerializationModuleTest.class);

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
