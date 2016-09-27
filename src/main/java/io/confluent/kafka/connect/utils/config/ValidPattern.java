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
package io.confluent.kafka.connect.utils.config;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import org.apache.kafka.common.config.ConfigDef;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    Preconditions.checkNotNull(o, "%s: Cannot be null.", s);
    Preconditions.checkState(o instanceof String, "%s: '%s' is not a String.", s, o);
    Matcher matcher = this.pattern.matcher((String) o);
    Preconditions.checkState(matcher.matches(), "%s: '%s' does not match pattern '%s'.", s, o, pattern.pattern());
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("pattern", this.pattern.pattern())
        .toString();
  }
}
