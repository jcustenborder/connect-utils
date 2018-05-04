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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class EnumRecommender implements ConfigDef.Recommender {
  final Set<String> validEnums;
  final Class<?> enumClass;
  final VisibleCallback visible;


  EnumRecommender(Class<?> enumClass, VisibleCallback visible, String... excludes) {
    Preconditions.checkNotNull(enumClass, "enumClass cannot be null");
    Preconditions.checkState(enumClass.isEnum(), "enumClass must be an enum.");
    Preconditions.checkNotNull(visible, "visible cannot be null");
    this.enumClass = enumClass;
    this.visible = visible;
    Set<String> validEnums = new LinkedHashSet<>();
    for (Object o : enumClass.getEnumConstants()) {
      String key = o.toString();
      validEnums.add(key);
    }
    validEnums.removeAll(Arrays.asList(excludes));
    this.validEnums = ImmutableSet.copyOf(validEnums);
  }


  @Override
  public List<Object> validValues(String s, Map<String, Object> map) {
    return ImmutableList.copyOf(this.validEnums);
  }

  @Override
  public boolean visible(String s, Map<String, Object> map) {
    return this.visible.visible(s, map);
  }
}
