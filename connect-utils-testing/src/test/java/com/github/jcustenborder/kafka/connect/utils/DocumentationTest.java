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

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;

import java.util.Arrays;
import java.util.List;

public class DocumentationTest extends BaseDocumentationTest {

  @Override
  protected List<Schema> schemas() {
    Schema person = SchemaBuilder.struct()
        .name("com.github.jcustenborder.kafka.connect.utils.Person")
        .doc("This schema represents a person.")
        .field("firstName", SchemaBuilder.string().doc("The first name of the person").build())
        .field("lastName", SchemaBuilder.string().doc("The last name of the person").build())
        .build();
    Schema simpleMapSchema = SchemaBuilder
        .map(Schema.STRING_SCHEMA, Schema.STRING_SCHEMA)
        .build();
    Schema structMap = SchemaBuilder
        .map(
            Schema.STRING_SCHEMA,
            person
        ).build();
    Schema listSchema = SchemaBuilder.array(person).optional().build();
    Schema nestedSchema = SchemaBuilder.struct()
        .name("com.github.jcustenborder.kafka.connect.utils.NestedSchema")
        .doc("This schema is an example of nesting.")
        .field("simpleMapSchema", simpleMapSchema)
        .field("structMap", structMap)
        .field("listSchema", listSchema)
        .field("person", person)
        .build();

    return Arrays.asList(
        Schema.STRING_SCHEMA,
        nestedSchema,
        SchemaBuilder.struct()
            .name("com.github.jcustenborder.kafka.connect.utils.DocumentationTest")
            .doc("This is a test schema used for the documentation.")
            .field("noDoc", Schema.OPTIONAL_STRING_SCHEMA)
            .field("Doc", SchemaBuilder.string().doc("Lorem ipsum dolor sit amet, " +
                "consectetur adipiscing elit. Nulla commodo interdum nunc, hendrerit accumsan " +
                "lorem bibendum eget. Pellentesque vel vestibulum velit, eu tempor tellus. Ut et" +
                "sapien augue. Suspendisse quis dictum eros. Curabitur gravida rhoncus ipsum, congue " +
                "pellentesque enim dignissim et. Nunc laoreet, velit non interdum hendrerit, mauris " +
                "velit aliquet enim, et tincidunt nunc eros ac mauris. Maecenas semper odio nec " +
                "tellus interdum imperdiet. Donec vehicula nisl ligula, sed bibendum massa " +
                "venenatis vel. Maecenas porta, lorem lobortis imperdiet luctus, ante ex interdum " +
                "augue, ullamcorper rhoncus leo libero non purus. Aliquam ullamcorper, dui nec " +
                "molestie tincidunt, purus lorem condimentum leo, ac pretium justo mi id tortor. " +
                "Donec tempor est ut feugiat euismod. In vel enim non odio rutrum tincidunt sed " +
                "eget erat.").build())
            .field("defaultValue", SchemaBuilder.int32().defaultValue(123).build())
            .build(),
        SchemaBuilder.map(
            Schema.STRING_SCHEMA,
            Schema.STRING_SCHEMA
        ).doc("This is something")
            .build()
    );
  }
}
