/**
 * Copyright (C) 2016 Jeremy Custenborder (jcustenborder@gmail.com)
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

import com.google.common.base.Preconditions;
import org.apache.kafka.common.config.AbstractConfig;

public class ConfigUtils {
  private ConfigUtils() {
  }

  public static <T> Class<T> getClass(AbstractConfig config, String key, Class<T> expected) {
    Preconditions.checkNotNull(config, "config cannot be null");
    Preconditions.checkNotNull(key, "key cannot be null");
    Preconditions.checkNotNull(expected, "expected cannot be null");
    Class<?> cls = config.getClass(key);
    Preconditions.checkState(expected.isAssignableFrom(cls), "'%s' is not assignable from '%s'", expected.getSimpleName(), cls.getSimpleName());
    return (Class<T>) cls;
  }
}
