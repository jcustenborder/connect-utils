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
package com.github.jcustenborder.kafka.connect.utils.config;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Validator is used to ensure that the input string is an element in the enum.
 */
public class ValidEnum implements ConfigDef.Validator {
  final Set<String> validEnums;
  final Class<?> enumClass;

  /**
   * Method is used to create a new instance of the enum validator.
   *
   * @param enumClass Enum class with the entries to validate for.
   * @param excludes  Enum entries to exclude from the validator.
   * @return ValidEnum
   */
  public static ValidEnum of(Class<?> enumClass, String... excludes) {
    return new ValidEnum(enumClass, excludes);
  }

  private ValidEnum(Class<?> enumClass, String... excludes) {
    Preconditions.checkNotNull(enumClass, "enumClass cannot be null");
    Preconditions.checkState(enumClass.isEnum(), "enumClass must be an enum.");
    Set<String> validEnums = new LinkedHashSet<>();
    for (Object o : enumClass.getEnumConstants()) {
      String key = o.toString();
      validEnums.add(key);
    }
    validEnums.removeAll(Arrays.asList(excludes));
    this.validEnums = validEnums;
    this.enumClass = enumClass;
  }

  @Override
  public void ensureValid(String s, Object o) {
    Preconditions.checkState(validEnums.contains(o),
        "'%s' is not a valid value for %s. Valid values are %s.",
        o,
        enumClass.getSimpleName(),
        ConfigUtils.enumValues(enumClass)
    );
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("enum", this.enumClass.getSimpleName())
        .add("allowed", this.validEnums)
        .toString();
  }
}