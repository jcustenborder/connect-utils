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
package com.github.jcustenborder.kafka.connect.utils.templates.model;

import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import org.apache.kafka.common.config.ConfigDef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Configuration {

  final List<Item> requiredConfigs;
  final List<Group> groups;

  public List<Group> getGroups() {
    return groups;
  }

  private Configuration(Collection<Item> configs) {
    this.requiredConfigs = configs
        .stream()
        .filter(Item::isRequired)
        .collect(Collectors.toList());

    Multimap<String, Item> groupToItem = LinkedListMultimap.create();
    for (Item item : configs) {
      groupToItem.put(item.group, item);
    }
    List<Group> groups = new ArrayList<>();
    for (String group : groupToItem.keySet()) {
      Collection<Item> items = groupToItem.get(group);
      groups.add(new Group(group, new ArrayList<>(items)));
    }
    Collections.sort(groups);


    this.groups = ImmutableList.copyOf(groups);
  }

  public static Configuration from(ConfigDef config) {
    List<Item> configs = config.configKeys().entrySet().stream()
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
        .map(entry -> Item.of(entry.getValue()))
        .collect(Collectors.toList());
    return new Configuration(configs);
  }

  public List<Item> getRequiredConfigs() {
    return this.requiredConfigs;
  }

  public static class Group implements Comparable<Group> {
    final String name;
    final List<Item> items;

    public Group(String name, List<Item> items) {
      this.name = name;
      this.items = items;
    }

    public String getName() {
      return this.name;
    }

    public List<Item> getItems() {
      return this.items;
    }

    @Override
    public int compareTo(Group that) {
      if (null == that) {
        return 1;
      }

      String thisName =
          Item.GENERAL_GROUP.equalsIgnoreCase(this.name) ?
              "ZZZZZZZZZZZ" :
              this.name;
      String thatName =
          Item.GENERAL_GROUP.equalsIgnoreCase(that.name) ?
              "ZZZZZZZZZZZ" :
              that.name;

      return thisName.compareToIgnoreCase(thatName);
    }
  }


  public static class Item {
    public static final String GENERAL_GROUP = "General";
    private final String name;
    private final String importance;
    private final String doc;
    private final String defaultValue;
    private final String validator;
    private final String type;
    private final boolean isRequired;
    private final String group;

    private Item(String name, ConfigDef.Importance importance, String doc, Object defaultValue, ConfigDef.Validator validator, ConfigDef.Type type, boolean isRequired, String group) {
      this.name = name;
      this.importance = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, importance.toString());
      this.doc = !Strings.isNullOrEmpty(doc) ? doc : "";
      this.defaultValue = (null != defaultValue && ConfigDef.NO_DEFAULT_VALUE != defaultValue) ? defaultValue.toString() : "";
      this.validator = null != validator ? validator.toString() : "";
      this.type = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, type.toString());
      this.isRequired = isRequired;
      this.group = Strings.isNullOrEmpty(group) ? GENERAL_GROUP : group;
    }

    public static Item of(ConfigDef.ConfigKey configKey) {
      return new Item(configKey.name, configKey.importance,
          configKey.documentation, configKey.defaultValue, configKey.validator, configKey.type,
          !configKey.hasDefault(),
          configKey.group
      );
    }

    public String getName() {
      return name;
    }

    public String getImportance() {
      return importance;
    }

    public String getDoc() {
      return doc;
    }

    public String getDefaultValue() {
      return defaultValue;
    }

    public String getValidator() {
      return validator;
    }

    public String getType() {
      return type;
    }

    public String getGroup() {
      return group;
    }

    public boolean isRequired() {
      return this.isRequired;
    }
  }
}
