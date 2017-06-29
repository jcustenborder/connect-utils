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
package com.github.jcustenborder.kafka.connect.utils.jackson;

import com.google.common.io.BaseEncoding;
import org.apache.kafka.connect.data.Date;
import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.data.Time;
import org.apache.kafka.connect.data.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

class ValueHelper {
  static final Logger log = LoggerFactory.getLogger(ValueHelper.class);

  static Object int8(Object o) {
    if (o instanceof Long) {
      Number number = (Number) o;
      return number.byteValue();
    }
    return o;
  }

  static Object int16(Object o) {
    if (o instanceof Long) {
      Number number = (Number) o;
      return number.shortValue();
    }
    return o;
  }

  static Object int32(Object o) {
    if (o instanceof Long) {
      Number number = (Number) o;
      return number.intValue();
    }

    return o;
  }

  static Object int64(Object value) {
    if (value instanceof Number) {
      Number number = (Number) value;
      return number.longValue();
    }
    return value;
  }

  static Object float64(Object o) {
    if (o instanceof Number) {
      Number integer = (Number) o;
      return integer.doubleValue();
    }

    return o;
  }

  static Object float32(Object o) {
    if (o instanceof Number) {
      Number integer = (Number) o;
      return integer.floatValue();
    }

    return o;
  }

  static Object bytes(Object value) {
    if (value instanceof String) {
      String s = (String) value;
      return BaseEncoding.base64().decode(s);
    }
    return value;
  }

  static Object decimal(Schema schema, Object value) {
    if (value instanceof byte[]) {
      byte[] bytes = (byte[]) value;
      return Decimal.toLogical(schema, bytes);
    }
    if (value instanceof BigDecimal) {
      BigDecimal decimal = (BigDecimal) value;
      final int scale = Integer.parseInt(schema.parameters().get(Decimal.SCALE_FIELD));
      if (scale == decimal.scale()) {
        return decimal;
      } else {
        return decimal.setScale(scale);
      }
    }

    return value;
  }

  static Object date(Schema schema, Object value) {
    if (value instanceof Number) {
      Number number = (Number) value;
      return Date.toLogical(schema, number.intValue());
    }
    return value;
  }

  static Object time(Schema schema, Object value) {
    if (value instanceof Number) {
      Number number = (Number) value;
      return Time.toLogical(schema, number.intValue());
    }
    return value;
  }

  static Object timestamp(Schema schema, Object value) {
    if (value instanceof Number) {
      Number number = (Number) value;
      return Timestamp.toLogical(schema, number.longValue());
    }
    return value;
  }

  public static Object value(Schema schema, Object value) {
    if (null == value) {
      return null;
    }

    Object result;
    log.trace("schema.type() = {}", schema.type());
    switch (schema.type()) {
      case BYTES:
        if (Decimal.LOGICAL_NAME.equals(schema.name())) {
          result = decimal(schema, value);
        } else {
          result = bytes(value);
        }
        break;
      case INT32:
        if (Date.LOGICAL_NAME.equals(schema.name())) {
          result = date(schema, value);
        } else if (Time.LOGICAL_NAME.equals(schema.name())) {
          result = time(schema, value);
        } else {
          result = int32(value);
        }
        break;
      case INT16:
        result = int16(value);
        break;
      case INT64:
        if (Timestamp.LOGICAL_NAME.equals(schema.name())) {
          result = timestamp(schema, value);
        } else {
          result = int64(value);
        }
        break;
      case INT8:
        result = int8(value);
        break;
      case FLOAT32:
        result = float32(value);
        break;
      case FLOAT64:
        result = float64(value);
        break;
      case STRUCT:
        if (value instanceof Struct) {
          log.trace("Struct");
          result = value;
        } else if (value instanceof Map) {
          log.trace("Map");
          Map<String, Object> map = (Map<String, Object>) value;
          if (map.containsKey("schema") && map.get("fieldValues") instanceof List) {
            log.trace("struct stored as map.");
            Struct struct = ObjectMapperFactory.INSTANCE.convertValue(value, Struct.class);
            result = struct;
          } else {
            log.trace("map");
            Struct struct = new Struct(schema);
            for (Map.Entry<String, Object> kvp : map.entrySet()) {
              log.trace("field {}", kvp.getKey());
              Field field = schema.field(kvp.getKey());
              struct.put(field, kvp.getValue());
            }
            result = struct;
          }
        } else {
          log.trace("not Struct or Map.");
          result = value;
        }
        break;
      default:
        result = value;
        break;
    }

    return result;
  }

//  public Object convert(Object value) {
//    Object result;
//    if (value instanceof java.sql.Date) {
//      java.sql.Date d = (java.sql.Date) value;
//      result = new java.util.Date(d.getTime());
//    } else {
//      result = value;
//    }
//    return result;
//  }
}
