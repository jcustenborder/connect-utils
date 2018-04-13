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
package com.github.jcustenborder.kafka.connect.utils.templates;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jcustenborder.kafka.connect.utils.docs.Example;
import com.github.jcustenborder.kafka.connect.utils.templates.model.Configurable;
import com.github.jcustenborder.kafka.connect.utils.templates.model.Configuration;
import com.github.jcustenborder.kafka.connect.utils.templates.model.SinkConnectorData;
import com.github.jcustenborder.kafka.connect.utils.templates.model.SourceConnectorData;
import com.github.jcustenborder.kafka.connect.utils.templates.model.TransformationData;
import org.apache.kafka.connect.sink.SinkConnector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class TemplateHelper {

  protected static final String REQUIRED_CONFIG = "< Required Configuration >";
  protected final ObjectMapper objectMapper;

  public TemplateHelper() {
    this.objectMapper = new ObjectMapper();
    this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

  }

  protected Properties newProperties() {
    return new SortedProperties();
  }

  protected ObjectNode createJsonNode(Example example) {
    ObjectNode configNode = this.objectMapper.createObjectNode();
    ObjectNode outputNode;

    if (Example.Type.Connector == example.getType()) {
      configNode.put("name", example.className().getSimpleName() + "1");
      configNode.put("connector.class", example.classDisplayName());
      configNode.put("tasks.max", "1");

      for (Map.Entry<String, String> kvp : example.getConfig().entrySet()) {
        configNode.put(kvp.getKey(), kvp.getValue());
      }

      if (SinkConnector.class.isAssignableFrom(example.className())) {
        configNode.put("topics", REQUIRED_CONFIG);
      } else {

      }
      outputNode = this.objectMapper.createObjectNode();
      outputNode.put("config", configNode);
    } else if (Example.Type.Transformation == example.getType()) {
      outputNode = configNode;
      configNode.put("name", "Connector1");
      configNode.put("connector.class", "org.apache.kafka.some.SourceConnector");

      final String transformName = example.className().getName();
      final String prefix = String.format("transforms.%s.", transformName);
      configNode.put("transforms", transformName);
      configNode.put(prefix + "type", example.className().getName());

      for (Map.Entry<String, String> kvp : example.getConfig().entrySet()) {
        configNode.put(prefix + kvp.getKey(), kvp.getValue());
      }
    } else {
      throw new UnsupportedOperationException();
    }

    return outputNode;
  }

  protected ObjectNode createJsonNode(Configurable template) {
    ObjectNode configNode = this.objectMapper.createObjectNode();
    ObjectNode outputNode;
    if (template instanceof TransformationData) {
      outputNode = configNode;
      configNode.put("name", "Connector1");
      configNode.put("connector.class", "org.apache.kafka.some.SourceConnector");
      configNode.put("transforms", "tran");
      configNode.put("transforms.tran.type", template.getClassName());

      for (Configuration.Item entry : template.getConfig().getRequiredConfigs()) {
        configNode.put(
            String.format("transforms.tran.%s", entry.getName()),
            REQUIRED_CONFIG
        );
      }
    } else {
      configNode.put("name", template.getSimpleName() + "1");
      configNode.put("connector.class", template.getClassName());
      configNode.put("tasks.max", "1");

      if (template instanceof SourceConnectorData) {

      } else if (template instanceof SinkConnectorData) {
        configNode.put("topics", REQUIRED_CONFIG);
      }

      for (Configuration.Item entry : template.getConfig().getRequiredConfigs()) {
        configNode.put(entry.getName(), REQUIRED_CONFIG);
      }
      outputNode = this.objectMapper.createObjectNode();
      outputNode.put("config", configNode);
    }
    return outputNode;
  }

  protected Properties createProperties(Configurable template) {
    Properties properties = newProperties();

    if (template instanceof TransformationData) {
      properties.put("name", "Connector1");
      properties.put("connector.class", "org.apache.kafka.some.SourceConnector");
      properties.put("tasks.max", "1");
      properties.put("transforms", "tran");
      properties.put("transforms.tran.type", template.getClassName());

      for (Configuration.Item entry : template.getConfig().getRequiredConfigs()) {
        properties.put(
            String.format("transforms.tran.%s", entry.getName()),
            REQUIRED_CONFIG
        );
      }
    } else {
      properties.put("name", template.getSimpleName() + "1");
      properties.put("connector.class", template.getClassName());
      properties.put("tasks.max", "1");

      if (template instanceof SourceConnectorData) {

      } else if (template instanceof SinkConnectorData) {
        properties.put("topics", REQUIRED_CONFIG);
      }

      for (Configuration.Item entry : template.getConfig().getRequiredConfigs()) {
        properties.put(entry.getName(), REQUIRED_CONFIG);
      }
    }
    return properties;
  }

  class SortedProperties extends Properties {
    List<Object> keys = new ArrayList<>();

    @Override
    public synchronized Object put(Object key, Object value) {
      keys.add(key);
      return super.put(key, value);
    }

    @Override
    public synchronized Enumeration<Object> keys() {
      return Collections.enumeration(keys);
    }
  }
}
