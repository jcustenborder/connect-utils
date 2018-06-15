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
package com.github.jcustenborder.kafka.connect.utils.templates.model;

import com.github.jcustenborder.kafka.connect.utils.config.AnnotationHelper;
import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Connector;
import org.apache.kafka.connect.sink.SinkConnector;
import org.apache.kafka.connect.source.SourceConnector;
import org.apache.kafka.connect.transforms.Transformation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Configurable {
  private final String title;
  private final String description;
  private final String className;
  private final String warning;
  private final String tip;
  private final String important;
  private final String danger;
  private final String note;
  private final String simpleName;
  private final Configuration config;
  private final Class<?> cls;
  List<Example> examples;
  private final List<String> resourceFilters;

  public Configurable(Class<?> cls) {
    this.cls = cls;
    this.title = AnnotationHelper.title(cls);
    this.description = AnnotationHelper.description(cls);
    this.className = cls.getName();
    this.simpleName = cls.getSimpleName();
    this.warning = AnnotationHelper.warning(cls);
    this.tip = AnnotationHelper.tip(cls);
    this.important = AnnotationHelper.important(cls);
    this.danger = AnnotationHelper.danger(cls);
    this.note = AnnotationHelper.note(cls);

    final ConfigDef configDef;
    List<String> resourceFilters = new ArrayList<>();
    try {
      if (Connector.class.isAssignableFrom(cls)) {
        Connector connector = (Connector) cls.newInstance();
        resourceFilters.add(cls.getName().replace('.', '/') + "/");
        configDef = connector.config();
      } else if (Transformation.class.isAssignableFrom(cls)) {
        if ("Key".equalsIgnoreCase(cls.getSimpleName()) || "Value".equalsIgnoreCase(cls.getSimpleName())) {
          resourceFilters.add(cls.getSuperclass().getName().replace('.', '/') + "/");
        } else {
          resourceFilters.add(cls.getName().replace('.', '/') + "/");
        }

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
    this.resourceFilters = ImmutableList.copyOf(resourceFilters);

    this.config = Configuration.from(configDef);
  }

  public List<Example> getExamples() {
    return examples;
  }

  public void setExamples(List<Example> examples) {
    this.examples = examples;
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

  Predicate<String> resourceFilter() {
    return s -> {
      for (String filter : resourceFilters) {
        if (s.startsWith(filter))
          return true;
      }

      return false;
    };
  }

  public String getResourceFilter() {
    return this.className.replace('.', '/') + "/";
  }

  public Class<?> getCls() {
    return cls;
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

  public Configuration getConfig() {
    return this.config;
  }

  public String getSimpleName() {
    return simpleName;
  }

  private String getLowerCamelHyphenSimpleName() {
    return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, this.cls.getSimpleName());
  }

  private String getLowerCamelUnderscoreSimpleName() {
    return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, this.cls.getSimpleName());
  }

  public String getRstConnectorTag() {
    return "_" + getLowerCamelHyphenSimpleName();
  }

  public String getRstConnectorConfigTag() {
    return "_" + getLowerCamelHyphenSimpleName() + "-config";
  }

  public String getRstConnectorExamplesTag() {
    return "_" + getLowerCamelHyphenSimpleName() + "-examples";
  }

  public String getConfluentConnectorFileName() {
    return getLowerCamelUnderscoreSimpleName();
  }

  public String getConfluentConnectorConfigFileName() {
    return getLowerCamelUnderscoreSimpleName() + "_config";
  }
  public String getConfluentConnectorExampleFileName() {
    return getLowerCamelUnderscoreSimpleName() + "_example";
  }

  public File confluentConnectorRst(File docroot) {
    return new File(
        docroot,
        getConfluentConnectorFileName() + ".rst"
    );
  }

  public File confluentConfigRst(File docroot) {
    return new File(
        docroot,
        getConfluentConnectorConfigFileName() + ".rst"
    );
  }

  public File confluentExampleRst(File docroot) {
    return new File(
        docroot,
        getConfluentConnectorExampleFileName() + ".rst"
    );
  }

  public File connectorRst(File root) {
    final File parent;
    if (SourceConnector.class.isAssignableFrom(cls)) {
      parent = new File(root, "sources");
    } else if (SinkConnector.class.isAssignableFrom(cls)) {
      parent = new File(root, "sinks");
    } else {
      throw new UnsupportedOperationException(cls.getSimpleName() + " is not a source or sink");
    }

    return new File(parent, this.simpleName + ".rst");
  }

}
