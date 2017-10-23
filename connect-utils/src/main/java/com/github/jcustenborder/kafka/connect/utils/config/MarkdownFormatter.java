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

import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.data.Date;
import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Time;
import org.apache.kafka.connect.data.Timestamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MarkdownFormatter {

  private static List<ConfigDef.ConfigKey> getSortedList(Map<String, ConfigDef.ConfigKey> configKeys) {
    List<ConfigDef.ConfigKey> configs = new ArrayList<ConfigDef.ConfigKey>(configKeys.values());
    Collections.sort(configs, new Comparator<ConfigDef.ConfigKey>() {
      public int compare(ConfigDef.ConfigKey k1, ConfigDef.ConfigKey k2) {
        // first take anything with no default value (therefore required)
        if (!k1.hasDefault() && k2.hasDefault())
          return -1;
        else if (!k2.hasDefault() && k1.hasDefault())
          return 1;

        // then sort by importance
        int cmp = k1.importance.compareTo(k2.importance);
        if (cmp == 0)
          // then sort in alphabetical order
          return k1.name.compareTo(k2.name);
        else
          return cmp;
      }
    });
    return configs;
  }

  static String getDefaultValue(ConfigDef.ConfigKey def) {
    if (def.hasDefault()) {
      if (def.defaultValue == null)
        return "null";
      else if (def.type == ConfigDef.Type.STRING && def.defaultValue.toString().isEmpty())
        return "\"\"";
      else
        return def.defaultValue.toString();
    } else
      return "";
  }

  public static String toMarkdown(ConfigDef configDef) {

    List<ConfigDef.ConfigKey> sortedConfigs = getSortedList(configDef.configKeys());
    String[] headers = new String[]{
        "Name", "Description", "Type", "Default", "Valid Values", "Importance"
    };

    List<List<String>> rows = new ArrayList<>();
    rows.add(Arrays.asList(headers));

    for (ConfigDef.ConfigKey def : sortedConfigs) {
      List<String> row = new ArrayList<>(headers.length);
      for (int i = 0; i < headers.length; i++) {
        String value;
        switch (i) {
          case 0: //Name
            value = def.name;
            break;
          case 1:
            value = null == def.documentation ? "" : def.documentation;
            break;
          case 2:
            value = def.type.toString().toLowerCase();
            break;
          case 3:
            String defaultValue = getDefaultValue(def);
            value = null == defaultValue ? "" : defaultValue;
            break;
          case 4:
            String validValues = def.validator != null ? def.validator.toString() : "";
            value = null == validValues ? "" : validValues;
            break;
          case 5:
            value = def.importance.toString().toLowerCase();
            break;
          default:
            throw new IllegalArgumentException("There are more headers than columns.");
        }
        row.add(value);
      }
      rows.add(row);
    }

    return markdownTable(rows);
  }

  static List<Integer> lengths(List<List<String>> rows) {
    List<Integer> lengths = new ArrayList<>(rows.size());
    for (int i = 0; i < rows.get(0).size(); i++) {
      lengths.add(rows.get(0).get(i).length());
    }
    for (List<String> row : rows) {
      for (int i = 0; i < row.size(); i++) {
        int previous = lengths.get(i);
        int current;
        if (Strings.isNullOrEmpty(row.get(i))) {
          current = 0;
        } else {
          current = row.get(i).length();
        }
        int value = Math.max(current, previous);
        lengths.set(i, value);
      }
    }
    return lengths;
  }

  static String markdownTable(List<List<String>> rows) {
    StringBuilder builder = new StringBuilder();
    List<Integer> lengths = lengths(rows);

    int index = 0;
    for (List<String> row : rows) {
      List<String> copy = new ArrayList<>(row.size());
      for (int i = 0; i < row.size(); i++) {
        String f = Strings.padEnd(row.get(i) == null ? "" : row.get(i), lengths.get(i), ' ');
        copy.add(f);
      }

      builder.append("| ");
      Joiner.on(" | ").appendTo(builder, copy);
      builder.append(" |\n");

      if (index == 0) {
        List<String> bar = new ArrayList<>(lengths.size());

        for (Integer length : lengths) {
          bar.add(Strings.repeat("-", length + 2));
        }
        builder.append("|");
        Joiner.on("|").appendTo(builder, bar);
        builder.append("|\n");
      }

      index++;
    }

    return builder.toString();
  }

  static String schema(Schema schema) {
    String result;
    if (!Strings.isNullOrEmpty(schema.name())) {
      if (Time.LOGICAL_NAME.equals(schema.name())) {
        result = "[Time](https://kafka.apache.org/0102/javadoc/org/apache/kafka/connect/data/Time.html)";
      } else if (Date.LOGICAL_NAME.equals(schema.name())) {
        result = "[Date](https://kafka.apache.org/0102/javadoc/org/apache/kafka/connect/data/Date.html)";
      } else if (Timestamp.LOGICAL_NAME.equals(schema.name())) {
        result = "[Timestamp](https://kafka.apache.org/0102/javadoc/org/apache/kafka/connect/data/Timestamp.html)";
      } else if (Decimal.LOGICAL_NAME.equals(schema.name())) {
        result = "[Decimal](https://kafka.apache.org/0102/javadoc/org/apache/kafka/connect/data/Decimal.html)";
      } else {
        result = String.format("[%s](#%s)", schema.name(), schema.name());
      }
    } else {
      if (Schema.Type.ARRAY == schema.type()) {
        result = String.format("Array of %s", schema(schema.valueSchema()));
      } else if (Schema.Type.MAP == schema.type()) {
        result = String.format("Map of <%s, %s>", schema(schema.keySchema()), schema(schema.valueSchema()));
      } else {
        result = String.format("[%s](https://kafka.apache.org/0102/javadoc/org/apache/kafka/connect/data/Schema.Type.html#%s)",
            CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, schema.type().toString()), schema.type()
        );
      }
    }

    return result;
  }

  public static String toMarkdown(Schema schema) {
    Preconditions.checkNotNull(schema, "schema cannot be null.");

    StringBuilder builder = new StringBuilder();

    builder.append("## ");
    if (Strings.isNullOrEmpty(schema.name())) {
      builder.append(schema.type());
    } else {
      builder.append(schema.name());
    }

    if (!Strings.isNullOrEmpty(schema.doc())) {
      builder.append("\n\n");
      builder.append(schema.doc());
    }

    if (Schema.Type.STRUCT == schema.type()) {
      List<List<String>> rows = new ArrayList<>();
      rows.add(
          ImmutableList.of(
              "Name",
              "Optional",
              "Schema",
              "Default Value",
              "Documentation"
          )
      );

      for (Field field : schema.fields()) {
        Schema fieldSchema = field.schema();

        List<String> row = ImmutableList.of(
            field.name(),
            String.valueOf(fieldSchema.isOptional()),
            schema(fieldSchema),
            fieldSchema.defaultValue() == null ? "" : fieldSchema.defaultValue().toString(),
            Strings.isNullOrEmpty(fieldSchema.doc()) ? "" : fieldSchema.doc()
        );
        rows.add(row);
      }

      builder.append("\n\n");
      builder.append(markdownTable(rows));
    } else if (Schema.Type.MAP == schema.type()) {

    } else {

    }

    return builder.toString();
  }
}
