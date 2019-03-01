/**
 * Copyright © 2017 Jeremy Custenborder (jcustenborder@gmail.com)
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
package com.github.jcustenborder.kafka.connect.utils.transformation;

import com.github.jcustenborder.kafka.connect.utils.data.SchemaHelper;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.data.Time;
import org.apache.kafka.connect.data.Timestamp;
import org.apache.kafka.connect.transforms.Transformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @param <R>
 */
public abstract class BaseKeyValueTransformation<R extends ConnectRecord<R>> implements Transformation<R> {
  protected final boolean isKey;
  private static final Logger log = LoggerFactory.getLogger(BaseKeyValueTransformation.class);

  protected BaseKeyValueTransformation(boolean isKey) {
    this.isKey = isKey;
  }

  protected SchemaAndValue processMap(R record, Map<String, Object> input) {
    throw new UnsupportedOperationException("MAP is not a supported type.");
  }

  protected SchemaAndValue processStruct(R record, Schema inputSchema, Struct input) {
    throw new UnsupportedOperationException("STRUCT is not a supported type.");
  }

  protected SchemaAndValue processString(R record, Schema inputSchema, String input) {
    throw new UnsupportedOperationException("STRING is not a supported type.");
  }

  protected SchemaAndValue processBytes(R record, Schema inputSchema, byte[] input) {
    throw new UnsupportedOperationException("BYTES is not a supported type.");
  }

  protected SchemaAndValue processInt8(R record, Schema inputSchema, byte input) {
    throw new UnsupportedOperationException("INT8 is not a supported type.");
  }

  protected SchemaAndValue processInt16(R record, Schema inputSchema, short input) {
    throw new UnsupportedOperationException("INT16 is not a supported type.");
  }

  protected SchemaAndValue processInt32(R record, Schema inputSchema, int input) {
    throw new UnsupportedOperationException("INT32 is not a supported type.");
  }

  protected SchemaAndValue processInt64(R record, Schema inputSchema, long input) {
    throw new UnsupportedOperationException("INT64 is not a supported type.");
  }

  protected SchemaAndValue processBoolean(R record, Schema inputSchema, boolean input) {
    throw new UnsupportedOperationException("BOOLEAN is not a supported type.");
  }

  protected SchemaAndValue processTimestamp(R record, Schema inputSchema, Date input) {
    throw new UnsupportedOperationException("Timestamp is not a supported type.");
  }

  protected SchemaAndValue processDate(R record, Schema inputSchema, Date input) {
    throw new UnsupportedOperationException("Date is not a supported type.");
  }

  protected SchemaAndValue processTime(R record, Schema inputSchema, Date input) {
    throw new UnsupportedOperationException("Time is not a supported type.");
  }

  protected SchemaAndValue processDecimal(R record, Schema inputSchema, BigDecimal input) {
    throw new UnsupportedOperationException("Decimal is not a supported type.");
  }

  protected SchemaAndValue processFloat64(R record, Schema inputSchema, double input) {
    throw new UnsupportedOperationException("FLOAT64 is not a supported type.");
  }

  protected SchemaAndValue processFloat32(R record, Schema inputSchema, float input) {
    throw new UnsupportedOperationException("FLOAT32 is not a supported type.");
  }

  protected SchemaAndValue processArray(R record, Schema inputSchema, List<Object> input) {
    throw new UnsupportedOperationException("ARRAY is not a supported type.");
  }

  protected SchemaAndValue processMap(R record, Schema inputSchema, Map<Object, Object> input) {
    throw new UnsupportedOperationException("MAP is not a supported type.");
  }

  private static final Schema OPTIONAL_TIMESTAMP = Timestamp.builder().optional().build();


  protected SchemaAndValue process(R record, SchemaAndValue input) {
    final SchemaAndValue result;

    if (null == input.schema() && null == input.value()) {
      return new SchemaAndValue(
          null,
          null
      );
    }

    if (input.value() instanceof Map) {
      log.trace("process() - Processing as map");
      result = processMap(record, (Map<String, Object>) input.value());
      return result;
    }

    if (null == input.schema()) {
      log.trace("process() - Determining schema");
      final Schema schema = SchemaHelper.schema(input.value());
      return process(record, new SchemaAndValue(schema, input.value()));
    }

    log.trace("process() - input.value() has as schema. schema = {}", input.schema());
    if (Schema.Type.STRUCT == input.schema().type()) {
      result = processStruct(record, input.schema(), (Struct) input.value());
    } else if (Timestamp.LOGICAL_NAME.equals(input.schema().name())) {
      result = processTimestamp(record, input.schema(), (Date) input.value());
    } else if (org.apache.kafka.connect.data.Date.LOGICAL_NAME.equals(input.schema().name())) {
      result = processDate(record, input.schema(), (Date) input.value());
    } else if (Time.LOGICAL_NAME.equals(input.schema().name())) {
      result = processTime(record, input.schema(), (Date) input.value());
    } else if (Decimal.LOGICAL_NAME.equals(input.schema().name())) {
      result = processDecimal(record, input.schema(), (BigDecimal) input.value());
    } else if (Schema.Type.STRING == input.schema().type()) {
      result = processString(record, input.schema(), (String) input.value());
    } else if (Schema.Type.BYTES == input.schema().type()) {
      result = processBytes(record, input.schema(), (byte[]) input.value());
    } else if (Schema.Type.INT8 == input.schema().type()) {
      result = processInt8(record, input.schema(), (byte) input.value());
    } else if (Schema.Type.INT16 == input.schema().type()) {
      result = processInt16(record, input.schema(), (short) input.value());
    } else if (Schema.Type.INT32 == input.schema().type()) {
      result = processInt32(record, input.schema(), (int) input.value());
    } else if (Schema.Type.INT64 == input.schema().type()) {
      result = processInt64(record, input.schema(), (long) input.value());
    } else if (Schema.Type.FLOAT32 == input.schema().type()) {
      result = processFloat32(record, input.schema(), (float) input.value());
    } else if (Schema.Type.FLOAT64 == input.schema().type()) {
      result = processFloat64(record, input.schema(), (double) input.value());
    } else if (Schema.Type.ARRAY == input.schema().type()) {
      result = processArray(record, input.schema(), (List<Object>) input.value());
    } else if (Schema.Type.MAP == input.schema().type()) {
      result = processMap(record, input.schema(), (Map<Object, Object>) input.value());
    } else if (Schema.Type.BOOLEAN == input.schema().type()) {
      result = processBoolean(record, input.schema(), (boolean) input.value());
    } else {
      throw new UnsupportedOperationException(
          String.format(
              "Schema is not supported. type='%s' name='%s'",
              input.schema().type(),
              input.schema().name()
          )
      );
    }

    return result;
  }


  @Override
  public R apply(R record) {
    SchemaAndValue key = new SchemaAndValue(record.keySchema(), record.key());
    SchemaAndValue value = new SchemaAndValue(record.valueSchema(), record.value());
    final SchemaAndValue input = this.isKey ? key : value;
    final SchemaAndValue result = process(record, input);
    if (this.isKey) {
      key = result;
    } else {
      value = result;
    }

    return record.newRecord(
        record.topic(),
        record.kafkaPartition(),
        key.schema(),
        key.value(),
        value.schema(),
        value.value(),
        record.timestamp(),
        record.headers()
    );
  }
}
