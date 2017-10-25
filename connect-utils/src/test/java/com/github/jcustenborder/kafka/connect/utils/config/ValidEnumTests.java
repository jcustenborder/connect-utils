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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidEnumTests {

  public enum TestEnum {
    one,
    two,
    three
  }

  @Test
  public void valid() {
    ValidEnum validEnum = ValidEnum.of(TestEnum.class);
    validEnum.ensureValid("testing", TestEnum.one.name());
  }

  @Test
  public void invalid() {
    assertThrows(ConfigException.class, () -> {
      ValidEnum validEnum = ValidEnum.of(TestEnum.class);
      validEnum.ensureValid("testing", "missing");
    });
  }

  @Test
  public void excluded() {
    assertThrows(ConfigException.class, () -> {
      ValidEnum validEnum = ValidEnum.of(TestEnum.class, "two");
      validEnum.ensureValid("testing", "two");
    });
  }

  @Test
  public void display() {
    final String expected = "ValidEnum{enum=TestEnum, allowed=[one, two, three]}";
    ValidEnum validEnum = ValidEnum.of(TestEnum.class);
    assertEquals(expected, validEnum.toString());
  }
}
