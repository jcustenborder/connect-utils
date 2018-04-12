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
package com.github.jcustenborder.kafka.connect.utils.docs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jcustenborder.kafka.connect.utils.jackson.ObjectMapperFactory;
import org.apache.kafka.connect.connector.Connector;
import org.apache.kafka.connect.storage.Converter;
import org.apache.kafka.connect.transforms.Transformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

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
  String note;
  @JsonProperty
  String danger;
  @JsonProperty
  String important;
  @JsonProperty
  String warning;
  @JsonProperty
  String tip;

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

  public void setType(Type type) {
    this.type = type;
  }

  public String getDanger() {
    return danger;
  }

  public void setDanger(String danger) {
    this.danger = danger;
  }

  public String getImportant() {
    return important;
  }

  public void setImportant(String important) {
    this.important = important;
  }

  public String getWarning() {
    return warning;
  }

  public void setWarning(String warning) {
    this.warning = warning;
  }

  public String getTip() {
    return tip;
  }

  public void setTip(String tip) {
    this.tip = tip;
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

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public Class getClassName() {
    return className;
  }

  public void setClassName(Class className) {
    this.className = className;
  }

  public String getResourceFile() {
    return resourceFile;
  }

  public void setResourceFile(String resourceFile) {
    this.resourceFile = resourceFile;
  }

  public Map<String, String> getConfig() {
    return config;
  }

  public void setConfig(Map<String, String> config) {
    this.config = config;
  }

  public enum Type {
    Connector,
    Transformation,
    Converter
  }

}
