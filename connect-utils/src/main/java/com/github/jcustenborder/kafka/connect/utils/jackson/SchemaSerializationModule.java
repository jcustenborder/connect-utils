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
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class SchemaSerializationModule extends SimpleModule {

  public SchemaSerializationModule() {
    super();
    addSerializer(Schema.class, new Serializer());
    addDeserializer(Schema.class, new Deserializer());
  }

  public static class Storage {
    String name;
    String doc;
    Schema.Type type;
    Object defaultValue;
    Integer version;
    Map<String, String> parameters;
    boolean isOptional;
    Schema keySchema;
    Schema valueSchema;
    Map<String, Schema> fieldSchemas;

    Storage(Schema schema) {
      this.name = schema.name();
      this.doc = schema.doc();
      this.type = schema.type();
      this.defaultValue = schema.defaultValue();
      this.version = schema.version();
      this.parameters = schema.parameters();
      this.isOptional = schema.isOptional();

      if (Schema.Type.MAP == this.type) {
        this.keySchema = schema.keySchema();
        this.valueSchema = schema.valueSchema();
      } else if (Schema.Type.ARRAY == this.type) {
        this.keySchema = null;
        this.valueSchema = schema.valueSchema();
      } else if (Schema.Type.STRUCT == this.type) {
        this.fieldSchemas = new LinkedHashMap<>();
        for (Field field : schema.fields()) {
          this.fieldSchemas.put(field.name(), field.schema());
        }
      }
    }

    public Schema build() {
      SchemaBuilder builder;

      switch (this.type) {
        case MAP:
          Preconditions.checkNotNull(this.keySchema, "keySchema cannot be null.");
          Preconditions.checkNotNull(this.valueSchema, "valueSchema cannot be null.");
          builder = SchemaBuilder.map(this.keySchema, this.valueSchema);
          break;
        case ARRAY:
          Preconditions.checkNotNull(this.valueSchema, "valueSchema cannot be null.");
          builder = SchemaBuilder.array(this.valueSchema);
          break;
        default:
          builder = SchemaBuilder.type(this.type);
          break;
      }

      if (Schema.Type.STRUCT == this.type) {
        for (Map.Entry<String, Schema> kvp : this.fieldSchemas.entrySet()) {
          builder.field(kvp.getKey(), kvp.getValue());
        }
      }

      if (!Strings.isNullOrEmpty(this.name)) {
        builder.name(this.name);
      }

      if (!Strings.isNullOrEmpty(this.doc)) {
        builder.doc(this.doc);
      }

      if (null != this.defaultValue) {
        builder.defaultValue(this.defaultValue);
      }

      if (null != this.parameters) {
        builder.parameters(this.parameters);
      }

      if (this.isOptional) {
        builder.optional();
      }

      if (null != this.version) {
        builder.version(this.version);
      }

      return builder.build();
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this.getClass())
          .add("type", this.type)
          .add("name", this.name)
          .add("isOptional", this.isOptional)
          .add("version", this.version)
          .add("keySchema", this.keySchema)
          .add("valueSchema", this.valueSchema)
          .add("parameters", this.parameters)
          .add("doc", this.doc)
          .add("defaultValue", this.defaultValue)
          .omitNullValues()
          .toString();
    }
  }

  static class Serializer extends JsonSerializer<Schema> {
    @Override
    public void serialize(Schema schema, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
      Storage storage = new Storage(schema);
      jsonGenerator.writeObject(storage);
    }
  }

  static class Deserializer extends JsonDeserializer<Schema> {
    @Override
    public Schema deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
      Storage storage = jsonParser.readValueAs(Storage.class);
      return storage.build();
    }
  }
}
