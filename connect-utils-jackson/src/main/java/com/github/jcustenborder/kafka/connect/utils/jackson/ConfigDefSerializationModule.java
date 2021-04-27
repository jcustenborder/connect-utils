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
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.types.Password;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ConfigDefSerializationModule extends SimpleModule {
  private static final Logger log = LoggerFactory.getLogger(ConfigDefSerializationModule.class);

  public ConfigDefSerializationModule() {
    super();
    addSerializer(Password.class, new Serializer());
    addDeserializer(Password.class, new Deserializer());
    addSerializer(ConfigDef.Validator.class, new JsonSerializer<ConfigDef.Validator>() {
      @Override
      public void serialize(ConfigDef.Validator validator, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(validator.toString());
      }
    });
  }

  static class Serializer extends JsonSerializer<Password> {
    @Override
    public void serialize(Password password, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
      jsonGenerator.writeString(password.toString());
    }
  }

  static class Deserializer extends JsonDeserializer<Password> {
    @Override
    public Password deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
      String text = jsonParser.readValueAs(String.class);
      return new Password(text);
    }
  }
}
