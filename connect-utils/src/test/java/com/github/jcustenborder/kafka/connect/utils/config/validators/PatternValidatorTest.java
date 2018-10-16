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
package com.github.jcustenborder.kafka.connect.utils.config.validators;

import com.google.common.collect.ImmutableList;
import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PatternValidatorTest {

  @Test
  public void validString() {
    PatternValidator validator = new PatternValidator();
    validator.ensureValid("foo", ".+");
  }

  @Test
  public void validList() {
    PatternValidator validator = new PatternValidator();
    validator.ensureValid("foo", ImmutableList.of(".+"));
  }

  @Test
  public void invalidType() {
    ConfigException configException = assertThrows(ConfigException.class, ()->{
      PatternValidator validator = new PatternValidator();
      validator.ensureValid("foo", 1234);
    });

    assertTrue(configException.getMessage().contains("foo"));
  }
}
