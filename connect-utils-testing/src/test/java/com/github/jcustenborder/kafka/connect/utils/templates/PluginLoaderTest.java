package com.github.jcustenborder.kafka.connect.utils.templates;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.jcustenborder.kafka.connect.utils.TestKeyAndValueTransformation;
import com.github.jcustenborder.kafka.connect.utils.TestSinkConnector;
import com.github.jcustenborder.kafka.connect.utils.TestSourceConnector;
import com.github.jcustenborder.kafka.connect.utils.TestTransformation;
import com.github.jcustenborder.kafka.connect.utils.ToUpperCase;
import com.github.jcustenborder.kafka.connect.utils.jackson.ObjectMapperFactory;
import com.github.jcustenborder.kafka.connect.utils.nodoc.NoDocTestSinkConnector;
import com.github.jcustenborder.kafka.connect.utils.nodoc.NoDocTestSourceConnector;
import com.github.jcustenborder.kafka.connect.utils.nodoc.NoDocTestTransformation;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.sink.SinkConnector;
import org.apache.kafka.connect.source.SourceConnector;
import org.apache.kafka.connect.transforms.Transformation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static com.github.jcustenborder.kafka.connect.utils.SinkRecordHelper.write;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PluginLoaderTest {
  private static final Logger log = LoggerFactory.getLogger(PluginLoaderTest.class);
  PluginLoader pluginLoader = new PluginLoader(TestTransformation.class.getPackage());

  @Test
  public void findTransformations() {
    final Set<Class<? extends Transformation>> expected = ImmutableSet.of(
        NoDocTestTransformation.class,
        TestTransformation.class,
        ToUpperCase.class,
        TestKeyAndValueTransformation.class
    );
    final Set<Class<? extends Transformation>> actual = pluginLoader.findTransformations();

    assertEquals(expected, actual);
  }

  @Test
  public void findSinkConnectors() {
    final Set<Class<? extends SinkConnector>> expected = ImmutableSet.of(
        TestSinkConnector.class,
        NoDocTestSinkConnector.class
    );
    final Set<Class<? extends SinkConnector>> actual = pluginLoader.findSinkConnectors();

    assertEquals(expected, actual);
  }

  @Test
  public void findSourceConnectors() {
    final Set<Class<? extends SourceConnector>> expected = ImmutableSet.of(
        TestSourceConnector.class,
        NoDocTestSourceConnector.class
    );
    final Set<Class<? extends SourceConnector>> actual = pluginLoader.findSourceConnectors();

    assertEquals(expected, actual);
  }

  @Test
  public void load() {
    Plugin plugin = pluginLoader.load();
  }

  @BeforeAll
  public static void beforeAll() {
    ObjectMapperFactory.INSTANCE.configure(SerializationFeature.INDENT_OUTPUT, true);
  }

  @Test
  public void transformationExample() throws IOException {
    ImmutableTransformationExample.Builder builder = ImmutableTransformationExample.builder();
    builder.name("Simple");
    builder.description("This example takes the key of the message and uppercases it.");
    builder.config(
        ImmutableMap.of("foo", "bar")
    );
    builder.input(
        write(
            "topic",
            new SchemaAndValue(Schema.STRING_SCHEMA, "five"),
            new SchemaAndValue(Schema.STRING_SCHEMA, "5")
        )
    );
    builder.childClass("Key");
    ObjectMapperFactory.INSTANCE.writeValue(
        new File("src/test/resources/com/github/jcustenborder/kafka/connect/utils/ToUpperCase/test001.json"),
        builder.build()
    );

//
    log.info(ObjectMapperFactory.INSTANCE.writeValueAsString(builder.build()));
  }

  @Test
  public void sinkConnectorExample() throws IOException {
    File inputFile = new File("/Users/jeremy/source/opensource/kafka-connect/connect-utils/connect-utils-testing/src/test/resources/com/github/jcustenborder/kafka/connect/utils/TestSinkConnector/transforms.json");
    File outputFile = new File("/Users/jeremy/source/opensource/kafka-connect/connect-utils/connect-utils-testing/src/test/resources/com/github/jcustenborder/kafka/connect/utils/TestSinkConnector/transforms2.json");
    Plugin.SinkConnectorExample input = ObjectMapperFactory.INSTANCE.readValue(inputFile, Plugin.SinkConnectorExample.class);

    Schema keySchema = SchemaBuilder.struct().field("id", Schema.INT64_SCHEMA).build();
    Struct key = new Struct(keySchema).put("id", 12342L);
    Schema valueSchema = SchemaBuilder.struct()
        .field("id", Schema.INT64_SCHEMA)
        .field("firstName", Schema.STRING_SCHEMA)
        .field("lastName", Schema.STRING_SCHEMA)
        .build();
    Struct value = new Struct(valueSchema)
        .put("id", 12342L)
        .put("firstName", "example")
        .put("lastName", "user");

    ImmutableSinkConnectorExample.Builder builder = ImmutableSinkConnectorExample.builder()
        .from(input)
        .input(
            write("soe-foo",
                key,
                value
            )
        ).output(new String[]{"INSERT INTO foo (id, firstName, lastName) values (12342, 'example', 'user');"});

    ObjectMapperFactory.INSTANCE.writeValue(outputFile, builder.build());
  }
}
