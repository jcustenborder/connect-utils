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

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.header.Header;

class HeaderImpl implements Header {
  final String key;
  final Schema schema;
  final Object value;

  HeaderImpl(String key, Schema schema, Object value) {
    this.key = key;
    this.schema = schema;
    this.value = value;
  }

  @Override
  public String key() {
    return this.key;
  }

  @Override
  public Schema schema() {
    return this.schema;
  }

  @Override
  public Object value() {
    return this.value;
  }

  @Override
  public Header with(Schema schema, Object value) {
    return new HeaderImpl(this.key, schema, value);
  }

  @Override
  public Header rename(String s) {
    return new HeaderImpl(this.key, this.schema, this.value);
  }
}
