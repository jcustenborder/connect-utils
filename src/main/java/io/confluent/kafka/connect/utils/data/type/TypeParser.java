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
import org.apache.kafka.connect.data.Schema;

public interface TypeParser {
  /**
   * Method is used to parse a String to an object representation of a Kafka Connect Type
   *
   * @param s input string to parseString
   * @return Object representation of the Kafka Connect Type
   */
  Object parseString(String s, Schema schema);

  /**
   * Method is used to return the expected class for the conversion. This is mainly used for
   * error messages when a type cannot be parsed.
   *
   * @return Class the parser will return.
   */
  Class<?> expectedClass();

  Object parseJsonNode(JsonNode input, Schema schema);
}
