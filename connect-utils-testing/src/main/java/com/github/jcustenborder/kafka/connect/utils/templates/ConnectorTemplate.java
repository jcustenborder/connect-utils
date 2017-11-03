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

import com.github.jcustenborder.kafka.connect.utils.config.Description;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationDanger;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationImportant;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationNote;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationTip;
import com.github.jcustenborder.kafka.connect.utils.config.DocumentationWarning;
import com.github.jcustenborder.kafka.connect.utils.config.Title;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Connector;
import org.apache.kafka.connect.transforms.Transformation;

public class ConnectorTemplate {
  private final String title;
  private final String description;
  private final String className;
  private final String warning;
  private final String tip;
  private final String important;
  private final String danger;
  private final String note;
  private final String simpleName;
  private final String diagramFileName;
  private final TemplateConfigDef config;

  public ConnectorTemplate(Class<?> cls) {
    this.title = title(cls);
    this.description = description(cls);
    this.className = cls.getName();
    this.simpleName = cls.getSimpleName();
    this.diagramFileName = this.simpleName + ".svg";
    this.warning = warning(cls);
    this.tip = tip(cls);
    this.important = important(cls);
    this.danger = danger(cls);
    this.note = note(cls);

    final ConfigDef configDef;

    try {
      if (Connector.class.isAssignableFrom(cls)) {
        Connector connector = (Connector) cls.newInstance();
        configDef = connector.config();
      } else if (Transformation.class.isAssignableFrom(cls)) {
        Transformation transformation = (Transformation) cls.newInstance();
        configDef = transformation.config();
      } else {
        throw new UnsupportedOperationException(
            String.format("Class %s is not supported", cls.getName())
        );
      }
    } catch (InstantiationException | IllegalAccessException e) {
      throw new IllegalStateException(e);
    }

    this.config = TemplateConfigDef.from(configDef);
  }

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

  public String getNote() {
    return note;
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

  protected static String title(Class<?> aClass) {
    final String result;
    Title annotation = aClass.getAnnotation(Title.class);

    if (null != annotation) {
      result = annotation.value();
    } else {
      result = aClass.getSimpleName();
    }
    return result;
  }

  protected static String description(Class<?> aClass) {
    final String result;
    Description annotation = aClass.getAnnotation(Description.class);

    if (null != annotation) {
      result = annotation.value();
    } else {
      result = null;
    }
    return result;
  }

  protected static String danger(Class<?> aClass) {
    final String result;
    DocumentationDanger annotation = aClass.getAnnotation(DocumentationDanger.class);

    if (null != annotation) {
      result = annotation.value();
    } else {
      result = null;
    }
    return result;
  }

  protected static String important(Class<?> aClass) {
    final String result;
    DocumentationImportant annotation = aClass.getAnnotation(DocumentationImportant.class);

    if (null != annotation) {
      result = annotation.value();
    } else {
      result = null;
    }
    return result;
  }

  protected static String tip(Class<?> aClass) {
    final String result;
    DocumentationTip annotation = aClass.getAnnotation(DocumentationTip.class);

    if (null != annotation) {
      result = annotation.value();
    } else {
      result = null;
    }
    return result;
  }

  protected static String note(Class<?> aClass) {
    final String result;
    DocumentationNote annotation = aClass.getAnnotation(DocumentationNote.class);

    if (null != annotation) {
      result = annotation.value();
    } else {
      result = null;
    }
    return result;
  }

  protected static String warning(Class<?> aClass) {
    final String result;
    DocumentationWarning annotation = aClass.getAnnotation(DocumentationWarning.class);

    if (null != annotation) {
      result = annotation.value();
    } else {
      result = null;
    }
    return result;
  }

  public String getSimpleName() {
    return simpleName;
  }

  public String getDiagramFileName() {
    return diagramFileName;
  }
}
