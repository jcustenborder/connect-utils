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
package com.github.jcustenborder.kafka.connect.utils.config.recommenders;

import com.google.common.base.Preconditions;
import org.apache.kafka.common.config.ConfigDef;

import java.util.List;
import java.util.Map;

class VisibleIfRecommender implements ConfigDef.Recommender {
  final String configKey;
  final Object value;
  final ValidValuesCallback validValuesCallback;

  VisibleIfRecommender(String configKey, Object value, ValidValuesCallback validValuesCallback) {
    Preconditions.checkNotNull(configKey, "configKey cannot be null.");
    Preconditions.checkNotNull(value, "value cannot be null.");
    Preconditions.checkNotNull(validValuesCallback, "validValuesCallback cannot be null.");
    this.configKey = configKey;
    this.value = value;
    this.validValuesCallback = validValuesCallback;
  }

  @Override
  public List<Object> validValues(String s, Map<String, Object> map) {
    return this.validValuesCallback.validValues(s, map);
  }

  @Override
  public boolean visible(String key, Map<String, Object> settings) {
    Object v = settings.get(this.configKey);
    return this.value.equals(v);
  }
}
