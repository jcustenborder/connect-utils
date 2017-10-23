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
package com.github.jcustenborder.kafka.connect.utils.templates;

import com.github.jcustenborder.kafka.connect.utils.config.Description;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationDanger;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationImportant;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationTip;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationWarning;
import com.github.jcustenborder.kafka.connect.utils.config.Title;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Connector;
import org.apache.kafka.connect.transforms.Transformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TemplateInput {
  private static final Logger log = LoggerFactory.getLogger(TemplateInput.class);
  private String title;
  private String description;
  private String className;
  private String warning;
  private String tip;
  private String important;
  private String danger;
  private TemplateConfigDef config;

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getClassName() {
    return className;
  }

  public String getWarning() {
    return warning;
  }

  public String getTip() {
    return tip;
  }

  public String getImportant() {
    return important;
  }

  public String getDanger() {
    return danger;
  }

  public TemplateConfigDef getConfig() {
    return this.config;
  }

  private static String title(Class<?> aClass) {
    final String result;
    Title annotation = aClass.getAnnotation(Title.class);

    if (null != annotation) {
      result = annotation.value();
    } else {
      result = aClass.getSimpleName();
    }
    return result;
  }

  private static String description(Class<?> aClass) {
    final String result;
    Description annotation = aClass.getAnnotation(Description.class);

    if (null != annotation) {
      result = annotation.value();
    } else {
      result = null;
    }
    return result;
  }

  private static String danger(Class<?> aClass) {
    final String result;
    DocumentationDanger annotation = aClass.getAnnotation(DocumentationDanger.class);

    if (null != annotation) {
      result = annotation.value();
    } else {
      result = null;
    }
    return result;
  }

  private static String important(Class<?> aClass) {
    final String result;
    DocumentationImportant annotation = aClass.getAnnotation(DocumentationImportant.class);

    if (null != annotation) {
      result = annotation.value();
    } else {
      result = null;
    }
    return result;
  }

  private static String tip(Class<?> aClass) {
    final String result;
    DocumentationTip annotation = aClass.getAnnotation(DocumentationTip.class);

    if (null != annotation) {
      result = annotation.value();
    } else {
      result = null;
    }
    return result;
  }

  private static String warning(Class<?> aClass) {
    final String result;
    DocumentationWarning annotation = aClass.getAnnotation(DocumentationWarning.class);

    if (null != annotation) {
      result = annotation.value();
    } else {
      result = null;
    }
    return result;
  }

  private static void populateTemplate(Class<?> aClass, TemplateInput result, ConfigDef config) {
    result.className = aClass.getName();
    result.title = title(aClass);
    result.description = description(aClass);
    result.tip = tip(aClass);
    result.warning = warning(aClass);
    result.important = important(aClass);
    result.danger = danger(aClass);
    result.config = TemplateConfigDef.from(config);
  }

  public static TemplateInput fromTransformation(Class<? extends Transformation> transform) throws IllegalAccessException, InstantiationException {
    final TemplateInput result = new TemplateInput();

    Transformation sourceConnector = transform.newInstance();
    ConfigDef config = sourceConnector.config();
    assertNotNull(config, "config() cannot return a null.");

    populateTemplate(transform, result, config);

    return result;
  }


  public static TemplateInput fromConnector(Class<? extends Connector> connectorClass) throws IllegalAccessException, InstantiationException {
    final TemplateInput result = new TemplateInput();
    Connector connector = connectorClass.newInstance();
    ConfigDef config = connector.config();
    assertNotNull(config, "config() cannot return a null.");
    populateTemplate(connectorClass, result, config);
    return result;
  }

  public static void checkLength(Map<String, Integer> columnLengths, String name, Object value) {
    final int length = (value != null ? value.toString().length() : 0) + 2;

    final int current = columnLengths.getOrDefault(name, 0);

    if (length > current) {
      columnLengths.put(name, length);
    }
  }

}
