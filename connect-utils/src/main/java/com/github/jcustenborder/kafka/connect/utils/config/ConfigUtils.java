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

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import org.apache.kafka.common.config.AbstractConfig;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class ConfigUtils {
  /**
   * Method is used to return a class that should be assignable to the expected class. For example when a user implements
   * an interface that is loaded at runtime. It is good to ensure that the proper interface has been implemented.
   *
   * @param config   config that has the class setting
   * @param key      key to read
   * @param expected expected parent class or interface
   * @param <T>      Class type to return.
   * @return Returns a class if it's assignable from the specified class.
   * @throws IllegalStateException Thrown if it fails expected.isAssignableFrom(cls).
   */
  public static <T> Class<T> getClass(AbstractConfig config, String key, Class<T> expected) {
    Preconditions.checkNotNull(config, "config cannot be null");
    Preconditions.checkNotNull(key, "key cannot be null");
    Preconditions.checkNotNull(expected, "expected cannot be null");
    Class<?> cls = config.getClass(key);
    Preconditions.checkState(expected.isAssignableFrom(cls), "'%s' is not assignable from '%s'", expected.getSimpleName(), cls.getSimpleName());
    return (Class<T>) cls;
  }

  /**
   * Method is used to return an enum value from a given string.
   *
   * @param enumClass Class for the resulting enum value
   * @param config    config to read the value from
   * @param key       key for the value
   * @param <T>       Enum class to return type for.
   * @return enum value for the given key.
   * @see ValidEnum
   */
  public static <T extends Enum<T>> T getEnum(Class<T> enumClass, AbstractConfig config, String key) {
    Preconditions.checkNotNull(enumClass, "enumClass cannot be null");
    Preconditions.checkState(enumClass.isEnum(), "enumClass must be an enum.");
    String textValue = config.getString(key);
    return Enum.valueOf(enumClass, textValue);
  }

  /**
   * Method is used to return the values for an enum.
   *
   * @param enumClass Enum class to return the constants for.
   * @return Returns a comma seperated string of all of the values in the enum.
   */
  public static String enumValues(Class<?> enumClass) {
    Preconditions.checkNotNull(enumClass, "enumClass cannot be null");
    Preconditions.checkState(enumClass.isEnum(), "enumClass must be an enum.");
    return Joiner.on(", ").join(enumClass.getEnumConstants());
  }

  /**
   * Method is used to return a File checking to ensure that it is an absolute path.
   *
   * @param config config to read the value from
   * @param key    key for the value
   * @return File for the config value.
   */
  public static File getAbsoluteFile(AbstractConfig config, String key) {
    Preconditions.checkNotNull(config, "config cannot be null");
    String path = config.getString(key);
    File file = new File(path);
    Preconditions.checkState(file.isAbsolute(), "'%s' must be an absolute path.", key);
    return new File(path);
  }

  static InetSocketAddress parseInetSocketAddress(String s) {
    Preconditions.checkNotNull(s, "s cannot be null.");
    Matcher matcher = ValidHostnameAndPort.HOSTNAME_PATTERN.matcher(s);
    Preconditions.checkState(matcher.matches(), "'%s' does not match '%s'", s, ValidHostnameAndPort.HOSTNAME_PATTERN.pattern());
    final Integer port = Ints.tryParse(matcher.group(2));
    Preconditions.checkState(port >= 1 && port <= 65535, "Invalid port value %s. Must be between 1 and 65535", port);
    return new InetSocketAddress(matcher.group(1), port);
  }

  /**
   * Method is used to return an InetSocketAddress from a hostname:port string.
   *
   * @param config config to read the value from
   * @param key    key for the value
   * @return InetSocketAddress for the supplied string.
   */
  public static InetSocketAddress inetSocketAddress(AbstractConfig config, String key) {
    Preconditions.checkNotNull(config, "config cannot be null");
    String value = config.getString(key);
    return parseInetSocketAddress(value);
  }

  /**
   * Method is used to return a list of InetSocketAddress from a config list of hostname:port strings.
   *
   * @param config config to read the value from
   * @param key    key for the value
   * @return List of InetSocketAddress for the supplied strings.
   */
  public static List<InetSocketAddress> inetSocketAddresses(AbstractConfig config, String key) {
    Preconditions.checkNotNull(config, "config cannot be null");
    List<String> value = config.getList(key);
    List<InetSocketAddress> addresses = new ArrayList<>(value.size());
    for (String s : value) {
      addresses.add(parseInetSocketAddress(s));
    }
    return ImmutableList.copyOf(addresses);
  }


}
