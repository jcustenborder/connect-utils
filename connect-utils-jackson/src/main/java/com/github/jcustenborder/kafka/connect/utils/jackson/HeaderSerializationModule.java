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
import org.apache.kafka.connect.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HeaderSerializationModule extends SimpleModule {
  private static final Logger log = LoggerFactory.getLogger(HeaderSerializationModule.class);

  public HeaderSerializationModule() {
    super();
    addSerializer(Header.class, new Serializer());
    addDeserializer(Header.class, new Deserializer());
  }

  static class Serializer extends JsonSerializer<Header> {
    @Override
    public void serialize(Header header, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
      KeyValue storage = new KeyValue();
      storage.name = header.key();
      storage.schema = header.schema();
      storage.value(header.value());
      jsonGenerator.writeObject(storage);
    }
  }

  static class Deserializer extends JsonDeserializer<Header> {
    @Override
    public Header deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
      KeyValue storage = jsonParser.readValueAs(KeyValue.class);
      return new HeaderImpl(storage.name, storage.schema, storage.value());
    }
  }
}
