/**
 * Copyright © 2016 Jeremy Custenborder (jcustenborder@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jcustenborder.kafka.connect.utils.jackson;

import com.github.jcustenborder.kafka.connect.utils.AssertConnectRecord;
import com.google.common.collect.ImmutableMap;
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
    AssertConnectRecord.assertSourceRecord(expected, actual);
  }

}
