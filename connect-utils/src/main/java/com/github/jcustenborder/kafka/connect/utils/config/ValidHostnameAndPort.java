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

import com.google.common.base.Preconditions;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validator is used to validate that a hostname:port string is valid.
 */
public class ValidHostnameAndPort implements ConfigDef.Validator {
  private static final Logger log = LoggerFactory.getLogger(ValidHostnameAndPort.class);

  static final Pattern HOSTNAME_PATTERN = Pattern.compile("^(.+)\\:(\\d{1,5})$");

  @Override
  public void ensureValid(String key, Object value) {
    log.trace("ensureValid('{}', '{}')", key, value);
    if (value instanceof String) {
      try {
        Matcher matcher = HOSTNAME_PATTERN.matcher((CharSequence) value);
        Preconditions.checkState(matcher.matches(), "'%s' does not match pattern '%s'.", key, HOSTNAME_PATTERN.pattern());
        final int port = Integer.parseInt(matcher.group(2));
        Preconditions.checkState(port >= 1 && port <= 65535, "'%s' port value %s is out of range. Port must be between 1 and 65535.", key, port);
      } catch (Exception ex) {
        throw new ConfigException(
            String.format("'%s' is not a valid hostname and port.", key),
            ex
        );
      }
    } else if (value instanceof List) {
      List<String> list = (List<String>) value;
      for (String s : list) {
        ensureValid(key, s);
      }
    } else {
      throw new ConfigException(
          String.format("'%s' must be a string or a list.", key)
      );
    }
  }


  public static ValidHostnameAndPort of() {
    return new ValidHostnameAndPort();
  }
}
