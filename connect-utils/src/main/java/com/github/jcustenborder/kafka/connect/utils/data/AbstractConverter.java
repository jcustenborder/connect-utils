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

import com.google.common.base.Preconditions;
import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.data.Timestamp;
import org.apache.kafka.connect.errors.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public abstract class AbstractConverter<T> {
  private static final Logger log = LoggerFactory.getLogger(AbstractConverter.class);

  protected abstract T newValue();

  protected abstract void setStringField(final T result, final String fieldName, final String value);

  protected abstract void setFloat32Field(final T result, final String fieldName, final Float value);

  protected abstract void setFloat64Field(final T result, final String fieldName, final Double value);

  protected abstract void setTimestampField(final T result, final String fieldName, final Date value);

  protected abstract void setDateField(final T result, final String fieldName, final Date value);

  protected abstract void setTimeField(final T result, final String fieldName, final Date value);

  protected abstract void setInt8Field(final T result, final String fieldName, final Byte value);

  protected abstract void setInt16Field(final T result, final String fieldName, final Short value);

  protected abstract void setInt32Field(final T result, final String fieldName, final Integer value);

  protected abstract void setInt64Field(final T result, final String fieldName, final Long value);

  protected abstract void setBytesField(final T result, final String fieldName, final byte[] value);

  protected abstract void setDecimalField(final T result, final String fieldName, final BigDecimal value);

  protected abstract void setBooleanField(final T result, final String fieldName, final Boolean value);

  protected abstract void setStructField(final T result, final String fieldName, final Struct value);

  protected abstract void setArray(final T result, final String fieldName, final Schema schema, final List array);

  protected abstract void setMap(final T result, final String fieldName, final Schema schema, final Map map);

  protected abstract void setNullField(final T result, final String fieldName);

  public T convert(final Object value) {
    Preconditions.checkNotNull(value, "value cannot be null.");
    T result = newValue();

    if (value instanceof Struct) {
      convertStruct(result, (Struct) value);
    } else if (value instanceof Map) {
      convertMap(result, (Map) value);
    } else {
      throw new DataException(
          String.format("Only Schema (%s) or Schema less (%s) are supported. %s is not a supported type.",
              Struct.class.getName(),
              Map.class.getName(),
              value.getClass().getName()
          )
      );
    }
    return result;
  }

  void convertMap(final T result, Map value) {
    for (final Object key : value.keySet()) {
      Preconditions.checkState(key instanceof String, "Map key must be a String.");


    }
  }

  void convertStruct(final T result, Struct struct) {
    final Schema schema = struct.schema();

    for (final Field field : schema.fields()) {
      final String fieldName = field.name();
      log.trace("convertStruct() - Processing '{}'", field.name());
      final Object fieldValue = struct.get(field);

      if (null == fieldValue) {
        log.trace("convertStruct() - Setting '{}' to null.", fieldName);
        setNullField(result, fieldName);
        continue;
      }

      log.trace("convertStruct() - Field '{}'.field().schema().type() = '{}'", fieldName, field.schema().type());
      switch (field.schema().type()) {
        case STRING:
          log.trace("convertStruct() - Processing '{}' as string.", fieldName);
          setStringField(result, fieldName, (String) fieldValue);
          break;
        case INT8:
          log.trace("convertStruct() - Processing '{}' as int8.", fieldName);
          setInt8Field(result, fieldName, (Byte) fieldValue);
          break;
        case INT16:
          log.trace("convertStruct() - Processing '{}' as int16.", fieldName);
          setInt16Field(result, fieldName, (Short) fieldValue);
          break;
        case INT32:
          Integer int32Value = (Integer) fieldValue;

          if (org.apache.kafka.connect.data.Date.LOGICAL_NAME.equals(field.schema().name())) {
            log.trace("convertStruct() - Processing '{}' as date.", fieldName);
            setDateField(result, fieldName, org.apache.kafka.connect.data.Date.toLogical(field.schema(), int32Value));
          } else if (org.apache.kafka.connect.data.Time.LOGICAL_NAME.equals(field.schema().name())) {
            log.trace("convertStruct() - Processing '{}' as time.", fieldName);
            setTimeField(result, fieldName, org.apache.kafka.connect.data.Time.toLogical(field.schema(), int32Value));
          } else {
            log.trace("convertStruct() - Processing '{}' as int32.", fieldName);
            setInt32Field(result, fieldName, int32Value);
          }
          break;
        case INT64:
          Long int64Value = (Long) fieldValue;
          if (Timestamp.LOGICAL_NAME.equals(field.schema().name())) {
            log.trace("convertStruct() - Processing '{}' as timestamp.", fieldName);
            setTimestampField(result, fieldName, Timestamp.toLogical(field.schema(), int64Value));
          } else {
            log.trace("convertStruct() - Processing '{}' as int64.", fieldName);
            setInt64Field(result, fieldName, int64Value);
          }
          break;
        case BYTES:
          byte[] bytes = (byte[]) fieldValue;
          if (Decimal.LOGICAL_NAME.equals(field.schema().name())) {
            log.trace("convertStruct() - Processing '{}' as decimal.", fieldName);
            setDecimalField(result, fieldName, Decimal.toLogical(field.schema(), bytes));
          } else {
            log.trace("convertStruct() - Processing '{}' as bytes.", fieldName);
            setBytesField(result, fieldName, bytes);
          }
          break;
        case FLOAT32:
          log.trace("convertStruct() - Processing '{}' as float32.", fieldName);
          setFloat32Field(result, fieldName, (Float) fieldValue);
          break;
        case FLOAT64:
          log.trace("convertStruct() - Processing '{}' as float64.", fieldName);
          setFloat64Field(result, fieldName, (Double) fieldValue);
          break;
        case BOOLEAN:
          log.trace("convertStruct() - Processing '{}' as boolean.", fieldName);
          setBooleanField(result, fieldName, (Boolean) fieldValue);
          break;
        case STRUCT:
          log.trace("convertStruct() - Processing '{}' as struct.", fieldName);
          setStructField(result, fieldName, (Struct) fieldValue);
          break;
        case ARRAY:
          log.trace("convertStruct() - Processing '{}' as array.", fieldName);
          setArray(result, fieldName, schema, (List) fieldValue);
          break;
        case MAP:
          log.trace("convertStruct() - Processing '{}' as map.", fieldName);
          setMap(result, fieldName, schema, (Map) fieldValue);
          break;
        default:
          throw new DataException("Unsupported schema.type(): " + schema.type());
      }
    }
  }
}
