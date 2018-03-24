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

import com.google.common.collect.ImmutableList;
import org.apache.kafka.common.config.ConfigDef;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TemplateConfigDef implements Table {

  final List<TemplateConfigEntry> configs;

  private TemplateConfigDef(List<TemplateConfigEntry> configs) {
    this.configs = ImmutableList.copyOf(configs);
  }

  @Override
  public String getTitle() {
    return "Configuration";
  }

  @Override
  public List<String> getHeaders() {
    return Arrays.asList(
        "Name",
        "Type",
        "Importance",
        "Default Value",
        "Validator",
        "Documentation"
    );
  }

  @Override
  public List<List<String>> getRowData() {
    List<List<String>> result = new ArrayList<>(this.configs.size());

    for (TemplateConfigEntry entry : this.configs) {
      result.add(ImmutableList.of(
          entry.getName(),
          entry.getType(),
          entry.getImportance(),
          entry.getDefaultValue(),
          entry.getValidator(),
          entry.getDoc()
          )
      );
    }

    return ImmutableList.copyOf(result);
  }

  public List<TemplateConfigEntry> getConfigs() {
    return this.configs;
  }
  public List<TemplateConfigEntry> getRequiredConfigs() {
    return this.configs.stream().filter(entry -> entry.isRequired()).collect(Collectors.toList());
  }

  public static TemplateConfigDef from(ConfigDef config) {
    List<TemplateConfigEntry> configs = config.configKeys().entrySet().stream()
        .sorted((k1, k2) -> {
          // first take anything with no default value (therefore required)
          if (!k1.getValue().hasDefault() && k2.getValue().hasDefault())
            return -1;
          else if (!k2.getValue().hasDefault() && k1.getValue().hasDefault())
            return 1;

          // then sort by importance
          int cmp = k1.getValue().importance.compareTo(k2.getValue().importance);
          if (cmp == 0)
            // then sort in alphabetical order
            return k1.getValue().name.compareTo(k2.getValue().name);
          else
            return cmp;
        })
        .map(entry -> TemplateConfigEntry.of(entry.getValue()))
        .collect(Collectors.toList());
    return new TemplateConfigDef(configs);
  }
}
