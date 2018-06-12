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
package com.github.jcustenborder.kafka.connect.utils.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.common.base.Preconditions;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class KeyValue {
  private static final Logger log = LoggerFactory.getLogger(KeyValue.class);
  String name;
  Schema schema;
  private Object storage;
  private Struct struct;

  public void value(Object value) {
    log.trace("value(Object) - name = '{}', schema.type() = {}", this.name, this.schema.type());
    if (value != null) {
      switch (this.schema.type()) {
        case STRUCT:
          Preconditions.checkState(value instanceof Struct, "value must be a struct.");
          this.struct = (Struct) value;
          break;
        default:
          this.storage = value;
          break;
      }
    }
  }

  public Object value() {
    Object result;
    log.trace("value() - name = '{}'", this.name);
    switch (this.schema.type()) {
      case MAP:
        Preconditions.checkState(this.storage == null || this.storage instanceof Map, "storage should be a Map.");
        if (null != this.storage) {
          Map<Object, Object> input = (Map<Object, Object>) this.storage;
          log.trace("value() - converting storage map of {} value(s) to Map<{}, {}>", input.size(), this.schema.keySchema(), this.schema.valueSchema());
          Map<Object, Object> map = new LinkedHashMap<>(input.size());
          for (Map.Entry<Object, Object> kvp : input.entrySet()) {
            log.trace("value() - converting key.");
            Object key = ValueHelper.value(this.schema.keySchema(), kvp.getKey());
            log.trace("value() - converting value.");
            Object value = ValueHelper.value(this.schema.valueSchema(), kvp.getValue());
            map.put(key, value);
          }
          result = map;
        } else {
          result = null;
        }
        break;
      case ARRAY:
        Preconditions.checkState(this.storage == null || this.storage instanceof List, "storage should be a List.");
        if (null != storage) {
          List<Object> input = (List<Object>) this.storage;
          log.trace("value() - converting storage map of {} value(s) to List<{}>", input.size(), this.schema.valueSchema());
          List<Object> list = new ArrayList<>(input.size());
          for (Object o : input) {
            log.trace("value() - converting key.");
            Object value = ValueHelper.value(this.schema.valueSchema(), o);
            list.add(value);
          }
          result = list;
        } else {
          result = null;
        }
        break;
      case STRUCT:
        result = this.struct;
        break;
      default:
        result = ValueHelper.value(this.schema, this.storage);
        break;
    }

    return result;
  }
}
