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
package com.github.jcustenborder.kafka.connect.utils.templates;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.source.SourceRecord;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;


@Value.Immutable
public interface Plugin extends Notes {
  String getIntroduction();

  String getPluginName();

  String getPluginOwner();

  List<Transformation> getTransformations();

  List<SinkConnector> getSinkConnectors();

  List<SourceConnector> getSourceConnectors();

  List<Converter> getConverters();

  @Value.Immutable
  interface Item {
    String getName();

    ConfigDef.Importance getImportance();

    String getDoc();

    @Nullable
    Object getDefaultValue();

    @Nullable
    ConfigDef.Validator getValidator();

    ConfigDef.Type getType();

    String getGroup();

    boolean isRequired();
  }

  @Value.Immutable
  interface Group {
    String getName();

    List<Item> getItems();
  }

  @Value.Immutable
  interface Converter extends Notes {
    Class getCls();
  }

  interface Configurable extends Notes {
    Class getCls();

    Configuration getConfiguration();

    List<String> getExamples();
  }

  @Value.Immutable
  interface Configuration {
    List<Plugin.Group> getGroups();

    List<Plugin.Item> getRequiredConfigs();
  }


  interface Connector extends Configurable {

  }

  @Value.Immutable
  interface SourceConnector extends Connector {

  }

  @Value.Immutable
  interface SinkConnector extends Connector {

  }

  @Value.Immutable
  interface Transformation extends Configurable {
    boolean isKeyValue();

    @Nullable
    Class getKey();

    @Nullable
    Class getValue();

  }

  interface Example extends Notes {
    @JsonProperty(value = "name")
    String getName();

    @JsonProperty(value = "config")
    Map<String, String> getConfig();
  }

  interface ConnectorExample extends Example {
    @JsonProperty(value = "transformations")
    Map<String, Map<String, String>> transformations();
  }

  @Value.Style(jdkOnly = true)
  @Value.Immutable
  @JsonSerialize(as = ImmutableSinkConnectorExample.class)
  @JsonDeserialize(as = ImmutableSinkConnectorExample.class)
  interface SinkConnectorExample extends ConnectorExample {
    @Nullable
    @JsonProperty(value = "input")
    SinkRecord getInput();

    @Nullable
    @JsonProperty(value = "output")
    Object getOutput();
  }

  @Value.Style(jdkOnly = true)
  @Value.Immutable
  @JsonSerialize(as = ImmutableSourceConnectorExample.class)
  @JsonDeserialize(as = ImmutableSourceConnectorExample.class)
  interface SourceConnectorExample extends ConnectorExample {
    @Nullable
    @JsonProperty(value = "output")
    SourceRecord getOutput();
  }

  @Value.Style(jdkOnly = true)
  @Value.Immutable
  @JsonSerialize(as = ImmutableTransformationExample.class)
  @JsonDeserialize(as = ImmutableTransformationExample.class)
  @JsonPropertyOrder({
      "title", "warning", "tip", "important",
      "danger", "note", "settings", "input",
      "output", "inputFocus", "outputFocus"})
  interface TransformationExample extends Example {
    @Nullable
    String getChildClass();

    @Nullable
    @JsonProperty(value = "input")
    SinkRecord getInput();
  }

  @Value.Immutable
  interface TransformationExampleInput {
    @Nullable
    String getConfig();

    List<Integer> getInputEmphasizeLines();

    @Nullable
    String getInputJson();

    List<Integer> getOutputEmphasizeLines();

    @Nullable
    String getOutputJson();

    TransformationExample getExample();
  }

  @Value.Immutable
  interface SinkConnectorExampleInput {
    @Nullable
    String getConfig();

    SinkConnectorExample getExample();

    @Nullable
    String getInputJson();

    @Nullable
    String getInputDescription();

    @Nullable
    String getOutputJson();

    @Nullable
    String getOutputDescription();
  }

  @Value.Immutable
  interface SourceConnectorExampleInput {
    @Nullable
    String getConfig();

    SourceConnectorExample getExample();

    @Nullable
    String getOutputJson();

    @Nullable
    String getOutputDescription();

  }
}
