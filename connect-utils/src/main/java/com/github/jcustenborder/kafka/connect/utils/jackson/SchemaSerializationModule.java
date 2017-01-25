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

import java.io.IOException;

public class SchemaSerializationModule extends SimpleModule {

  public SchemaSerializationModule() {
    super();
    addSerializer(Schema.class, new Serializer());
    addDeserializer(Schema.class, new Deserializer());
  }

  public static class Storage {

  }

  static class Serializer extends JsonSerializer<Schema> {
    @Override
    public void serialize(Schema schema, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {

    }
  }

  static class Deserializer extends JsonDeserializer<Schema> {

    @Override
    public Schema deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
      return null;
    }
  }
}
