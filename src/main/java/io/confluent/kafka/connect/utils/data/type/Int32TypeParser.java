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
package io.confluent.kafka.connect.utils.data.type;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;
import org.apache.kafka.connect.data.Schema;

public class Int32TypeParser implements TypeParser {
  @Override
  public Object parseString(String s, final Schema schema) {
    return Integer.parseInt(s);
  }

  @Override
  public Class<?> expectedClass() {
    return Integer.class;
  }

  @Override
  public Object parseJsonNode(JsonNode input, Schema schema) {
    Preconditions.checkState(input.isInt(), "'%s' is not a '%s'", input.textValue(), expectedClass().getSimpleName());
    return input.intValue();
  }
}
