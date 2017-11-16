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
package com.github.jcustenborder.kafka.connect.utils.config;

import org.apache.kafka.common.config.ConfigDef;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigKeyBuilderTest {

  @Test
  public void nullName() {
    IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
      ConfigKeyBuilder builder = ConfigKeyBuilder.of(null, ConfigDef.Type.STRING);
      builder.build();
    });
    assertTrue(exception.getMessage().contains("name"));
  }

  @Test
  public void minimal() {
    final ConfigDef.ConfigKey actual = ConfigKeyBuilder.of("testing", ConfigDef.Type.STRING)
        .build();
    assertEquals("testing", actual.name, "name should match.");
    assertEquals(ConfigDef.Type.STRING, actual.type, "type should match.");

    new ConfigDef().define(actual).toEnrichedRst();
  }

  @Test
  public void build() {
    final ConfigDef.ConfigKey actual = ConfigKeyBuilder.of("testing", ConfigDef.Type.STRING)
        .documentation("Testing")
        .importance(ConfigDef.Importance.HIGH)
        .build();
    assertEquals("testing", actual.name, "name should match.");
    assertEquals(ConfigDef.Type.STRING, actual.type, "type should match.");
    assertEquals("Testing", actual.documentation, "documentation should match.");
    assertEquals(ConfigDef.Importance.HIGH, actual.importance, "importance should match.");

    new ConfigDef().define(actual).toEnrichedRst();

  }
}
