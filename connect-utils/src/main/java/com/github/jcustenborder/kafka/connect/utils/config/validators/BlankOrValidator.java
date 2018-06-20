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
package com.github.jcustenborder.kafka.connect.utils.config.validators;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.kafka.common.config.ConfigDef;

class BlankOrValidator implements ConfigDef.Validator {
  final ConfigDef.Validator validator;

  BlankOrValidator(ConfigDef.Validator validator) {
    Preconditions.checkNotNull(validator, "validator cannot be null.");
    this.validator = validator;
  }

  @Override
  public void ensureValid(String key, Object o) {
    if (o instanceof String) {
      final String s = o.toString();
      if (!Strings.isNullOrEmpty(s)) {
        validator.ensureValid(key, o);
      }
    }
  }

  @Override
  public String toString() {
    return String.format("Empty String or %s", this.validator);
  }

  public static ConfigDef.Validator of(ConfigDef.Validator validator) {
    return new BlankOrValidator(validator);
  }
}
