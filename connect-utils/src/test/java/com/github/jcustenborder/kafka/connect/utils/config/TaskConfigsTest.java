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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskConfigsTest {

  @Test
  public void single() {
    final Map<String, String> settings = ImmutableMap.of(
        "foo", "bar"
    );
    final List<Map<String, String>> expected = ImmutableList.of(
        settings
    );
    final List<Map<String, String>> actual = TaskConfigs.single(settings);
    assertEquals(expected, actual);
  }

  @Test
  public void multiple() {
    final Map<String, String> settings = ImmutableMap.of(
        "foo", "bar"
    );
    final List<Map<String, String>> expected = ImmutableList.of(
        settings,
        settings,
        settings
    );
    final List<Map<String, String>> actual = TaskConfigs.multiple(settings, 3);
    assertEquals(expected, actual);
  }
}
