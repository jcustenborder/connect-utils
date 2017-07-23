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

import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidHostnameAndPortTests {

  ValidHostnameAndPort validator;

  @BeforeEach
  public void setup() {
    this.validator = new ValidHostnameAndPort();
  }

  @Test
  public void ensureValidExceptions() {
    ConfigException exception = assertThrows(ConfigException.class, () -> {
      validator.ensureValid("test", null);
    });
    assertTrue(exception.getMessage().contains("must be a string or a list."));
    exception = assertThrows(ConfigException.class, () -> {
      validator.ensureValid("test", "");
    });
    assertTrue(exception.getMessage().contains("does not match pattern"));
    exception = assertThrows(ConfigException.class, () -> {
      validator.ensureValid("test", "localhost:99999");
    });
    assertTrue(exception.getMessage().contains("Port must be between 1 and 65535"));
  }

  @Test
  public void ensureValidString() {
    this.validator.ensureValid("test", "localhost:11211");
  }

  @Test
  public void ensureValidList() {
    this.validator.ensureValid("test", Arrays.asList("localhost:11211"));
  }

}
