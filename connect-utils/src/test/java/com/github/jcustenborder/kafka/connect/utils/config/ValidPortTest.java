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

import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidPortTest {

  @Test
  public void ensureValid() {
    ValidPort range = ValidPort.of();
    range.ensureValid("port", 1025);
  }

  @Test
  public void ensureValid_low() {
    assertThrows(ConfigException.class, () -> {
      ValidPort range = ValidPort.of();
      range.ensureValid("port", 1);
    });
  }

  @Test
  public void ensureValid_high() {
    assertThrows(ConfigException.class, () -> {
      ValidPort range = ValidPort.of();
      range.ensureValid("port", 65536);
    });

  }

  @Test
  public void constructor_high() {
    assertThrows(IllegalStateException.class, () -> {
      ValidPort range = ValidPort.of(1, Integer.MAX_VALUE);
    });

  }

  @Test
  public void constructor_low() {
    assertThrows(IllegalStateException.class, () -> {
      ValidPort range = ValidPort.of(Integer.MIN_VALUE, 65535);
    });
  }

  @Test
  public void constructor_lowgreaterthanhi() {
    assertThrows(IllegalStateException.class, () -> {
      ValidPort range = ValidPort.of(2, 1);
    });
  }

  @Test
  public void display() {
    final String expected = "ValidPort{start=1025, end=65535}";
    ValidPort range = ValidPort.of();
    assertEquals(expected, range.toString());
  }

}
