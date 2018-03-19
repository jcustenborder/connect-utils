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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Timestamp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SchemaHelper {

  static final Map<Class<?>, Schema.Type> PRIMITIVES;

  static {
    Map<Class<?>, Schema.Type> primitives = new HashMap<>();
    primitives.put(String.class, Schema.Type.STRING);
    primitives.put(Boolean.class, Schema.Type.BOOLEAN);
    primitives.put(Byte.class, Schema.Type.INT8);
    primitives.put(Short.class, Schema.Type.INT16);
    primitives.put(Integer.class, Schema.Type.INT32);
    primitives.put(Long.class, Schema.Type.INT64);
    primitives.put(Float.class, Schema.Type.FLOAT32);
    primitives.put(Double.class, Schema.Type.FLOAT64);
    primitives.put(byte[].class, Schema.Type.BYTES);
    PRIMITIVES = ImmutableMap.copyOf(primitives);
  }

  public static Schema schema(Object input) {
    return builder(input).build();
  }

  public static SchemaBuilder builder(Object input) {
    Preconditions.checkNotNull(input, "input cannot be null.");
    final SchemaBuilder builder;

    if (PRIMITIVES.containsKey(input.getClass())) {
      final Schema.Type type = PRIMITIVES.get(input.getClass());
      builder = SchemaBuilder.type(type);
    } else if (input instanceof Date) {
      builder = Timestamp.builder();
    } else if (input instanceof BigDecimal) {
      builder = Decimal.builder(((BigDecimal) input).scale());
    } else {
      throw new UnsupportedOperationException(
          String.format("Unsupported Type: %s", input.getClass())
      );
    }

    return builder.optional();
  }


}
