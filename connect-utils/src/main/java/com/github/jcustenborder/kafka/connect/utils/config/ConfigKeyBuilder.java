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
package com.github.jcustenborder.kafka.connect.utils.config;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.kafka.common.config.ConfigDef;

import java.util.List;

public class ConfigKeyBuilder {
  final String name;
  final ConfigDef.Type type;
  String documentation;
  Object defaultValue;
  ConfigDef.Validator validator;
  ConfigDef.Importance importance;
  String group;
  int orderInGroup;
  ConfigDef.Width width;
  String displayName;
  List<String> dependents;
  ConfigDef.Recommender recommender;
  boolean internalConfig;

  private ConfigKeyBuilder(String group, String name, ConfigDef.Type type) {
    this(name, type);
    this.group = group;
  }

  private ConfigKeyBuilder(String name, ConfigDef.Type type) {
    this.name = name;
    this.type = type;
  }

  public static ConfigKeyBuilder of(String group, String name, ConfigDef.Type type) {
    return new ConfigKeyBuilder(group, name, type);
  }

  public static ConfigKeyBuilder of(String name, ConfigDef.Type type) {
    return new ConfigKeyBuilder(name, type);
  }

  public ConfigDef.ConfigKey build() {
    Preconditions.checkState(!Strings.isNullOrEmpty(this.name), "name cannot be null or blank.");
    Preconditions.checkState(!Strings.isNullOrEmpty(this.documentation), "documentation cannot be null or blank.");
    return new ConfigDef.ConfigKey(this.name, this.type, this.defaultValue, this.validator, this.importance, this.documentation, this.group, this.orderInGroup, this.width, this.displayName, this.dependents, this.recommender, this.internalConfig);
  }

  public String name() {
    return this.name;
  }

  public ConfigDef.Type type() {
    return this.type;
  }

  public String documentation() {
    return this.documentation;
  }

  public ConfigKeyBuilder documentation(String documentation) {
    this.documentation = documentation;
    return this;
  }

  public Object defaultValue() {
    return this.defaultValue;
  }

  public ConfigKeyBuilder defaultValue(Object defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

  public ConfigDef.Validator validator() {
    return this.validator;
  }

  public ConfigKeyBuilder validator(ConfigDef.Validator validator) {
    this.validator = validator;
    return this;
  }

  public ConfigDef.Importance importance() {
    return this.importance;
  }

  public ConfigKeyBuilder importance(ConfigDef.Importance importance) {
    this.importance = importance;
    return this;
  }

  public String group() {
    return this.group;
  }

  public ConfigKeyBuilder group(String group) {
    this.group = group;
    return this;
  }

  public int orderInGroup() {
    return this.orderInGroup;
  }

  public ConfigKeyBuilder orderInGroup(int orderInGroup) {
    this.orderInGroup = orderInGroup;
    return this;
  }

  public ConfigDef.Width width() {
    return this.width;
  }

  public ConfigKeyBuilder width(ConfigDef.Width width) {
    this.width = width;
    return this;
  }

  public String displayName() {
    return this.displayName;
  }

  public ConfigKeyBuilder displayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

  public List<String> dependents() {
    return this.dependents;
  }

  public ConfigKeyBuilder dependents(List<String> dependents) {
    this.dependents = dependents;
    return this;
  }

  public ConfigDef.Recommender recommender() {
    return this.recommender;
  }

  public ConfigKeyBuilder recommender(ConfigDef.Recommender recommender) {
    this.recommender = recommender;
    return this;
  }

  public boolean isinternalConfig() {
    return this.internalConfig;
  }

  public ConfigKeyBuilder internalConfig(boolean internalConfig) {
    this.internalConfig = internalConfig;
    return this;
  }
}