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
import org.apache.kafka.common.utils.Time;

import java.io.IOException;

public class TimeSerializationModule extends SimpleModule {

  public TimeSerializationModule() {
    super();
    addSerializer(Time.class, new Serializer());
    addDeserializer(Time.class, new Deserializer());
  }

  public static class Storage {
    public long milliseconds;
    public long nanoseconds;

    public Storage() {

    }

    public Storage(Time time) {
      this.milliseconds = time.milliseconds();
      this.nanoseconds = time.nanoseconds();
    }
  }

  static class Serializer extends JsonSerializer<Time> {
    @Override
    public void serialize(Time time, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
      Storage storage = new Storage(time);
      jsonGenerator.writeObject(storage);
    }
  }

  static class Deserializer extends JsonDeserializer<Time> {

    @Override
    public Time deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
      final Storage storage = jsonParser.readValueAs(Storage.class);

      return new Time() {
        @Override
        public long milliseconds() {
          return storage.milliseconds;
        }

        @Override
        public long nanoseconds() {
          return storage.nanoseconds;
        }

        @Override
        public void sleep(long l) {
          try {
            Thread.sleep(l);
          } catch (InterruptedException e) {

          }
        }
      };
    }
  }
}
