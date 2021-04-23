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
import com.google.common.base.Strings;
import com.google.common.io.Files;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.source.SourceRecord;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


@Value.Immutable
public interface Plugin extends Notes {
  String getIntroduction();

  String getPluginName();

  String getPluginOwner();

  List<Transformation> getTransformations();

  List<SinkConnector> getSinkConnectors();

  List<SourceConnector> getSourceConnectors();

  List<Converter> getConverters();

  List<ConfigProvider> getConfigProviders();

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
  interface Converter extends Configurable {
    Class getCls();
  }

  @Value.Immutable
  interface ConfigProvider extends Configurable {
    Class getCls();
  }

  interface Configurable extends Notes {
    Class getCls();

    @Nullable
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

  @Value.Immutable
  interface SchemaInput {
    @Nullable
    String getName();

    @Nullable
    @Value.Derived
    default String getSchemaLink() {
      String name = getName();
      if (Strings.isNullOrEmpty(name)) {
        return String.format("schema-%s", getType()).toLowerCase();
      }
      return String.format("schema-%s", name).replace('.', '-').toLowerCase();
    }

    @Nullable
    @Value.Derived
    default String getShortName() {
      String name = getName();
      if (Strings.isNullOrEmpty(name)) {
        return name;
      }
      String shortName = Files.getFileExtension(name);
      return shortName;
    }

    @Nullable
    String getDoc();

    boolean isOptional();

    Schema.Type getType();

    @Nullable
    String getFieldName();

    @Nullable
    List<SchemaInput> getFields();

    @Nullable
    SchemaInput key();

    @Nullable
    SchemaInput value();

    @Value.Derived
    default String getRefLink() {
      StringBuilder builder = new StringBuilder();

      if (Schema.Type.MAP == getType()) {
        builder.append(":ref:`schema-map`");
        builder.append(" < ");
        builder.append(":ref:`");
        builder.append(key().getSchemaLink());
        builder.append("` , :ref:`");
        builder.append(value().getSchemaLink());
        builder.append("` > ");
      } else if (Schema.Type.ARRAY == getType()) {
        builder.append(":ref:`schema-array`");
        builder.append(" < ");
        builder.append(":ref:`");
        builder.append(value().getSchemaLink());
        builder.append("` >");
      } else {
        builder.append(":ref:`");
        builder.append(getSchemaLink());
        builder.append('`');
      }

      return builder.toString();
    }

//    @Value.Derived
//    default String getLink() {
//      String result;
//      if (!Strings.isNullOrEmpty(getName())) {
//        result = String.format("schema-%s", getShortName());
//      } else {
//        result = "schema-unknown";
//      }
//
//      return result;
//    }

    @Value.Derived
    default String getTable() {
      StringBuilder builder = new StringBuilder();
      List<String> headers = Arrays.asList(
          "Name",
          "Type",
          "Required",
          "Description"
      );

      List<List<String>> rows = new ArrayList<>();
      rows.add(headers);

      if (null != getFields()) {
        for (SchemaInput field : getFields()) {
          rows.add(
              Arrays.asList(
                  field.getFieldName(),
                  field.getRefLink(),
                  Boolean.toString(!field.isOptional()),
                  Strings.isNullOrEmpty(field.getDoc()) ? "" : field.getDoc()
              )
          );
        }
      }


      List<Integer> rowSizes = new ArrayList<>();
      for (int i = 0; i < headers.size(); i++) {
        rowSizes.add(0);
      }

      for (List<String> row : rows) {
        for (int i = 0; i < headers.size(); i++) {
          final int current = rowSizes.get(i);
          final int cellLength = row.get(i).length();
          rowSizes.set(i, Math.max(current, cellLength));
        }
      }


      String rowFormat = "\n|";
      String rowEndFormat = "\n+";
      String headerEndFormat = "\n+";

      for (Integer size : rowSizes) {
        rowFormat += " %s |";
        rowEndFormat += Strings.repeat("-", size + 2) + "+";
        headerEndFormat += Strings.repeat("=", size + 2) + "+";
      }

      String finalRowFormat = rowFormat;
      Function<List<String>, String> formatRow = row -> {
        String[] padded = new String[row.size()];
        for (int i = 0; i < row.size(); i++) {
          String value = row.get(i);
          final Integer expectedSize = rowSizes.get(i);
          padded[i] = Strings.padEnd(value, expectedSize, ' ');
        }
        return String.format(finalRowFormat, padded);
      };

      builder.append(rowEndFormat);
      builder.append(formatRow.apply(headers));
      builder.append(headerEndFormat);

      for (int rowIndex = 1; rowIndex < rows.size(); rowIndex++) {
        List<String> row = rows.get(rowIndex);
        builder.append(formatRow.apply(row));
        builder.append(rowEndFormat);
      }

      return builder.toString();
    }
  }
}
