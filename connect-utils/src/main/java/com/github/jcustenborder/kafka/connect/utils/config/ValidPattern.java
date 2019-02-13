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

import com.google.common.base.Preconditions;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validator used to ensure that a value matches a regular expression.
 * @see com.github.jcustenborder.kafka.connect.utils.config.validators.Validators#patternMatches(String)
 * @see com.github.jcustenborder.kafka.connect.utils.config.validators.Validators#patternMatches(Pattern)
 */
@Deprecated
public class ValidPattern implements ConfigDef.Validator {
  final Pattern pattern;

  public static ValidPattern of(String pattern) {
    Pattern regexPattern = Pattern.compile(pattern);
    return of(regexPattern);
  }

  public static ValidPattern of(Pattern pattern) {
    return new ValidPattern(pattern);
  }

  private ValidPattern(Pattern pattern) {
    Preconditions.checkNotNull(pattern, "pattern cannot be null");
    this.pattern = pattern;
  }

  @Override
  public void ensureValid(String s, Object o) {
    if (null == o || !(o instanceof String)) {
      throw new ConfigException(s, "Must be a string and cannot be null.");
    }
    Matcher matcher = this.pattern.matcher((String) o);
    if (!matcher.matches()) {
      throw new ConfigException(
          s,
          String.format("'%s' does not match pattern '%s'.", o, pattern.pattern())
      );
    }
  }

  @Override
  public String toString() {
    return String.format("Matches regex( %s )", this.pattern.pattern());
  }
}
