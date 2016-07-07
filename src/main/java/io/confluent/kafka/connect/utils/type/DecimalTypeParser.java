/**
 * Copyright (C) 2016 Jeremy Custenborder (jcustenborder@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.confluent.kafka.connect.utils.type;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.errors.DataException;

import java.math.BigDecimal;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class DecimalTypeParser implements TypeParser {
  final Cache<Schema, Integer> schemaCache;

  public DecimalTypeParser() {
    this.schemaCache = CacheBuilder.newBuilder()
        .expireAfterWrite(60, TimeUnit.SECONDS)
        .build();

  }

  private static int scale(Schema schema) {
    String scaleString = (String) schema.parameters().get("scale");
    if (scaleString == null) {
      throw new DataException("Invalid Decimal schema: scale parameter not found.");
    } else {
      try {
        return Integer.parseInt(scaleString);
      } catch (NumberFormatException var3) {
        throw new DataException("Invalid scale parameter found in Decimal schema: ", var3);
      }
    }
  }

  @Override
  public Object parseString(String s, final Schema schema) {
    int scale;
    try {
      scale = this.schemaCache.get(schema, new Callable<Integer>() {
        @Override
        public Integer call() throws Exception {
          return scale(schema);
        }
      });
    } catch (ExecutionException e) {
      throw new DataException(e);
    }


    return new BigDecimal(s).setScale(scale);
  }
}
