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
package com.github.jcustenborder.kafka.connect.utils.templates;

import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TemplateSchema implements Table {
  private final Schema schema;

  public Schema getSchema() {
    return schema;
  }

  private TemplateSchema(Schema schema) {
    this.schema = schema;
  }

  @Override
  public String getTitle() {
    String result;
    if (Strings.isNullOrEmpty(this.schema.name())) {
      result = "";
    } else {
      result = Files.getFileExtension(schema.name());
    }
    return result;
  }

  @Override
  public List<String> getHeaders() {
    return Arrays.asList(
        "Name",
        "Type",
        "Optional",
        "Default Value",
        "Documentation"
    );
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

  @Override
  public List<List<String>> getRowData() {
    List<List<String>> rows = new ArrayList<>();

    if (Schema.Type.STRUCT != this.schema.type()) {
      return rows;
    }

    for (Field field : this.schema.fields()) {
      rows.add(
          ImmutableList.of(
              field.name(),
              type(field.schema()),
              String.format("%s", field.schema().isOptional()),
              null != field.schema().defaultValue() ? field.schema().defaultValue().toString() : "",
              field.schema().doc()
          )
      );
    }


    return rows;
  }

  public static TemplateSchema of(Schema schema) {
    return new TemplateSchema(schema);
  }
}
