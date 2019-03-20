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
package com.github.jcustenborder.kafka.connect.utils;

import com.github.jcustenborder.kafka.connect.utils.config.Description;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationDanger;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationImportant;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationNote;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationTip;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationWarning;
import com.github.jcustenborder.kafka.connect.utils.config.Title;
import com.google.common.collect.ImmutableList;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.transforms.Transformation;

import java.util.Map;

@Title("TestTransformation")
@Description("This transformation is used to rename fields in the key.")
@DocumentationNote("This is a note")
@DocumentationTip("This is a tip")
@DocumentationImportant("This is important")
@DocumentationDanger("This is a danger")
@DocumentationWarning("This is a warning")
public class TestTransformation<R extends ConnectRecord<R>> implements Transformation<R> {
  @Override
  public R apply(R sourceRecord) {
    return null;
  }

  @Override
  public ConfigDef config() {
    return new ConfigDef()
        .define("important", ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "This is an important value.")
        .define("int", ConfigDef.Type.INT, Integer.MAX_VALUE, ConfigDef.Importance.HIGH, "This is an important value.")
        .define("short", ConfigDef.Type.SHORT, Short.MAX_VALUE, ConfigDef.Importance.HIGH, "This is an important value.")
        .define("long", ConfigDef.Type.LONG, Long.MAX_VALUE, ConfigDef.Importance.HIGH, "This is an important value.")
        .define("double", ConfigDef.Type.DOUBLE, Double.MAX_VALUE, ConfigDef.Importance.HIGH, "This is an important value.")
        .define("boolean", ConfigDef.Type.BOOLEAN, Boolean.TRUE, ConfigDef.Importance.HIGH, "This is an important value.")
        .define("list", ConfigDef.Type.LIST, ImmutableList.of("one", "two", "three"), ConfigDef.Importance.HIGH, "This is an important value.")
        .define("password", ConfigDef.Type.PASSWORD, "password", ConfigDef.Importance.HIGH, "This is an important value.")
        ;
  }

  @Override
  public void close() {

  }

  @Override
  public void configure(Map<String, ?> map) {

  }
}
