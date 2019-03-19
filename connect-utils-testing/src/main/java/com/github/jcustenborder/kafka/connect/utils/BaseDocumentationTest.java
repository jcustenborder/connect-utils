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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import com.github.jcustenborder.kafka.connect.utils.jackson.ObjectMapperFactory;
import com.github.jcustenborder.kafka.connect.utils.templates.ImmutableSinkConnectorExampleInput;
import com.github.jcustenborder.kafka.connect.utils.templates.ImmutableSourceConnectorExampleInput;
import com.github.jcustenborder.kafka.connect.utils.templates.ImmutableTransformationExampleInput;
import com.github.jcustenborder.kafka.connect.utils.templates.Plugin;
import com.github.jcustenborder.kafka.connect.utils.templates.PluginLoader;
import com.google.common.base.CaseFormat;
import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import freemarker.cache.ClassTemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigValue;
import org.apache.kafka.connect.connector.Connector;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.transforms.Transformation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;


public abstract class BaseDocumentationTest {
  private static final Logger log = LoggerFactory.getLogger(BaseDocumentationTest.class);

  protected List<Schema> schemas() {
    return Arrays.asList();
  }

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
        .filter((Predicate<Class<? extends T>>) aClass -> Arrays.stream(aClass.getConstructors())
            .filter(c -> Modifier.isPublic(c.getModifiers()))
            .anyMatch(c -> c.getParameterCount() == 0))
        .sorted(Comparator.comparing(Class::getName))
        .collect(Collectors.toList());
    return classes;
  }

  final File targetDirectory = new File("target");
  final File outputDirectory = new File(this.targetDirectory, "docs");
  final File sourcesDirectory = new File(this.outputDirectory, "sources");
  final File sourcesExamplesDirectory = new File(this.sourcesDirectory, "examples");
  final File sinksDirectory = new File(this.outputDirectory, "sinks");
  final File sinksExamplesDirectory = new File(this.sinksDirectory, "examples");
  final File transformationsDirectory = new File(this.outputDirectory, "transformations");
  final File transformationsExampleDirectory = new File(this.transformationsDirectory, "examples");

  PluginLoader pluginLoader;
  Plugin plugin;

  @BeforeEach
  public void before() throws MalformedURLException {
    ObjectMapperFactory.INSTANCE.configure(SerializationFeature.INDENT_OUTPUT, true);

    Arrays.asList(
        this.targetDirectory,
        this.outputDirectory,
        this.sourcesDirectory,
        this.sourcesExamplesDirectory,
        this.sinksDirectory,
        this.sinksExamplesDirectory,
        this.transformationsDirectory,
        this.transformationsExampleDirectory
    ).stream()
        .filter(f -> !f.isDirectory())
        .forEach(File::mkdirs);


    log.info("before() - {}", this.getClass());
    Package pkg = this.getPackage();

    if (null == this.pluginLoader || null == this.plugin) {
      log.info("before() - Loading plugin");
      this.pluginLoader = new PluginLoader(pkg);
      this.plugin = this.pluginLoader.load();
    }
  }

//  protected List<Map.Entry<String, ConfigDef.ConfigKey>> required(ConfigDef configDef) {
//    List<Map.Entry<String, ConfigDef.ConfigKey>> entries = new ArrayList<>();
//
//    for (Map.Entry<String, ConfigDef.ConfigKey> kvp : configDef.configKeys().entrySet()) {
//      if (!kvp.getValue().hasDefault()) {
//        entries.add(kvp);
//      }
//    }
//
//    return ImmutableList.copyOf(entries);
//  }

//  DynamicTest connectorRstTest(final File outputFile, Configurable configurable, final String templateName, final boolean writeExamples) {
//    final File parentDirectory = outputFile.getParentFile();
//    if (!parentDirectory.isDirectory()) {
//      parentDirectory.mkdirs();
//    }
//
//    return dynamicTest(configurable.getSimpleName(), () -> {
//      write(outputFile, configurable, templateName);
//
//      if (writeExamples) {
//        for (Example example : configurable.getExamples()) {
//          example.writeJsonExample(parentDirectory);
//          example.writePropertiesExample(parentDirectory);
//        }
//      }
//
//    });
//  }

  DynamicTest connectorRstTest(final File outputFile, Plugin.Configurable configurable, final String templateName, final boolean writeExamples) {
    return dynamicTest(configurable.getCls().getSimpleName(), () -> {
      write(outputFile, configurable, templateName);
    });
  }

  private void write(File outputFile, Object input, String templateName) throws IOException, TemplateException {
    Template template = configuration.getTemplate(templateName);
    log.info("Writing {}", outputFile);
    try (Writer writer = Files.newWriter(outputFile, Charsets.UTF_8)) {
      process(writer, template, input);
    }
  }

