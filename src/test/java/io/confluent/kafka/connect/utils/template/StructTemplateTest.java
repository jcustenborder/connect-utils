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
package io.confluent.kafka.connect.utils.template;

import com.google.common.collect.ImmutableMap;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class StructTemplateTest {

  Struct struct;
  StructTemplate structTemplate;

  @Before
  public void before() {
    this.structTemplate = new StructTemplate();

    Schema schema = SchemaBuilder.struct()
        .name("TestStruct")
        .field("stringField", Schema.STRING_SCHEMA)
        .field("nullStringField", Schema.OPTIONAL_STRING_SCHEMA)
        .build();

    this.struct = new Struct(schema);
    this.struct.put("stringField", "TestValue");
    this.struct.validate();
  }


  @Test(expected = DataException.class)
  public void missingTemplate() {
    this.structTemplate.execute("missingTemplate", ImmutableMap.of("String", "String"));
  }

  @Test
  public void mapValues() {
    Map<String, ?> values = ImmutableMap.of("key", "value");
    this.structTemplate.addTemplate("test", "${key}");
    String actual = this.structTemplate.execute("test", values);
    assertEquals("value", actual);
  }

  @Test
  public void mapMissingValues() {
    Map<String, ?> values = ImmutableMap.of("key", "value");
    this.structTemplate.addTemplate("test", "${bar}");
    String actual = this.structTemplate.execute("test", values);
    assertEquals("", actual);
  }

  @Test
  public void connectRecord() {
    Map<String, ?> values = ImmutableMap.of("key", "value");
    this.structTemplate.addTemplate("test", "${topic}");
    SinkRecord record = new SinkRecord("testing", 1, null, null, this.struct.schema(), this.struct, 123456L);
    String actual = this.structTemplate.execute("test", record, this.struct, values);
    assertEquals("testing", actual);
  }

  @Test
  public void connectRecordMissingValues() {
    Map<String, ?> values = ImmutableMap.of("key", "value");
    this.structTemplate.addTemplate("test", "${foo}");
    SinkRecord record = new SinkRecord("testing", 1, null, null, this.struct.schema(), this.struct, 123456L);
    String actual = this.structTemplate.execute("test", record, this.struct, values);
    assertEquals("", actual);
  }
}
