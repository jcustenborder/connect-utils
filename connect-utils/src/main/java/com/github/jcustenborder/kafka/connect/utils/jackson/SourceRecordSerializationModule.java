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
import org.apache.kafka.connect.source.SourceRecord;

import java.io.IOException;
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
          timestamp
      );
    }


  }

  static class Serializer extends JsonSerializer<SourceRecord> {

    @Override
    public void serialize(SourceRecord sourceRecord, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
      Storage storage = new Storage();
      storage.sourcePartition = sourceRecord.sourcePartition();
      storage.sourceOffset = sourceRecord.sourceOffset();
      storage.topic = sourceRecord.topic();
      storage.kafkaPartition = sourceRecord.kafkaPartition();
      storage.keySchema = sourceRecord.keySchema();
      storage.key = sourceRecord.key();
      storage.valueSchema = sourceRecord.valueSchema();
      storage.value = sourceRecord.value();
      storage.timestamp = sourceRecord.timestamp();
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
