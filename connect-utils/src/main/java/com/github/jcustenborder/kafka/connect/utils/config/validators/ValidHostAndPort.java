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
package com.github.jcustenborder.kafka.connect.utils.config.validators;

import com.google.common.base.Strings;
import com.google.common.net.HostAndPort;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

import java.util.List;

/**
 * Validator to ensure that a configuration setting is a hostname and port.
 * @see Validators#validHostAndPort()
 * @see Validators#validHostAndPort(Integer, boolean, boolean)
 */
@Deprecated
public class ValidHostAndPort implements ConfigDef.Validator {
  final Integer defaultPort;
  final boolean requireBracketsForIPv6;
  final boolean portRequired;

  private ValidHostAndPort(Integer defaultPort, boolean requireBracketsForIPv6, boolean portRequired) {
    this.defaultPort = defaultPort;
    this.requireBracketsForIPv6 = requireBracketsForIPv6;
    this.portRequired = portRequired;
  }

  public static ValidHostAndPort of() {
    return new ValidHostAndPort(null, false, true);
  }

  public static ValidHostAndPort of(Integer defaultPort, boolean requireBracketsForIPv6, boolean portRequired) {
    return new ValidHostAndPort(defaultPort, requireBracketsForIPv6, portRequired);
  }

  void validate(final String setting, final String input) {
    HostAndPort hostAndPort = HostAndPort.fromString(input);
    if (this.requireBracketsForIPv6) {
      hostAndPort = hostAndPort.requireBracketsForIPv6();
    }
    if (null != this.defaultPort) {
      hostAndPort.withDefaultPort(this.defaultPort);
    }

    if (Strings.isNullOrEmpty(hostAndPort.getHost())) {
      throw new ConfigException(String.format("'%s'(%s) host cannot be blank or null.", setting, input));
    }

    if (this.portRequired && !hostAndPort.hasPort()) {
      throw new ConfigException(String.format("'%s'(%s) must specify a port.", setting, input));
    }
  }


  @Override
  public void ensureValid(final String setting, final Object value) {
    if (value instanceof String) {
      final String input = (String) value;
      validate(setting, input);
    } else if (value instanceof List) {
      final List<String> inputs = (List<String>) value;
      for (String input : inputs) {
        validate(setting, input);
      }
    } else {
      throw new ConfigException(String.format("'%s' must be a String or List", setting));
    }
  }
}
