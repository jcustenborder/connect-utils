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
package com.github.jcustenborder.kafka.connect.utils.config;

import org.apache.kafka.common.config.ConfigDef;

import java.util.Comparator;

public class ConfigKeyComparator implements Comparator<ConfigDef.ConfigKey> {
  @Override
  public int compare(ConfigDef.ConfigKey k1, ConfigDef.ConfigKey k2) {
    // first take anything with no default value (therefore required)
    if (!k1.hasDefault() && k2.hasDefault())
      return -1;
    else if (!k2.hasDefault() && k1.hasDefault())
      return 1;

    // then sort by importance
    int cmp = k1.importance.compareTo(k2.importance);
    if (cmp == 0)
      // then sort in alphabetical order
      return k1.name.compareTo(k2.name);
    else
      return cmp;
  }

  public static final Comparator<ConfigDef.ConfigKey> INSTANCE = new ConfigKeyComparator();
}