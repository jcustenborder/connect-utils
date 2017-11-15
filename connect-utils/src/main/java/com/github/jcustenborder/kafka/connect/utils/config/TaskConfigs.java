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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TaskConfigs {
  /**
   * Method will create a single from the supplied settings.
   *
   * @param settings
   * @return
   */
  public static List<Map<String, String>> single(Map<String, String> settings) {
    Preconditions.checkNotNull(settings, "settings cannot be null.");
    return ImmutableList.of(settings);
  }

  /**
   * Method is used to generate a list of taskConfigs based on the supplied settings.
   *
   * @param settings
   * @param taskCount
   * @return
   */
  public static List<Map<String, String>> multiple(Map<String, String> settings, final int taskCount) {
    Preconditions.checkNotNull(settings, "settings cannot be null.");
    Preconditions.checkState(taskCount > 0, "taskCount must be greater than 0.");
    final List<Map<String, String>> result = new ArrayList<>(taskCount);
    for (int i = 0; i < taskCount; i++) {
      result.add(settings);
    }
    return ImmutableList.copyOf(result);
  }
}
