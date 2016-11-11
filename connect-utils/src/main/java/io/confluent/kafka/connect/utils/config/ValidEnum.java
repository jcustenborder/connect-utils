/**
 * Copyright © 2016 Jeremy Custenborder (jcustenborder@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.confluent.kafka.connect.utils.config;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import org.apache.kafka.common.config.ConfigDef;

import java.util.HashSet;
import java.util.Set;

/**
 * Validator is used to ensure that the input string is an element in the enum.
 */
public class ValidEnum implements ConfigDef.Validator {
  final Set<String> validEnums;
  final Class<?> enumClass;

  public static ValidEnum of(Class<?> enumClass) {
    return new ValidEnum(enumClass);
  }

  private ValidEnum(Class<?> enumClass) {
    Preconditions.checkNotNull(enumClass, "enumClass cannot be null");
    Preconditions.checkState(enumClass.isEnum(), "enumClass must be an enum.");
    Set<String> validEnums = new HashSet<>();
    for (Object o : enumClass.getEnumConstants()) {
      String key = o.toString();
      validEnums.add(key);
    }
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
        .add("enumClass", this.enumClass.getSimpleName())
        .add("validEnums", this.validEnums)
        .toString();
  }
}