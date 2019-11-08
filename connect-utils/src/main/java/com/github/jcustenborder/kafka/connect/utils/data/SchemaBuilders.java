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
package com.github.jcustenborder.kafka.connect.utils.data;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public class SchemaBuilders {

  public static SchemaBuilder of(Schema schema, String... excludeFields) {
    return of(schema, Arrays.asList(excludeFields));
  }

  public static SchemaBuilder of(Schema schema, Collection<String> excludeFields) {

    Set<String> exclude = null != excludeFields ? ImmutableSet.copyOf(excludeFields) : ImmutableSet.of();
    SchemaBuilder builder;

    if (Schema.Type.ARRAY == schema.type()) {
      builder = SchemaBuilder.array(schema.valueSchema());
    } else if (Schema.Type.MAP == schema.type()) {
      builder = SchemaBuilder.map(schema.keySchema(), schema.valueSchema());
    } else {
      builder = SchemaBuilder.type(schema.type());
    }

    if (schema.isOptional()) {
      builder.optional();
    }
    if (!Strings.isNullOrEmpty(schema.name())) {
      builder.name(schema.name());
    }
    if (!Strings.isNullOrEmpty(schema.doc())) {
      builder.doc(schema.doc());
    }
    builder.version(schema.version());

    if (null != schema.parameters()) {
      builder.parameters(schema.parameters());
    }

    if (Schema.Type.STRUCT == schema.type()) {
      schema.fields()
          .stream()
          .filter(field -> !exclude.contains(field.name()))
          .forEach(field -> builder.field(field.name(), field.schema()));
    }

    return builder;
  }
}
