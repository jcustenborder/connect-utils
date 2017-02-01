package com.github.jcustenborder.kafka.connect.utils.jackson;

import com.google.common.collect.ImmutableMap;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.github.jcustenborder.kafka.connect.utils.AssertConnectRecord.assertSourceRecord;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SourceRecordSerializationModuleTest {
  private static final Logger log = LoggerFactory.getLogger(SourceRecordSerializationModuleTest.class);

  @Test
  public void roundtrip() throws IOException {
    final Schema expectedKeySchema = SchemaBuilder.struct()
        .name("key")
        .field("id", Schema.INT64_SCHEMA)
        .build();
    final Struct expectedKey = new Struct(expectedKeySchema)
        .put("id", 1234L);
    final Schema expectedValueSchema = SchemaBuilder.struct()
        .name("value")
        .field("firstName", Schema.STRING_SCHEMA)
        .field("lastName", Schema.STRING_SCHEMA)
        .build();
    final Struct expectedValue = new Struct(expectedValueSchema)
        .put("firstName", "foo")
        .put("lastName", "bar");
    final SourceRecord expected = new SourceRecord(
        ImmutableMap.of(),
        ImmutableMap.of(),
        "test",
        1,
        expectedKeySchema,
        expectedKey,
        expectedValueSchema,
        expectedValue,
        1485910473123L
    );

    final String temp = ObjectMapperFactory.INSTANCE.writeValueAsString(expected);
    log.trace(temp);
    final SourceRecord actual = ObjectMapperFactory.INSTANCE.readValue(temp, SourceRecord.class);
    assertSourceRecord(expected, actual);
  }

}
