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
package io.confluent.kafka.connect.utils.config;

import org.apache.kafka.common.config.ConfigDef;

import java.util.ArrayList;
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
    StringBuilder b = new StringBuilder();

    List<ConfigDef.ConfigKey> configs = getSortedList(configDef.configKeys());
    String[] headers = new String[]{
        "Name", "Description", "Type", "Default", "Valid Values", "Importance"
    };
    int[] lengths = new int[headers.length];
    for (int i = 0; i < headers.length; i++) {
      lengths[i] = headers[i].length();
    }

    for (ConfigDef.ConfigKey def : configs) {
      for (int i = 0; i < headers.length; i++) {
        int length = 0;
        switch (i) {
          case 0: //Name
            length = null == def.name ? 0 : def.name.length();
            break;
          case 1:
            length = null == def.documentation ? 0 : def.documentation.length();
            break;
          case 2:
            length = null == def.type ? 0 : def.type.toString().length();
            break;
          case 3:
            String defaultValue = getDefaultValue(def);
            length = null == defaultValue ? 0 : defaultValue.length();
            break;
          case 4:
            String validValues = def.validator != null ? def.validator.toString() : "";
            length = null == validValues ? 0 : validValues.length();
            break;
          case 5:
            length = null == def.importance ? 0 : def.importance.toString().length();
            break;
          default:
            throw new IllegalArgumentException("There are more headers than columns.");
        }
        if (length > lengths[i]) {
          lengths[i] = length;
        }
      }
    }

    for (int i = 0; i < headers.length; i++) {
      String header = headers[i];
      String format = " %-" + lengths[i] + "s ";
      String value = String.format(format, header);

      if (i == 0) {
        b.append("|");
      }
      b.append(value);
      b.append("|");
      if (i == headers.length - 1) {
        b.append("\n");
      }
    }

    for (int i = 0; i < headers.length; i++) {
      String format = " %-" + lengths[i] + "s ";
      String value = String.format(format, "").replace(" ", "-");

      if (i == 0) {
        b.append("|");
      }
      b.append(value);
      b.append("|");
      if (i == headers.length - 1) {
        b.append("\n");
      }
    }

    for (ConfigDef.ConfigKey def : configs) {
      for (int i = 0; i < headers.length; i++) {
        int length = lengths[i];
        String format = " %-" + lengths[i] + "s ";
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

        if (i == 0) {
          b.append("|");
        }
        b.append(String.format(format, value));
        b.append("|");
        if (i == headers.length - 1) {
          b.append("\n");
        }
      }
    }

    return b.toString();
  }
}
