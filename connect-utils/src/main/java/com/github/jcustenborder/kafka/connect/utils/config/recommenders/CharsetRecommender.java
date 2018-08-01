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

import com.google.common.collect.ImmutableList;
import org.apache.kafka.common.config.ConfigDef;

import java.util.List;
import java.util.Map;

class CharsetRecommender implements ConfigDef.Recommender {
  private final List<Object> charsets;
  private final VisibleCallback visible;

  CharsetRecommender(Iterable<String> charsets, VisibleCallback visible) {
    this.visible = visible;
    this.charsets = ImmutableList.copyOf(charsets);
  }

  @Override
  public List<Object> validValues(String s, Map<String, Object> map) {
    return this.charsets;
  }

  @Override
  public boolean visible(String s, Map<String, Object> map) {
    return this.visible.visible(s, map);
  }
}
