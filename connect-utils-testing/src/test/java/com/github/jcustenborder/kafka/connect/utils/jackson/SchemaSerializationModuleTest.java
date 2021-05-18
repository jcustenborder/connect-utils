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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jcustenborder.kafka.connect.utils.AssertSchema;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.github.jcustenborder.kafka.connect.utils.AssertSchema.assertSchema;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;


public class SchemaSerializationModuleTest {
  private static final Logger log = LoggerFactory.getLogger(SchemaSerializationModuleTest.class);
  ObjectMapper objectMapper;

  @BeforeEach
  public void before() {
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new SchemaSerializationModule());
  }

  @TestFactory
  Stream<DynamicTest> roundTrip() {
    List<Schema> schemas = new ArrayList<>();
    schemas.add(
        SchemaBuilder.struct()
            .name("test")
            .doc("This is a test")
            .version(16)
            .field("first_name", Schema.OPTIONAL_STRING_SCHEMA)
            .field("last_name", Schema.OPTIONAL_STRING_SCHEMA)
            .field("defaultValueField", SchemaBuilder.int64().defaultValue(1234L).optional().build())
            .build()
    );

    for (Schema.Type schemaType : Arrays.asList(
        Schema.Type.INT8,
        Schema.Type.INT16,
        Schema.Type.INT32,
        Schema.Type.INT64,
        Schema.Type.FLOAT32,
        Schema.Type.FLOAT64,
        Schema.Type.STRING,
        Schema.Type.BOOLEAN)) {

      schemas.add(
          SchemaBuilder.type(schemaType)
              .name(String.format("%s", schemaType))
              .build()
      );
      schemas.add(
          SchemaBuilder.type(schemaType)
              .name(String.format("Optional%s", schemaType))
              .optional()
              .build()
      );
      schemas.add(
          SchemaBuilder.array(SchemaBuilder.type(schemaType).build())
              .name(String.format("Array%s", schemaType))
              .build()
      );
    }


    return schemas.stream().map(expected -> dynamicTest(
        String.format("%s:%s", expected.type(), null == expected.name() ? "" : expected.name()),
        () -> {
          String input = this.objectMapper.writeValueAsString(expected);
          log.trace("Serialized to \n{}", input);
          Schema actual = this.objectMapper.readValue(input, Schema.class);
          AssertSchema.assertSchema(expected, actual);
        }
    ));

  }


}
