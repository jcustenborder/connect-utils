/**
 * Copyright (C) ${project.inceptionYear} Jeremy Custenborder (jcustenborder@gmail.com)
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
package io.confluent.kafka.connect.conversion;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import org.apache.kafka.connect.data.Schema;


class ConverterKey implements Comparable<ConverterKey> {
  public final Schema.Type type;
  public final String logicalName;

  ConverterKey(Schema schema) {
    this(schema.type(), schema.name());
  }

  ConverterKey(Schema.Type type, String logicalName) {
    this.type = type;
    this.logicalName = logicalName == null ? "" : logicalName;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.type, this.logicalName);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("type", this.type)
        .add("logicalName", this.logicalName)
        .toString();
  }


  @Override
  public int compareTo(ConverterKey that) {
    if (null == that) {
      return 1;
    }
    return ComparisonChain.start()
        .compare(this.type, that.type)
        .compare(this.logicalName, that.logicalName)
        .result();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ConverterKey) {
      return compareTo((ConverterKey) obj) == 0;
    } else {
      return false;
    }
  }
}
