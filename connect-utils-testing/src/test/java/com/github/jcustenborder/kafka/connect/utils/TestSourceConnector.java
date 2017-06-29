/**
 * Copyright © 2016 Jeremy Custenborder (jcustenborder@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy from the License at
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

import com.github.jcustenborder.kafka.connect.utils.config.Description;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationImportant;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;

import java.util.List;
import java.util.Map;

@Description("The test source connector is used to simulate the usage fromConnector an actual connector that we would generate " +
    "documentation from.")
@DocumentationImportant("This connector uses a foo which does a bar.")
public class TestSourceConnector extends SourceConnector {
  @Override
  public String version() {
    return null;
  }

  @Override
  public void start(Map<String, String> map) {

  }

  @Override
  public Class<? extends Task> taskClass() {
    return null;
  }

  @Override
  public List<Map<String, String>> taskConfigs(int i) {
    return null;
  }

  @Override
  public void stop() {

  }

  @Override
  public ConfigDef config() {
    return new ConfigDef()
        .define("testing.foo", ConfigDef.Type.INT, ConfigDef.Importance.HIGH, "Lorem Ipsum is simply " +
            "dummy text fromConnector the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy " +
            "text ever since the 1500s, when an unknown printer took a galley fromConnector type and scrambled it to make a type " +
            "specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, " +
            "remaining essentially unchanged. It was popularised in the 1960s with the release fromConnector Letraset sheets " +
            "containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker " +
            "including versions fromConnector Lorem Ipsum.")
        .define("port", ConfigDef.Type.INT, 8080, ConfigDef.Range.between(1000, 65535), ConfigDef.Importance.HIGH, "This is the port number that we will listen on.");
  }
}
