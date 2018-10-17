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

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.HostAndPort;
import com.google.common.primitives.Ints;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigException;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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

  static HostAndPort hostAndPort(String input, Integer defaultPort) {
    final HostAndPort result = HostAndPort.fromString(input);

    if (null != defaultPort) {
      result.withDefaultPort(defaultPort);
    }

    return result;
  }

  /**
   * Method is used to parse a string ConfigDef item to a HostAndPort
   *
   * @param config      Config to read from
   * @param key         ConfigItem to get the host string from.
   * @param defaultPort The default port to use if a port was not specified. Can be null.
   * @return HostAndPort based on the ConfigItem.
   */
  public static HostAndPort hostAndPort(AbstractConfig config, String key, Integer defaultPort) {
    final String input = config.getString(key);
    return hostAndPort(input, defaultPort);
  }

  /**
   * Method is used to parse a string ConfigDef item to a HostAndPort
   *
   * @param config Config to read from
   * @param key    ConfigItem to get the host string from.
   * @return HostAndPort based on the ConfigItem.
   */
  public static HostAndPort hostAndPort(AbstractConfig config, String key) {
    return hostAndPort(config, key, null);
  }

  /**
   * Method is used to parse a list ConfigDef item to a list of HostAndPort
   *
   * @param config      Config to read from
   * @param key         ConfigItem to get the host string from.
   * @param defaultPort The default port to use if a port was not specified. Can be null.
   * @return
   */
  public static List<HostAndPort> hostAndPorts(AbstractConfig config, String key, Integer defaultPort) {
    final List<String> inputs = config.getList(key);
    List<HostAndPort> result = new ArrayList<>();
    for (final String input : inputs) {
      final HostAndPort hostAndPort = hostAndPort(input, defaultPort);
      result.add(hostAndPort);
    }

    return ImmutableList.copyOf(result);
  }

  /**
   * Method is used to parse hosts and ports
   *
   * @param config
   * @param key
   * @return
   */
  public static List<HostAndPort> hostAndPorts(AbstractConfig config, String key) {
    return hostAndPorts(config, key, null);
  }

  static URL url(String key, String value) {
    try {
      return new URL(value);
    } catch (MalformedURLException e) {
      ConfigException configException = new ConfigException(
          key, value, "Could not parse to URL."
      );
      configException.initCause(e);

      throw configException;
    }
  }

  /**
   * Method is used to retrieve a URL from a configuration key.
   *
   * @param config Config to read
   * @param key    Key to read
   * @return URL for the value.
   */
  public static URL url(AbstractConfig config, String key) {
    final String value = config.getString(key);
    return url(key, value);
  }

  /**
   * Method is used to retrieve a list of URL(s) from a configuration key.
   *
   * @param config Config to read
   * @param key    Key to read
   * @return URL for the value.
   */
  public static List<URL> urls(AbstractConfig config, String key) {
    List<URL> result = new ArrayList<>();
    List<String> input = config.getList(key);
    for (String s : input) {
      result.add(url(key, s));
    }
    return ImmutableList.copyOf(result);
  }

  static URI uri(String key, String value) {
    try {
      return new URI(value);
    } catch (URISyntaxException e) {
      ConfigException configException = new ConfigException(
          key, value, "Could not parse to URI."
      );
      configException.initCause(e);

      throw configException;
    }
  }

  /**
   * Method is used to retrieve a URI from a configuration key.
   *
   * @param config Config to read
   * @param key    Key to read
   * @return URI for the value.
   */
  public static URI uri(AbstractConfig config, String key) {
    final String value = config.getString(key);
    return uri(key, value);
  }

  /**
   * Method is used to retrieve a list of URI(s) from a configuration key.
   *
   * @param config Config to read
   * @param key    Key to read
   * @return URI for the value.
   */
  public static List<URI> uris(AbstractConfig config, String key) {
    List<URI> result = new ArrayList<>();
    List<String> input = config.getList(key);
    for (String s : input) {
      result.add(uri(key, s));
    }
    return ImmutableList.copyOf(result);
  }

  /**
   * Method is used to retrieve a list and convert it to an immutable set.
   *
   * @param config Config to read
   * @param key    Key to read
   * @return ImmutableSet with the contents of the config key.
   * @see com.google.common.collect.ImmutableSet
   */
  public static Set<String> getSet(AbstractConfig config, String key) {
    List<String> value = config.getList(key);
    return ImmutableSet.copyOf(value);
  }

  /**
   * Method is used to create a pattern based on the config element.
   *
   * @param config
   * @param key
   * @return
   */
  public static Pattern pattern(AbstractConfig config, String key) {
    String pattern = config.getString(key);

    try {
      return Pattern.compile(pattern);
    } catch (PatternSyntaxException e) {
      throw new ConfigException(
          key,
          pattern,
          String.format(
              "Could not compile regex '%s'.",
              pattern
          )
      );
    }
  }
}
