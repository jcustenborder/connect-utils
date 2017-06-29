/**
 * Copyright © 2016 Jeremy Custenborder (jcustenborder@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jcustenborder.kafka.connect.utils.data;

import com.google.common.base.MoreObjects;
import org.apache.kafka.connect.data.Schema;

public class SchemaUtils {
  public static String toString(Schema schema) {
    return MoreObjects.toStringHelper(schema)
        .add("type", schema.type())
        .add("name", schema.name())
        .add("isOptional", schema.isOptional())
        .add("version", schema.version())
        .omitNullValues()
        .toString();
  }
}
