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
package com.github.jcustenborder.kafka.connect.utils;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionUtil {
  private static final Logger log = LoggerFactory.getLogger(VersionUtil.class);
  final static String FALLBACK_VERSION = "0.0.0.0";

  public static String version(Class<?> cls) {
    String result;

    try {
      result = cls.getPackage().getImplementationVersion();

      if (Strings.isNullOrEmpty(result)) {
        result = FALLBACK_VERSION;
      }
    } catch (Exception ex) {
      log.error("Exception thrown while getting error", ex);
      result = FALLBACK_VERSION;
    }
    return result;
  }

}
