/**
 * Copyright © 2016 Jeremy Custenborder (jcustenborder@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jcustenborder.kafka.connect.utils;

import com.github.jcustenborder.kafka.connect.utils.config.Description;
import com.github.jcustenborder.kafka.connect.utils.config.MarkdownFormatter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Connector;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.transforms.Transformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

public abstract class BaseDocumentationTest {
  private static final Logger log = LoggerFactory.getLogger(BaseDocumentationTest.class);

  protected List<Schema> schemas() {
    return Arrays.asList();
  }

  protected abstract String[] packages();

  Reflections reflections;


  @BeforeEach
  public void before() throws MalformedURLException {
    log.info("before() - Configuring reflections to use package '{}'", packages());
    this.reflections = new Reflections(new ConfigurationBuilder()
        .setUrls(ClasspathHelper.forJavaClassPath())
        .forPackages(packages())
    );
  }

  protected List<Map.Entry<String, ConfigDef.ConfigKey>> required(ConfigDef configDef) {
    List<Map.Entry<String, ConfigDef.ConfigKey>> entries = new ArrayList<>();

    for (Map.Entry<String, ConfigDef.ConfigKey> kvp : configDef.configKeys().entrySet()) {
      if (!kvp.getValue().hasDefault()) {
        entries.add(kvp);
      }
    }

    return ImmutableList.copyOf(entries);
  }

  @Test
  public void markdown() throws IOException, IllegalAccessException, InstantiationException {
    Set<Class<? extends Transformation>> transformClasses = reflections.getSubTypesOf(Transformation.class);
    List<Class<? extends Transformation>> transformClassesSorted = new ArrayList<>(transformClasses);
    transformClassesSorted.sort(Comparator.comparing(Class::getName));

    Set<Class<? extends Connector>> connectorClasses = reflections.getSubTypesOf(Connector.class);
    List<Class<? extends Connector>> connectorClassesSorted = new ArrayList<>(connectorClasses);
    connectorClassesSorted.sort(Comparator.comparing(Class::getName));

    assertFalse(connectorClassesSorted.isEmpty(), "No connector classes were found.");
    try (StringWriter stringWriter = new StringWriter()) {
      try (PrintWriter writer = new PrintWriter(stringWriter)) {
        writer.println();
        writer.println("# Configuration");
        writer.println();

        for (Class<? extends Connector> connectorClass : connectorClassesSorted) {
          if (Modifier.isAbstract(connectorClass.getModifiers())) {
            log.trace("Skipping {} because it's abstract.", connectorClass.getName());
            continue;
          }

          writer.printf("## %s", connectorClass.getSimpleName());
          writer.println();

          Description descriptionAttribute = connectorClass.getAnnotation(Description.class);

          if (null != descriptionAttribute && !Strings.isNullOrEmpty(descriptionAttribute.value())) {
            writer.println();
            writer.append(descriptionAttribute.value());
            writer.println();
          } else {

          }

          writer.println();

          Connector connector = connectorClass.newInstance();
          ConfigDef configDef = connector.config();

          writer.println("```properties");
          writer.println("name=connector1");
          writer.println("tasks.max=1");
          writer.printf("connector.class=%s", connectorClass.getName());
          writer.println();
          writer.println();
          writer.println("# Set these required values");
          List<Map.Entry<String, ConfigDef.ConfigKey>> requiredValues = required(configDef);
          for (Map.Entry<String, ConfigDef.ConfigKey> kvp : requiredValues) {
            writer.printf("%s=", kvp.getKey());
            writer.println();
          }
          writer.println("```");
          writer.println();

          writer.println(MarkdownFormatter.toMarkdown(configDef));
        }

        for (Class<? extends Transformation> transformationClass : transformClassesSorted) {
          if (Modifier.isAbstract(transformationClass.getModifiers())) {
            log.trace("Skipping {} because it's abstract.", transformationClass.getName());
            continue;
          }

          writer.printf("## %s", transformationClass.getSimpleName());
          writer.println();

          Description descriptionAttribute = transformationClass.getAnnotation(Description.class);

          if (null != descriptionAttribute && !Strings.isNullOrEmpty(descriptionAttribute.value())) {
            writer.println();
            writer.append(descriptionAttribute.value());
            writer.println();
          } else {

          }

          writer.println();

          Transformation transformation = transformationClass.newInstance();
          ConfigDef configDef = transformation.config();

          writer.println("```properties");
          final String transformName = transformationClass.getSimpleName().toLowerCase();
          writer.printf("transforms=%s", transformName);
          writer.println();
          writer.printf("transforms.%s.type=%s", transformName, transformationClass.getName());
          writer.println();
          writer.println();
          writer.println("# Set these required values");
          List<Map.Entry<String, ConfigDef.ConfigKey>> requiredValues = required(configDef);
          for (Map.Entry<String, ConfigDef.ConfigKey> kvp : requiredValues) {
            writer.printf("transforms.%s.%s=", transformName, kvp.getKey());
            writer.println();
          }
          writer.println("```");
          writer.println();
        }

        List<Schema> schemas = schemas();

        if (!schemas.isEmpty()) {
          writer.println();
          writer.println("# Schemas");
          writer.println();

          for (Schema schema : schemas()) {
            writer.println(MarkdownFormatter.toMarkdown(schema));
          }
        }
      }

      log.info("{}", stringWriter);
    }
  }


}
