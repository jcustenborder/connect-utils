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
package com.github.jcustenborder.kafka.connect.utils.templates.model;

import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;

import java.util.ArrayList;
import java.util.List;

public class SchemaData {

  final String name;
  final String shortName;
  final String doc;
  final Schema.Type type;
  final List<FieldData> fields;
  final String displayType;
  final boolean required;
  final Object defaultValue;

  private SchemaData(Schema schema) {
    this.name = schema.name();

    if(Strings.isNullOrEmpty(this.name)) {
      this.shortName = this.name;
    } else {
      int i = this.name.lastIndexOf(".");
      if (i > 0) {
        this.shortName = this.name.substring(i + 1);
      } else {
        this.shortName = this.name;
      }
    }


    this.doc = Strings.isNullOrEmpty(schema.doc()) ? "" : schema.doc();
    this.type = schema.type();
    this.required = !schema.isOptional();
    this.defaultValue = schema.defaultValue();
    List<FieldData> fields = new ArrayList<>();

    if (Schema.Type.STRUCT == this.type) {
      for (Field field : schema.fields()) {
        fields.add(new FieldData(field.name(), field.schema()));
      }
    }
    this.fields = ImmutableList.copyOf(fields);
    this.displayType = type(schema);
  }

  public static SchemaData of(Schema schema) {
    return new SchemaData(schema);
  }

  public String getName() {
    return name;
  }

  public String getShortName() {
    return shortName;
  }

  public String getDoc() {
    return doc;
  }

  public Schema.Type getType() {
    return type;
  }

  public List<FieldData> getFields() {
    return fields;
  }

  public String getDisplayType() {
    return displayType;
  }

  public boolean getRequired() {
    return required;
  }

  public Object getDefaultValue() {
    return defaultValue;
  }

  String type(Schema schema) {
    String result;
    switch (schema.type()) {
      case ARRAY:
        result = String.format("Array of %s", type(schema.valueSchema()));
        break;
      case MAP:
        result = String.format("Map of %s, %s", type(schema.keySchema()), type(schema.valueSchema()));
        break;
      default:
        result = String.format(":ref:`schema-%s`", CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, schema.type().toString()));
        break;
    }
    return result;
  }

  public static class FieldData {
    public final String name;
    public final SchemaData schema;

    public FieldData(String name, Schema schema) {
      this.name = name;
      this.schema = SchemaData.of(schema);
    }

    public String getName() {
      return this.name;
    }

    public SchemaData getSchema() {
      return this.schema;
    }


  }
}