//  DynamicTest transformRstTest(TransformationData transformationTemplate, final String templateName, final File parentDirectory) {
//    if (!parentDirectory.isDirectory()) {
//      parentDirectory.mkdirs();
//    }
//
//    final String testName = transformationTemplate.getTestName();
//
//    return dynamicTest(testName, () -> {
//      final File outputFile = new File(parentDirectory, testName.toLowerCase() + ".rst");
//
//      for (Example example : transformationTemplate.getExamples()) {
//        example.writeJsonExample(parentDirectory);
//        example.writePropertiesExample(parentDirectory);
//      }
//
//      write(outputFile, transformationTemplate, templateName);
//    });
//  }


  @TestFactory
  public Stream<DynamicTest> rstSources() {
    final String templateName = "rst/source.rst.ftl";

    return this.plugin.getSourceConnectors()
        .stream()
        .map(sc -> connectorRstTest(
            new File(this.sourcesDirectory, sc.getCls().getSimpleName() + ".rst"),
            sc,
            templateName,
            true
            )
        );
  }

  @TestFactory
  public Stream<DynamicTest> rstSinks() {
    final String templateName = "rst/sink.rst.ftl";

    return this.plugin.getSinkConnectors()
        .stream()
        .map(sc -> connectorRstTest(
            outputRST(this.sinksDirectory, sc.getCls()),
            sc,
            templateName,
            true
            )
        );
  }

  void process(Writer writer, Template template, Object input) throws IOException, TemplateException {
    Map<String, Object> variables = ImmutableMap.of(
        "input", input
    );
    template.process(variables, writer);
  }

  void assertConfig(Class<? extends Connector> connectorClass, Map<String, String> config) throws IllegalAccessException, InstantiationException {
    log.info("Creating instance of {}", connectorClass.getName());
    Connector connector = connectorClass.newInstance();
    ConfigDef configDef = connector.config();
    assertNotNull(configDef, "connector.config() should always return a config.");

    int errorCount = 0;
    List<ConfigValue> values = configDef.validate(config);
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
  }

  @TestFactory
  public Stream<DynamicTest> validateSinkConnectorExamples() {
    Map<File, Plugin.SinkConnector> sinkConnectorExamples = examples(this.plugin.getSinkConnectors());


    return sinkConnectorExamples.entrySet().stream()
        .map(e -> dynamicTest(String.format("%s/%s", e.getValue().getCls().getSimpleName(), e.getKey().getName()), () -> {
          final Plugin.SinkConnectorExample example = loadExample(e, Plugin.SinkConnectorExample.class);
          final Plugin.SinkConnector sinkConnector = e.getValue();
          final File rstOutputFile = outputRST(
              this.sinksExamplesDirectory,
              sinkConnector.getCls(),
              e.getKey()
          );
          assertConfig(sinkConnector.getCls(), example.getConfig());
          ImmutableSinkConnectorExampleInput.Builder builder = ImmutableSinkConnectorExampleInput.builder();
          builder.example(example);
          String config = connectorConfig(sinkConnector, example);
          builder.config(config);
          if (null != example.getInput()) {
            builder.inputJson(writeValueAsIndentedString(example.getInput()));
          }
          if (null != example.getOutput()) {
            builder.outputJson(writeValueAsIndentedString(example.getOutput()));
          }
          write(rstOutputFile, builder.build(), "rst/sinkConnectorExample.rst.ftl");
        }));
  }

  private String writeValueAsIndentedString(Object o) throws JsonProcessingException {
    String result = ObjectMapperFactory.INSTANCE.writeValueAsString(o);
    return result.replaceAll("(?m)^", "    ");
  }

  private String connectorConfig(Plugin.Connector connector, Plugin.ConnectorExample example) throws JsonProcessingException {
    ObjectNode config = ObjectMapperFactory.INSTANCE.createObjectNode();
    config.put("connector.class", connector.getCls().getName());
    if (connector instanceof Plugin.SinkConnector) {
      config.put("topic", "<required setting>");
    }
    for (Map.Entry<String, String> e : example.getConfig().entrySet()) {
      config.put(e.getKey(), e.getValue());
    }

    for (Map.Entry<String, Map<String, String>> transform : example.transformations().entrySet()) {
      assertTrue(
          transform.getValue().containsKey("type"),
          String.format("Transform '%s' does not have a type property.", transform.getKey())
      );

      for (Map.Entry<String, String> entry : transform.getValue().entrySet()) {
        String key = String.format("transforms.%s.%s", transform.getKey(), entry.getKey());
        config.put(key, entry.getValue());
      }
    }

    return writeValueAsIndentedString(config);
  }

  @TestFactory
  public Stream<DynamicTest> validateSourceConnectorExamples() {
    Map<File, Plugin.SourceConnector> sourceConnectorExamples = examples(this.plugin.getSourceConnectors());

    return sourceConnectorExamples.entrySet().stream()
        .map(e -> dynamicTest(String.format("%s/%s", e.getValue().getCls().getSimpleName(), e.getKey().getName()), () -> {
          final Plugin.SourceConnectorExample example = loadExample(e, Plugin.SourceConnectorExample.class);
          final Plugin.SourceConnector sourceConnector = e.getValue();
          final File rstOutputFile = outputRST(
              this.sourcesExamplesDirectory,
              sourceConnector.getCls(),
              e.getKey()
          );
          assertConfig(sourceConnector.getCls(), example.getConfig());
          ImmutableSourceConnectorExampleInput.Builder builder = ImmutableSourceConnectorExampleInput.builder();
          builder.example(example);
          String config = connectorConfig(sourceConnector, example);
          builder.config(config);

          if (null != example.getOutput()) {
            builder.outputJson(ObjectMapperFactory.INSTANCE.writeValueAsString(example.getOutput()));
          }

          write(rstOutputFile, builder.build(), "rst/sourceConnectorExample.rst.ftl");
        }));
  }

  <T extends Plugin.Configurable> Map<File, T> examples(List<T> input) {
    Map<File, T> result = new LinkedHashMap<>();
    for (T configurable : input) {
      for (String example : configurable.getExamples()) {
        result.put(new File(example), configurable);
      }
    }
    return result;
  }


  @TestFactory
  public Stream<DynamicTest> validateTransformationExamples() {
    Map<File, Plugin.Transformation> transformationExamples = examples(this.plugin.getTransformations());

    return transformationExamples.entrySet().stream()
        .map(e -> dynamicTest(String.format("%s/%s", e.getValue().getCls().getSimpleName(), e.getKey().getName()), () -> {
          final Plugin.Transformation transformation = e.getValue();
          final File rstOutputFile = outputRST(
              this.transformationsExampleDirectory,
              transformation.getCls(),
              e.getKey()
          );
          final Plugin.TransformationExample example = loadExample(e, Plugin.TransformationExample.class);

          final Class<? extends Transformation> transformationClass;

          if (Strings.isNullOrEmpty(example.getChildClass())) {
            transformationClass = transformation.getCls();
          } else {
            Optional<Class> childClass = Arrays.stream(transformation.getCls().getClasses())
                .filter(c -> example.getChildClass().equals(c.getSimpleName()))
                .findFirst();
            assertTrue(
                childClass.isPresent(),
                String.format(
                    "Could not find child '%s' of class '%s'",
                    example.getChildClass(),
                    transformation.getCls().getName()
                )
            );
            transformationClass = childClass.get();
          }

          Transformation<SinkRecord> transform = transformationClass.newInstance();
          transform.configure(example.getConfig());

          ImmutableTransformationExampleInput.Builder builder = ImmutableTransformationExampleInput.builder();
          builder.example(example);

          if (null != example.getConfig() && !example.getConfig().isEmpty()) {
            String transformKey = CaseFormat.UPPER_CAMEL.to(
                CaseFormat.LOWER_CAMEL,
                transformation.getCls().getSimpleName()
            );
            String transformPrefix = "transforms." + transformKey + ".";
            LinkedHashMap<String, String> config = new LinkedHashMap<>();
            config.put("transforms", transformKey);
            config.put(transformPrefix + "type", transformationClass.getName());

            for (Map.Entry<String, String> a : example.getConfig().entrySet()) {
              config.put(transformPrefix + a.getKey(), a.getValue());
            }

            String configJson = writeValueAsIndentedString(config);
            builder.config(configJson);
          }

          if (null != example.getInput()) {
            String inputJson = writeValueAsIndentedString(example.getInput());
            builder.inputJson(inputJson);

            SinkRecord output = transform.apply(example.getInput());
            if (null != output) {
              String outputJson = writeValueAsIndentedString(output);
              builder.outputJson(outputJson);

              final List<String> inputLines = Arrays.asList(inputJson.split("\\r?\\n"));
              final List<String> outputLines = Arrays.asList(outputJson.split("\\r?\\n"));


              Patch<String> patch = DiffUtils.diff(inputLines, outputLines);
              for (AbstractDelta<String> delta : patch.getDeltas()) {
                log.trace("delta: start={} end={}", delta.getTarget().getPosition(), delta.getTarget().last());
                final int lineStart = delta.getTarget().getPosition() + 1;
                final int lineEnd = lineStart + delta.getTarget().size() - 1;

                for (int i = lineStart; i <= lineEnd; i++) {
                  builder.addOutputEmphasizeLines(i);
                }
              }
            }
          }

          try (Writer writer = Files.newWriter(rstOutputFile, Charsets.UTF_8)) {
            Template template = configuration.getTemplate("rst/transformationExample.rst.ftl");
            process(writer, template, builder.build());
          }
        }));
  }

  private <T> T loadExample(Map.Entry<File, ?> e, Class<T> cls) throws IOException {
    log.info("loadExample() - file = '{}'", e.getKey().getAbsolutePath());
    try (InputStream inputStream = this.getClass().getResourceAsStream(e.getKey().getAbsolutePath())) {
      return ObjectMapperFactory.INSTANCE.readValue(inputStream, cls);
    }
  }


  private File outputRST(File parentDirectory, Class<?> cls) {
    return new File(parentDirectory, cls.getSimpleName() + ".rst");
  }

  private File outputRST(File parentDirectory, Class<?> cls, File exampleFile) {
    return new File(
        parentDirectory,
        String.format(
            "%s.%s.rst",
            cls.getSimpleName(),
            Files.getNameWithoutExtension(exampleFile.getName())
        )
    );
  }


  @TestFactory
  public Stream<DynamicTest> rstTransformations() throws IOException, TemplateException {
    if (!this.plugin.getTransformations().isEmpty()) {
      final File outputFile = new File(outputDirectory, "transformations.rst");

      Template template = configuration.getTemplate("rst/transformations.rst.ftl");
      try (Writer writer = Files.newWriter(outputFile, Charsets.UTF_8)) {
        process(writer, template, this.plugin);
      }
    }

    final String templateName = "rst/transformation.rst.ftl";

    return this.plugin.getTransformations()
        .stream()
        .map(sc -> connectorRstTest(
            outputRST(this.transformationsDirectory, sc.getCls()),
            sc,
            templateName,
            true
            )
        );
  }

