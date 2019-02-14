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

import com.github.jcustenborder.kafka.connect.utils.config.ValidEnum;
import com.github.jcustenborder.kafka.connect.utils.config.ValidPattern;
import com.github.jcustenborder.kafka.connect.utils.config.ValidPort;
import com.google.common.base.Preconditions;
import org.apache.kafka.common.config.ConfigDef.Validator;
import org.apache.kafka.common.config.ConfigException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public class Validators {
  private Validators() {

  }

  /**
   * Method will return a validator that will ensure that a regular expression properly compiles.
   *
   * @return Method will return a validator that will ensure that a regular expression properly compiles.
   */
  public static Validator pattern() {
    return new PatternValidator();
  }

  /**
   * Method will return a validator that will validate that a string matches a regular expression
   *
   * @param pattern
   * @return Method will return a validator that will validate that a string matches a regular expression
   * @see java.util.regex.Matcher#matches()
   */
  public static Validator patternMatches(String pattern) {
    return ValidPattern.of(pattern);
  }

  /**
   * Method will return a validator that will validate that a string matches a regular expression
   *
   * @param pattern
   * @return validator that will validate that a string matches a regular expression
   * @see java.util.regex.Matcher#matches()
   */
  public static Validator patternMatches(Pattern pattern) {
    return ValidPattern.of(pattern);
  }

  /**
   * Method will return a validator that will accept a blank string. Any other value will be passed on to the supplied validator.
   *
   * @param validator Validator to test non blank values with.
   * @return validator that will accept a blank string. Any other value will be passed on to the supplied validator.
   */
  public static Validator blankOr(Validator validator) {
    Preconditions.checkNotNull(validator, "validator cannot be null.");
    return BlankOrValidator.of(validator);
  }

  /**
   * Method will return a validator that will ensure that a String or List contains a URI with the
   * proper syntax.
   *
   * @param schemes The uri schemes that are valid. If empty anything can be accepted.
   * @return validator
   */
  public static Validator validURI(String... schemes) {
    return new ValidURI(schemes);
  }

  /**
   * Method will return a validator that will ensure that a String or List contains an Url
   *
   * @return validator
   */
  public static Validator validUrl() {
    return new ValidUrl();
  }


  /**
   * Method will return a validator that will ensure that a String or List contains a charset that
   * is supported by the system.
   *
   * @param charsets The charsets that are allowed. If empty all of the available charsets on the
   *                 system will be included.
   * @return validator
   */
  public static Validator validCharset(String... charsets) {
    if (null == charsets || charsets.length == 0) {
      return new ValidCharset();
    } else {
      return new ValidCharset(charsets);
    }
  }

  /**
   * Method will return a validator that will ensure that a String or List contains a charset that
   * is supported by the system.
   *
   * @return validator
   */
  public static Validator validCharset() {
    return new ValidCharset();
  }

  /**
   * Method is used to create a new INSTANCE of the enum validator.
   *
   * @param enumClass Enum class with the entries to validate for.
   * @param excludes  Enum entries to exclude from the validator.
   * @return validator
   */
  public static Validator validEnum(Class<? extends Enum> enumClass, Enum... excludes) {
    String[] ex = new String[excludes.length];
    for (int i = 0; i < ex.length; i++) {
      ex[i] = excludes[i].toString();
    }
    return ValidEnum.of(enumClass, ex);
  }

  /**
   * Creates a validator in the port range specified.
   *
   * @return validator
   */
  public static Validator validPort() {
    return ValidPort.of();
  }

  /**
   * Creates a validator in the port range specified.
   *
   * @param start The low port of the range.
   * @param end   The high port of the range.
   * @return ConfigDef.Validator Validator for the port range specified.
   * @throws IllegalStateException Throws if the start not a valid port number.
   * @throws IllegalStateException Throws if the end not a valid port number.
   * @throws IllegalStateException Throws if the start is greater than end.
   */
  public static Validator validPort(int start, int end) {
    return ValidPort.of(start, end);
  }

  /**
   * Validator to ensure that a configuration setting is a hostname and port.
   *
   * @return validator
   */
  public static Validator validHostAndPort() {
    return ValidHostAndPort.of();
  }

  /**
   * Validator to ensure that a configuration setting is a hostname and port.
   *
   * @param defaultPort
   * @param requireBracketsForIPv6
   * @param portRequired
   * @return validator
   */
  public static Validator validHostAndPort(Integer defaultPort, boolean requireBracketsForIPv6, boolean portRequired) {
    return ValidHostAndPort.of(defaultPort, requireBracketsForIPv6, portRequired);
  }

  /**
   * Validator is used to ensure that the KeyStore type specified is valid.
   * @return
   */
  public static Validator validKeyStoreType() {
    return (s, o) -> {
      if (!(o instanceof String)) {
        throw new ConfigException(s, o, "Must be a string.");
      }

      String keyStoreType = o.toString();
      try {
        KeyStore.getInstance(keyStoreType);
      } catch (KeyStoreException e) {
        ConfigException exception = new ConfigException(s, o, "Invalid KeyStore type");
        exception.initCause(e);
        throw exception;
      }
    };
  }

  /**
   * Validator is used to ensure that the KeyManagerFactory Algorithm specified is valid.
   * @return
   */
  public static Validator validKeyManagerFactory() {
    return (s, o) -> {
      if (!(o instanceof String)) {
        throw new ConfigException(s, o, "Must be a string.");
      }

      String keyStoreType = o.toString();
      try {
        KeyManagerFactory.getInstance(keyStoreType);
      } catch (NoSuchAlgorithmException e) {
        ConfigException exception = new ConfigException(s, o, "Invalid Algorithm");
        exception.initCause(e);
        throw exception;
      }
    };
  }

  /**
   * Validator is used to ensure that the TrustManagerFactory Algorithm specified is valid.
   * @return
   */
  public static Validator validTrustManagerFactory() {
    return (s, o) -> {
      if (!(o instanceof String)) {
        throw new ConfigException(s, o, "Must be a string.");
      }

      String keyStoreType = o.toString();
      try {
        TrustManagerFactory.getInstance(keyStoreType);
      } catch (NoSuchAlgorithmException e) {
        ConfigException exception = new ConfigException(s, o, "Invalid Algorithm");
        exception.initCause(e);
        throw exception;
      }
    };
  }

  /**
   * Validator is used to ensure that the TrustManagerFactory Algorithm specified is valid.
   * @return
   */
  public static Validator validSSLContext() {
    return (s, o) -> {
      if (!(o instanceof String)) {
        throw new ConfigException(s, o, "Must be a string.");
      }

      String keyStoreType = o.toString();
      try {
        SSLContext.getInstance(keyStoreType);
      } catch (NoSuchAlgorithmException e) {
        ConfigException exception = new ConfigException(s, o, "Invalid Algorithm");
        exception.initCause(e);
        throw exception;
      }
    };
  }


}
