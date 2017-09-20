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

import com.github.jcustenborder.kafka.connect.utils.config.Description;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationTip;
import com.github.jcustenborder.kafka.connect.utils.config.Title;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.transforms.Transformation;

import java.util.Map;

@Description("This is a testing transformation.")
public abstract class TestTransform<R extends ConnectRecord<R>> implements Transformation<R> {
  @Override
  public R apply(R sourceRecord) {
    return null;
  }

  @Override
  public ConfigDef config() {
    return new ConfigDef()
        .define("important", ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "This is an important value.");
  }

  @Override
  public void close() {

  }

  @Override
  public void configure(Map<String, ?> map) {

  }

  @Title("TestTransform(Key)")
  @Description("This transformation is used to rename fields in the key.")
  @DocumentationTip("This transformation is used to manipulate fields in the Key of the record.")
  public static class Key<R extends ConnectRecord<R>> extends TestTransform<R> {

  }

  @Title("TestTransform(Value)")
  @Description("This transformation is used to rename fields in the value.")
  @DocumentationTip("This transformation is used to manipulate fields in the Key of the record.")
  public static class Value<R extends ConnectRecord<R>> extends TestTransform<R> {

  }
}
