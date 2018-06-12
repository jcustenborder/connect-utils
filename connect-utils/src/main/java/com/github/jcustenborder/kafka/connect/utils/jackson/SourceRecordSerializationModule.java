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
package com.github.jcustenborder.kafka.connect.utils.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.header.Header;
import org.apache.kafka.connect.source.SourceRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SourceRecordSerializationModule extends SimpleModule {

  public SourceRecordSerializationModule() {
    super();
    addSerializer(SourceRecord.class, new Serializer());
    addDeserializer(SourceRecord.class, new Deserializer());
  }

  public static class Storage {
    public Map<String, ?> sourcePartition;
    public Map<String, ?> sourceOffset;
    public String topic;
    public Integer kafkaPartition;
    public Schema keySchema;
    public Object key;
    public Schema valueSchema;
    public Object value;
    public Long timestamp;
    public List<Header> headers;

    public Object value() {
      if (this.valueSchema != null) {
        return ValueHelper.value(this.valueSchema, this.value);
      } else {
        return this.value;
      }
    }

    public Object key() {
      if (this.keySchema != null) {
        return ValueHelper.value(this.keySchema, this.key);
      } else {
        return this.key;
      }
    }


    public SourceRecord build() {
      return new SourceRecord(
          sourcePartition,
          sourceOffset,
          topic,
          kafkaPartition,
          keySchema,
          key(),
          valueSchema,
          value(),
          timestamp,
          headers
      );
    }


  }

  static class Serializer extends JsonSerializer<SourceRecord> {

    @Override
    public void serialize(SourceRecord record, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
      Storage storage = new Storage();
      storage.sourcePartition = record.sourcePartition();
      storage.sourceOffset = record.sourceOffset();
      storage.topic = record.topic();
      storage.kafkaPartition = record.kafkaPartition();
      storage.keySchema = record.keySchema();
      storage.key = record.key();
      storage.valueSchema = record.valueSchema();
      storage.value = record.value();
      storage.timestamp = record.timestamp();
      if (null != record.headers()) {
        List<Header> headers = new ArrayList<>();
        for (Header header : record.headers()) {
          headers.add(header);
        }
        storage.headers = headers;
      }
      jsonGenerator.writeObject(storage);
    }
  }

  static class Deserializer extends JsonDeserializer<SourceRecord> {

    @Override
    public SourceRecord deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
      Storage storage = jsonParser.readValueAs(Storage.class);
      return storage.build();
    }
  }
}
