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

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

class PatternValidator implements ConfigDef.Validator {

  static void validatePattern(String setting, String pattern) {
    try {
      Pattern.compile(pattern);
    } catch (PatternSyntaxException e) {
      throw new ConfigException(
          setting,
          pattern,
          String.format(
              "Could not compile regex '%s'.",
              pattern
          )
      );
    }
  }


  @Override
  public void ensureValid(String setting, Object value) {
    if (value instanceof String) {
      String s = (String) value;
      validatePattern(setting, s);
    } else if (value instanceof List) {
      List<String> list = (List<String>) value;
      for (String s : list) {
        validatePattern(setting, s);
      }
    } else {
      throw new ConfigException(setting, value, "value must be a String or List.");
    }
  }
}
