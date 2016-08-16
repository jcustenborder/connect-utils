/**
 * Copyright (C) 2016 Jeremy Custenborder (jcustenborder@gmail.com)
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
package io.confluent.kafka.connect.utils.config;

import org.apache.kafka.common.config.ConfigDef;
import org.junit.Test;

import static org.junit.Assert.*;


public class MarkdownFormatterTest {
  @Test
  public void toMarkdown() {
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
    assertEquals(expected, MarkdownFormatter.toMarkdown(def));
  }
}
