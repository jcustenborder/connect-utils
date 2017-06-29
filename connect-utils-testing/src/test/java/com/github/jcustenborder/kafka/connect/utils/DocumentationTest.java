/**
 * Copyright © 2016 Jeremy Custenborder (jcustenborder@gmail.com)
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
package com.github.jcustenborder.kafka.connect.utils;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;

import java.util.Arrays;
import java.util.List;

public class DocumentationTest extends BaseDocumentationTest {

  @Override
  protected List<Schema> schemas() {
    return Arrays.asList(
        Schema.STRING_SCHEMA,
        SchemaBuilder.struct()
            .name("com.github.jcustenborder.kafka.connect.utils.DocumentationTest")
            .doc("This is a test schema used for the documentation.")
            .field("noDoc", Schema.OPTIONAL_STRING_SCHEMA)
            .field("Doc", SchemaBuilder.string().doc("Testing").build())
            .field("defaultValue", SchemaBuilder.int32().defaultValue(123).build())
            .build(),
        SchemaBuilder.map(
            Schema.STRING_SCHEMA,
            Schema.STRING_SCHEMA
        )
    );
  }

  @Override
  protected String[] packages() {
    return new String[]{this.getClass().getPackage().getName()};
  }
}
