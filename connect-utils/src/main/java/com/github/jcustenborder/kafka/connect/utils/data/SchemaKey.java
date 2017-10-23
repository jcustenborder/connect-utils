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
import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;
import org.apache.kafka.connect.data.Schema;

import java.util.Objects;

public class SchemaKey implements Comparable<SchemaKey> {
  public final String name;
  public final Integer version;
  public final Schema.Type type;

  private SchemaKey(final Schema schema) {
    this.name = schema.name();
    this.version = schema.version();
    this.type = schema.type();
  }

  public static SchemaKey of(final Schema schema) {
    Preconditions.checkNotNull(schema, "schema cannot be null.");
    return new SchemaKey(schema);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.name, this.type, this.version);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Schema) {
      return equals(of((Schema) obj));
    }

    if (!(obj instanceof SchemaKey)) {
      return false;
    }

    return 0 == compareTo((SchemaKey) obj);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("name", this.name)
        .add("type", this.type)
        .add("version", this.version)
        .omitNullValues()
        .toString();
  }

  @Override
  public int compareTo(SchemaKey that) {
    return ComparisonChain.start()
        .compare(this.type, that.type)
        .compare((null == this.name ? "" : this.name), (null == that.name ? "" : that.name))
        .compare((null == this.version ? 0 : this.version), (null == that.version ? 0 : that.version))
        .result();
  }
}