//  @Disabled
//  @TestFactory
//  public Stream<DynamicTest> rstSchemas() throws IOException, TemplateException {
//    final File parentDirectory = new File(outputDirectory, "schemas");
//    final List<Schema> schemas = schemas();
//
//    if (!schemas.isEmpty()) {
//      if (!parentDirectory.exists()) {
//        parentDirectory.mkdirs();
//      }
//
//      final File outputFile = new File(outputDirectory, "schemas.rst");
//      final String templateName = "rst/schemas.rst.ftl";
//
//      if (!this.pluginData.getSinkConnectors().isEmpty() ||
//          !this.pluginData.getSourceConnectors().isEmpty()) {
//        Template template = configuration.getTemplate(templateName);
//        try (Writer writer = Files.newWriter(outputFile, Charsets.UTF_8)) {
//          process(writer, template, this.pluginData);
//        }
//      }
//    }
//
//    final String templateName = "rst/schema.rst.ftl";
//    return this.schemas().stream()
//        .filter(schema -> !Strings.isNullOrEmpty(schema.name()))
//
//        .map(schema -> dynamicTest(String.format("%s.%s", schema.type(), schema.name()), () -> {
//          StringBuilder filenameBuilder = new StringBuilder()
//              .append(schema.type().toString().toLowerCase());
//          if (!Strings.isNullOrEmpty(schema.name())) {
//            filenameBuilder.append('.').append(schema.name());
//          }
//          filenameBuilder.append(".rst");
//          File outputFile = new File(parentDirectory, filenameBuilder.toString());
//          Template template = configuration.getTemplate(templateName);
//          log.info("Writing {}", outputFile);
//
//
//          try (Writer writer = Files.newWriter(outputFile, Charsets.UTF_8)) {
//            Map<String, Object> variables = ImmutableMap.of(
//                "input", SchemaData.of(schema),
//                "helper", new RstTemplateHelper()
//            );
//            template.process(variables, writer);
//          }
//        }));
//  }

  @Test
  public void rstIndex() throws IOException, TemplateException {
    final File outputFile = new File(this.outputDirectory, "index.rst");
    final String templateName = "rst/index.rst.ftl";

    Template template = configuration.getTemplate(templateName);
    try (Writer writer = Files.newWriter(outputFile, Charsets.UTF_8)) {
      process(writer, template, this.plugin);
    }
  }

  @Test
  public void rstConnectors() throws IOException, TemplateException {
    final File outputFile = new File(outputDirectory, "connectors.rst");
    final String templateName = "rst/connectors.rst.ftl";

    if (!this.plugin.getSinkConnectors().isEmpty() ||
        !this.plugin.getSourceConnectors().isEmpty()) {
      Template template = configuration.getTemplate(templateName);
      try (Writer writer = Files.newWriter(outputFile, Charsets.UTF_8)) {
        process(writer, template, this.plugin);
      }
    }
  }

  @Test
  public void readmeMD() throws IOException, TemplateException {
    final File outputFile = new File("target", "README.md");
    Template template = configuration.getTemplate("md/README.md.ftl");
    try (Writer writer = Files.newWriter(outputFile, Charsets.UTF_8)) {
      process(writer, template, this.plugin);
    }
  }
}
