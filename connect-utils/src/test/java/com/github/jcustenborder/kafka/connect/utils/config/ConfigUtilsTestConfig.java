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

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.io.File;
import java.util.Map;

class ConfigUtilsTestConfig extends AbstractConfig {

  public enum EnumTest {
    ONE,
    TWO,
    THREE,
    FOUR
  }

  public static String TEMP_DIR_CONF = "tmp.dir";
  public static String TEMP_DIR_DOC = "tmp.dir";

  public static String ENUM_VALUE_CONF = "enum.value";
  public static String ENUM_VALUE_DOC = "enum.value";

  public final File tmpDir;
  public final EnumTest enumTest;

  public ConfigUtilsTestConfig(Map<?, ?> parsedConfig) {
    super(getConf(), parsedConfig);

    this.tmpDir = ConfigUtils.getAbsoluteFile(this, TEMP_DIR_CONF);
    this.enumTest = ConfigUtils.getEnum(EnumTest.class, this, ENUM_VALUE_CONF);
  }


  public static ConfigDef getConf() {
    return new ConfigDef()
        .define(TEMP_DIR_CONF, ConfigDef.Type.STRING, "/tmp", ConfigDef.Importance.HIGH, TEMP_DIR_DOC)
        .define(ENUM_VALUE_CONF, ConfigDef.Type.STRING, EnumTest.ONE.name(), ConfigDef.Importance.HIGH, ENUM_VALUE_DOC);
  }
}
