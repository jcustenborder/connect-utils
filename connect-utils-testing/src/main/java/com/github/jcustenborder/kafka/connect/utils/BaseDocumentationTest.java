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

import com.github.jcustenborder.kafka.connect.utils.templates.ConnectorTemplate;
import com.github.jcustenborder.kafka.connect.utils.templates.PluginTemplate;
import com.github.jcustenborder.kafka.connect.utils.templates.SourceConnectorTemplate;
import com.github.jcustenborder.kafka.connect.utils.templates.TemplateSchema;
import com.github.jcustenborder.kafka.connect.utils.templates.TransformationTemplate;
import com.github.jcustenborder.kafka.connect.utils.templates.markdown.MarkdownTemplateHelper;
import com.github.jcustenborder.kafka.connect.utils.templates.rst.RstTemplateHelper;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import freemarker.cache.ClassTemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import guru.nidi.graphviz.attribute.RankDir;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Label;
import org.apache.kafka.common.config.ConfigDef;
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
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static guru.nidi.graphviz.model.Factory.to;
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

  <T> List<Class<? extends T>> list(Class<T> cls) {
    List<Class<? extends T>> classes = reflections.getSubTypesOf(cls)
        .stream()
        .filter(aClass -> packages.contains(aClass.getPackage().getName()))
        .filter(aClass -> Modifier.isPublic(aClass.getModifiers()))
        .filter(aClass -> !Modifier.isAbstract(aClass.getModifiers()))
        .collect(Collectors.toList());
    classes.sort(Comparator.comparing(Class::getName));
    return classes;
  }

  PluginTemplate pluginTemplate;
  Set<String> packages;

  @BeforeEach
  public void before() throws MalformedURLException {
    log.info("before() - Configuring reflections to use package '{}'", packages());

    if (null == this.reflections) {
      this.reflections = new Reflections(new ConfigurationBuilder()
          .setUrls(ClasspathHelper.forJavaClassPath())
          .forPackages(packages())
      );
    }

    this.packages = ImmutableSet.copyOf(packages());

    List<Class<? extends Transformation>> transformClasses = list(Transformation.class);
    List<Class<? extends SourceConnector>> sourceConnectorClasses = list(SourceConnector.class);
    List<Class<? extends SinkConnector>> sinkConnectorClasses = list(SinkConnector.class);

    this.pluginTemplate = PluginTemplate.from(sourceConnectorClasses, sinkConnectorClasses, transformClasses);
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

  DynamicTest connectorRstTest(ConnectorTemplate connectorTemplate, final String templateName, final File parentDirectory) {
    if (!parentDirectory.isDirectory()) {
      parentDirectory.mkdirs();
    }


    return dynamicTest(connectorTemplate.getSimpleName(), () -> {
      final File graphOutputFile = new File(parentDirectory, connectorTemplate.getDiagramFileName());

      final Graph g;
      if (connectorTemplate instanceof SourceConnectorTemplate) {
        g = graph()
            .graphAttr().with(RankDir.LEFT_TO_RIGHT)
            .directed()
            .with(
                node(connectorTemplate.getSimpleName()).with(Shape.RECTANGLE)
                    .link(
                        to(node("Kafka Connect").with(Shape.RECTANGLE).link(to(node("Kafka").with(Shape.RECTANGLE)).with(Label.of("Writes messages to")))).with(Label.of("Hosted by"))

                    )
            );
      } else {
        g = graph()
            .graphAttr().with(RankDir.LEFT_TO_RIGHT)
            .directed()
            .with(
                node("Kafka").with(Shape.RECTANGLE)
                    .link(
                        to(node("Kafka Connect").with(Shape.RECTANGLE).link(to(node(connectorTemplate.getSimpleName()).with(Shape.RECTANGLE)).with(Label.of("Writes data to")))).with(Label.of("Pulls Data from"))

                    )
            );
      }


      Graphviz.fromGraph(g)
          .width(350)
          .render(Format.SVG_STANDALONE)
          .toFile(graphOutputFile);

      final File outputFile = new File(parentDirectory, connectorTemplate.getSimpleName() + ".rst");

      Template template = configuration.getTemplate(templateName);
      log.info("Writing {}", outputFile);
      try (Writer writer = Files.newWriter(outputFile, Charsets.UTF_8)) {
        process(writer, template, connectorTemplate);
      }
    });
  }

  DynamicTest transformRstTest(TransformationTemplate transformationTemplate, final String templateName, final File parentDirectory) {
    if (!parentDirectory.isDirectory()) {
      parentDirectory.mkdirs();
    }

    final String testName = transformationTemplate.getTestName();

    return dynamicTest(testName, () -> {
      final File outputFile = new File(parentDirectory, testName.toLowerCase() + ".rst");

      Template template = configuration.getTemplate(templateName);
      log.info("Writing {}", outputFile);
      try (Writer writer = Files.newWriter(outputFile, Charsets.UTF_8)) {
        process(writer, template, transformationTemplate);
      }
    });
  }

  final File outputDirectory = new File("target/docs");

  @TestFactory
  public Stream<DynamicTest> sources() {
    final File parentDirectory = new File(outputDirectory, "sources");
    final String templateName = "rst/source.rst.ftl";
    return this.pluginTemplate.getSourceConnectors().stream().map(connectorTemplate -> connectorRstTest(connectorTemplate, templateName, parentDirectory));
  }

  @TestFactory
  public Stream<DynamicTest> sinks() {
    final File parentDirectory = new File(outputDirectory, "sinks");
    final String templateName = "rst/sink.rst.ftl";
    return this.pluginTemplate.getSinkConnectors().stream().map(connectorTemplate -> connectorRstTest(connectorTemplate, templateName, parentDirectory));
  }

  void process(Writer writer, Template template, Object input) throws IOException, TemplateException {
    Map<String, Object> variables = ImmutableMap.of(
        "input", input,
        "rstHelper", new RstTemplateHelper(),
        "markdownHelper", new MarkdownTemplateHelper()
    );
    template.process(variables, writer);
  }

  @TestFactory
  public Stream<DynamicTest> transformations() {
    final File parentDirectory = new File(outputDirectory, "transformations");
    final String templateName = "rst/transformation.rst.ftl";
    return this.pluginTemplate.getTransformations().stream().map(connectorTemplate -> transformRstTest(connectorTemplate, templateName, parentDirectory));
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
    return this.schemas().stream()
        .filter(schema -> !Strings.isNullOrEmpty(schema.name()))

        .map(schema -> dynamicTest(String.format("%s.%s", schema.type(), schema.name()), () -> {
          StringBuilder filenameBuilder = new StringBuilder()
              .append(schema.type().toString().toLowerCase());
          if (!Strings.isNullOrEmpty(schema.name())) {
            filenameBuilder.append('.').append(schema.name());
          }
          filenameBuilder.append(".rst");
          File outputFile = new File(parentDirectory, filenameBuilder.toString());
          Template template = configuration.getTemplate(templateName);
          log.info("Writing {}", outputFile);


          try (Writer writer = Files.newWriter(outputFile, Charsets.UTF_8)) {
            Map<String, Object> variables = ImmutableMap.of(
                "input", TemplateSchema.of(schema),
                "helper", new RstTemplateHelper()
            );
            template.process(variables, writer);
          }
        }));
  }

  @Test
  public void rst() throws IOException, TemplateException {
    final File outputFile = new File("target", "README.rst");
    Template template = configuration.getTemplate("rst/README.rst.ftl");
    log.info("Writing {}", outputFile);
    try (Writer writer = Files.newWriter(outputFile, Charsets.UTF_8)) {
      process(writer, template, this.pluginTemplate);
    }

  }

  @Test
  public void markdown() throws IOException, TemplateException {
    final File outputFile = new File("target", "README.md");
    Template template = configuration.getTemplate("md/README.md.ftl");
    final String output;
    try (StringWriter writer = new StringWriter()) {
      writer.write('\n');
      process(writer, template, this.pluginTemplate);
      output = writer.toString();
    }
    log.info("\n{}", output);
    Files.write(output, outputFile, Charsets.UTF_8);

  }
}
