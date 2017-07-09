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
package com.github.jcustenborder.kafka.connect.utils;

import com.github.jcustenborder.kafka.connect.utils.config.Description;
import com.github.jcustenborder.kafka.connect.utils.config.MarkdownFormatter;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import freemarker.cache.ClassTemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Connector;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.sink.SinkConnector;
import org.apache.kafka.connect.source.SourceConnector;
import org.apache.kafka.connect.transforms.Transformation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public abstract class BaseDocumentationTest {
  private static final Logger log = LoggerFactory.getLogger(BaseDocumentationTest.class);

  protected List<Schema> schemas() {
    return Arrays.asList();
  }

  protected abstract String[] packages();

  static Configuration configuration;
  static ClassTemplateLoader loader;

  @BeforeAll
  public static void loadTemplates() {
    loader = new ClassTemplateLoader(
        BaseDocumentationTest.class,
        "templates"
    );

    configuration = new Configuration(Configuration.getVersion());
    configuration.setDefaultEncoding("UTF-8");
    configuration.setTemplateLoader(loader);
    configuration.setObjectWrapper(new BeansWrapper(Configuration.getVersion()));
  }


  Reflections reflections;
  List<Class<? extends Transformation>> transformClasses;
  List<Class<? extends Connector>> connectorClasses;
  List<Class<? extends SourceConnector>> sourceConnectorClasses;
  List<Class<? extends SinkConnector>> sinkConnectorClasses;


  <T> List<Class<? extends T>> list(Class<T> cls) {
    List<Class<? extends T>> classes = reflections.getSubTypesOf(cls)
        .stream()
        .filter(aClass -> !Modifier.isAbstract(aClass.getModifiers()) && Modifier.isPublic(aClass.getModifiers()))
        .collect(Collectors.toList());
    classes.sort(Comparator.comparing(Class::getName));
    return classes;
  }

  @BeforeEach
  public void before() throws MalformedURLException {
    log.info("before() - Configuring reflections to use package '{}'", packages());

    if (null == this.reflections) {
      this.reflections = new Reflections(new ConfigurationBuilder()
          .setUrls(ClasspathHelper.forJavaClassPath())
          .forPackages(packages())
      );
    }

    if (null == this.transformClasses) {
      this.transformClasses = list(Transformation.class);
    }
    if (null == this.sourceConnectorClasses) {
      this.sourceConnectorClasses = list(SourceConnector.class);
    }
    if (null == this.sinkConnectorClasses) {
      this.sinkConnectorClasses = list(SinkConnector.class);
    }
    if (null == connectorClasses) {
      this.connectorClasses = new ArrayList<>(this.sourceConnectorClasses.size() + this.sinkConnectorClasses.size());
      this.connectorClasses.addAll(this.sourceConnectorClasses);
      this.connectorClasses.addAll(this.sinkConnectorClasses);
    }
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

  DynamicTest connectorRstTest(Class<? extends Connector> connectorClass, final String templateName, final File parentDirectory) {
    if (!parentDirectory.isDirectory()) {
      parentDirectory.mkdirs();
    }

    return dynamicTest(connectorClass.getSimpleName(), () -> {
      final File outputFile = new File(parentDirectory, connectorClass.getSimpleName().toLowerCase() + ".rst");
      TemplateInput input = TemplateInput.fromConnector(connectorClass);
      Template template = configuration.getTemplate(templateName);
      log.info("Writing {}", outputFile);
      try (Writer writer = Files.newWriter(outputFile, Charsets.UTF_8)) {
        process(writer, template, input);
      }
    });
  }

  DynamicTest transformRstTest(Class<? extends Transformation> transformationClass, final String templateName, final File parentDirectory) {
    if (!parentDirectory.isDirectory()) {
      parentDirectory.mkdirs();
    }

    return dynamicTest(transformationClass.getSimpleName(), () -> {
      final File outputFile = new File(parentDirectory, transformationClass.getSimpleName().toLowerCase() + ".rst");
      TemplateInput input = TemplateInput.fromTransformation(transformationClass);
      Template template = configuration.getTemplate(templateName);
      log.info("Writing {}", outputFile);
      try (Writer writer = Files.newWriter(outputFile, Charsets.UTF_8)) {
        process(writer, template, input);
      }
    });
  }

  final File outputDirectory = new File("target/docs");

  @TestFactory
  public Stream<DynamicTest> sources() {
    final File parentDirectory = new File(outputDirectory, "sources");
    final String templateName = "rst/source.rst.ftl";
    return this.sourceConnectorClasses.stream().map(aClass -> connectorRstTest(aClass, templateName, parentDirectory));
  }

  @TestFactory
  public Stream<DynamicTest> sinks() {
    final File parentDirectory = new File(outputDirectory, "sinks");
    final String templateName = "rst/sink.rst.ftl";
    return this.sinkConnectorClasses.stream().map(aClass -> connectorRstTest(aClass, templateName, parentDirectory));
  }

  void process(Writer writer, Template template, Object input) throws IOException, TemplateException {
    Map<String, Object> variables = ImmutableMap.of("input", input);
    template.process(variables, writer);
  }

  @TestFactory
  public Stream<DynamicTest> transformations() {
    final File parentDirectory = new File(outputDirectory, "transformations");
    final String templateName = "rst/transformation.rst.ftl";
    return this.transformClasses.stream().map(aClass -> transformRstTest(aClass, templateName, parentDirectory));
  }

  @TestFactory
  public Stream<DynamicTest> schema() throws IOException {
    final File parentDirectory = new File(outputDirectory, "schemas");
    if (!parentDirectory.exists()) {
      parentDirectory.mkdirs();
    }

    List<Schema> schemas = schemas();

    if (null != schemas && !schemas.isEmpty()) {
      final File schemaRstPath = new File(outputDirectory, "schemas.rst");
      final String schemaRst = "=======\n" +
          "Schemas\n" +
          "=======\n" +
          "\n" +
          ".. toctree::\n" +
          "    :maxdepth: 0\n" +
          "    :caption: Schemas:\n" +
          "    :glob:\n" +
          "\n" +
          "    schemas/*";
      Files.write(schemaRst, schemaRstPath, Charsets.UTF_8);
    }


    final String templateName = "rst/schema.rst.ftl";
    return this.schemas().stream().filter(schema -> !Strings.isNullOrEmpty(schema.name())).map(schema -> dynamicTest(String.format("%s.%s", schema.type(), schema.name()), () -> {
      StringBuilder filenameBuilder = new StringBuilder()
          .append(schema.type().toString().toLowerCase());
      if (!Strings.isNullOrEmpty(schema.name())) {
        filenameBuilder.append('.').append(schema.name());
      }
      filenameBuilder.append(".rst");
      File outputFile = new File(parentDirectory, filenameBuilder.toString());
      Template template = configuration.getTemplate(templateName);
      log.info("Writing {}", outputFile);

      Map<String, Integer> lengths = new LinkedHashMap<>();
      TemplateInput.checkLength(lengths, "name", "name");
      TemplateInput.checkLength(lengths, "optional", "Optional");
      TemplateInput.checkLength(lengths, "schema", "Schema");
      TemplateInput.checkLength(lengths, "defaultValue", "Default Value");
      TemplateInput.checkLength(lengths, "doc", "Documentation");

      for (Field field : schema.fields()) {
        TemplateInput.checkLength(lengths, "name", field.name());
        TemplateInput.checkLength(lengths, "optional", field.schema().isOptional());
        TemplateInput.checkLength(lengths, "schema", Strings.padEnd(field.schema().type().toString(), 50, ' '));
        TemplateInput.checkLength(lengths, "defaultValue", field.schema().defaultValue());
        TemplateInput.checkLength(lengths, "doc", field.schema().doc());
      }


      try (Writer writer = Files.newWriter(outputFile, Charsets.UTF_8)) {
        Map<String, Object> variables = ImmutableMap.of(
            "input", schema,
            "lengths", lengths
        );
        template.process(variables, writer);
      }
    }));
  }

  @Test
  public void markdown() throws IOException, IllegalAccessException, InstantiationException {
    try (StringWriter stringWriter = new StringWriter()) {
      try (PrintWriter writer = new PrintWriter(stringWriter)) {
        writer.println();
        writer.println("# Configuration");
        writer.println();

        for (Class<? extends Connector> connectorClass : connectorClasses) {
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

        for (Class<? extends Transformation> transformationClass : transformClasses) {
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
          writer.println(MarkdownFormatter.toMarkdown(configDef));
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
