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
package com.github.jcustenborder.kafka.connect.utils.data.type;


import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.Schema;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DecimalTypeParserTests {

  @Test
  public void testSchemaCache() {
    List<Schema> schemas = new ArrayList<>();
    for (int i = 1; i < 30; i++) {
      schemas.add(Decimal.schema(i));
    }

    DecimalTypeParser typeConverter = new DecimalTypeParser();

    for (int i = 0; i < 1000; i++) {
      for (Schema schema : schemas) {
        Object value = typeConverter.parseString("0", schema);
      }
    }

    assertEquals(schemas.size(), typeConverter.schemaCache.size());
  }

}
