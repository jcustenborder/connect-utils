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

import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import org.apache.kafka.common.config.ConfigDef;

public class TemplateConfigEntry {
  private final String name;
  private final String importance;
  private final String doc;
  private final String defaultValue;
  private final String validator;
  private final String type;
  private final boolean isRequired;

  private TemplateConfigEntry(String name, ConfigDef.Importance importance, String doc, Object defaultValue, ConfigDef.Validator validator, ConfigDef.Type type, boolean isRequired) {
    this.name = name;
    this.importance = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, importance.toString());
    this.doc = !Strings.isNullOrEmpty(doc) ? doc : "";
    this.defaultValue = (null != defaultValue && ConfigDef.NO_DEFAULT_VALUE != defaultValue) ? defaultValue.toString() : "";
    this.validator = null != validator ? validator.toString() : "";
    this.type = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, type.toString());
    this.isRequired = isRequired;
  }

  public String name() {
    return name;
  }

  public String importance() {
    return importance;
  }

  public String doc() {
    return doc;
  }

  public String defaultValue() {
    return defaultValue;
  }

  public String validator() {
    return validator;
  }

  public String type() {
    return this.type;
  }

  public static TemplateConfigEntry of(ConfigDef.ConfigKey configKey) {

    return new TemplateConfigEntry(configKey.name, configKey.importance,
        configKey.documentation, configKey.defaultValue, configKey.validator, configKey.type,
        !configKey.hasDefault()
    );
  }

  public boolean isRequired() {
    return this.isRequired;
  }
}
