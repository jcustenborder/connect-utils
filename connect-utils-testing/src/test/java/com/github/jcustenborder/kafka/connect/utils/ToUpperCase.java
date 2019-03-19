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
package com.github.jcustenborder.kafka.connect.utils;

import com.github.jcustenborder.kafka.connect.utils.config.Description;
import com.github.jcustenborder.kafka.connect.utils.config.Title;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.transforms.Transformation;

import java.util.Map;

@Title("ToUpperCase")
@Description("This transformation is used to uppercase a string.")
public abstract class ToUpperCase<R extends ConnectRecord<R>> implements Transformation<R> {
  final boolean isKey;

  protected ToUpperCase(boolean isKey) {
    this.isKey = isKey;
  }

  @Override
  public R apply(R record) {
    if (isKey) {
      return record.newRecord(
          record.topic(),
          record.kafkaPartition(),
          record.keySchema(),
          record.key().toString().toUpperCase(),
          record.valueSchema(),
          record.value(),
          record.timestamp()
      );
    } else {
      return record.newRecord(
          record.topic(),
          record.kafkaPartition(),
          record.keySchema(),
          record.key(),
          record.valueSchema(),
          record.value().toString().toUpperCase(),
          record.timestamp()
      );
    }
  }

  @Override
  public ConfigDef config() {
    return new ConfigDef();
  }

  @Override
  public void close() {

  }

  @Override
  public void configure(Map<String, ?> map) {

  }

  public static class Key<R extends ConnectRecord<R>> extends ToUpperCase<R> {
    public Key() {
      super(true);
    }
  }

  public static class Value<R extends ConnectRecord<R>> extends ToUpperCase<R> {
    public Value() {
      super(false);
    }
  }
}
