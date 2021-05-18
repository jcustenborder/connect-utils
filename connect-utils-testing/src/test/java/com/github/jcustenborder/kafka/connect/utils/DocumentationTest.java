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

import com.github.jcustenborder.kafka.connect.utils.templates.ImmutableConfigProvider;
import com.github.jcustenborder.kafka.connect.utils.templates.ImmutableConfiguration;
import com.github.jcustenborder.kafka.connect.utils.templates.ImmutableGroup;
import com.github.jcustenborder.kafka.connect.utils.templates.ImmutableItem;
import com.github.jcustenborder.kafka.connect.utils.templates.Plugin;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.apache.kafka.common.config.ConfigDef.Importance.HIGH;
import static org.apache.kafka.common.config.ConfigDef.Type.INT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class DocumentationTest extends BaseDocumentationTest {
  private static final Logger log = LoggerFactory.getLogger(DocumentationTest.class);

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

  @Test
  public void plugin() {
    assertNotNull(this.plugin);
    assertEquals("connect-utils", this.plugin.getPluginName());
    assertEquals("jcustenborder", this.plugin.getPluginOwner());
  }

  @Disabled
  @TestFactory
  public Stream<DynamicTest> testConfigProviders() throws IOException {
    List<Plugin.ConfigProvider> testCases = Arrays.asList(
        ImmutableConfigProvider.builder()
            .cls(TestConfigProvider.class)
            .warning("This is a warning")
            .tip("This is a tip")
            .important("This is important")
            .danger("This is a danger")
            .note("This is a note")
            .title("Test Source Connector")
            .description("The test source connector is used to simulate the usage fromConnector an actual connector that we would generate documentation from.")
            .build(),
        ImmutableConfigProvider.builder()
            .cls(TestConfigProviderWithConfig.class)
            .warning("This is a warning")
            .tip("This is a tip")
            .important("This is important")
            .danger("This is a danger")
            .note("This is a note")
            .title("Test Config Provider")
            .description("The test source connector is used to simulate the usage fromConnector an actual connector that we would generate documentation from.")
            .configuration(
                ImmutableConfiguration.builder()
                    .configDef(new ConfigDef())
                    .addGroups(
                        ImmutableGroup.builder()
                            .name("General")
                            .addItems(
                                ImmutableItem.builder()
                                    .name("testing.bar")
                                    .importance(HIGH)
                                    .doc("Testing the bar object.")
                                    .type(INT)
                                    .group("General")
                                    .isRequired(true)
                                    .build(),
                                ImmutableItem.builder()
                                    .name("testing.foo")
                                    .importance(HIGH)
                                    .doc("Testing the bar object.")
                                    .type(INT)
                                    .group("General")
                                    .isRequired(true)
                                    .build()
                            )
                            .build()
                    )
                    .addRequiredConfigs(
                        ImmutableItem.builder()
                            .name("testing.bar")
                            .importance(HIGH)
                            .doc("Testing the bar object.")
                            .type(INT)
                            .group("General")
                            .isRequired(true)
                            .build(),
                        ImmutableItem.builder()
                            .name("testing.foo")
                            .importance(HIGH)
                            .doc("Testing the bar object.")
                            .type(INT)
                            .group("General")
                            .isRequired(true)
                            .build()
                    )
                    .build()
            )
            .build()
    );

    return testCases.stream()
        .map(expected -> dynamicTest(expected.getCls().getName(), () -> {
          Optional<Plugin.ConfigProvider> optionalConfigProvider = this.plugin.getConfigProviders().stream()
              .filter(c -> expected.getCls().equals(c.getCls()))
              .findFirst();
          assertTrue(optionalConfigProvider.isPresent(), "ConfigProvider should be present.");
          Plugin.ConfigProvider actual = optionalConfigProvider.get();
          assertEquals(expected, actual);
        }));
  }
}