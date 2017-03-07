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
package com.github.jcustenborder.kafka.connect.utils.config;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class MarkdownFormatterTest {
  private static Logger log = LoggerFactory.getLogger(MarkdownFormatterTest.class);
  @Test
  public void toMarkdownConfigDef() {
    final String expected =
        "| Name                  | Description                                                                                                                                                                                                                                                                                    | Type     | Default               | Valid Values | Importance |\n" +
            "|-----------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------|-----------------------|--------------|------------|\n" +
            "| ssl.key.password      | The password of the private key in the key store file. This is optional for client.                                                                                                                                                                                                            | password | null                  |              | high       |\n" +
            "| ssl.keystore.location | The location of the key store file. This is optional for client and can be used for two-way authentication for client.                                                                                                                                                                         | string   | null                  |              | high       |\n" +
            "| ssl.enabled.protocols | The SSL protocol used to generate the SSLContext. Default setting is TLS, which is fine for most cases. Allowed values in recent JVMs are TLS, TLSv1.1 and TLSv1.2. SSL, SSLv2 and SSLv3 may be supported in older JVMs, but their usage is discouraged due to known security vulnerabilities. | string   | TLSv1.2,TLSv1.1,TLSv1 |              | medium     |\n";

    ConfigDef def = new ConfigDef()
        .define("ssl.key.password", ConfigDef.Type.PASSWORD, null, ConfigDef.Importance.HIGH, "The password of the private key in the key store file. This is optional for client.")
        .define("ssl.keystore.location", ConfigDef.Type.STRING, null, ConfigDef.Importance.HIGH, "The location of the key store file. This is optional for client and can be used for two-way authentication for client.")
        .define("ssl.enabled.protocols", ConfigDef.Type.STRING, "TLSv1.2,TLSv1.1,TLSv1", ConfigDef.Importance.MEDIUM, "The SSL protocol used to generate the SSLContext. Default setting is TLS, which is fine for most cases. Allowed values in recent JVMs are TLS, TLSv1.1 and TLSv1.2. SSL, SSLv2 and SSLv3 may be supported in older JVMs, but their usage is discouraged due to known security vulnerabilities.");
    final String actual = MarkdownFormatter.toMarkdown(def);
    log.trace("\n{}", actual);
    assertEquals(expected, actual);
  }

  @Test
  public void toMarkdownSchema() {
    final String expected = "## com.github.jcustenborder.kafka.connect.utils.config.TestingSchema\n" +
        "\n" +
        "This schema is used for testing that the documentation generation feature is working properly.\n" +
        "\n" +
        "| Name         | Optional | Schema                                                                                                                  | Default Value | Documentation                                   |\n" +
        "|--------------|----------|-------------------------------------------------------------------------------------------------------------------------|---------------|-------------------------------------------------|\n" +
        "| firstName    | false    | [String](https://kafka.apache.org/0102/javadoc/org/apache/kafka/connect/data/Schema.Type.html#STRING)                   |               | The first name for the user                     |\n" +
        "| lastName     | false    | [String](https://kafka.apache.org/0102/javadoc/org/apache/kafka/connect/data/Schema.Type.html#STRING)                   |               | The last name for the user                      |\n" +
        "| emailAddress | false    | [String](https://kafka.apache.org/0102/javadoc/org/apache/kafka/connect/data/Schema.Type.html#STRING)                   |               | The users primary email address.                |\n" +
        "| point        | true     | [com.github.jcustenborder.kafka.connect.utils.config.Point](#com.github.jcustenborder.kafka.connect.utils.config.Point) |               | Latitude and Longitude of a point on the earth. |\n";

    Schema pointSchema = SchemaBuilder.struct()
        .name("com.github.jcustenborder.kafka.connect.utils.config.Point")
        .doc("Latitude and Longitude of a point on the earth.")
        .optional()
        .field("latitude", SchemaBuilder.float32().doc("latitude of the point.").build())
        .field("longitude", SchemaBuilder.float32().doc("longitude of the point.").build())
        .build();

    Schema schema = SchemaBuilder.struct()
        .name("com.github.jcustenborder.kafka.connect.utils.config.TestingSchema")
        .doc("This schema is used for testing that the documentation generation feature is working properly.")
        .field("firstName", SchemaBuilder.string().doc("The first name for the user").build())
        .field("lastName", SchemaBuilder.string().doc("The last name for the user").build())
        .field("emailAddress", SchemaBuilder.string().doc("The users primary email address.").build())
        .field("point", pointSchema)
        .build();

    final String actual = MarkdownFormatter.toMarkdown(schema);
    log.trace("\n{}", actual);
    assertEquals(expected, actual);
  }
}
