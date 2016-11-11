/**
 * Copyright © 2016 Jeremy Custenborder (jcustenborder@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.confluent.kafka.connect.utils.config;

import org.junit.Test;

public class ValidPortTest {

  @Test
  public void ensureValid() {
    ValidPort range = ValidPort.of();
    range.ensureValid("port", 1025);
  }

  @Test(expected = IllegalStateException.class)
  public void ensureValid_low() {
    ValidPort range = ValidPort.of();
    range.ensureValid("port", 1);
  }

  @Test(expected = IllegalStateException.class)
  public void ensureValid_high() {
    ValidPort range = ValidPort.of();
    range.ensureValid("port", 65536);
  }

  @Test(expected = IllegalStateException.class)
  public void constructor_high() {
    ValidPort range = ValidPort.of(1, Integer.MAX_VALUE);
  }

  @Test(expected = IllegalStateException.class)
  public void constructor_low() {
    ValidPort range = ValidPort.of(Integer.MIN_VALUE, 65535);
  }

  @Test(expected = IllegalStateException.class)
  public void constructor_lowgreaterthanhi() {
    ValidPort range = ValidPort.of(2, 1);
  }
}
