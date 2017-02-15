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

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Connector;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

public abstract class BaseDocumentationTest {
  private static final Logger log = LoggerFactory.getLogger(BaseDocumentationTest.class);


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

  @Test
  public void markdown() throws IOException, IllegalAccessException, InstantiationException {
    Set<Class<? extends Connector>> connectorClasses = reflections.getSubTypesOf(Connector.class);
    List<Class<? extends Connector>> sorted = new ArrayList<>(connectorClasses);
    sorted.sort(Comparator.comparing(Class::getName));

    assertFalse(sorted.isEmpty(), "No connector classes were found.");
    try (StringWriter stringWriter = new StringWriter()) {
      try (PrintWriter writer = new PrintWriter(stringWriter)) {
        writer.println();
        writer.println("# Configuration");
        writer.println();

        for (Class<? extends Connector> connectorClass : sorted) {
          if (Modifier.isAbstract(connectorClass.getModifiers())) {
            log.trace("Skipping {} because it's abstract.", connectorClass.getName());
            continue;
          }

          writer.printf("## %s", connectorClass.getSimpleName());
          writer.println();
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
          for (Map.Entry<String, ConfigDef.ConfigKey> kvp : configDef.configKeys().entrySet()) {
            writer.printf("%s=", kvp.getKey());
            writer.println();
          }
          writer.println("```");
          writer.println();

          writer.println(MarkdownFormatter.toMarkdown(configDef));
        }
      }

      log.info("{}", stringWriter);
    }
  }


}
