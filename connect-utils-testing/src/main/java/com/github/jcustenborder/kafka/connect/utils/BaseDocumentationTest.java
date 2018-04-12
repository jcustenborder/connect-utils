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

import com.github.jcustenborder.kafka.connect.utils.docs.Example;
import com.github.jcustenborder.kafka.connect.utils.templates.markdown.MarkdownTemplateHelper;
import com.github.jcustenborder.kafka.connect.utils.templates.model.Configurable;
import com.github.jcustenborder.kafka.connect.utils.templates.model.PluginData;
import com.github.jcustenborder.kafka.connect.utils.templates.model.SchemaData;
import com.github.jcustenborder.kafka.connect.utils.templates.model.SourceConnectorData;
import com.github.jcustenborder.kafka.connect.utils.templates.model.TransformationData;
import com.github.jcustenborder.kafka.connect.utils.templates.rst.RstTemplateHelper;
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
import guru.nidi.graphviz.attribute.RankDir;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Label;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigValue;
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
import org.reflections.scanners.ResourcesScanner;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static guru.nidi.graphviz.model.Factory.to;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public abstract class BaseDocumentationTest {
  private static final Logger log = LoggerFactory.getLogger(BaseDocumentationTest.class);

  protected List<Schema> schemas() {
    return Arrays.asList();
  }

  @Deprecated
  protected abstract String[] packages();

  protected Package getPackage() {
    return this.getClass().getPackage();
  }


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


  static <T> List<Class<? extends T>> list(Reflections reflections, Package pkg, Class<? extends T> cls) {
    List<Class<? extends T>> classes = reflections.getSubTypesOf(cls)
        .stream()
        .filter(c -> c.getName().startsWith(pkg.getName()))
        .filter(c -> Modifier.isPublic(c.getModifiers()))
        .filter(c -> !Modifier.isAbstract(c.getModifiers()))
        .collect(Collectors.toList());
    classes.sort(Comparator.comparing(Class::getName));
    return classes;
  }

  PluginData pluginData;

  static Map<Class, PluginData> pluginTemplateLookup = new LinkedHashMap<>();

  @BeforeEach
  public void before() throws MalformedURLException {
    log.info("before() - {}", this.getClass());
    Package pkg = this.getPackage();

    this.pluginData = pluginTemplateLookup.computeIfAbsent(this.getClass(), c -> {
      Reflections reflections = new Reflections(new ConfigurationBuilder()
          .setUrls(ClasspathHelper.forJavaClassPath())
          .forPackages(pkg.getName())
          .addScanners(new ResourcesScanner())
      );
      Set<String> resources = reflections.getResources(p -> p.endsWith(".json"));

      List<Class<? extends Transformation>> transformClasses = list(reflections, pkg, Transformation.class);
      List<Class<? extends SourceConnector>> sourceConnectorClasses = list(reflections, pkg, SourceConnector.class);
      List<Class<? extends SinkConnector>> sinkConnectorClasses = list(reflections, pkg, SinkConnector.class);
      return PluginData.from(pkg, resources, sourceConnectorClasses, sinkConnectorClasses, transformClasses);
    });
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

  DynamicTest connectorRstTest(Configurable configurable, final String templateName, final File parentDirectory) {
    if (!parentDirectory.isDirectory()) {
      parentDirectory.mkdirs();
    }


    return dynamicTest(configurable.getSimpleName(), () -> {
      final File graphOutputFile = new File(parentDirectory, configurable.getDiagramFileName());

      final Graph g;
      if (configurable instanceof SourceConnectorData) {
        g = graph()
            .graphAttr().with(RankDir.LEFT_TO_RIGHT)
            .directed()
            .with(
                node(configurable.getSimpleName()).with(Shape.RECTANGLE)
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
                        to(node("Kafka Connect").with(Shape.RECTANGLE).link(to(node(configurable.getSimpleName()).with(Shape.RECTANGLE)).with(Label.of("Writes data to")))).with(Label.of("Pulls Data from"))

                    )
            );
      }


      Graphviz.fromGraph(g)
          .width(350)
          .render(Format.SVG_STANDALONE)
          .toFile(graphOutputFile);

      final File outputFile = new File(parentDirectory, configurable.getSimpleName() + ".rst");

      Template template = configuration.getTemplate(templateName);
      log.info("Writing {}", outputFile);
      try (Writer writer = Files.newWriter(outputFile, Charsets.UTF_8)) {
        process(writer, template, configurable);
      }

      for (Example example : configurable.getExamples()) {
        example.writeJsonExample(parentDirectory);
        example.writePropertiesExample(parentDirectory);
      }

    });
  }

  DynamicTest transformRstTest(TransformationData transformationTemplate, final String templateName, final File parentDirectory) {
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
  public Stream<DynamicTest> rstSources() {
    final File parentDirectory = new File(outputDirectory, "sources");
    final String templateName = "rst/source.rst.ftl";
    return this.pluginData.getSourceConnectors().stream().map(connectorTemplate -> connectorRstTest(connectorTemplate, templateName, parentDirectory));
  }

  @TestFactory
  public Stream<DynamicTest> rstSinks() {
    final File parentDirectory = new File(outputDirectory, "sinks");
    final String templateName = "rst/sink.rst.ftl";
    return this.pluginData.getSinkConnectors().stream().map(connectorTemplate -> connectorRstTest(connectorTemplate, templateName, parentDirectory));
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
  public Stream<DynamicTest> validateExamples() {
    List<Example> examples = new ArrayList<>();
    this.pluginData.all().stream().forEach(p -> examples.addAll(p.getExamples()));

    return examples.stream()
        .map(e -> dynamicTest(String.format("%s/%s", e.className().getSimpleName(), e.resourceFile()), () -> {
          Object instance = e.className().newInstance();
          final ConfigDef configDef;
          if (instance instanceof SourceConnector) {
            configDef = ((SourceConnector) instance).config();
          } else if (instance instanceof SinkConnector) {
            configDef = ((SinkConnector) instance).config();
          } else if (instance instanceof Transformation) {
            configDef = ((Transformation) instance).config();
          } else {
            throw new UnsupportedOperationException();
          }

          int errorCount = 0;
          List<ConfigValue> values = configDef.validate(e.getConfig());
          for (ConfigValue value : values) {
            errorCount += value.errorMessages().size();
          }

          assertEquals(0, errorCount, () -> {
            StringBuilder builder = new StringBuilder();
            builder.append("Example validation was not successful.");
            builder.append('\n');

            for (ConfigValue value : values) {
              for (String s : value.errorMessages()) {
                builder.append(value.name());
                builder.append(": ");
                builder.append(s);
                builder.append('\n');
              }
            }
            return builder.toString();
          });
        }));
  }

  @TestFactory
  public Stream<DynamicTest> rstTransformations() throws IOException, TemplateException {
    if (!this.pluginData.getTransformations().isEmpty()) {
      final File outputFile = new File(outputDirectory, "transformations.rst");
      if (!this.pluginData.getSinkConnectors().isEmpty() ||
          !this.pluginData.getSourceConnectors().isEmpty()) {
        Template template = configuration.getTemplate("rst/transformations.rst.ftl");
        try (Writer writer = Files.newWriter(outputFile, Charsets.UTF_8)) {
          process(writer, template, this.pluginData);
        }
      }
    }

    final File parentDirectory = new File(outputDirectory, "transformations");
    final String templateName = "rst/transformation.rst.ftl";
    return this.pluginData.getTransformations().stream().map(connectorTemplate -> transformRstTest(connectorTemplate, templateName, parentDirectory));
  }

  @TestFactory
  public Stream<DynamicTest> rstSchemas() throws IOException, TemplateException {
    final File parentDirectory = new File(outputDirectory, "schemas");
    final List<Schema> schemas = schemas();

    if (!schemas.isEmpty()) {
      if (!parentDirectory.exists()) {
        parentDirectory.mkdirs();
      }

      final File outputFile = new File(outputDirectory, "schemas.rst");
      final String templateName = "rst/schemas.rst.ftl";

      if (!this.pluginData.getSinkConnectors().isEmpty() ||
          !this.pluginData.getSourceConnectors().isEmpty()) {
        Template template = configuration.getTemplate(templateName);
        try (Writer writer = Files.newWriter(outputFile, Charsets.UTF_8)) {
          process(writer, template, this.pluginData);
        }
      }
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
                "input", SchemaData.of(schema),
                "helper", new RstTemplateHelper()
            );
            template.process(variables, writer);
          }
        }));
  }

  @Test
  public void rstConnectors() throws IOException, TemplateException {
    final File outputFile = new File(outputDirectory, "connectors.rst");
    final String templateName = "rst/connectors.rst.ftl";

    if (!this.pluginData.getSinkConnectors().isEmpty() ||
        !this.pluginData.getSourceConnectors().isEmpty()) {
      Template template = configuration.getTemplate(templateName);
      try (Writer writer = Files.newWriter(outputFile, Charsets.UTF_8)) {
        process(writer, template, this.pluginData);
      }
    }
  }

  @Test
  public void readmeRST() throws IOException, TemplateException {
    final File outputFile = new File("target", "README.rst");
    Template template = configuration.getTemplate("rst/README.rst.ftl");
    log.info("Writing {}", outputFile);
    try (Writer writer = Files.newWriter(outputFile, Charsets.UTF_8)) {
      process(writer, template, this.pluginData);
    }

  }

  @Test
  public void readmeMD() throws IOException, TemplateException {
    final File outputFile = new File("target", "README.md");
    Template template = configuration.getTemplate("md/README.md.ftl");
    final String output;
    try (StringWriter writer = new StringWriter()) {
      writer.write('\n');
      process(writer, template, this.pluginData);
      output = writer.toString();
    }
    log.info("\n{}", output);
    Files.write(output, outputFile, Charsets.UTF_8);

  }
}
