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
package com.github.jcustenborder.kafka.connect.utils.templates.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jcustenborder.kafka.connect.utils.jackson.ObjectMapperFactory;
import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import com.google.common.io.Files;
import org.apache.kafka.connect.connector.Connector;
import org.apache.kafka.connect.sink.SinkConnector;
import org.apache.kafka.connect.storage.Converter;
import org.apache.kafka.connect.transforms.Transformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Example {
  private static final Logger log = LoggerFactory.getLogger(Example.class);

  @JsonIgnore
  Class className;
  @JsonIgnore
  String resourceFile;
  @JsonIgnore
  Type type;

  @JsonProperty
  String name;
  @JsonProperty
  String description;
  @JsonProperty
  Map<String, String> config;
  @JsonProperty
  Map<String, Map<String, String>> transformations;
  @JsonProperty
  String note;
  @JsonProperty
  String danger;
  @JsonProperty
  String important;
  @JsonProperty
  String warning;
  @JsonProperty
  String tip;

  public String getExamplePrefix() {
    StringBuilder builder = new StringBuilder();

    if (Transformation.class.isAssignableFrom(this.className)) {
      if ("Key".equalsIgnoreCase(this.className.getSimpleName()) || "Value".equalsIgnoreCase(this.className.getSimpleName())) {
        builder.append(this.className.getSuperclass().getSimpleName());
        builder.append('.');
        builder.append(this.className.getSimpleName());
      } else {
        builder.append(this.className.getSimpleName());
      }
    } else {
      builder.append(this.className.getSimpleName());
    }
    return builder.toString();
  }

  public String validateTestCaseName() {
    return new File(getExamplePrefix(), this.resourceFile).toString();
  }

  public String getExampleJsonFile() {
    StringBuilder builder = new StringBuilder();
    builder.append(getExamplePrefix());
    builder.append('.');
    String n = Files.getNameWithoutExtension(this.resourceFile);
    builder.append(n);
    return getExamplePrefix() + "." + n + ".example.json";
  }

  public String getExamplePropertiesFile() {
    StringBuilder builder = new StringBuilder();
    builder.append(getExamplePrefix());
    builder.append('.');
    String n = Files.getNameWithoutExtension(this.resourceFile);
    builder.append(n);
    return getExamplePrefix() + "." + n + ".example.properties";
  }

  String transformKey() {
    StringBuilder builder = new StringBuilder();
    if ("Key".equalsIgnoreCase(this.className.getSimpleName()) || "Value".equalsIgnoreCase(this.className.getSimpleName())) {
      builder.append(this.className.getSuperclass().getSimpleName());
      builder.append(this.className.getSimpleName());
    } else {
      builder.append(this.className.getSimpleName());
    }
    String result = builder.toString();
    return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, result);
  }


  public static Example load(Class cls, String resourceName) {
    log.info("Loading {} with class {}", resourceName, cls);
    try (InputStream inputStream = cls.getResourceAsStream(resourceName)) {
      Example example = ObjectMapperFactory.INSTANCE.readValue(inputStream, Example.class);
      example.className = cls;
      example.resourceFile = new File(resourceName).getName();


      if (Connector.class.isAssignableFrom(cls)) {
        example.type = Type.Connector;
      } else if (Transformation.class.isAssignableFrom(cls)) {
        example.type = Type.Transformation;
      } else if (Converter.class.isAssignableFrom(cls)) {
        example.type = Type.Converter;
      }

      return example;
    } catch (IOException e) {
      throw new IllegalStateException("Exception thrown while loading " + resourceName, e);
    }
  }

  public Type getType() {
    return type;
  }


  public String getDanger() {
    return danger;
  }


  public String getImportant() {
    return important;
  }


  public String getWarning() {
    return warning;
  }


  public String getTip() {
    return tip;
  }


  public String resourceFile() {
    return this.resourceFile;
  }

  public Class className() {
    return this.className;
  }

  public String classDisplayName() {
    return this.className.getName();
  }

  public String getName() {
    return name;
  }


  public String getDescription() {
    return description;
  }


  public String getNote() {
    return note;
  }


  public Class getClassName() {
    return className;
  }


  public String getResourceFile() {
    return resourceFile;
  }


  public Map<String, String> getConfig() {
    return config;
  }


  private ObjectNode connectorJson() {
    ObjectNode node = ObjectMapperFactory.INSTANCE.createObjectNode();
    String connectorName = CaseFormat.UPPER_CAMEL.to(
        CaseFormat.LOWER_CAMEL,
        this.className.getSimpleName()
    ) + "1";
    node.put("name", connectorName);

    ObjectNode config = ObjectMapperFactory.INSTANCE.createObjectNode();
    node.set("config", config);
    config.put("connector.class", this.className.getName());
    config.put("tasks.max", "1");
    if (SinkConnector.class.isAssignableFrom(this.className)) {
      config.put("topics", "topic1,topic2,topic3");
    }
    for (Map.Entry<String, String> kvp : this.config.entrySet()) {
      config.put(kvp.getKey(), kvp.getValue());
    }
    if (null != this.transformations && !transformations.isEmpty()) {
      Set<String> transformKeys = this.transformations.keySet();
      config.put("transforms", Joiner.on(',').join(transformKeys));
      for (String transformKey : transformKeys) {
        Map<String, String> transformSettings = this.transformations.get(transformKey);
        for (Map.Entry<String, String> kvp : transformSettings.entrySet()) {
          final String key = String.format("transforms.%s.%s", transformKey, kvp.getKey());
          config.put(key, kvp.getValue());
        }
      }
    }

    return node;
  }

  private Map<String, String> connectorProperties() {
    Map<String, String> properties = new LinkedHashMap<>();
    properties.put("connector.class", this.className.getName());
    properties.put("tasks.max", "1");
    if (SinkConnector.class.isAssignableFrom(this.className)) {
      properties.put("topics", "topic1,topic2,topic3");
    }
    for (Map.Entry<String, String> kvp : this.config.entrySet()) {
      properties.put(kvp.getKey(), kvp.getValue());
    }
    if (null != this.transformations && !transformations.isEmpty()) {
      Set<String> transformKeys = this.transformations.keySet();
      properties.put("transforms", Joiner.on(',').join(transformKeys));
      for (String transformKey : transformKeys) {
        Map<String, String> transformSettings = this.transformations.get(transformKey);
        for (Map.Entry<String, String> kvp : transformSettings.entrySet()) {
          final String key = String.format("transforms.%s.%s", transformKey, kvp.getKey());
          properties.put(key, kvp.getValue());
        }
      }
    }

    return properties;
  }

  private ObjectNode transformationJson() {
    ObjectNode node = ObjectMapperFactory.INSTANCE.createObjectNode();
    node.put("name", "connector1");

    ObjectNode config = ObjectMapperFactory.INSTANCE.createObjectNode();
    node.set("config", config);
    config.put("connector.class", "...");
    config.put("tasks.max", "1");
    String key = transformKey();


    config.put("transforms", key);
    config.put(key + ".type", this.className.getName());
    for (Map.Entry<String, String> kvp : this.config.entrySet()) {
      config.put(key + "." + kvp.getKey(), kvp.getValue());
    }
    return node;
  }

  private Map<String, String> transformationProperties() {
    Map<String, String> properties = new LinkedHashMap<>();
    properties.put("connector.class", "...");
    properties.put("tasks.max", "1");
    String key = transformKey();
    config.put("transforms", key);
    config.put(key + ".type", this.className.getName());
    for (Map.Entry<String, String> kvp : this.config.entrySet()) {
      properties.put(key + "." + kvp.getKey(), kvp.getValue());
    }
    return properties;
  }

  public void writePropertiesExample(File parentFolder) throws IOException {
    File outputFile = new File(parentFolder, this.getExamplePropertiesFile());
    try (FileWriter writer = new FileWriter(outputFile)) {
      writePropertiesExample(writer);
    }
  }

  public void writePropertiesExample(Writer writer) throws IOException {
    final Map<String, String> example;

    switch (this.type) {
      case Connector:
        example = connectorProperties();
        break;
      case Transformation:
        example = transformationProperties();
        break;
      default:
        throw new UnsupportedOperationException(
            String.format("%s is not supported", this.type)
        );
    }
    store(writer, example);
  }

  static void store(Writer writer, Map<String, String> properties) throws IOException {
    for (Map.Entry<String, String> kvp : properties.entrySet()) {
      writer.append(
          String.format("%s=%s", kvp.getKey(), kvp.getValue())
      );
      writer.write('\n');
    }
  }

  public String getMarkdownProperties() throws IOException {
    StringWriter writer = new StringWriter();
    writePropertiesExample(writer);
    return writer.toString();
  }

  public String getMarkdownJson() throws IOException {
    StringWriter writer = new StringWriter();
    writeJsonExample(writer);
    return writer.toString();
  }

  public void writeJsonExample(Writer writer) throws IOException {
    final ObjectNode example;

    switch (this.type) {
      case Connector:
        example = connectorJson();
        break;
      case Transformation:
        example = transformationJson();
        break;
      default:
        throw new UnsupportedOperationException(
            String.format("%s is not supported", this.type)
        );
    }

    ObjectMapperFactory.INSTANCE.configure(SerializationFeature.INDENT_OUTPUT, true);
    ObjectMapperFactory.INSTANCE.writeValue(writer, example);
  }

  public void writeJsonExample(File parentFolder) throws IOException {
    File outputFile = new File(parentFolder, this.getExampleJsonFile());
    try (Writer writer = new FileWriter(outputFile)) {
      writeJsonExample(writer);
    }
  }


  public enum Type {
    Connector,
    Transformation,
    Converter
  }

}
